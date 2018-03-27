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

package io.bluego.powercollections

/**
 * This [Map] has unique keys [K] and unique values [V].
 */
interface BiMap<K: Any, V: Any> : Map<K, V> {

    override val values: Set<V>

    /**
     * Swaps [keys] and [values].
     * Grants search access to [keys] by [values].
     */
    val inverse: BiMap<V, K>
}

/**
 * This [MutableMap] has unique [keys] and unique [values].
 */
interface MutableBiMap<K: Any, V: Any> : BiMap<K, V>, MutableMap<K, V> {

    override val values: MutableSet<V>

    override val inverse: MutableBiMap<V, K>

    /**
     * Removes an already existing pair that contains a value that is identical to the new [value].
     */
    fun forcePut(key: K, value: V): V?
}

abstract class AbstractBiMap<K: Any, V: Any> protected constructor(
        private val mDirect: MutableMap<K, V>,
        private val mReverse: MutableMap<V, K>)
    : MutableBiMap<K, V>
{
    override val size: Int
        get() = mDirect.size

    override val inverse: MutableBiMap<V, K> by lazy {
        object : AbstractBiMap<V, K>(mReverse, mDirect) {
            override val inverse: MutableBiMap<K, V>
                get() = this@AbstractBiMap
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> =
            BiMapSet(mDirect.entries, { it.key }, { BiMapEntry(it) })

    override val keys: MutableSet<K>
        get() = BiMapSet(mDirect.keys, { it }, { it })

    override val values: MutableSet<V>
        get() = inverse.keys


    constructor() : this(mutableMapOf(), mutableMapOf())


    override fun forcePut(key: K, value: V): V? {
        val oldValue = mDirect.put(key, value)?.also { mReverse.remove(it) }
        mReverse.put(value, key)?.also { mDirect.remove(it) }
        return oldValue
    }

    override fun put(key: K, value: V): V? {
        require(value !in mReverse) { "BiMap already contains value $value" }
        return forcePut(key, value)
    }

    override fun putAll(from: Map<out K, V>) {
        from.values.forEach { value ->
            require(value !in mReverse) { "BiMap already contains value $value" }
        }
        from.entries.forEach { forcePut(it.key, it.value) }
    }

    override fun remove(key: K): V? {
        return mDirect.remove(key)?.also { mReverse.remove(it) }
    }

    override fun clear() {
        mDirect.clear()
        mReverse.clear()
    }

    override fun get(key: K): V? = mDirect[key]

    override fun containsKey(key: K): Boolean = key in mDirect

    override fun containsValue(value: V): Boolean = value in mReverse

    override fun isEmpty(): Boolean = mDirect.isEmpty()

    private inner class BiMapSet<T>(
            private val mElements: MutableSet<T>,
            private val mKeyGetter: (T) -> K,
            private val mElementWrapper: (T) -> T)
        : MutableSet<T> by mElements
    {
        override fun remove(element: T): Boolean {
            if (element !in this) return false

            val key = mKeyGetter(element)
            val value = mDirect.remove(key) ?: return false
            try {
                mReverse.remove(value)
            } catch (throwable: Throwable) {
                mDirect[key] = value
                throw throwable
            }
            return true
        }

        override fun clear() {
            mDirect.clear()
            mReverse.clear()
        }

        override fun iterator(): MutableIterator<T> {
            val iterator = mElements.iterator()
            return BiMapSetIterator(iterator, mKeyGetter, mElementWrapper)
        }
    }

    private inner class BiMapSetIterator<out T>(
            private val mIterator: MutableIterator<T>,
            private val mKeyGetter: (T) -> K,
            private val mElementWrapper: (T) -> T)
        : MutableIterator<T>
    {
        private var mLast: T? = null

        override fun hasNext(): Boolean {
            return mIterator.hasNext()
        }

        override fun next(): T {
            val element = mIterator.next().apply {
                mLast = this
            }
            return mElementWrapper(element)
        }

        override fun remove() {
            checkNotNull(mLast as Any?) { "Move to an element before removing it" }
            try {
                val key = mKeyGetter(mLast!!)
                val value = mDirect[key] ?: throw IllegalStateException("BiMap doesn't contain key $key")
                mReverse.remove(value)
                try {
                    mIterator.remove()
                } catch (throwable: Throwable) {
                    mReverse[value] = key
                    throw throwable
                }
            } finally {
                mLast = null
            }
        }
    }

    private inner class BiMapEntry(private val mEntry: MutableMap.MutableEntry<K, V>)
        : MutableMap.MutableEntry<K, V> by mEntry
    {
        override fun setValue(newValue: V): V {
            if (mEntry.value == newValue) {
                mReverse[newValue] = mEntry.key
                return try {
                    mEntry.setValue(newValue)
                } catch (throwable: Throwable) {
                    mReverse[mEntry.value] = mEntry.key
                    throw throwable
                }
            } else {
                require(newValue !in mReverse) { "BiMap already contains value $newValue" }
                mReverse[newValue] = mEntry.key
                return try {
                    mEntry.setValue(newValue)
                } catch (throwable: Throwable) {
                    mReverse.remove(newValue)
                    throw throwable
                }
            }
        }
    }
}

class HashBiMap<K: Any, V: Any>(capacity: Int = 16) : AbstractBiMap<K, V>(HashMap(capacity), HashMap(capacity)) {
    companion object {
        fun <K: Any, V: Any> create(map: Map<K, V>): HashBiMap<K, V> {
            val biMap = HashBiMap<K, V>()
            biMap.putAll(map)
            return biMap
        }
    }
}

/**
 * Creates an empty [MutableBiMap].
 *
 * @return Empty [MutableBiMap]
 */
fun <K: Any, V: Any> mutableBiMapOf(): MutableBiMap<K, V> = HashBiMap()

/**
 * Creates an [MutableBiMap] with [elements].
 *
 * @return [MutableBiMap] with [elements]
 */
fun <K: Any, V: Any> mutableBiMapOf(vararg elements: Pair<K, V>): MutableBiMap<K, V>
        = HashBiMap.create(mapOf(*elements))

/**
 * Creates an [MutableBiMap] with [map].
 *
 * @return [MutableBiMap] with [map]
 */
fun <K: Any, V: Any> mutableBiMapOf(map: Map<K, V>): MutableBiMap<K, V>
        = HashBiMap.create(map)

/**
 * Creates an empty [BiMap].
 *
 * @return Empty [BiMap]
 */
fun <K: Any, V: Any> biMapOf(): BiMap<K, V> = mutableBiMapOf()

/**
 * Creates an [BiMap] with [elements].
 *
 * @return [BiMap] with [elements]
 */
fun <K: Any, V: Any> biMapOf(vararg elements: Pair<K, V>): BiMap<K, V>
        = mutableBiMapOf(*elements)

/**
 * Creates an [BiMap] with [map].
 *
 * @return [BiMap] with [map]
 */
fun <K: Any, V: Any> biMapOf(map: Map<K, V>): BiMap<K, V>
        = mutableBiMapOf(map)