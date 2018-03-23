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

package io.bluego.powercollections.observable.list

import kotlin.test.assertEquals

@Suppress("ConstantConditionIf")
class DummyListObserver<E>(val isDebug: Boolean = false) : ListObserver<E> {

    val addedList = mutableListOf<Pair<Int, E?>>()
    val removedList = mutableListOf<Pair<Int, E?>>()
    val replacedList = mutableListOf<Pair<Int, Pair<E?, E?>>>()

    private fun reset() {
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

    override fun wasAdded(index: Int, element: E?) {
        if (isDebug) println("Added: index: $index element: $element")
        addedList.add(index to element)
    }

    override fun wasAdded(elements: Map<Int, E?>) {
        if (isDebug) println("Added: $elements")
        addedList.addAll(elements.toList())
    }

    override fun wasRemoved(index: Int, element: E?) {
        if (isDebug) println("Removed: index: $index element: $element")
        removedList.add(index to element)
    }

    override fun wasRemoved(elements: Map<Int, E?>) {
        if (isDebug) println("Removed: $elements")
        removedList.addAll(elements.toList())
    }

    override fun wasReplaced(index: Int, lastElement: E?, newElement: E?) {
        if (isDebug) println("Replaced $index: $lastElement with $newElement")
        replacedList.add(index to (lastElement to newElement))
    }

    override fun wasReplaced(map: Map<Int, Pair<E?, E?>>) {
        if (isDebug) println("Replaced: $map")
        replacedList.addAll(map.toList())
    }
    override fun notifyDataChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun notifyDataChanged(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}