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

import io.bluego.powercollections.weak.adapter.WeakCollectionAdapter
import io.bluego.powercollections.weak.adapter.WeakElement
import java.lang.ref.WeakReference

/**
 * All elements of the [Collection] are wrapped in a [WeakReference].
 */
interface WeakCollection<out T>: Collection<T>, Weakable

/**
 * All elements of the [MutableCollection] are wrapped in a [WeakReference].
 */
interface MutableWeakCollection<T>: WeakCollection<T>, MutableCollection<T>

abstract class AbstractWeakCollection<T: Any> protected constructor(
        private val mCollection: MutableCollection<WeakElement<T>>)
    : MutableWeakCollection<T?>
{

    constructor(): this(arrayListOf())

    private val mAdapter = WeakCollectionAdapter(mCollection)

    override fun add(element: T?): Boolean = mAdapter.add(element, mCollection::add)

    override fun addAll(elements: Collection<T?>): Boolean = mAdapter.addAll(elements, mCollection::addAll)

    override fun clear() = mAdapter.clear(mCollection::clear)

    override fun iterator(): MutableIterator<T?> = mAdapter.iterator(mCollection::iterator)

    override fun remove(element: T?): Boolean = mAdapter.remove(element, mCollection::remove)

    override fun removeAll(elements: Collection<T?>): Boolean = mAdapter.removeAll(elements, mCollection::removeAll)

    override fun retainAll(elements: Collection<T?>): Boolean = mAdapter.retainAll(elements, mCollection::retainAll)

    override val size: Int
        get() = mAdapter.size(mCollection::size)

    override fun contains(element: T?): Boolean = mAdapter.contains(element, mCollection::contains)

    override fun containsAll(elements: Collection<T?>): Boolean = mAdapter.containsAll(elements, mCollection::containsAll)

    override fun isEmpty(): Boolean = mAdapter.isEmpty(mCollection::isEmpty)

    override fun optimize() = mAdapter.optimize()

    override fun forceOptimize() = mAdapter.forceOptimize()
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

