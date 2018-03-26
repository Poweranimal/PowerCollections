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

import io.bluego.powercollections.bounded.Utils.checkInitialMaxCapacity
import io.bluego.powercollections.bounded.Utils.checkResize

/**
 * A [Map] that has a fixed capacity of [Map.entries].
 */
interface BoundedMap<K, out V>: Map<K, V>, Boundable

/**
 * A [MutableList] that has a fixed capacity of [Map.entries].
 */
interface MutableBoundedMap<K, V>: MutableMap<K, V>, BoundedMap<K, V>, MutableBoundable {

    /**
     * Puts the entry in the [MutableBoundedMap].
     * @throws IndexOutOfBoundsException, if the [MutableMap]'s [maxCapacity] is reached.
     */
    override fun put(key: K, value: V): V?

    /**
     * Puts the entries in the [MutableBoundedMap].
     * @throws IndexOutOfBoundsException, if the [MutableMap]'s [maxCapacity] is reached.
     */
    override fun putAll(from: Map<out K, V>)

    /**
     * Adds items to the [MutableBoundedMap].
     * If the [MutableBoundedMap]'s [maxCapacity] is reached, the item gets added to the tail
     * and the head (the oldest item) gets removed.
     * @return true, if head item was removed
     */
    fun forcePut(key: K, value: V): Boolean

    /**
     * Adds items to the [MutableBoundedMap].
     * If the [MutableBoundedMap]'s [maxCapacity] is reached, the item gets added to the tail
     * and the head (the oldest item) gets removed.
     * @return true, if head item was removed
     */
    fun forcePutAll(from: Map<out K, V>): Boolean
}

/**
 * A [MutableBoundedMap] baked by [LinkedHashMap] that has a fixed capacity determined by [maxCapacity].
 */
abstract class AbstractBoundedMap<K, V>
    : LinkedHashMap<K, V>, MutableBoundedMap<K, V>
{

    constructor(maxCapacity: Int) : super() {
        checkInitialMaxCapacity(maxCapacity)
        this.maxCapacity = maxCapacity
    }

    constructor(maxCapacity: Int, initialCapacity: Int, loadFactor: Float)
            : super(initialCapacity, loadFactor)  {
        checkInitialMaxCapacity(maxCapacity)
        this.maxCapacity = maxCapacity
    }

    constructor(maxCapacity: Int, initialCapacity: Int)
            : super(initialCapacity) {
        checkInitialMaxCapacity(maxCapacity)
        this.maxCapacity = maxCapacity
    }

    constructor(maxCapacity: Int, m: Map<out K, V>?)
            : super(m)  {
        checkInitialMaxCapacity(maxCapacity)
        this.maxCapacity = maxCapacity
    }

    constructor(maxCapacity: Int, initialCapacity: Int, loadFactor: Float, accessOrder: Boolean)
            : super(initialCapacity, loadFactor, accessOrder) {
        checkInitialMaxCapacity(maxCapacity)
        this.maxCapacity = maxCapacity
    }

    /**
     * The max capacity of the [BoundedMap].
     * Cannot contain more entries than [maxCapacity].
     */
    final override var maxCapacity: Int
        private set

    private var mEldestEntryRemoved = false
    private var mIsRunningSensitive = false

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = super.entries

    override val keys: MutableSet<K>
        get() = super.keys

    override val values: MutableCollection<V>
        get() = super.values

    override fun removeEldestEntry(p0: MutableMap.MutableEntry<K, V>?): Boolean {
        val result = size > maxCapacity
        if (result) {
            mEldestEntryRemoved = true
            if (mIsRunningSensitive) {
                remove(entries.last().key) ?: throw NoSuchElementException()
                mEldestEntryRemoved = false
                throw IndexOutOfBoundsException(
                        "The BoundedMap's maxCapacity of $maxCapacity is already reached")
            }
        }
        return result
    }

    override fun put(key: K, value: V): V? = runSensitive { super.put(key, value) }

    override fun putAll(from: Map<out K, V>) = runSensitive { super.putAll(from) }

    override fun putIfAbsent(key: K, value: V): V?
            = runSensitive { super<java.util.LinkedHashMap>.putIfAbsent(key, value) }

    @Suppress("ReplacePutWithAssignment")
    override fun forcePut(key: K, value: V): Boolean
            = checkEldestEntryRemoved { super.put(key, value) }

    override fun forcePutAll(from: Map<out K, V>): Boolean
            = checkEldestEntryRemoved { super.putAll(from) }

    override fun forceResize(newSize: Int): Boolean {
        maxCapacity = newSize
        return if (newSize < size) {
            this.iterator().removeAll(size - newSize)
            true
        } else false
    }

    override fun resize(newSize: Int) {
        checkResize(size, newSize)
        maxCapacity = newSize
    }

    private inline fun <E> runSensitive(action: () -> E) : E {
        mIsRunningSensitive = true
        val result: E = action()
        mIsRunningSensitive = false
        return result
    }

    private inline fun checkEldestEntryRemoved(action: () -> Unit) : Boolean {
        action()
        val result = mEldestEntryRemoved
        mEldestEntryRemoved = false
        return result
    }

    /**
     * Remove [n] elements from [MutableIterator].
     * Starts removing from the first [Iterator.next] element.
     */
    private fun <E> MutableIterator<E>.removeAll(n: Int) {
        check(n > 0) { "n must be greater than 0" }
        var counter = 0
        do {
            if (!hasNext()) throw IndexOutOfBoundsException("ERROR: n (= $n) is greater than the " +
                    "iterators max size (= $counter)!")
            if (counter >= n) return
            next()
            remove()
            counter++
        } while (true)
    }
}

internal class BoundedMapBuilder<K, V> : AbstractBoundedMap<K, V> {

    private constructor(maxCapacity: Int) : super(maxCapacity)
    private constructor(maxCapacity: Int, initialCapacity: Int, loadFactor: Float)
            : super(maxCapacity, initialCapacity, loadFactor)
    private constructor(maxCapacity: Int, initialCapacity: Int)
            : super(maxCapacity, initialCapacity)
    private constructor(maxCapacity: Int, m: Map<out K, V>?) : super(maxCapacity, m)
    private constructor(maxCapacity: Int, initialCapacity: Int, loadFactor: Float,
                        accessOrder: Boolean)
            : super(maxCapacity, initialCapacity, loadFactor, accessOrder)

    companion object {

        fun <K, V> create(maxCapacity: Int) = BoundedMapBuilder<K, V>(maxCapacity)

        fun <K, V> create(maxCapacity: Int, initialCapacity: Int)
                = BoundedMapBuilder<K, V>(maxCapacity, initialCapacity)

        fun <K, V> create(maxCapacity: Int, initialCapacity: Int, loadFactor: Float)
                = BoundedMapBuilder<K, V>(maxCapacity, initialCapacity, loadFactor)

        fun <K, V> create(maxCapacity: Int, initialCapacity: Int, loadFactor: Float,
                          accessOrder: Boolean)
                = BoundedMapBuilder<K, V>(maxCapacity, initialCapacity, loadFactor, accessOrder)

        fun <K, V> create(maxCapacity: Int, map: Map<K, V>) = BoundedMapBuilder(maxCapacity, map)

    }
}

/**
 * Creates an empty [MutableBoundedMap].
 *
 * @return Empty [MutableBoundedMap]
 */
fun <K, V> mutableBoundedMapOf(maxCapacity: Int): MutableBoundedMap<K, V>
        = BoundedMapBuilder.create(maxCapacity)

/**
 * Creates an [MutableBoundedMap] with [elements].
 *
 * @return [MutableBoundedMap] with [elements]
 */
fun <K, V> mutableBoundedMapOf(maxCapacity: Int, vararg elements: Pair<K, V>): MutableBoundedMap<K, V> =
        BoundedMapBuilder.create(maxCapacity, mapOf(*elements))

/**
 * Creates an [MutableBoundedMap] with [map].
 *
 * @return [MutableBoundedMap] with [map]
 */
fun <K, V> mutableBoundedMapOf(maxCapacity: Int, map: Map<K, V>): MutableBoundedMap<K, V> =
        BoundedMapBuilder.create(maxCapacity, map)

/**
 * Creates an empty [BoundedMap].
 *
 * @return Empty [BoundedMap]
 */
fun <K, V> boundedMapOf(maxCapacity: Int): BoundedMap<K, V> = mutableBoundedMapOf(maxCapacity)

/**
 * Creates an [BoundedMap] with [elements].
 *
 * @return [BoundedMap] with [elements]
 */
fun <K, V> boundedMapOf(maxCapacity: Int, vararg elements: Pair<K, V>): BoundedMap<K, V> =
        mutableBoundedMapOf(maxCapacity, *elements)

/**
 * Creates an [BoundedMap] with [map].
 *
 * @return [BoundedMap] with [map]
 */
fun <K, V> boundedMapOf(maxCapacity: Int, map: Map<K, V>): BoundedMap<K, V> =
        mutableBoundedMapOf(maxCapacity, map)