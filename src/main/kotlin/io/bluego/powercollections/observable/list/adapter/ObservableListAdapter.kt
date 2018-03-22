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

package io.bluego.powercollections.observable.list.adapter

import io.bluego.powercollections.observable.list.ListObserver
import java.util.function.Predicate
import java.util.function.UnaryOperator
import kotlin.properties.Delegates

internal open class ObservableListAdapter<out T: MutableList<E>, E>(private val mObserver: ListObserver<E>,
                                                                    private val mList: T)
{
    protected var isSilent = false

    open fun notifyDataChanged() = mObserver.notifyDataChanged()

    open fun notifyDataChanged(index: Int) = mObserver.notifyDataChanged(index)

    open fun runSilentChanges(silentChanges: T.() -> Unit) {
        isSilent = true
        silentChanges(mList)
        isSilent = false
    }

    open fun add(element: E, add: (E) -> Boolean): Boolean {
        return add(element).apply { if (!isSilent) mObserver.wasAdded(mList.lastIndex, element) }
    }

    open fun add(index: Int, element: E, add: (Int, E) -> Unit) {
        add(index, element).also { if (!isSilent) mObserver.wasAdded(mList.lastIndex, element) }
    }

    open fun addAll(index: Int, elements: Collection<E>, addAll: (Int, Collection<E>) -> Boolean)
            : Boolean
    {
        val startIndex = mList.size
        return addAll(index, elements).also {
            if (!isSilent && it) mObserver.wasAdded(
                    elements.mapIndexed { index, e -> startIndex + index to e }.toMap())
        }
    }

    open fun addAll(elements: Collection<E>, addAll: (Collection<E>) -> Boolean): Boolean {
        val startIndex = mList.size
        return addAll(elements).also {
            if (!isSilent && it) mObserver.wasAdded(
                    elements.mapIndexed { index, e -> startIndex + index to e }.toMap())
        }
    }

    open fun clear(clear: () -> Unit) {
        if (!isSilent) {
            val list = mList.mapIndexed { index, e -> Pair(index, e) }.toMap()
            clear()
            mObserver.wasRemoved(list)
        } else clear()
    }

    open fun remove(element: E, remove: (E) -> Boolean): Boolean {
        return if (!isSilent) {
            val index = mList.indexOf(element)
            return if (index != -1) {
                val result = remove(element)
                assert(result) { "Result cannot be false. There must be a bug..." }
                mObserver.wasRemoved(index, element)
                result
            } else false
        } else remove(element)
    }

    open fun removeIf(p0: Predicate<in E>, removeIf: (Predicate<E>) -> Boolean): Boolean {
        throw NotImplementedError()
    }

    open fun removeAt(index: Int, removeAt: (Int) -> E): E {
        return removeAt(index).also { if (!isSilent) mObserver.wasRemoved(index, it) }
    }

    open fun removeAll(elements: Collection<E>, removeAll: (Collection<E>) -> Boolean): Boolean {
        return if (!isSilent) {
            val toBeRemoved = elements.mapNotNull {
                val index = mList.indexOf(it)
                if (index > -1) Pair(index, it).apply {println("FOUND: $it at $index") }
                else null
            }.toMap()
            if (toBeRemoved.isNotEmpty()) {
                val result = removeAll(elements)
                mObserver.wasRemoved(toBeRemoved)
                assert(result) { "Result must not be false. There must be a bug..." }
                result
            } else false
        } else removeAll(elements)
    }

    open fun removeRange(fromIndex: Int, toIndex: Int, removeRange: (Int, Int) -> Unit) {
        val rng = 0 .. mList.size
        var removedRange by Delegates.notNull<Map<Int, E>>()
        if (fromIndex in rng && toIndex in rng) {
            removedRange = mList.subList(fromIndex, toIndex).mapIndexed { index, e -> index to e }.toMap()
        }
        removeRange(fromIndex, toIndex)
        mObserver.wasRemoved(removedRange)
    }

    open fun replaceAll(p0: UnaryOperator<E>, replaceAll: (UnaryOperator<E>) -> Unit) {
        throw NotImplementedError()
    }

    open fun retainAll(elements: Collection<E>, retainAll: () -> MutableListIterator<E>): Boolean {
        requireNotNull(elements)
        var modified = false
        val it = retainAll()
        while (it.hasNext()) {
            if (!elements.contains(it.next())) {
                it.remove()
                modified = true
            }
        }
        return modified
    }

    open fun set(index: Int, element: E, set: (Int, E) -> E): E {
        return if (!isSilent) {
            val result = set(index, element)
            mObserver.wasReplaced(index, result, element)
            result
        } else set(index, element)
    }

    open fun subList(fromIndex: Int, toIndex: Int, subList: (Int, Int) -> MutableList<E>): MutableList<E> {
        return subList(fromIndex, toIndex)
    }

    open fun iterator(iterator: () -> MutableIterator<E>): MutableIterator<E> {
        return MutableObservableIterator(iterator())
    }

    open fun listIterator(listIterator: () -> MutableListIterator<E>): MutableListIterator<E> {
        return MutableObservableListIterator(listIterator())
    }

    open fun listIterator(index: Int, listIterator: (Int) -> MutableListIterator<E>): MutableListIterator<E> {
        return MutableObservableListIterator(listIterator(index))
    }

    protected open inner class MutableObservableIterator(
            private val mIterator: MutableIterator<E>)
        :MutableIterator<E> by mIterator
    {
        protected var last: E? = null

        override fun hasNext(): Boolean = mIterator.hasNext()

        override fun next(): E = mIterator.next().apply { last = this }

        //TODO: write more efficient index tracker
        override fun remove() {
            if (!isSilent) {
                val index = mList.indexOf(last)
                check(index >= 0) { "Index must not be smaller than 0. There must be a bug." }
                mIterator.remove()
                mObserver.wasRemoved(index, last)
            } else mIterator.remove()
        }
    }

    //TODO: Must be tested!!!
    protected open inner class MutableObservableListIterator(
            private val mIterator: MutableListIterator<E>)
        : MutableObservableIterator(mIterator), MutableListIterator<E>
    {
        override fun remove() {
            if (!isSilent) {
                val index = nextIndex() - 1
                mIterator.remove()
                mObserver.wasRemoved(index, last)
            } else mIterator.remove()
        }

        override fun previous(): E = mIterator.previous().apply { last = this }

        override fun add(element: E) {
            if (!isSilent) {
                val index = nextIndex()
                mIterator.add(element)
                mObserver.wasAdded(index, element)
            } else mIterator.add(element)
        }

        override fun set(element: E) {
            if (!isSilent) {
                val index = nextIndex() - 1
                mIterator.set(element)
                mObserver.wasReplaced(index, last, element)
            } else mIterator.set(element)
        }

        override fun hasPrevious(): Boolean = mIterator.hasPrevious()

        override fun nextIndex(): Int = mIterator.nextIndex()

        override fun previousIndex(): Int = mIterator.previousIndex()
    }
}