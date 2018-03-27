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

import io.bluego.powercollections._testutils.templates.MapTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("PropertyName")
class BiMapTest: MapTest<Int, String>() {

    override val `map of 3 different items`: LinkedHashMap<Int, String>
        get() = LinkedHashMap(mapOf(0 to "zero", 1 to "one", 2 to "two"))

    override val `map of 4 different items`: LinkedHashMap<Int, String>
        get() = LinkedHashMap(mapOf(10 to "ten", 11 to "eleven", 12 to "twelve", 13 to "thirteen"))

    override val `get map to test`: () -> MutableBiMap<Int, String>
        get() = { mutableBiMapOf() }


    override fun `test mutableEntry`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertEquals(`map of 3 different items`.entries, map.entries)

        map.entries.forEachIndexed { index, mutableEntry ->
            mutableEntry.setValue(`map of 4 different items`.at(index).value)
        }

        assertEquals(`map of 3 different items`.entries.mapIndexed { index, mutableEntry -> mutableEntry.key to `map of 4 different items`.valueAt(index) },
                map.entries.map(Map.Entry<Int, String>::toPair))
    }

    @Test
    fun `test put existing key`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        map[`map of 3 different items`.keyAt(0)] = `map of 4 different items`.valueAt(0)

        val referenceMap = `map of 3 different items`
        referenceMap[referenceMap.keyAt(0)] = `map of 4 different items`.valueAt(0)

        assertEquals<Map<Int, String>>(referenceMap, map)
    }

    @Test
    fun `test put existing value`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertFailsWith(IllegalArgumentException::class) {
            map[`map of 4 different items`.keyAt(0)] = `map of 3 different items`.valueAt(0)
        }

        assertEquals<Map<Int, String>>(`map of 3 different items`, map)

    }

    @Test
    fun `test iterator setValue existing value`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        val iter = map.iterator()

        iter.next().setValue(`map of 4 different items`.valueAt(0))

        assertFailsWith(IllegalArgumentException::class) {

            iter.next().setValue(`map of 3 different items`.valueAt(0))
        }

        val referenceMap = `map of 3 different items`
        referenceMap[referenceMap.keyAt(0)] = `map of 4 different items`.valueAt(0)

        assertEquals<Map<Int, String>>(referenceMap, map)

    }

    @Test
    fun `test inverse`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertEquals<Map<Int, String>>(`map of 3 different items`, map)


        val inversedMap = `map of 3 different items`.map { it.value to it.key }.toMap()

        assertEquals(inversedMap, map.inverse)
    }
}