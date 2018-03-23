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

package io.bluego.powercollections.observable

import kotlin.test.assertEquals

abstract class AbstractDummyObserver<I, E>(val isDebug: Boolean = false) {

    val addedList = mutableListOf<Pair<I, E>>()
    val removedList = mutableListOf<Pair<I, E>>()
    val replacedList = mutableListOf<Pair<I, Pair<E, E>>>()

    fun reset() {
        removedList.clear()
        addedList.clear()
        replacedList.clear()
    }

    inline fun assertWasAdded(number: Int, action: () -> Unit) {
        val start = addedList.size
        action()
        assertEquals(number, addedList.size - start, if (isDebug) "assertWasAdded" else null)
    }

    inline fun assertWasRemoved(number: Int, action: () -> Unit) {
        val start = removedList.size
        action()
        assertEquals(number, removedList.size - start, if (isDebug) "assertWasRemoved" else null)
    }

    inline fun assertWasReplaced(number: Int, action: () -> Unit) {
        val start = replacedList.size
        action()
        assertEquals(number, replacedList.size - start, if (isDebug) "AssertWasReplaced" else null)
    }

    inline fun assertWas(added: Int, removed: Int, replaced: Int, action: () -> Unit) {
        assertWasAdded(added) {
            assertWasRemoved(removed) {
                assertWasReplaced(replaced, action)
            }
        }
    }

}