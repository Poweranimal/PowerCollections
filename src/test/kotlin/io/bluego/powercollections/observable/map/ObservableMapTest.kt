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

import io.bluego.powercollections.MapTest

//TODO: add replaced tests
class ObservableMapTest : MapTest() {


    private val mDummyObserver = DummyMapObserver<Int, String>()

    override fun `get map to test`(): MutableMap<Int, String> {
        return mutableObservableMapOf(mDummyObserver)
    }

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
}