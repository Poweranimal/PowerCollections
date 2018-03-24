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

package io.bluego.powercollections.bounded

import java.util.*

/**
 * A [List] that has a fixed capacity.
 */
interface BoundedList<out E>: List<E>, Boundable

/**
 * A [MutableList] that has a fixed capacity.
 */
interface MutableBoundedList<E>: MutableList<E>, BoundedList<E>, MutableBoundable {

    /**
     * Can only add an item to the [MutableBoundedList], if its [maxCapacity] is not reached.
     */
    override fun add(element: E): Boolean

    /**
     * Can only add an item to the [MutableBoundedList], if its [maxCapacity] is not reached.
     */
    override fun add(index: Int, element: E)

    /**
     * Can only add items to the [MutableBoundedList], if its [maxCapacity] is not reached.
     */
    override fun addAll(elements: Collection<E>): Boolean

    /**
     * Can only add items to the [MutableBoundedList], if its [maxCapacity] is not reached.
     */
    override fun addAll(index: Int, elements: Collection<E>): Boolean

    /**
     * Adds items to the [MutableBoundedList].
     * If the [MutableBoundedList]'s [maxCapacity] is reached, the item gets added to the tail
     * and the head (the oldest item) gets removed.
     * @see [MutableList.add]
     * @return true, if head item was removed
     */
    fun forceAdd(element: E): Boolean

    /**
     * Adds items to the [MutableBoundedList].
     * If the [MutableBoundedList]'s [maxCapacity] is reached, the item gets added to the tail
     * and the head (the oldest item) gets removed.
     * @see [MutableList.add]
     * @return true, if head item was removed
     */
    fun forceAdd(index: Int, element: E): Boolean

    /**
     * Adds items to the [MutableBoundedList].
     * If the [MutableBoundedList]'s [maxCapacity] is reached, the item gets added to the tail
     * and the head (the oldest item) gets removed.
     * @see [MutableList.addAll]
     * @return true, if head item was removed
     */
    fun forceAddAll(elements: Collection<E>): Boolean

    /**
     * Adds items to the [MutableBoundedList].
     * If the [MutableBoundedList]'s [maxCapacity] is reached, the item gets added to the tail
     * and the head (the oldest item) gets removed.
     * @see [MutableList.addAll]
     * @return true, if head item was removed
     */
    fun forceAddAll(index: Int, elements: Collection<E>): Boolean

}

/**
 * A [MutableBoundedList] baked by [LinkedList] that has a fixed capacity determined by [maxCapacity].
 */
abstract class AbstractBoundedList<E>
    : LinkedList<E>, MutableBoundedList<E>
{
    constructor(maxCapacity: Int) : super() {
        checkInitialMaxCapacity(maxCapacity)
        this.maxCapacity = maxCapacity
    }

    constructor(maxCapacity: Int, p0: Collection<E>?) : super(p0) {
        checkInitialMaxCapacity(maxCapacity)
        this.maxCapacity = maxCapacity
    }

    final override var maxCapacity: Int
        private set

    /**
     * [LinkedList] calls [java.util.LinkedList.addFirst]
     */
    override fun push(p0: E) {
        super.push(p0)
    }

    /**
     * [LinkedList] calls [java.util.LinkedList.add]
     */
    override fun offer(p0: E): Boolean = super.offer(p0)

    /**
     * [LinkedList] calls [java.util.LinkedList.addFirst]
     */
    override fun offerFirst(p0: E): Boolean = super.offerFirst(p0)

    /**
     * [LinkedList] calls [java.util.LinkedList.addLast]
     */
    override fun offerLast(p0: E): Boolean = super.offerLast(p0)

    /**
     * @throws IndexOutOfBoundsException, if the [BoundedList] is full of items
     */
    override fun add(element: E): Boolean {
        checkCapacity()
        return super.add(element)
    }

    /**
     * @throws IndexOutOfBoundsException, if the [BoundedList] is full of items
     */
    override fun add(index: Int, element: E) {
        checkCapacity()
        return super.add(index, element)
    }

    /**
     * @throws IndexOutOfBoundsException, if the [BoundedList] is full of items
     */
    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        checkCapacity(elements)
        return super.addAll(index, elements)
    }

    /**
     * [LinkedList] calls [java.util.LinkedList.addAll]
     */
    override fun addAll(elements: Collection<E>): Boolean {
        return super.addAll(elements)
    }

    override fun addFirst(p0: E) {
        checkCapacity()
        super.addFirst(p0)
    }

    override fun addLast(p0: E) {
        checkCapacity()
        super.addLast(p0)
    }

    override fun forceAdd(element: E): Boolean {
        super.add(element)
        return if (maxCapacity < size) {
            super.removeFirst()
            true
        } else false
    }

    override fun forceAdd(index: Int, element: E): Boolean {
        super.add(index, element)
        return if (maxCapacity < size) {
            super.removeFirst()
            true
        } else false
    }

    override fun forceAddAll(index: Int, elements: Collection<E>): Boolean {
        if (index !in 0 .. size) throw IndexOutOfBoundsException()
        val freeSpace = maxCapacity - size
        return if (elements.size > freeSpace) {

            if (elements.size > maxCapacity) {
                val dropCount = elements.size - maxCapacity
                if (isNotEmpty()) clear()
                addAll(0, elements.drop(dropCount))
                true
            } else {
                val dropCount = elements.size - freeSpace
                removeRange(0, dropCount)
                addAll(if (size < index) size else index, elements)
                true
            }

        } else {
            addAll(index, elements)
            false
        }
    }

    override fun forceAddAll(elements: Collection<E>): Boolean {
        val freeSpace = maxCapacity - size
        return if (elements.size > freeSpace) {

            if (elements.size > maxCapacity) {
                val dropCount = elements.size - maxCapacity
                if (isNotEmpty()) clear()
                addAll(elements.drop(dropCount))
                true
            } else {
                val dropCount = elements.size - freeSpace
                removeRange(0, dropCount)
                addAll(elements)
                true
            }

        } else {
            addAll(elements)
            false
        }
    }

    /**
     * Can only decrease the [maxCapacity], if [newSize] >= currentSize, otherwise it throws an
     * Exception.
     * @throws IllegalStateException
     * @see MutableBoundable.resize
     */
    override fun resize(newSize: Int) {
        checkResize(size, newSize)
        maxCapacity = newSize
    }

    /**
     * If [newSize] < [size], the difference ([size] - [newSize]) of old items (the ones at the head) will be removed.
     * @see MutableBoundable.forceResize
     */
    override fun forceResize(newSize: Int): Boolean {
        maxCapacity = newSize
        return if (newSize < size) {
            removeRange(0, size - newSize)
            true
        } else false
    }

    /*
     * VERY IMPORTANT:
     * Do not remove 'super<LinkedList>'.
     * If so, there'll be a stack overflow error.
     */
    override fun removeAt(index: Int): E {
        return super<LinkedList>.removeAt(index)
    }

    override fun iterator(): MutableIterator<E> {
        return super.iterator()
    }

    override fun listIterator(): MutableListIterator<E>
            = BoundedListIterator(super.listIterator())

    override fun listIterator(index: Int): MutableListIterator<E>
            = BoundedListIterator(super.listIterator(index))

    private inner class BoundedListIterator(private val mIterator: MutableListIterator<E>)
        : MutableListIterator<E> by mIterator
    {
        override fun add(element: E) {
            checkCapacity()
            mIterator.add(element)
        }
    }

    /*
     * IMPORTANT!!!
     *
     * The returned subList is not synchronized with this list.
     */
    //TODO: add sublist synchronization
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E>
            = super.subList(fromIndex, toIndex)
}

internal class BoundedListBuilder<E>: AbstractBoundedList<E> {
    private constructor(maxCapacity: Int) : super(maxCapacity)
    private constructor(maxCapacity: Int, p0: Collection<E>?) : super(maxCapacity, p0)

    companion object {

        fun <E> create(maxCapacity: Int) = BoundedListBuilder<E>(maxCapacity)

        fun <E> create(maxCapacity: Int, collection: Collection<E>)
                = BoundedListBuilder(maxCapacity, collection)

    }
}

/**
 * Creates an empty [MutableBoundedList].
 *
 * @return Empty [MutableBoundedList]
 */
fun <E> mutableBoundedListOf(maxCapacity: Int): MutableBoundedList<E>
        = BoundedListBuilder.create(maxCapacity)

/**
 * Creates an [MutableBoundedList] with [elements].
 *
 * @return [MutableBoundedList] with [elements]
 */
fun <E> mutableBoundedListOf(maxCapacity: Int, vararg elements: E): MutableBoundedList<E>
        = BoundedListBuilder.create(maxCapacity, listOf(*elements))

/**
 * Creates an empty [BoundedList].
 *
 * @return Emtpy [BoundedList]
 */
fun <E> boundedListOf(maxCapacity: Int): BoundedList<E> = mutableBoundedListOf(maxCapacity)

/**
 * Creates an [BoundedList] with [elements]s.
 *
 * @return [BoundedList] with [elements]s
 */
fun <E> boundedListOf(maxCapacity: Int, vararg elements: E): BoundedList<E> =
        mutableBoundedListOf(maxCapacity, *elements)