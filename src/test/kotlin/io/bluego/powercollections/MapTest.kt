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
import kotlin.test.*

@Suppress("RemoveRedundantBackticks", "PropertyName", "LocalVariableName")
abstract class MapTest<K, V> {

    protected abstract val `map of 3 different items`: LinkedHashMap<K, V>

    protected abstract val `map of 4 different items`: LinkedHashMap<K, V>

    protected abstract val `get map to test`: () -> MutableMap<K, V>

    init {
        @Suppress("LeakingThis")
        assert(`map of 3 different items`.size == 3)
        @Suppress("LeakingThis")
        assert(`map of 4 different items`.size == 4)
    }

    @Test
    open fun `test putAll`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        map.assertSize(3)

    }

    @Test
    open fun `test size`() {

        val map = `get map to test`()

        map.assertSize(0)

        map.putAll(`map of 3 different items`)

        map.assertSize(3)
    }

    @Test
    open fun `test remove`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        map.assertSize(3)

        assertTrue { map.remove(`map of 3 different items`.at(0)) }

        map.assertSize(2)

        assertTrue { map.remove(`map of 3 different items`.at(1)) }

        map.assertSize(1)

        assertTrue { map.remove(`map of 3 different items`.at(2)) }

        map.assertSize(0)

        assertFalse { map.remove(`map of 3 different items`.at(2)) }

        map.assertSize(0)

    }

    @Test
    open fun `test put`() {

        val map = `get map to test`()

        assertNull(map.put(`map of 4 different items`.at(0)))

        assertNull(map.put(`map of 4 different items`.at(1)))

        assertNull(map.put(`map of 4 different items`.at(2)))

        map.assertSize(3)

    }

    @Test
    open fun `test containsKey`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`.drop(1))

        assertTrue { map.containsKey(`map of 3 different items`.keyAt(1)) }

        assertTrue { map.containsKey(`map of 3 different items`.keyAt(2)) }

        assertFalse { map.containsKey(`map of 3 different items`.keyAt(0)) }

    }

    @Test
    open fun `test containsValue`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`.drop(1))

        assertTrue { map.containsValue(`map of 3 different items`.valueAt(1)) }

        assertTrue { map.containsValue(`map of 3 different items`.valueAt(2)) }

        assertFalse { map.containsValue(`map of 3 different items`.valueAt(0)) }

    }

    @Test
    open fun `test get`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`.take(2))

        assertEquals(`map of 3 different items`.valueAt(0), map[`map of 3 different items`.keyAt(0)])

        assertEquals(`map of 3 different items`.valueAt(1), map[`map of 3 different items`.keyAt(1)])

        assertNull(map.get(`map of 3 different items`.keyAt(2)))

    }

    @Test
    open fun `test isEmpty`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertFalse { map.isEmpty() }

        assertEquals(Unit, map.clear())

        assertTrue { map.isEmpty() }

    }

    @Test
    open fun `test keys`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertEquals(`map of 3 different items`.keys, map.keys)

    }

    @Test
    open fun `test values`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertEquals(`map of 3 different items`.values.toList(), map.values.toList())

    }

    @Test
    open fun `test entries`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertEquals(`map of 3 different items`.entries, map.entries)

    }

    @Test
    open fun `test mutableEntry`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        assertEquals(`map of 3 different items`.entries, map.entries)

        val newValue = `map of 4 different items`.at(0).value

        for (entry in map.entries) {
            entry.setValue(newValue)
        }

        assertEquals(`map of 3 different items`.entries.map { it.key to newValue }, map.entries.map(Map.Entry<K, V>::toPair))

    }

    @Test
    open fun `test iterator`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        val iterator = map.iterator()

        map.assertSize(3)

        repeat(3) {

            assertTrue { iterator.hasNext() }

            if (it < 3) assertEquals(`map of 3 different items`.at(it), iterator.next())
            else assertFailsWith(ArrayIndexOutOfBoundsException::class) { iterator.next() }

            assertEquals(Unit, iterator.remove())

            map.assertSize(3 - (it + 1))
        }

        map.assertSize(0)
    }

    @Test
    open fun `test clear`() {

        val map = `get map to test`()

        map.assertSize(0)

        map.putAll(`map of 3 different items`)

        map.assertSize(3)

        assertEquals(Unit, map.clear())

        map.assertSize(0)
    }

    @Test
    open fun `test replace`() {

        val map = `get map to test`()

        map.putAll(`map of 3 different items`)

        val failEntry0 = `map of 4 different items`.at(2)

        val failEntry1 = `map of 4 different items`.at(3)

        val entry0 = `map of 3 different items`.at(0)

        assertTrue(map.replace(entry0.key, entry0.value, `map of 4 different items`.valueAt(0)))

        assertEquals(`map of 3 different items`.apply { replace(`map of 3 different items`.keyAt(0), `map of 4 different items`.valueAt(0)) }, map)

        assertFalse(map.replace(failEntry0.key, failEntry0.value, failEntry1.value))


        val entry1 = `map of 3 different items`.at(1)

        assertEquals(`map of 3 different items`.valueAt(1), map.replace(entry1.key, `map of 4 different items`.valueAt(1)))

        assertEquals(`map of 3 different items`.apply {
            replace(`map of 3 different items`.keyAt(0), `map of 4 different items`.valueAt(0))
            replace(`map of 3 different items`.keyAt(1), `map of 4 different items`.valueAt(1)) }, map)

        assertNull(map.replace(failEntry0.key, failEntry1.value))

    }

    @Test
    open fun `test putIfAbsent`() {

        val map = `get map to test`()

        val failEntry0 = `map of 4 different items`.at(3)

        map.putAll(`map of 3 different items`)

        map.assertSize(3)

        assertEquals(`map of 3 different items`.valueAt(0), map.putIfAbsent(`map of 3 different items`.keyAt(0), failEntry0.value))

        map.assertSize(3)


        val newEntry = `map of 4 different items`.at(0)

        assertNull(map.putIfAbsent(newEntry.key, newEntry.value))

        map.assertSize(4)

        assertEquals(`map of 3 different items` + `map of 4 different items`.take(1), map)

    }

    protected fun Map<*, *>.assertSize(size: Int) {
        assertEquals(size, this.size)
    }

    protected fun <K, V> MutableMap<K, V>.remove(entry: Map.Entry<K, V>): Boolean {
        return this.remove(entry.key, entry.value)
    }

    protected fun <K, V> LinkedHashMap<K, V>.at(index: Int): Map.Entry<K, V> {
        return this.asIterable().elementAtOrNull(index) ?: throw IndexOutOfBoundsException()
    }

    protected fun <K, V> LinkedHashMap<K, V>.keyAt(index: Int): K {
        return this.at(index).key
    }

    protected fun <K, V> LinkedHashMap<K, V>.valueAt(index: Int): V {
        return this.at(index).value
    }

    protected fun <K, V> LinkedHashMap<K, V>.removeAt(index: Int): V {
        return this.remove(this.at(index).key) ?: throw NoSuchElementException()
    }

    protected fun <K, V> MutableMap<K, V>.put(entry: Map.Entry<K, V>): V? {
        return this.put(entry.key, entry.value)
    }

    protected fun <K, V> LinkedHashMap<K, V>.drop(number: Int): Map<K, V> {
        return this.asIterable().drop(number).associate(Map.Entry<K, V>::toPair)
    }

    protected fun <K, V> LinkedHashMap<K, V>.take(number: Int): Map<K, V> {
        return this.asIterable().take(number).associate(Map.Entry<K, V>::toPair)
    }
}