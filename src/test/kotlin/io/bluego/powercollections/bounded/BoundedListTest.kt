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

import io.bluego.powercollections.ListTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("RemoveRedundantBackticks", "LocalVariableName")
open class BoundedListTest: ListTest() {


    override fun `get list of size maxCapacity 3`(): MutableBoundedList<String> {
        return mutableBoundedListOf(3)
    }

    @Test
    override fun test_addAll() {

        val `first BoundedList` = `get list of size maxCapacity 3`()

        assertTrue { `first BoundedList`.addAll(`list of 3 different items`) }

        `first BoundedList`.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            `first BoundedList`.addAll(`list of 3 different items`)
        }

        `first BoundedList`.assertSize(3)

        /* _______________________________________________________________ */

        val `second BoundedList` = `get list of size maxCapacity 3`()

        assertFailsWith(IndexOutOfBoundsException::class) {
            `second BoundedList`.addAll(`list of 4 different items`)
        }

        `second BoundedList`.assertSize(0)

    }

    @Test
    override fun test_addAllByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        val `first item` = `list of 3 different items`.first()

        val `collection from 1 to 3` = `list of 3 different items`.drop(1)

        assertTrue { boundedList.add(`first item`) }

        boundedList.assertSize(1)

        assertTrue { boundedList.addAll(0, `collection from 1 to 3`) }

        assertEquals(`collection from 1 to 3`[0], boundedList[0])

        assertEquals(`collection from 1 to 3`[1], boundedList[1])

        assertEquals(`first item`, boundedList[2])

        boundedList.assertSize(3)


        assertFailsWith(IndexOutOfBoundsException::class) {

            boundedList.addAll(0, `list of 4 different items`)
        }

        boundedList.assertSize(3)
    }

    @Test
    open fun test_forceResize() {

        val `first boundedList` = `get list of size maxCapacity 3`()

        assertFalse { `first boundedList`.forceResize(1) }

        assertTrue { `first boundedList`.add(`list of 3 different items`[0]) }

        `first boundedList`.assertSize(1)

        assertFailsWith(IndexOutOfBoundsException::class) {
            `first boundedList`.add(`list of 3 different items`[1])
        }

        `first boundedList`.assertSize(1)


        val `second boundedList` = `get list of size maxCapacity 3`()

        assertTrue { `second boundedList`.addAll(`list of 3 different items`) }

        `second boundedList`.assertSize(3)

        assertTrue { `second boundedList`.forceResize(1) }

        `second boundedList`.assertSize(1)

        assertEquals(`list of 3 different items`[2], `second boundedList`.first())

        assertFailsWith(IndexOutOfBoundsException::class) {
            `second boundedList`.add(`list of 3 different items`[1])
        }

        `second boundedList`.assertSize(1)

    }

    @Test
    override fun test_add() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.add(`list of 4 different items`[0]) }

        assertTrue { boundedList.add(`list of 4 different items`[1]) }

        assertTrue { boundedList.add(`list of 4 different items`[2]) }

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.add(`list of 4 different items`[3])
        }

        boundedList.assertSize(3)

    }

    @Test
    override fun test_addByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        assertEquals(Unit, boundedList.add(0, `list of 4 different items`[0]) )

        assertEquals(Unit, boundedList.add(0, `list of 4 different items`[1]) )

        assertEquals(Unit, boundedList.add(0, `list of 4 different items`[2]) )

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.add(0, `list of 4 different items`[3])
        }

        boundedList.assertSize(3)

    }

    @Test
    open fun test_forceAdd() {

        val boundedList = `get list of size maxCapacity 3`()

        assertFalse { boundedList.forceAdd(`list of 4 different items`[0]) }

        assertFalse { boundedList.forceAdd(`list of 4 different items`[1]) }

        assertFalse { boundedList.forceAdd(`list of 4 different items`[2]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAdd(`list of 4 different items`[3]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.containsAll(`list of 4 different items`.drop(1)) }

    }

    @Test
    open fun test_forceAddByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        assertFalse { boundedList.forceAdd(0, `list of 4 different items`[0]) }

        assertFalse { boundedList.forceAdd(0, `list of 4 different items`[1]) }

        assertFalse { boundedList.forceAdd(0, `list of 4 different items`[2]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAdd(1, `list of 4 different items`[3]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.containsAll(`list of 4 different items`.take(2) + `list of 4 different items`[3]) }

    }

    @Test
    override fun test_iterator() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`)}

        val iterator = boundedList.iterator()

        boundedList.assertSize(3)

        repeat(3) {

            assertTrue { iterator.hasNext() }

            if (it < 3) assertEquals(`list of 3 different items`[it], iterator.next())
            else assertFailsWith(ArrayIndexOutOfBoundsException::class) { iterator.next() }

            assertEquals(Unit, iterator.remove())

            boundedList.assertSize(3 - (it + 1))
        }

        boundedList.assertSize(0)
    }

    @Test
    override fun test_listIterator() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`)}

        val listIterator = boundedList.listIterator()

        boundedList.assertSize(3)

        repeat(3) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`list of 3 different items`[it], listIterator.next())

            assertEquals(it + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it, listIterator.previousIndex())

            assertEquals(`list of 3 different items`[it], listIterator.previous())

            assertEquals(`list of 3 different items`[it], listIterator.next())

        }

        boundedList.assertSize(3)

        val `second listIterator` = boundedList.listIterator()

        repeat(3) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`list of 3 different items`[it], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            boundedList.assertSize(3 - (it + 1))
        }

        boundedList.assertSize(0)

        val `third listIterator` = boundedList.listIterator()

        repeat(3) {

            `third listIterator`.add(`list of 4 different items`[it])
        }

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {

            `third listIterator`.add(`list of 4 different items`[4])
        }

    }

    @Test
    override fun test_listIteratorByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`)}

        val listIterator = boundedList.listIterator(1)

        boundedList.assertSize(3)

        repeat(2) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`list of 3 different items`[it + 1], listIterator.next())

            assertEquals(it + 1 + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it + 1, listIterator.previousIndex())

            assertEquals(`list of 3 different items`[it + 1], listIterator.previous())

            assertEquals(`list of 3 different items`[it + 1], listIterator.next())

        }

        boundedList.assertSize(3)

        val `second listIterator` = boundedList.listIterator(1)

        repeat(2) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`list of 3 different items`[it + 1], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            boundedList.assertSize(3 - (it + 1))
        }

        boundedList.assertSize(1)

        val `third listIterator` = boundedList.listIterator(1)

        repeat(2) {

            `third listIterator`.add(`list of 4 different items`[it])
        }

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {

            `third listIterator`.add(`list of 4 different items`[4])
        }

    }

    override fun test_subList() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        val subList = boundedList.subList(1, 3)

        subList.assertSize(2)

        subList.clear()

        subList.assertSize(0)

        boundedList.assertSize(1)

        assertTrue { boundedList.contains(`list of 3 different items`[0]) }


        assertTrue { subList.add(`list of 4 different items`[0]) }

        assertFailsWith(IndexOutOfBoundsException::class) {

            subList.addAll(`list of 4 different items`.drop(1))
        }

        subList.assertSize(1)

        boundedList.assertSize(2)

    }

    @Test
    open fun test_maxCapacity() {

        val boundedList = `get list of size maxCapacity 3`()

        assertEquals(3, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(1))

        assertEquals(1, boundedList.maxCapacity)

        assertFalse { boundedList.forceResize(4) }

        assertEquals(4, boundedList.maxCapacity)
    }

    @Test
    open fun test_forceAddAll() {

        val boundedList = `get list of size maxCapacity 3`()

        boundedList.assertSize(0)

        assertTrue { boundedList.forceAddAll(`list of 4 different items`) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAddAll(`list of 4 different items`.take(3)) }
    }

    @Test
    open fun test_forceAddAllByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAddAll(1, `list of 4 different items`.take(3)) }

        boundedList.assertSize(3)

        assertEquals(`list of 4 different items`[0], boundedList[0])

        assertEquals(`list of 4 different items`[1], boundedList[1])

        assertEquals(`list of 4 different items`[2], boundedList[2])

        assertFalse { boundedList.contains(`list of 4 different items`[3]) }

    }

    @Test
    open fun test_resize() {

        val boundedList = `get list of size maxCapacity 3`()

        assertEquals(3, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(5))

        assertEquals(5, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(2))

        assertEquals(2, boundedList.maxCapacity)

        boundedList.assertSize(0)

        assertTrue { boundedList.addAll(`list of 3 different items`.take(2)) }

        boundedList.assertSize(2)

        assertEquals(Unit, boundedList.resize(3))

        assertTrue { boundedList.add(`list of 3 different items`[2]) }

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.add(`list of 4 different items`[0])
        }

        assertFailsWith(IllegalStateException::class) {
            boundedList.resize(2)
        }

    }

}