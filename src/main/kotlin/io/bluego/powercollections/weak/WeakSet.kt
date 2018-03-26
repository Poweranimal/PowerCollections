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
 * All elements of the [Set] are wrapped in a [WeakReference].
 */
interface WeakSet<out T>: Set<T>, WeakCollection<T>

/**
 * All elements of the [MutableSet] are wrapped in a [WeakReference].
 */
interface MutableWeakSet<T>: WeakSet<T>, MutableSet<T>, MutableWeakCollection<T>

abstract class AbstractWeakSet<T: Any> protected constructor(
        private val mSet: MutableSet<WeakElement<T>>)
    : MutableWeakSet<T?> {

    constructor() : this(hashSetOf())

    private val mAdapter = WeakCollectionAdapter(mSet)

    override fun add(element: T?): Boolean = mAdapter.add(element, mSet::add)

    override fun addAll(elements: Collection<T?>): Boolean = mAdapter.addAll(elements, mSet::addAll)

    override fun clear() = mAdapter.clear(mSet::clear)

    override fun iterator(): MutableIterator<T?> = mAdapter.iterator(mSet::iterator)

    override fun remove(element: T?): Boolean = mAdapter.remove(element, mSet::remove)

    override fun removeAll(elements: Collection<T?>): Boolean = mAdapter.removeAll(elements, mSet::removeAll)

    override fun retainAll(elements: Collection<T?>): Boolean = mAdapter.retainAll(elements, mSet::retainAll)

    override val size: Int
        get() = mAdapter.size(mSet::size)

    override fun contains(element: T?): Boolean = mAdapter.contains(element, mSet::contains)

    override fun containsAll(elements: Collection<T?>): Boolean = mAdapter.containsAll(elements, mSet::containsAll)

    override fun isEmpty(): Boolean = mAdapter.isEmpty(mSet::isEmpty)

    override fun optimize() = mAdapter.optimize()

    override fun forceOptimize() = mAdapter.forceOptimize()
}

class HashWeakSet<T: Any>(capacity: Int = 16) : AbstractWeakSet<T>(HashSet(capacity)) {

    companion object {
        fun <T: Any> create(set: Set<T>) : HashWeakSet<T> {
            val weakSet = HashWeakSet<T>()
            weakSet.addAll(set)
            return weakSet
        }
    }
}

/**
 * Creates an empty [MutableWeakSet].
 *
 * @return Empty [MutableWeakSet]
 */
fun <T: Any> mutableWeakSetOf(): MutableWeakSet<T?> = HashWeakSet()

/**
 * Creates an [MutableWeakSet] with [elements]s.
 *
 * @return [MutableWeakSet] with [elements]s
 */
fun <T: Any> mutableWeakSetOf(vararg elements: T): MutableWeakSet<T?>
        = HashWeakSet.create(setOf(*elements))

/**
 * Creates an empty [WeakSet].
 *
 * @return Empty [WeakSet]
 */
fun <T: Any> weakSetOf(): WeakSet<T?> = mutableWeakSetOf()

/**
 * Creates an [WeakSet] with [elements]s.
 *
 * @return [WeakSet] with [elements]s
 */
fun <T: Any> weakSetOf(vararg elements: T): WeakSet<T?>
        = mutableWeakSetOf(*elements)
