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

interface Boundable {

    /**
     * The max capacity of the implementing list.
     * Cannot contain more items than the determined max capacity.
     */
    val maxCapacity: Int
}

interface MutableBoundable : Boundable {

    /**
     * Increases or decreases the [maxCapacity].
     */
    fun resize(newSize: Int)

    /**
     * Increases or decreases the [maxCapacity].
     * @return true, if item/s got removed
     */
    fun forceResize(newSize: Int): Boolean
}


internal fun <T, E> T.checkCapacity(elements: Collection<E>) where T: Boundable, T: Collection<E>
        = checkCapacity(size + elements.size)

internal fun <T, E> T.checkCapacity() where T: Boundable, T: Collection<E> = checkCapacity(size + 1)

internal fun <T, E> T.checkCapacity(predictedSize: Int) where T: Boundable, T: Collection<E> {
    if (predictedSize > maxCapacity) throw IndexOutOfBoundsException(
            "The maxCapacity of $maxCapacity is already reached")
}

internal fun Boundable.checkResize(currentSize: Int, newSize: Int) {
    if (newSize < currentSize) throw IllegalStateException("Resizing would lose data")
}

internal fun Boundable.checkInitialMaxCapacity(initialMaxCapacity: Int) {
    if (initialMaxCapacity <= 0) throw IndexOutOfBoundsException(
            "The minimum maxCapacity must be greater than 0")
}