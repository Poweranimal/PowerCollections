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

package io.bluego.powercollections._testutils.templates

import io.bluego.powercollections._testutils.assertSize
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("PropertyName")
abstract class SetTest<E>: CollectionTest<E>() {

    abstract override val `list of 3 different items`: List<E>

    abstract override val `list of 4 different items`: List<E>

    abstract override val `get collection to test`: () -> MutableSet<E>


    @Test
    open fun `test add same item`() {

        val set = `get collection to test`()

        assertTrue(set.add(`list of 3 different items`[0]))

        set.assertSize(1)

        assertFalse(set.add(`list of 3 different items`[0]))

        set.assertSize(1)

        assertEquals(`list of 3 different items`.take(1), set.toList())
    }

    @Test
    open fun `test addAll same item`() {

        val set = `get collection to test`()

        assertTrue(set.addAll(`list of 3 different items`))

        set.assertSize(3)

        assertFalse(set.addAll(`list of 3 different items`))

        set.assertSize(3)

        assertEquals(`list of 3 different items`, set.toList())

    }
}