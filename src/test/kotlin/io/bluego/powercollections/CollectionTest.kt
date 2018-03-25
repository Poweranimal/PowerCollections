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

@Suppress("LocalVariableName", "PropertyName")
abstract class CollectionTest<E> {

    protected abstract val `collection of 3 different items`: Collection<E>

    protected abstract val `collection of 4 different items`: Collection<E>

    protected abstract val `get collection to test`: () -> MutableCollection<E>


    init {
        @Suppress("LeakingThis")
        assert(`collection of 3 different items`.size == 3)
        @Suppress("LeakingThis")
        assert(`collection of 4 different items`.size == 4)
    }

    @Test
    open fun `test addAll`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`) }

        collection.assertSize(3)

    }

    @Test
    open fun `test size`() {

        val collection = `get collection to test`()

        collection.assertSize(0)

        assertTrue { collection.addAll(`collection of 3 different items`) }

        collection.assertSize(3)
    }

    @Test
    open fun `test remove`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`) }

        collection.assertSize(3)

        assertTrue { collection.remove(`collection of 3 different items`[0]) }

        collection.assertSize(2)

        assertTrue { collection.remove(`collection of 3 different items`[1]) }

        collection.assertSize(1)

        assertTrue { collection.remove(`collection of 3 different items`[2]) }

        collection.assertSize(0)

        assertFalse { collection.remove(`collection of 3 different items`[2]) }

        collection.assertSize(0)

    }

    @Test
    open fun `test removeIf`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`) }

        collection.assertSize(3)

        assertTrue(collection.removeIf { it == `collection of 3 different items`[0] })

        collection.assertSize(2)

        assertFalse(collection.removeIf { it == `collection of 3 different items`[0] })

        collection.assertSize(2)

    }

    @Test
    open fun `test add`() {

        val collection = `get collection to test`()

        assertTrue { collection.add(`collection of 4 different items`[0]) }

        assertTrue { collection.add(`collection of 4 different items`[1]) }

        assertTrue { collection.add(`collection of 4 different items`[2]) }

        collection.assertSize(3)

    }

    @Test
    open fun `test contains`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`.drop(1)) }

        assertTrue { collection.contains(`collection of 3 different items`[1]) }

        assertTrue { collection.contains(`collection of 3 different items`[2]) }

        assertFalse { collection.contains(`collection of 3 different items`[0]) }

    }

    @Test
    open fun `test containsAll`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`) }

        assertTrue { collection.containsAll(`collection of 3 different items`) }

        assertFalse { collection.containsAll(`collection of 4 different items`) }
    }

    @Test
    open fun `test isEmpty`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`) }

        assertFalse { collection.isEmpty() }

        assertEquals(Unit, collection.clear())

        assertTrue { collection.isEmpty() }

    }

    @Test
    open fun `test iterator`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`) }

        val iterator = collection.iterator()

        collection.assertSize(3)

        repeat(3) {

            assertTrue { iterator.hasNext() }

            if (it < 3) assertEquals(`collection of 3 different items`[it], iterator.next())
            else assertFailsWith(ArrayIndexOutOfBoundsException::class) { iterator.next() }

            assertEquals(Unit, iterator.remove())

            collection.assertSize(3 - (it + 1))
        }

        collection.assertSize(0)
    }

    @Test
    open fun `test clear`() {

        val collection = `get collection to test`()

        collection.assertSize(0)

        assertTrue { collection.addAll(`collection of 3 different items`) }

        collection.assertSize(3)

        assertEquals(Unit, collection.clear())

        collection.assertSize(0)
    }

    @Test
    open fun `test retainAll`() {

        val collection = `get collection to test`()

        assertTrue { collection.addAll(`collection of 3 different items`) }

        collection.assertSize(3)

        assertTrue { collection.retainAll(`collection of 3 different items`.drop(2)) }

        collection.assertSize(1)

        assertEquals(`collection of 3 different items`[2], collection[0])

        assertFalse { collection.retainAll(`collection of 3 different items`) }

        collection.assertSize(1)

    }

    @Test
    open fun `test removeAll`() {

        val `first collection` = `get collection to test`()

        assertTrue { `first collection`.addAll(`collection of 3 different items`) }

        `first collection`.assertSize(3)

        assertTrue { `first collection`.removeAll(`collection of 3 different items`) }

        `first collection`.assertSize(0)


        val `second collection` = `get collection to test`()

        assertTrue { `second collection`.addAll(`collection of 3 different items`.take(2)) }

        `second collection`.assertSize(2)

        assertFalse { `second collection`.removeAll(`collection of 4 different items`) }

        `second collection`.assertSize(2)

    }

    private operator fun <E> Collection<E>.get(index: Int): E {
        return if (this is MutableList) this[index]
        else this.asIterable().elementAt(index)
    }

}