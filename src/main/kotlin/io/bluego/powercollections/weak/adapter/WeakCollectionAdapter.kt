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

package io.bluego.powercollections.weak.adapter

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

internal open class WeakCollectionAdapter<E: Any, out T: MutableCollection<WeakElement<E>>>(private val mCollection: T) {

    private val mQueue = ReferenceQueue<E?>()

    open fun add(element: E?, add: (WeakElement<E>) -> Boolean): Boolean {
        processQueue()
        return WeakElement.create(element, mQueue)?.let(add) ?: false
    }

    open fun addAll(elements: Collection<E?>, addAll: (Collection<WeakElement<E>>) -> Boolean): Boolean {
        processQueue()
        return elements.mapNotNull { WeakElement.create(it, mQueue) }.let {
            if (it.isEmpty()) false
            else addAll(it)
        }
    }

    open fun clear(clear: () -> Unit) = clear()

    open fun iterator(iterator: () -> MutableIterator<WeakElement<E>>): MutableIterator<E?> {
        processQueue()
        return ReferenceIterator(iterator())
    }

    open fun remove(element: E?, remove: (WeakElement<E>) -> Boolean): Boolean
            = WeakElement.create(element)?.let(remove) ?: false

    open fun removeAll(elements: Collection<E?>, removeAll: (Collection<WeakElement<E>>) -> Boolean): Boolean
            = removeAll(elements.mapNotNull { WeakElement.create(it) }).apply { processQueue() }

    open fun retainAll(elements: Collection<E?>, retainAll: (Collection<WeakElement<E>>) -> Boolean): Boolean
            =  retainAll(elements.mapNotNull { WeakElement.Companion.create(it) }).apply { processQueue() }

    open fun size(size: () -> Int): Int {
        processQueue()
        return size()
    }

    open fun contains(element: E?, contains: (WeakElement<E>) -> Boolean): Boolean
            = WeakElement.create(element)?.let(contains) ?: false

    open fun containsAll(elements: Collection<E?>, containsAll: (Collection<WeakElement<E>>) -> Boolean): Boolean {
        val weakElements = elements.mapNotNull { WeakElement.create(it) }
        return containsAll(weakElements)
    }

    open fun isEmpty(isEmpty: () -> Boolean): Boolean = isEmpty()

    open fun optimize() = processQueue()

    open fun forceOptimize() {
        mCollection.iterator().let {
            while (it.hasNext()) { it.next().get() ?: it.remove() }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun processQueue() {
        do mQueue.poll()?.apply { mCollection.remove(this as WeakElement<E>) } ?: return
        while (true)
    }

    private inner class ReferenceIterator(private val mIterator: MutableIterator<WeakElement<E>>)
        : MutableIterator<E?>
    {
        override fun hasNext(): Boolean = mIterator.hasNext()

        override fun next(): E? = mIterator.next().get()

        override fun remove() = mIterator.remove()
    }
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