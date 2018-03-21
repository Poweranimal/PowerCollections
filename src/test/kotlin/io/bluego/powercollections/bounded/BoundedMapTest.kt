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

import io.bluego.powercollections.MapTest
import org.junit.Test
import kotlin.test.*

//TODO: more extensive tests!
@Suppress("RemoveRedundantBackticks", "PropertyName", "LocalVariableName")
open class BoundedMapTest: MapTest() {

    override fun `get map to test`(): MutableBoundedMap<Int, String> {
        return mutableBoundedMapOf(3)
    }

    @Test
    override fun `test putAll`() {

        val `first BoundedMap` = `get map to test`()

        `first BoundedMap`.putAll(`map of 3 different items`)

        `first BoundedMap`.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            `first BoundedMap`.putAll(`map of 4 different items`)
        }

        `first BoundedMap`.assertSize(3)

        /* _______________________________________________________________ */

        val `second BoundedMap` = `get map to test`()

        assertFailsWith(IndexOutOfBoundsException::class) {
            `second BoundedMap`.putAll(`map of 4 different items`)
        }

        /*
        IMPORTANT
        putAll first puts all items in the map and throws an error afterwards,
        if the additional entries exceed the maxSize
         */
        `second BoundedMap`.assertSize(3)

    }

    @Test
    open fun `test forceResize`() {

        val `first boundedMap` = `get map to test`()

        assertFalse { `first boundedMap`.forceResize(1) }

        assertNull(`first boundedMap`.put(`map of 3 different items`.at(0)))

        `first boundedMap`.assertSize(1)

        assertFailsWith(IndexOutOfBoundsException::class) {
            `first boundedMap`.put(`map of 3 different items`.at(1))
        }

        `first boundedMap`.assertSize(1)


        val `second boundedMap` = `get map to test`()

        `second boundedMap`.putAll(`map of 3 different items`)

        `second boundedMap`.assertSize(3)

        assertTrue { `second boundedMap`.forceResize(1) }

        `second boundedMap`.assertSize(1)

        assertEquals(`map of 3 different items`.valueAt(2), `second boundedMap`.get(`map of 3 different items`.keyAt(2)))

        assertFailsWith(IndexOutOfBoundsException::class) {
            `second boundedMap`.put(`map of 3 different items`.at(1))
        }

        `second boundedMap`.assertSize(1)

    }

    @Test
    override fun `test put`() {

        val boundedList = `get map to test`()

        assertNull(boundedList.put(`map of 4 different items`.at(0)))

        assertNull(boundedList.put(`map of 4 different items`.at(1)))

        assertNull(boundedList.put(`map of 4 different items`.at(2)))

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.put(`map of 4 different items`.at(3))
        }

        boundedList.assertSize(3)

        println(boundedList)
    }

    @Test
    open fun `test forcePut`() {

        val boundedList = `get map to test`()

        assertFalse { boundedList.forcePut(`map of 4 different items`.at(0)) }

        assertFalse { boundedList.forcePut(`map of 4 different items`.at(1)) }

        assertFalse { boundedList.forcePut(`map of 4 different items`.at(2)) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forcePut(`map of 4 different items`.at(3)) }

        boundedList.assertSize(3)

        assertTrue { boundedList.containsAll(`map of 4 different items`.drop(1)) }

    }

    @Test
    override fun `test iterator`() {

        val boundedList = `get map to test`()

        boundedList.putAll(`map of 3 different items`)

        val iterator = boundedList.iterator()

        boundedList.assertSize(3)

        repeat(3) {

            assertTrue { iterator.hasNext() }

            if (it < 3) assertEquals(`map of 3 different items`.at(it), iterator.next())
            else assertFailsWith(ArrayIndexOutOfBoundsException::class) { iterator.next() }

            assertEquals(Unit, iterator.remove())

            boundedList.assertSize(3 - (it + 1))
        }

        boundedList.assertSize(0)
    }

    @Test
    open fun `test maxCapacity`() {

        val boundedList = `get map to test`()

        assertEquals(3, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(1))

        assertEquals(1, boundedList.maxCapacity)

        assertFalse { boundedList.forceResize(4) }

        assertEquals(4, boundedList.maxCapacity)
    }

    @Test
    open fun `test forcePutAll`() {

        val boundedList = `get map to test`()

        boundedList.assertSize(0)

        assertTrue { boundedList.forcePutAll(`map of 4 different items`) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forcePutAll(`map of 4 different items`.take(3)) }
    }

    @Test
    open fun `test resize`() {

        val boundedList = `get map to test`()

        assertEquals(3, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(5))

        assertEquals(5, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(2))

        assertEquals(2, boundedList.maxCapacity)

        boundedList.assertSize(0)

        boundedList.putAll(`map of 3 different items`.take(2))

        boundedList.assertSize(2)

        assertEquals(Unit, boundedList.resize(3))

        assertNull(boundedList.put(`map of 3 different items`.at(2)))

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.put(`map of 4 different items`.at(0))
        }

        assertFailsWith(IllegalStateException::class) {
            boundedList.resize(2)
        }

    }

    protected fun <K, V> MutableBoundedMap<K, V>.forcePut(entry: Map.Entry<K, V>): Boolean {
        return this.forcePut(entry.key, entry.value)
    }

    protected fun <K, V> Map<K, V>.containsAll(map: Map<K, V>): Boolean {
        return this.entries.containsAll(map.entries)
    }

}