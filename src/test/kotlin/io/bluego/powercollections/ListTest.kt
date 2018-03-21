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

package io.bluego.powercollections

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("RemoveRedundantBackticks", "PropertyName", "LocalVariableName")
open class ListTest {

    open val `list of 3 different items` = listOf("zero", "one", "two")

    open val `list of 4 different items` = listOf("_zero", "_one", "_two", "_three")

    open fun `get list of size maxCapacity 3`(): MutableList<String> {
        return mutableListOf()
    }

    @Test
    open fun test_addAll() {

        val first_boundedList = `get list of size maxCapacity 3`()

        assertTrue { first_boundedList.addAll(`list of 3 different items`) }

        first_boundedList.assertSize(3)

    }

    @Test
    open fun test_addAllByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        val `first item` = `list of 3 different items`.first()

        val `collection from 1 to 2` = `list of 3 different items`.drop(1)

        assertTrue { boundedList.add(`first item`) }

        boundedList.assertSize(1)

        assertTrue { boundedList.addAll(0, `collection from 1 to 2`) }

        assertEquals(`collection from 1 to 2`[0], boundedList[0])

        assertEquals(`collection from 1 to 2`[1], boundedList[1])

        assertEquals(`first item`, boundedList[2])

        boundedList.assertSize(3)

    }

    @Test
    open fun test_size() {

        val boundedList = `get list of size maxCapacity 3`()

        boundedList.assertSize(0)

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        boundedList.assertSize(3)
    }

    @Test
    open fun test_remove() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        boundedList.assertSize(3)

        assertTrue { boundedList.remove(`list of 3 different items`[0]) }

        boundedList.assertSize(2)

        assertTrue { boundedList.remove(`list of 3 different items`[1]) }

        boundedList.assertSize(1)

        assertTrue { boundedList.remove(`list of 3 different items`[2]) }

        boundedList.assertSize(0)

        assertFalse { boundedList.remove(`list of 3 different items`[2]) }

        boundedList.assertSize(0)

    }

    @Test
    open fun test_removeAt() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        boundedList.assertSize(3)

        assertEquals(`list of 3 different items`[2], boundedList.removeAt(2))

        boundedList.assertSize(2)

        assertEquals(`list of 3 different items`[1], boundedList.removeAt(1))

        boundedList.assertSize(1)

        assertEquals(`list of 3 different items`[0], boundedList.removeAt(0))

        boundedList.assertSize(0)

        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList.removeAt(0)
        }

        boundedList.assertSize(0)

    }

    @Test
    open fun test_add() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.add(`list of 4 different items`[0]) }

        assertTrue { boundedList.add(`list of 4 different items`[1]) }

        assertTrue { boundedList.add(`list of 4 different items`[2]) }

        boundedList.assertSize(3)

    }

    @Test
    open fun test_addByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        assertEquals(Unit, boundedList.add(0, `list of 4 different items`[0]))

        assertEquals(Unit, boundedList.add(0, `list of 4 different items`[1]))

        assertEquals(Unit, boundedList.add(0, `list of 4 different items`[2]))

        boundedList.assertSize(3)

    }

    @Test
    open fun test_contains() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`.drop(1)) }

        assertTrue { boundedList.contains(`list of 3 different items`[1]) }

        assertTrue { boundedList.contains(`list of 3 different items`[2]) }

        assertFalse { boundedList.contains(`list of 3 different items`[0]) }

    }

    @Test
    open fun test_containsAll() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        assertTrue { boundedList.containsAll(`list of 3 different items`) }

        assertFalse { boundedList.containsAll(`list of 4 different items`) }
    }

    @Test
    open fun test_get() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`.take(2)) }

        assertEquals(`list of 3 different items`[0], boundedList[0])

        assertEquals(`list of 3 different items`[1], boundedList[1])


        assertFailsWith(IndexOutOfBoundsException::class) {
            boundedList[2]
        }

    }

    @Test
    open fun test_indexOf() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`.take(2)) }

        assertEquals(0, boundedList.indexOf(`list of 3 different items`[0]))

        assertEquals(1, boundedList.indexOf(`list of 3 different items`[1]))

        assertEquals(-1, boundedList.indexOf(`list of 3 different items`[2]))


    }

    @Test
    open fun test_isEmpty() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        assertFalse { boundedList.isEmpty() }

        assertEquals(Unit, boundedList.clear())

        assertTrue { boundedList.isEmpty() }

    }

    @Test
    open fun test_iterator() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

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
    open fun test_lastIndexOf() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.add(`list of 3 different items`[0]) }

        assertEquals(0, boundedList.lastIndexOf(`list of 3 different items`[0]))

        assertEquals(Unit, boundedList.add(0, `list of 3 different items`[1]))

        assertEquals(1, boundedList.lastIndexOf(`list of 3 different items`[0]))

        assertEquals(-1, boundedList.lastIndexOf(`list of 3 different items`[2]))

        assertTrue { boundedList.remove(`list of 3 different items`[0]) }

        assertEquals(-1, boundedList.lastIndexOf(`list of 3 different items`[0]))
    }

    @Test
    open fun test_clear() {

        val boundedList = `get list of size maxCapacity 3`()

        boundedList.assertSize(0)

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        boundedList.assertSize(3)

        assertEquals(Unit, boundedList.clear())

        boundedList.assertSize(0)
    }

    @Test
    open fun test_listIterator() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

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

        repeat(2) {

            `third listIterator`.add(`list of 4 different items`[it])
        }

        boundedList.assertSize(2)

    }

    @Test
    open fun test_listIteratorByIndex() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

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

        repeat(1) {

            `third listIterator`.add(`list of 4 different items`[it])
        }

        boundedList.assertSize(2)

    }

    @Test
    open fun test_retainAll() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        boundedList.assertSize(3)

        assertTrue { boundedList.retainAll(`list of 3 different items`.drop(2)) }

        boundedList.assertSize(1)

        assertEquals(`list of 3 different items`[2], boundedList[0])

        assertFalse { boundedList.retainAll(`list of 3 different items`) }

        boundedList.assertSize(1)

    }

    @Test
    open fun test_removeAll() {

        val `first boundedList` = `get list of size maxCapacity 3`()

        assertTrue { `first boundedList`.addAll(`list of 3 different items`) }

        `first boundedList`.assertSize(3)

        assertTrue { `first boundedList`.removeAll(`list of 3 different items`) }

        `first boundedList`.assertSize(0)


        val `second boundedList` = `get list of size maxCapacity 3`()

        assertTrue { `second boundedList`.addAll(`list of 3 different items`.take(2)) }

        `second boundedList`.assertSize(2)

        assertFalse { `second boundedList`.removeAll(`list of 4 different items`) }

        `second boundedList`.assertSize(2)

    }

    @Test
    open fun test_set() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        boundedList.assertSize(3)

        assertEquals(`list of 3 different items`[0], boundedList.set(0, `list of 4 different items`[0]))

        boundedList.assertSize(3)

        assertTrue { boundedList.containsAll(`list of 3 different items`.drop(1) + `list of 4 different items`[0]) }

        assertFailsWith(IndexOutOfBoundsException::class) {

            boundedList[3] = `list of 4 different items`[1]
        }
    }

    @Test
    open fun test_subList() {

        val boundedList = `get list of size maxCapacity 3`()

        assertTrue { boundedList.addAll(`list of 3 different items`) }

        val subList = boundedList.subList(1, 3)

        subList.assertSize(2)

        subList.clear()

        subList.assertSize(0)

        boundedList.assertSize(1)

        assertTrue { boundedList.contains(`list of 3 different items`[0]) }


        assertTrue { subList.add(`list of 4 different items`[0]) }

        assertTrue { subList.addAll(`list of 4 different items`.drop(1)) }

        subList.assertSize(4)

        boundedList.assertSize(5)

    }


    protected fun Collection<*>.assertSize(size: Int) {
        assertEquals(size, this.size)
    }
}