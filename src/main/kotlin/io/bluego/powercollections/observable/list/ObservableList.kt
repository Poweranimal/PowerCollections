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

import io.bluego.powercollections.observable.list.adapter.ObservableListAdapter
import java.util.function.Predicate
import java.util.function.UnaryOperator

interface ObservableList<out E>: List<E>

interface MutableObservableList<E>: ObservableList<E>, MutableList<E>, Observable<E> {

    fun runSilentChanges(silentChanges: MutableList<E>.() -> Unit)

}

abstract class AbstractObservableList<E>: ArrayList<E>, MutableObservableList<E>
{
    private val mAdapter: ObservableListAdapter<ArrayList<E>, E> by lazy {
        ObservableListAdapter(observer, this)
    }

    constructor(initialCapacity: Int, observer: ListObserver<E>) : super(initialCapacity)
    {
        this.observer = observer
    }

    constructor(observer: ListObserver<E>) : super() {
        this.observer = observer
    }

    constructor(c: Collection<E>?, observer: ListObserver<E>) : super(c)
    {
        this.observer = observer
    }

    final override var observer: ListObserver<E>
        private set

    override fun notifyDataChanged() = mAdapter.notifyDataChanged()

    override fun notifyDataChanged(index: Int) = mAdapter.notifyDataChanged(index)

    override fun runSilentChanges(silentChanges: MutableList<E>.() -> Unit) = mAdapter.runSilentChanges(silentChanges)

    override fun add(element: E): Boolean = mAdapter.add(element) { e ->  super.add(e) }

    override fun add(index: Int, element: E) = mAdapter.add(index, element) { i, e -> super.add(i, e) }

    override fun addAll(index: Int, elements: Collection<E>): Boolean
            = mAdapter.addAll(index, elements) { i, collection -> super.addAll(i, collection) }

    override fun iterator(): MutableIterator<E> = super.iterator()

    override fun listIterator(): MutableListIterator<E> = super.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = super.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E>
            = mAdapter.subList(fromIndex, toIndex) { fi, ti -> super.subList(fi, ti) }

    override fun addAll(elements: Collection<E>): Boolean =
            mAdapter.addAll(elements) { collection -> super.addAll(collection) }

    override fun clear() = mAdapter.clear { super.clear() }

    override fun remove(element: E): Boolean = mAdapter.remove(element) { e -> super.remove(e) }

    override fun removeAll(elements: Collection<E>): Boolean
            = mAdapter.removeAll(elements) { collection -> super.removeAll(collection) }

    override fun removeAt(index: Int): E = mAdapter.removeAt(index) { i -> super.removeAt(i) }

    override fun set(index: Int, element: E): E = mAdapter.set(index, element) { i, e -> super.set(i, e) }

    override fun replaceAll(p0: UnaryOperator<E>) = mAdapter.replaceAll(p0) { super<ArrayList>.replaceAll(p0) }

    override fun retainAll(elements: Collection<E>): Boolean = mAdapter.retainAll(elements, this::listIterator)

    override fun removeIf(p0: Predicate<in E>): Boolean
            = mAdapter.removeIf(p0) { predicate -> super< ArrayList>.removeIf(predicate) }

    override fun removeRange(fromIndex: Int, toIndex: Int)
            = mAdapter.removeRange(fromIndex, toIndex) { fi, ti -> super.removeRange(fi, ti) }

}

class ArrayObservableList<E> : AbstractObservableList<E>
{
    constructor(initialCapacity: Int, mObserver: ListObserver<E>) : super(initialCapacity, mObserver)
    constructor(mObserver: ListObserver<E>) : super(mObserver)
    constructor(c: Collection<E>?, mObserver: ListObserver<E>) : super(c, mObserver)
}


/**
 * Creates an empty [MutableObservableList].
 *
 * @return Empty [MutableObservableList]
 */
fun <E> mutableObservableListOf(observer: ListObserver<E>)
        : MutableObservableList<E> = ArrayObservableList(observer)

/**
 * @see ListObserverDSL
 * @see mutableObservableListOf
 */
fun <E> mutableObservableListOf(observer: ListObserverDSL<E>.() -> Unit) : MutableObservableList<E>
        = mutableObservableListOf(ListObserverDSL.create(observer))

/**
 * Creates an [MutableObservableList] with [elements].
 *
 * @return [MutableObservableList] with [elements]
 */
fun <E> mutableObservableListOf(
        observer: ListObserver<E>,
        vararg elements: E): MutableObservableList<E>
        = ArrayObservableList(listOf(*elements), observer)

/**
 * @see ListObserverDSL
 * @see mutableObservableListOf
 */
fun <E> mutableObservableListOf(
        observer: ListObserverDSL<E>.() -> Unit,
        vararg elements: E) : MutableObservableList<E>
        = mutableObservableListOf(ListObserverDSL.create(observer), *elements)

/**
 * Creates an empty [ObservableList].
 *
 * @return Emtpy [ObservableList]
 */
fun <E> observableListOf(
        observer: ListObserver<E>) : ObservableList<E>
        = mutableObservableListOf(observer)

/**
 * @see ListObserverDSL
 * @see observableListOf
 */
fun <E> observableListOf(
        observer: ListObserverDSL<E>.() -> Unit) : ObservableList<E>
        = mutableObservableListOf(observer)

/**
 * Creates an [ObservableList] with [elements]s.
 *
 * @return [ObservableList] with [elements]s
 */
fun <E> observableListOf(observer: ListObserver<E>, vararg elements: E) : ObservableList<E>
        = mutableObservableListOf(observer, *elements)

/**
 * @see ListObserverDSL
 * @see observableListOf
 */
fun <E> observableListOf(observer: ListObserverDSL<E>.() -> Unit, vararg elements: E): ObservableList<E>
        = mutableObservableListOf(observer, *elements)
