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

package io.bluego.powercollections.observable.map

import io.bluego.powercollections._testutils.templates.MapTest
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("PropertyName")
class ObservableMapTest : MapTest<Int, String>() {

    private val mDummyObserver = DummyMapObserver<Int, String>()

    override val `map of 3 different items`: LinkedHashMap<Int, String>
        get() = linkedMapOf(0 to "zero", 1 to "one", 2 to "two")

    override val `map of 4 different items`: LinkedHashMap<Int, String>
        get() = linkedMapOf(10 to "ten", 11 to "eleven", 12 to "twelve", 13 to "thirteen")

    override val `get map to test`: () -> MutableObservableMap<Int, String>
        get() = { mutableObservableMapOf(mDummyObserver) }


    override fun `test putAll`() {
        mDummyObserver.assertWas(3, 0, 0) {
            super.`test putAll`()
        }
    }

    override fun `test remove`() {
        mDummyObserver.assertWas(3, 3, 0) {
            super.`test remove`()
        }
    }

    override fun `test put`() {
        mDummyObserver.assertWas(3, 0, 0) {
            super.`test put`()
        }
    }

    override fun `test mutableEntry`() {
        mDummyObserver.assertWas(3, 0, 3) {
            super.`test mutableEntry`()
        }
    }

    override fun `test iterator`() {
        mDummyObserver.assertWas(3, 3, 0) {
            super.`test iterator`()
        }
    }

    override fun `test clear`() {
        mDummyObserver.assertWas(3, 3, 0) {
            super.`test clear`()
        }
    }

    override fun `test replace`() {
        mDummyObserver.assertWas(3, 0, 2) {
            super.`test replace`()
        }
    }

    override fun `test putIfAbsent`() {
        mDummyObserver.assertWas(4, 0, 0) {
            super.`test putIfAbsent`()
        }
    }

    @Test
    fun `test remove mutableEntry from entries`() {

        mDummyObserver.assertWas(3, 1, 0) {

            val map = `get map to test`()

            map.putAll(`map of 3 different items`)

            map.assertSize(3)

            map.entries.remove(`map of 3 different items`.at(0))

            map.assertSize(2)

        }
    }

    @Test
    fun `test replace from nullable value`() {

        val dummyObserver = DummyMapObserver<Int, String?>()

        dummyObserver.assertWas(3, 0, 2) {

            val nullableMap = mutableObservableMapOf(dummyObserver,
                    0 to null,
                    1 to null,
                    2 to "two"
            )

            assertTrue(nullableMap.replace(0, null, "zero"))

            assertNull(nullableMap.replace(1, "one"))

        }
    }

    @Test
    fun `test putIfAbsent from nullable value`() {

        val dummyObserver = DummyMapObserver<Int, String?>()

        dummyObserver.assertWas(3, 0, 1) {

            val nullableMap = mutableObservableMapOf(dummyObserver,
                    0 to null,
                    1 to null,
                    2 to "two"
            )

            assertNull(nullableMap.putIfAbsent(0, "zero"))

        }

    }
}