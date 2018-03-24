/*
 * MIT License
 *
 * Copyright (c) 2018 Felix Pröhl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.bluego.powercollections.observable.map.adapter

import io.bluego.powercollections.getKeyByValue
import io.bluego.powercollections.observable.map.MapObserver
import java.util.function.BiFunction

open class ObservableMapAdapter<K, V, out T: MutableMap<K, V>>(
        private val mObserver: MapObserver<K, V>,
        private val mMap: T)
{

    protected open var isSilent = false

    open fun getEntries(entries: () -> MutableSet<MutableMap.MutableEntry<K, V>>)
            : MutableSet<MutableMap.MutableEntry<K, V>>
            = ObserverMapSet(entries(), { it.key }, { it.value }, { ObserverMapEntry(it) })

    open fun getKeys(keys:() -> MutableSet<K>): MutableSet<K>
            = ObserverMapSet(keys(), { it }, { mMap[it]!! }, { it })

    open fun getValues(values: () -> MutableCollection<V>): MutableCollection<V>
            = ObserverMapCollection(values(), { mMap.getKeyByValue(it)!! }, { it }, { it })

    open fun notifyDataChanged() = mObserver.notifyDataChanged()

    open fun notifyDataChanged(key: K) = mObserver.notifyDataChanged(key)

    open fun runSilentChanges(silentChanges: T.() -> Unit) {
        isSilent = true
        silentChanges(mMap)
        isSilent = false
    }

    open fun clear(clear: () -> Unit) {
        if (!isSilent) {
            val map = mMap.toMap()
            clear()
            mObserver.wasRemoved(map)
        } else clear()
    }

    open fun put(key: K, value: V, put: (K, V) -> V?): V? {
        return if (!isSilent) {
            val oldValue: V? = put(key, value)
            if (oldValue !== null) mObserver.wasReplaced(key, oldValue, value)
            else mObserver.wasAdded(key, value)
            oldValue
        } else put(key, value)
    }

    open fun putAll(from: Map<out K, V>, putAll: (Map<out K, V>) -> Unit) {
        if (!isSilent) {
            val addedValues = mutableMapOf<K, V>()
            val replacedValues = mutableMapOf<K, Pair<V, V>>()
            from.entries.forEach { (key, value) ->
                runSilentChanges {
                    val oldValue = put(key, value)
                    if (oldValue !== null) replacedValues.put(key, oldValue to value)
                    else addedValues.put(key, value)
                }
            }
            putAll(from)
            if (addedValues.isNotEmpty()) mObserver.wasAdded(addedValues)
            if (replacedValues.isNotEmpty()) mObserver.wasReplaced(replacedValues)
        } else putAll(from)
    }

    open fun remove(key: K, remove: (K) -> V?): V? {
        return if (!isSilent) {
            val wasRemoved: V? = remove(key)
            if (wasRemoved !== null) mObserver.wasRemoved(key, wasRemoved)
            wasRemoved
        } else remove(key, remove)
    }

    open fun remove(key: K, value: V, remove: (K, V) -> Boolean): Boolean {
        return if (!isSilent) {
            val wasRemoved = remove(key, value)
            if (wasRemoved) mObserver.wasRemoved(key, value)
            wasRemoved
        } else remove(key, value)
    }

    open fun replace(p0: K, p1: V, p2: V, replace: (K, V, V) -> Boolean): Boolean {
        return if (!isSilent) {
            val wasReplaced = replace(p0, p1, p2)
            if (wasReplaced) mObserver.wasReplaced(p0, p1, p2)
            wasReplaced
        } else replace(p0, p1, p2)
    }

    @Suppress("UNCHECKED_CAST", "ALWAYS_NULL")
    open fun putIfAbsent(p0: K, p1: V, putIfAbsent: (K, V) -> V?): V? {
        return if (!isSilent) {
            val hasKey = mMap.containsKey(p0)
            val oldValue = putIfAbsent(p0, p1)
            if (oldValue === null) {
                if (hasKey) mObserver.wasReplaced(p0, oldValue as V, p1)
                else mObserver.wasAdded(p0, p1)
            }
            oldValue
        } else putIfAbsent(p0, p1)
    }

    @Suppress("UNCHECKED_CAST")
    open fun replace(p0: K, p1: V, replace: (K, V) -> V?): V? {
        return if (!isSilent) {
            val oldValue = replace(p0, p1)
            if (oldValue !== null || mMap.containsKey(p0)) mObserver.wasReplaced(p0, oldValue as V, p1)
            oldValue
        } else replace(p0, p1)
    }

    open fun replaceAll(p0: BiFunction<in K, in V, out V>,
                        replaceAll: (BiFunction<in K, in V, out V>) -> Unit)
    {
        throw NotImplementedError()
    }

    protected open inner class ObserverMapCollection<T>(
            private val mElements: MutableCollection<T>,
            private val mKeyGetter: (T) -> K,
            private val mValueGetter: (T) -> V,
            private val mElementWrapper: (T) -> T)
        : MutableCollection<T> by mElements
    {
        override fun remove(element: T): Boolean {
            val key = mKeyGetter(element)
            val value = mValueGetter(element)
            val result = mElements.remove(element)
            if (result) mObserver.wasRemoved(key, value)
            return result
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            throw NotImplementedError()
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            throw NotImplementedError()
        }

        override fun clear() = mElements.clear()

        override fun iterator(): MutableIterator<T> {
            val iterator = mElements.iterator()
            return ObserverMapCollectionIterator(iterator, mKeyGetter, mValueGetter, mElementWrapper)
        }

        protected open inner class ObserverMapCollectionIterator<out T>(
                private val mIterator: MutableIterator<T>,
                private val mKeyGetter: (T) -> K,
                private val mValueGetter: (T) -> V,
                private val mElementWrapper: (T) -> T)
            : MutableIterator<T>
        {

            private var mLast: T? = null

            override fun hasNext(): Boolean = mIterator.hasNext()

            override fun next(): T {
                val element = mIterator.next().apply { mLast = this }
                return mElementWrapper(element)
            }

            override fun remove() {
                checkNotNull(mLast as Any) { "Call Iterator.next()" }
                val key = mKeyGetter(mLast!!)
                val value = mValueGetter(mLast!!)
                try {
                    mIterator.remove()
                    mObserver.wasRemoved(key, value)
                } catch (throwable: Throwable) {
                    throw throwable
                }
            }
        }
    }

    protected open inner class ObserverMapSet<T>(
            private val mElements: MutableSet<T>,
            private val mKeyGetter: (T) -> K,
            private val mValueGetter: (T) -> V,
            private val mElementWrapper: (T) -> T)
        : MutableSet<T> by mElements
    {
        override fun remove(element: T): Boolean {
            val key = mKeyGetter(element)
            val value = mValueGetter(element)
            val result = mElements.remove(element)
            if (result) mObserver.wasRemoved(key, value)
            return result
        }

        override fun clear() = mElements.clear()

        override fun iterator(): MutableIterator<T> {
            val iterator = mElements.iterator()
            return ObserverMapSetIterator(iterator, mKeyGetter, mValueGetter, mElementWrapper)
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            throw NotImplementedError()
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            throw NotImplementedError()
        }

        protected open inner class ObserverMapSetIterator<out T>(
                private val mIterator: MutableIterator<T>,
                private val mKeyGetter: (T) -> K,
                private val mValueGetter: (T) -> V,
                private val mElementWrapper: (T) -> T)
            : MutableIterator<T>
        {

            private var mLast: T? = null

            override fun hasNext(): Boolean = mIterator.hasNext()

            override fun next(): T {
                val element = mIterator.next().apply { mLast = this }
                return mElementWrapper(element)
            }

            override fun remove() {
                checkNotNull(mLast as Any)
                val key = mKeyGetter(mLast!!)
                val value = mValueGetter(mLast!!)
                try {
                    mIterator.remove()
                    mObserver.wasRemoved(key, value)
                } catch (throwable: Throwable) {
                    throw throwable
                }
            }
        }
    }

    protected open inner class ObserverMapEntry(private val mEntry: MutableMap.MutableEntry<K, V>)
        : MutableMap.MutableEntry<K, V> by mEntry
    {
        override fun setValue(newValue: V): V {
            return try {
                val oldValue = mEntry.setValue(newValue)
                mObserver.wasReplaced(mEntry.key, oldValue, newValue)
                oldValue
            } catch (throwable: Throwable) {
                throw throwable
            }
        }
    }
}