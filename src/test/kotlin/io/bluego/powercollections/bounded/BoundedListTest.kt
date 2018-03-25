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
import io.bluego.powercollections.assertSize
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("RemoveRedundantBackticks", "LocalVariableName", "PropertyName")
open class BoundedListTest: ListTest<String>() {

    override val `collection of 3 different items`: List<String>
        get() = listOf("zero", "one", "two")

    override val `collection of 4 different items`: List<String>
        get() = listOf("_zero", "_one", "_two", "three")

    override val `get collection to test`: () -> MutableBoundedList<String>
        get() = { mutableBoundedListOf(3) }

    @Test
    override fun `test addAll`() {

        val `first BoundedList` = `get collection to test`()

        assertTrue { `first BoundedList`.addAll(`collection of 3 different items`) }

        `first BoundedList`.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            `first BoundedList`.addAll(`collection of 3 different items`)
        }

        `first BoundedList`.assertSize(3)

        /* _______________________________________________________________ */

        val `second BoundedList` = `get collection to test`()

        assertFailsWith(IndexOutOfBoundsException::class) {
            `second BoundedList`.addAll(`collection of 4 different items`)
        }

        `second BoundedList`.assertSize(0)

    }

    @Test
    override fun `test addAllByIndex`() {

        val boundedList = `get collection to test`()

        val `first item` = `collection of 3 different items`.first()

        val `collection from 1 to 3` = `collection of 3 different items`.drop(1)

        assertTrue { boundedList.add(`first item`) }

        boundedList.assertSize(1)

        assertTrue { boundedList.addAll(0, `collection from 1 to 3`) }

        assertEquals(`collection from 1 to 3`[0], boundedList[0])

        assertEquals(`collection from 1 to 3`[1], boundedList[1])

        assertEquals(`first item`, boundedList[2])

        boundedList.assertSize(3)


        assertFailsWith(IndexOutOfBoundsException::class) {

            boundedList.addAll(0, `collection of 4 different items`)
        }

        boundedList.assertSize(3)
    }

    @Test
    open fun `test forceResize`() {

        val `first boundedList` = `get collection to test`()

        assertFalse { `first boundedList`.forceResize(1) }

        assertTrue { `first boundedList`.add(`collection of 3 different items`[0]) }

        `first boundedList`.assertSize(1)

        assertFailsWith(IndexOutOfBoundsException::class) {
            `first boundedList`.add(`collection of 3 different items`[1])
        }

        `first boundedList`.assertSize(1)


        val `second boundedList` = `get collection to test`()

        assertTrue { `second boundedList`.addAll(`collection of 3 different items`) }

        `second boundedList`.assertSize(3)

        assertTrue { `second boundedList`.forceResize(1) }

        `second boundedList`.assertSize(1)

        assertEquals(`collection of 3 different items`[2], `second boundedList`.first())

        assertFailsWith(IndexOutOfBoundsException::class) {
            `second boundedList`.add(`collection of 3 different items`[1])
        }

        `second boundedList`.assertSize(1)

    }

    @Test
    override fun `test add`() {

        val boundedList = `get collection to test`()

        assertTrue { boundedList.add(`collection of 4 different items`[0]) }

        assertTrue { boundedList.add(`collection of 4 different items`[1]) }

        assertTrue { boundedList.add(`collection of 4 different items`[2]) }

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.add(`collection of 4 different items`[3])
        }

        boundedList.assertSize(3)

    }

    @Test
    override fun `test addByIndex`() {

        val boundedList = `get collection to test`()

        assertEquals(Unit, boundedList.add(0, `collection of 4 different items`[0]) )

        assertEquals(Unit, boundedList.add(0, `collection of 4 different items`[1]) )

        assertEquals(Unit, boundedList.add(0, `collection of 4 different items`[2]) )

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.add(0, `collection of 4 different items`[3])
        }

        boundedList.assertSize(3)

    }

    @Test
    open fun `test forceAdd`() {

        val boundedList = `get collection to test`()

        assertFalse { boundedList.forceAdd(`collection of 4 different items`[0]) }

        assertFalse { boundedList.forceAdd(`collection of 4 different items`[1]) }

        assertFalse { boundedList.forceAdd(`collection of 4 different items`[2]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAdd(`collection of 4 different items`[3]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.containsAll(`collection of 4 different items`.drop(1)) }

    }

    @Test
    open fun `test forceAddByIndex`() {

        val boundedList = `get collection to test`()

        assertFalse { boundedList.forceAdd(0, `collection of 4 different items`[0]) }

        assertFalse { boundedList.forceAdd(0, `collection of 4 different items`[1]) }

        assertFalse { boundedList.forceAdd(0, `collection of 4 different items`[2]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAdd(1, `collection of 4 different items`[3]) }

        boundedList.assertSize(3)

        assertTrue { boundedList.containsAll(`collection of 4 different items`.take(2) + `collection of 4 different items`[3]) }

    }

    @Test
    override fun `test iterator`() {

        val boundedList = `get collection to test`()

        assertTrue { boundedList.addAll(`collection of 3 different items`)}

        val iterator = boundedList.iterator()

        boundedList.assertSize(3)

        repeat(3) {

            assertTrue { iterator.hasNext() }

            if (it < 3) assertEquals(`collection of 3 different items`[it], iterator.next())
            else assertFailsWith(ArrayIndexOutOfBoundsException::class) { iterator.next() }

            assertEquals(Unit, iterator.remove())

            boundedList.assertSize(3 - (it + 1))
        }

        boundedList.assertSize(0)
    }

    @Test
    override fun `test listIterator`() {

        val boundedList = `get collection to test`()

        assertTrue { boundedList.addAll(`collection of 3 different items`)}

        val listIterator = boundedList.listIterator()

        boundedList.assertSize(3)

        repeat(3) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`collection of 3 different items`[it], listIterator.next())

            assertEquals(it + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it, listIterator.previousIndex())

            assertEquals(`collection of 3 different items`[it], listIterator.previous())

            assertEquals(`collection of 3 different items`[it], listIterator.next())

        }

        boundedList.assertSize(3)

        val `second listIterator` = boundedList.listIterator()

        repeat(3) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`collection of 3 different items`[it], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            boundedList.assertSize(3 - (it + 1))
        }

        boundedList.assertSize(0)

        val `third listIterator` = boundedList.listIterator()

        repeat(3) {

            `third listIterator`.add(`collection of 4 different items`[it])
        }

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {

            `third listIterator`.add(`collection of 4 different items`[4])
        }

    }

    @Test
    override fun `test listIteratorByIndex`() {

        val boundedList = `get collection to test`()

        assertTrue { boundedList.addAll(`collection of 3 different items`)}

        val listIterator = boundedList.listIterator(1)

        boundedList.assertSize(3)

        repeat(2) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`collection of 3 different items`[it + 1], listIterator.next())

            assertEquals(it + 1 + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it + 1, listIterator.previousIndex())

            assertEquals(`collection of 3 different items`[it + 1], listIterator.previous())

            assertEquals(`collection of 3 different items`[it + 1], listIterator.next())

        }

        boundedList.assertSize(3)

        val `second listIterator` = boundedList.listIterator(1)

        repeat(2) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`collection of 3 different items`[it + 1], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            boundedList.assertSize(3 - (it + 1))
        }

        boundedList.assertSize(1)

        val `third listIterator` = boundedList.listIterator(1)

        repeat(2) {

            `third listIterator`.add(`collection of 4 different items`[it])
        }

        boundedList.assertSize(3)

        assertFailsWith(IndexOutOfBoundsException::class) {

            `third listIterator`.add(`collection of 4 different items`[4])
        }

    }

    override fun `test subList`() {

        val boundedList = `get collection to test`()

        assertTrue { boundedList.addAll(`collection of 3 different items`) }

        val subList = boundedList.subList(1, 3)

        subList.assertSize(2)

        subList.clear()

        subList.assertSize(0)

        boundedList.assertSize(1)

        assertTrue { boundedList.contains(`collection of 3 different items`[0]) }


        assertTrue { subList.add(`collection of 4 different items`[0]) }

        assertFailsWith(IndexOutOfBoundsException::class) {

            subList.addAll(`collection of 4 different items`.drop(1))
        }

        subList.assertSize(1)

        boundedList.assertSize(2)

    }

    @Test
    open fun `test maxCapacity`() {

        val boundedList = `get collection to test`()

        assertEquals(3, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(1))

        assertEquals(1, boundedList.maxCapacity)

        assertFalse { boundedList.forceResize(4) }

        assertEquals(4, boundedList.maxCapacity)
    }

    @Test
    open fun `test forceAddAll`() {

        val boundedList = `get collection to test`()

        boundedList.assertSize(0)

        assertTrue { boundedList.forceAddAll(`collection of 4 different items`) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAddAll(`collection of 4 different items`.take(3)) }
    }

    @Test
    open fun `test forceAddAllByIndex`() {

        val boundedList = `get collection to test`()

        assertTrue { boundedList.addAll(`collection of 3 different items`) }

        boundedList.assertSize(3)

        assertTrue { boundedList.forceAddAll(1, `collection of 4 different items`.take(3)) }

        boundedList.assertSize(3)

        assertEquals(`collection of 4 different items`[0], boundedList[0])

        assertEquals(`collection of 4 different items`[1], boundedList[1])

        assertEquals(`collection of 4 different items`[2], boundedList[2])

        assertFalse { boundedList.contains(`collection of 4 different items`[3]) }

    }

    @Test
    open fun `test resize`() {

        val boundedList = `get collection to test`()

        assertEquals(3, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(5))

        assertEquals(5, boundedList.maxCapacity)

        assertEquals(Unit, boundedList.resize(2))

        assertEquals(2, boundedList.maxCapacity)

        boundedList.assertSize(0)

        assertTrue { boundedList.addAll(`collection of 3 different items`.take(2)) }

        boundedList.assertSize(2)

        assertEquals(Unit, boundedList.resize(3))

        assertTrue { boundedList.add(`collection of 3 different items`[2]) }

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.add(`collection of 4 different items`[0])
        }

        assertFailsWith(IllegalStateException::class) {
            boundedList.resize(2)
        }

    }

}