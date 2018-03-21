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

    open fun `get list to test`(): MutableList<String> {
        return mutableListOf()
    }

    @Test
    open fun `test addAll`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        list.assertSize(3)

    }

    @Test
    open fun `test addAllByIndex`() {

        val list = `get list to test`()

        val `first item` = `list of 3 different items`.first()

        val `collection from 1 to 2` = `list of 3 different items`.drop(1)

        assertTrue { list.add(`first item`) }

        list.assertSize(1)

        assertTrue { list.addAll(0, `collection from 1 to 2`) }

        assertEquals(`collection from 1 to 2`[0], list[0])

        assertEquals(`collection from 1 to 2`[1], list[1])

        assertEquals(`first item`, list[2])

        list.assertSize(3)

    }

    @Test
    open fun `test size`() {

        val list = `get list to test`()

        list.assertSize(0)

        assertTrue { list.addAll(`list of 3 different items`) }

        list.assertSize(3)
    }

    @Test
    open fun `test remove`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        list.assertSize(3)

        assertTrue { list.remove(`list of 3 different items`[0]) }

        list.assertSize(2)

        assertTrue { list.remove(`list of 3 different items`[1]) }

        list.assertSize(1)

        assertTrue { list.remove(`list of 3 different items`[2]) }

        list.assertSize(0)

        assertFalse { list.remove(`list of 3 different items`[2]) }

        list.assertSize(0)

    }

    @Test
    open fun `test removeAt`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        list.assertSize(3)

        assertEquals(`list of 3 different items`[2], list.removeAt(2))

        list.assertSize(2)

        assertEquals(`list of 3 different items`[1], list.removeAt(1))

        list.assertSize(1)

        assertEquals(`list of 3 different items`[0], list.removeAt(0))

        list.assertSize(0)

        assertFailsWith(IndexOutOfBoundsException::class) {
            list.removeAt(0)
        }

        list.assertSize(0)

    }

    @Test
    open fun `test add`() {

        val list = `get list to test`()

        assertTrue { list.add(`list of 4 different items`[0]) }

        assertTrue { list.add(`list of 4 different items`[1]) }

        assertTrue { list.add(`list of 4 different items`[2]) }

        list.assertSize(3)

    }

    @Test
    open fun `test addByIndex`() {

        val list = `get list to test`()

        assertEquals(Unit, list.add(0, `list of 4 different items`[0]))

        assertEquals(Unit, list.add(0, `list of 4 different items`[1]))

        assertEquals(Unit, list.add(0, `list of 4 different items`[2]))

        list.assertSize(3)

    }

    @Test
    open fun `test contains`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`.drop(1)) }

        assertTrue { list.contains(`list of 3 different items`[1]) }

        assertTrue { list.contains(`list of 3 different items`[2]) }

        assertFalse { list.contains(`list of 3 different items`[0]) }

    }

    @Test
    open fun `test containsAll`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        assertTrue { list.containsAll(`list of 3 different items`) }

        assertFalse { list.containsAll(`list of 4 different items`) }
    }

    @Test
    open fun `test get`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`.take(2)) }

        assertEquals(`list of 3 different items`[0], list[0])

        assertEquals(`list of 3 different items`[1], list[1])


        assertFailsWith(IndexOutOfBoundsException::class) {
            list[2]
        }

    }

    @Test
    open fun `test indexOf`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`.take(2)) }

        assertEquals(0, list.indexOf(`list of 3 different items`[0]))

        assertEquals(1, list.indexOf(`list of 3 different items`[1]))

        assertEquals(-1, list.indexOf(`list of 3 different items`[2]))


    }

    @Test
    open fun `test isEmpty`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        assertFalse { list.isEmpty() }

        assertEquals(Unit, list.clear())

        assertTrue { list.isEmpty() }

    }

    @Test
    open fun `test iterator`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        val iterator = list.iterator()

        list.assertSize(3)

        repeat(3) {

            assertTrue { iterator.hasNext() }

            if (it < 3) assertEquals(`list of 3 different items`[it], iterator.next())
            else assertFailsWith(ArrayIndexOutOfBoundsException::class) { iterator.next() }

            assertEquals(Unit, iterator.remove())

            list.assertSize(3 - (it + 1))
        }

        list.assertSize(0)
    }

    @Test
    open fun `test lastIndexOf`() {

        val list = `get list to test`()

        assertTrue { list.add(`list of 3 different items`[0]) }

        assertEquals(0, list.lastIndexOf(`list of 3 different items`[0]))

        assertEquals(Unit, list.add(0, `list of 3 different items`[1]))

        assertEquals(1, list.lastIndexOf(`list of 3 different items`[0]))

        assertEquals(-1, list.lastIndexOf(`list of 3 different items`[2]))

        assertTrue { list.remove(`list of 3 different items`[0]) }

        assertEquals(-1, list.lastIndexOf(`list of 3 different items`[0]))
    }

    @Test
    open fun `test clear`() {

        val list = `get list to test`()

        list.assertSize(0)

        assertTrue { list.addAll(`list of 3 different items`) }

        list.assertSize(3)

        assertEquals(Unit, list.clear())

        list.assertSize(0)
    }

    @Test
    open fun `test listIterator`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        val listIterator = list.listIterator()

        list.assertSize(3)

        repeat(3) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`list of 3 different items`[it], listIterator.next())

            assertEquals(it + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it, listIterator.previousIndex())

            assertEquals(`list of 3 different items`[it], listIterator.previous())

            assertEquals(`list of 3 different items`[it], listIterator.next())

        }

        list.assertSize(3)

        val `second listIterator` = list.listIterator()

        repeat(3) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`list of 3 different items`[it], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            list.assertSize(3 - (it + 1))
        }

        list.assertSize(0)

        val `third listIterator` = list.listIterator()

        repeat(2) {

            `third listIterator`.add(`list of 4 different items`[it])
        }

        list.assertSize(2)

    }

    @Test
    open fun `test listIteratorByIndex`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        val listIterator = list.listIterator(1)

        list.assertSize(3)

        repeat(2) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`list of 3 different items`[it + 1], listIterator.next())

            assertEquals(it + 1 + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it + 1, listIterator.previousIndex())

            assertEquals(`list of 3 different items`[it + 1], listIterator.previous())

            assertEquals(`list of 3 different items`[it + 1], listIterator.next())

        }

        list.assertSize(3)

        val `second listIterator` = list.listIterator(1)

        repeat(2) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`list of 3 different items`[it + 1], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            list.assertSize(3 - (it + 1))
        }

        list.assertSize(1)

        val `third listIterator` = list.listIterator(1)

        repeat(1) {

            `third listIterator`.add(`list of 4 different items`[it])
        }

        list.assertSize(2)

    }

    @Test
    open fun `test retainAll`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        list.assertSize(3)

        assertTrue { list.retainAll(`list of 3 different items`.drop(2)) }

        list.assertSize(1)

        assertEquals(`list of 3 different items`[2], list[0])

        assertFalse { list.retainAll(`list of 3 different items`) }

        list.assertSize(1)

    }

    @Test
    open fun `test removeAll`() {

        val `first list` = `get list to test`()

        assertTrue { `first list`.addAll(`list of 3 different items`) }

        `first list`.assertSize(3)

        assertTrue { `first list`.removeAll(`list of 3 different items`) }

        `first list`.assertSize(0)


        val `second list` = `get list to test`()

        assertTrue { `second list`.addAll(`list of 3 different items`.take(2)) }

        `second list`.assertSize(2)

        assertFalse { `second list`.removeAll(`list of 4 different items`) }

        `second list`.assertSize(2)

    }

    @Test
    open fun `test set`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        list.assertSize(3)

        assertEquals(`list of 3 different items`[0], list.set(0, `list of 4 different items`[0]))

        list.assertSize(3)

        assertTrue { list.containsAll(`list of 3 different items`.drop(1) + `list of 4 different items`[0]) }

        assertFailsWith(IndexOutOfBoundsException::class) {

            list[3] = `list of 4 different items`[1]
        }
    }

    @Test
    open fun `test subList`() {

        val list = `get list to test`()

        assertTrue { list.addAll(`list of 3 different items`) }

        val subList = list.subList(1, 3)

        subList.assertSize(2)

        subList.clear()

        subList.assertSize(0)

        list.assertSize(1)

        assertTrue { list.contains(`list of 3 different items`[0]) }

        assertTrue { subList.add(`list of 4 different items`[0]) }

        assertTrue { subList.addAll(`list of 4 different items`.drop(1)) }

        subList.assertSize(4)

        list.assertSize(5)

    }


    protected fun Collection<*>.assertSize(size: Int) {
        assertEquals(size, this.size)
    }
}