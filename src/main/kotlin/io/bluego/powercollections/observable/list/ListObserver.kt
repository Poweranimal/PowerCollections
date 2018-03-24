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

package io.bluego.powercollections.observable.list

interface ListObserver<in E> {

    /**
     * Manually called, if changes has been made
     */
    fun notifyDataChanged()

    /**
     * Manually called, if changes has been made at [index].
     */
    fun notifyDataChanged(index: Int)

    /**
     * Called when a new item was added.
     * Actions that may trigger this event:
     */
    fun wasAdded(index: Int, element: E)

    /**
     * Called when new items were added.
     */
    fun wasAdded(elements: Map<Int, E>)

    /**
     * Called when items were removed.
     */
    fun wasRemoved(index: Int, element: E)

    /**
     * Called when items were removed.
     */
    fun wasRemoved(elements: Map<Int, E>)

    /**
     * Called when a item was replaced by a new item.
     */
    fun wasReplaced(index: Int, lastElement: E, newElement: E)

    /**
     * Called when a item was replaced by a new item.
     */
    fun wasReplaced(map: Map<Int, Pair<E, E>>)
}

@Suppress("AddVarianceModifier")
class ListObserverDSL<E> private constructor() {

    companion object {

        fun <E>create(listObserverDSL: ListObserverDSL<E>.() -> Unit): ListObserver<E> {

            return ListObserverDSL<E>().apply(listObserverDSL).run {

                object : ListObserver<E> {
                    override fun notifyDataChanged() = mNotifyDataChanged()

                    override fun notifyDataChanged(index: Int) = mNotifyDataChangedIndex(index)

                    override fun wasAdded(index: Int, element: E) = mWasAdded(index, element)

                    override fun wasAdded(elements: Map<Int, E>) = mWasAddedMultiple(elements)

                    override fun wasRemoved(index: Int, element: E) = mWasRemoved(index, element)

                    override fun wasRemoved(elements: Map<Int, E>) = mWasRemovedMultiple(elements)

                    override fun wasReplaced(index: Int, lastElement: E, newElement: E)
                            = mWasReplaced(index, lastElement, newElement)

                    override fun wasReplaced(map: Map<Int, Pair<E, E>>) = mWasReplacedMultiple(map)

                }
            }
        }
    }

    private var mNotifyDataChanged: () -> Unit = throw NotImplementedError()
    private var mNotifyDataChangedIndex: (Int) -> Unit = throw NotImplementedError()
    private var mWasAdded: (Int, E) -> Unit = throw NotImplementedError()
    private var mWasAddedMultiple: (Map<Int, E>) -> Unit = throw NotImplementedError()
    private var mWasRemoved: (Int, E) -> Unit = throw NotImplementedError()
    private var mWasRemovedMultiple: (Map<Int, E>) -> Unit = throw NotImplementedError()
    private var mWasReplaced: (Int, E, E) -> Unit =  throw NotImplementedError()
    private var mWasReplacedMultiple: (Map<Int, Pair<E, E>>) -> Unit = throw NotImplementedError()


    /**
     * @see [ListObserver.notifyDataChanged]
     */
    fun notifyDataChanged(action: () -> Unit) {
        mNotifyDataChanged = action
    }

    /**
     * @see [ListObserver.notifyDataChanged]
     */
    fun notifyDataChangedByIndex(action: (index: Int) -> Unit) {
        mNotifyDataChangedIndex = action
    }

    /**
     * @see [ListObserver.wasAdded]
     */
    fun wasAdded(action: (index: Int, element: E) -> Unit) {
        mWasAdded = action
    }

    /**
     * @see [ListObserver.wasAdded]
     */
    fun wasAdded(action: (elements: Map<Int, E>) -> Unit) {
        mWasAddedMultiple = action
    }

    /**
     * @see [ListObserver.wasRemoved]
     */
    fun wasRemoved(action: (index: Int, element: E) -> Unit) {
        mWasRemoved = action
    }

    /**
     * @see [ListObserver.wasRemoved]
     */
    fun wasRemoved(action: (Map<Int, E>) -> Unit) {
        mWasRemovedMultiple = action
    }

    /**
     * @see [ListObserver.wasReplaced]
     */
    fun wasReplaced(action: (index: Int, lastElement: E, newElement: E) -> Unit) {
        mWasReplaced = action
    }

    /**
     * @see [ListObserver.wasReplaced]
     */
    fun wasReplaced(action: (Map<Int, Pair<E, E>>) -> Unit) {
        mWasReplacedMultiple = action
    }
}