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
import kotlin.test.assertTrue

@Suppress("RemoveRedundantBackticks", "PropertyName", "LocalVariableName")
abstract class ListTest<E>: CollectionTest<E>() {


    abstract override val `collection of 3 different items`: List<E>

    abstract override val `collection of 4 different items`: List<E>

    abstract override val `get collection to test`: () -> MutableList<E>


    @Test
    open fun `test addAllByIndex`() {

        val list = `get collection to test`()

        val `first item` = `collection of 3 different items`.first()

        val `collection from 1 to 2` = `collection of 3 different items`.drop(1)

        assertTrue { list.add(`first item`) }

        list.assertSize(1)

        assertTrue { list.addAll(0, `collection from 1 to 2`) }

        assertEquals(`collection from 1 to 2`[0], list[0])

        assertEquals(`collection from 1 to 2`[1], list[1])

        assertEquals(`first item`, list[2])

        list.assertSize(3)

    }

    @Test
    open fun `test removeAt`() {

        val list = `get collection to test`()

        assertTrue { list.addAll(`collection of 3 different items`) }

        list.assertSize(3)

        assertEquals(`collection of 3 different items`[2], list.removeAt(2))

        list.assertSize(2)

        assertEquals(`collection of 3 different items`[1], list.removeAt(1))

        list.assertSize(1)

        assertEquals(`collection of 3 different items`[0], list.removeAt(0))

        list.assertSize(0)

        assertFailsWith(IndexOutOfBoundsException::class) {
            list.removeAt(0)
        }

        list.assertSize(0)

    }

    @Test
    open fun `test addByIndex`() {

        val list = `get collection to test`()

        assertEquals(Unit, list.add(0, `collection of 4 different items`[0]))

        assertEquals(Unit, list.add(0, `collection of 4 different items`[1]))

        assertEquals(Unit, list.add(0, `collection of 4 different items`[2]))

        list.assertSize(3)

    }

    @Test
    open fun `test indexOf`() {

        val list = `get collection to test`()

        assertTrue { list.addAll(`collection of 3 different items`.take(2)) }

        assertEquals(0, list.indexOf(`collection of 3 different items`[0]))

        assertEquals(1, list.indexOf(`collection of 3 different items`[1]))

        assertEquals(-1, list.indexOf(`collection of 3 different items`[2]))


    }

    @Test
    open fun `test lastIndexOf`() {

        val list = `get collection to test`()

        assertTrue { list.add(`collection of 3 different items`[0]) }

        assertEquals(0, list.lastIndexOf(`collection of 3 different items`[0]))

        assertEquals(Unit, list.add(0, `collection of 3 different items`[1]))

        assertEquals(1, list.lastIndexOf(`collection of 3 different items`[0]))

        assertEquals(-1, list.lastIndexOf(`collection of 3 different items`[2]))

        assertTrue { list.remove(`collection of 3 different items`[0]) }

        assertEquals(-1, list.lastIndexOf(`collection of 3 different items`[0]))
    }

    @Test
    open fun `test listIterator`() {

        val list = `get collection to test`()

        assertTrue { list.addAll(`collection of 3 different items`) }

        val listIterator = list.listIterator()

        list.assertSize(3)

        repeat(3) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`collection of 3 different items`[it], listIterator.next())

            assertEquals(it + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it, listIterator.previousIndex())

            assertEquals(`collection of 3 different items`[it], listIterator.previous())

            assertEquals(`collection of 3 different items`[it], listIterator.next())

        }

        list.assertSize(3)

        val `second listIterator` = list.listIterator()

        repeat(3) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`collection of 3 different items`[it], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            list.assertSize(3 - (it + 1))
        }

        list.assertSize(0)

        val `third listIterator` = list.listIterator()

        repeat(2) {

            `third listIterator`.add(`collection of 4 different items`[it])
        }

        list.assertSize(2)

    }

    @Test
    open fun `test listIteratorByIndex`() {

        val list = `get collection to test`()

        assertTrue { list.addAll(`collection of 3 different items`) }

        val listIterator = list.listIterator(1)

        list.assertSize(3)

        repeat(2) {

            assertTrue { listIterator.hasNext() }

            assertEquals(`collection of 3 different items`[it + 1], listIterator.next())

            assertEquals(it + 1 + 1, listIterator.nextIndex())

            assertTrue { listIterator.hasPrevious() }

            assertEquals(it + 1, listIterator.previousIndex())

            assertEquals(`collection of 3 different items`[it + 1], listIterator.previous())

            assertEquals(`collection of 3 different items`[it + 1], listIterator.next())

        }

        list.assertSize(3)

        val `second listIterator` = list.listIterator(1)

        repeat(2) {

            assertTrue { `second listIterator`.hasNext() }

            assertEquals(`collection of 3 different items`[it + 1], `second listIterator`.next())

            assertEquals(Unit, `second listIterator`.remove())

            list.assertSize(3 - (it + 1))
        }

        list.assertSize(1)

        val `third listIterator` = list.listIterator(1)

        repeat(1) {

            `third listIterator`.add(`collection of 4 different items`[it])
        }

        list.assertSize(2)

    }

    @Test
    open fun `test subList`() {

        val list = `get collection to test`()

        assertTrue { list.addAll(`collection of 3 different items`) }

        val subList = list.subList(1, 3)

        subList.assertSize(2)

        subList.clear()

        subList.assertSize(0)

        list.assertSize(1)

        assertTrue { list.contains(`collection of 3 different items`[0]) }

        assertTrue { subList.add(`collection of 4 different items`[0]) }

        assertTrue { subList.addAll(`collection of 4 different items`.drop(1)) }

        subList.assertSize(4)

        list.assertSize(5)

    }

    @Test
    open fun `test set`() {

        val list = `get collection to test`()

        assertTrue { list.addAll(`collection of 3 different items`) }

        list.assertSize(3)

        assertEquals(`collection of 3 different items`[0], list.set(0, `collection of 4 different items`[0]))

        list.assertSize(3)

        assertTrue { list.containsAll(`collection of 3 different items`.drop(1) + `collection of 4 different items`[0]) }

        assertFailsWith(IndexOutOfBoundsException::class) {

            list[3] = `collection of 4 different items`[1]
        }
    }

    @Test
    open fun `test get`() {

        val list = `get collection to test`()

        assertTrue { list.addAll(`collection of 3 different items`.take(2)) }

        assertEquals(`collection of 3 different items`[0], list[0])

        assertEquals(`collection of 3 different items`[1], list[1])

        assertFailsWith(IndexOutOfBoundsException::class) {
            list[2]
        }

    }

    //TODO: implement
    override fun `test removeIf`() {
        //super.`test removeIf`()
    }
}