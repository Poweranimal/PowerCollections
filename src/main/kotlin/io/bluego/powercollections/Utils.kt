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

/**
 * Remove [n] elements from [MutableIterator].
 * Starts removing from the first [Iterator.next] element.
 */
internal fun <E> MutableIterator<E>.removeAll(n: Int) {
    check(n > 0) { "n must be greater than 0" }
    var counter = 0
    do {
        if (!hasNext()) throw IndexOutOfBoundsException("ERROR: n (= $n) is greater than the " +
                "iterators max size (= $counter)!")
        if (counter >= n) return
        next()
        remove()
        counter++
    } while (true)
}

internal fun <K, V> Map<K, V>.getKeyByValue(value: V): K? {
    for ((key, value1) in this) {
        if (value == value1) return key
    }
    return null
}