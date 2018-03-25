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

package io.bluego.powercollections.weak

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * All elements of the [Collection] are wrapped in a [WeakReference].
 */
interface WeakCollection<out T>: Collection<T> {

    /**
     * Removes all empty items that has been garbage collected.
     */
    fun optimize()

    /**
     * Removes all empty items that has been garbage collected.
     */
    fun forceOptimize()
}

/**
 * All elements of the [MutableCollection] are wrapped in a [WeakReference].
 */
interface MutableWeakCollection<T>: WeakCollection<T>, MutableCollection<T>

abstract class AbstractWeakCollection<T: Any> protected constructor(
        private val mCollection: MutableCollection<WeakElement<T>>)
    : MutableWeakCollection<T?>
{

    constructor(): this(arrayListOf())

    private val mQueue = ReferenceQueue<T?>()

    override fun add(element: T?): Boolean {
        processQueue()
        return WeakElement.create(element, mQueue)?.let { mCollection.add(it) } ?: false
    }

    override fun addAll(elements: Collection<T?>): Boolean {
        processQueue()
        return elements.mapNotNull { WeakElement.create(it, mQueue) }.let {
            if (it.isEmpty()) false
            else mCollection.addAll(it)
        }
    }

    override fun clear() = mCollection.clear()

    override fun iterator(): MutableIterator<T?> {
        processQueue()
        return ReferenceIterator(mCollection.iterator())
    }

    override fun remove(element: T?): Boolean =
            mCollection.remove(WeakElement.create(element)).apply { processQueue() }

    override fun removeAll(elements: Collection<T?>): Boolean =
            mCollection.removeAll(elements.map { WeakElement.create(it) })

    override fun retainAll(elements: Collection<T?>): Boolean {
        processQueue()
        return mCollection.retainAll(elements.map { WeakElement.create(it) })
    }

    override val size: Int
        get() {
            processQueue()
            return mCollection.size
        }

    override fun contains(element: T?): Boolean = mCollection.contains(WeakElement.create(element))

    override fun containsAll(elements: Collection<T?>): Boolean {
        val weakElements = elements.map { WeakElement.create(it) }
        return mCollection.containsAll(weakElements)
    }

    override fun isEmpty(): Boolean = mCollection.isEmpty()

    override fun optimize() = processQueue()

    override fun forceOptimize() {
        iterator().let {
            while (it.hasNext()) { it.next() ?: it.remove() }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun processQueue() {
        do mQueue.poll()?.apply { mCollection.remove(this as WeakElement<T>) } ?: return
        while (true)
    }

    private inner class ReferenceIterator(private val mIterator: MutableIterator<WeakElement<T>>)
        : MutableIterator<T?>
    {
        override fun hasNext(): Boolean = mIterator.hasNext()

        override fun next(): T? = mIterator.next().get()

        override fun remove() = mIterator.remove()
    }

    class WeakElement<T: Any> : WeakReference<T>
    {
        private var hash: Int = 0 /* Hashcode of key, stored here since the key
                           may be tossed by the GC */


        private constructor(element: T) : super(element) {
            hash = element.hashCode()
        }

        private constructor(element: T, q: ReferenceQueue<T?>) : super(element, q) {
            hash = element.hashCode()
        }

        companion object {

            fun <T: Any> create(element: T?):  WeakElement<T>? =
                    if (element === null) null else WeakElement(element)

            fun <T: Any> create(element: T?, queue: ReferenceQueue<T?>):  WeakElement<T>? =
                    if (element === null) null else WeakElement(element, queue)

        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is WeakElement<*>) return false
            val t = this.get()
            val u = other.get()
            if (t === u) return true
            return if (t == null || u == null) false else t == u
        }

        override fun hashCode(): Int = hash
    }
}

class HashWeakCollection<T: Any>(capacity: Int = 16) : AbstractWeakCollection<T>(ArrayList(capacity)) {

    companion object {
        fun <T: Any> create(list: List<T>) : HashWeakCollection<T> {
            val weakCollection = HashWeakCollection<T>()
            weakCollection.addAll(list)
            return weakCollection
        }
    }
}

/**
 * Creates an empty [MutableWeakCollection].
 *
 * @return Empty [MutableWeakCollection]
 */
fun <T: Any> mutableWeakCollectionOf(): MutableWeakCollection<T?> = HashWeakCollection()

/**
 * Creates an [MutableWeakCollection] with [elements]s.
 *
 * @return [MutableWeakCollection] with [elements]s
 */
fun <T: Any> mutableWeakCollectionOf(vararg elements: T): MutableWeakCollection<T?>
        = HashWeakCollection.create(arrayListOf(*elements))

/**
 * Creates an empty [WeakCollection].
 *
 * @return Empty [WeakCollection]
 */
fun <T: Any> weakCollectionOf(): WeakCollection<T?> = mutableWeakCollectionOf()

/**
 * Creates an [WeakCollection] with [elements]s.
 *
 * @return [WeakCollection] with [elements]s
 */
fun <T: Any> weakCollectionOf(vararg elements: T): WeakCollection<T?>
        = mutableWeakCollectionOf(*elements)

