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

import io.bluego.powercollections.ListTest

@Suppress("RemoveRedundantBackticks")
open class ObservableListTest : ListTest() {

    private val mDummyObserver = DummyListObserver<String>()

    override fun `get list to test`(): MutableList<String> {
        return mutableObservableListOf(mDummyObserver)
    }


    override fun `test add`() {
        mDummyObserver.assertWas(3, 0, 0) {
            super.`test add`()
        }
    }

    override fun `test addAll`() {
        mDummyObserver.assertWas(3, 0, 0) {
            super.`test addAll`()
        }
    }

    override fun `test addAllByIndex`() {
        mDummyObserver.assertWas(3, 0, 0) {
            super.`test addAllByIndex`()
        }
    }

    override fun `test addByIndex`() {
        mDummyObserver.assertWas(3, 0, 0) {
            super.`test addByIndex`()
        }
    }

    override fun `test iterator`() {
        mDummyObserver.assertWas(3, 3, 0) {
            super.`test iterator`()
        }
    }

    override fun `test listIterator`() {
        mDummyObserver.assertWas(5, 3, 0) {
            super.`test listIterator`()
        }
    }

    override fun `test listIteratorByIndex`() {
        mDummyObserver.assertWas(4, 2, 0) {
            super.`test listIteratorByIndex`()
        }
    }

    override fun `test remove`() {
        mDummyObserver.assertWas(3, 3, 0) {
            super.`test remove`()
        }
    }

    override fun `test removeAt`() {
        mDummyObserver.assertWas(3, 3, 0) {
            super.`test removeAt`()
        }
    }

    override fun `test clear`() {
        mDummyObserver.assertWas(3, 3, 0) {
            super.`test clear`()
        }
    }

    override fun `test retainAll`() {
        mDummyObserver.assertWas(3, 2, 0) {
            super.`test retainAll`()
        }
    }

    override fun `test removeAll`() {
        mDummyObserver.assertWas(5, 3, 0) {
            super.`test removeAll`()
        }
    }

    override fun `test set`() {
        mDummyObserver.assertWas(3, 0, 1) {
            super.`test set`()
        }
    }

    override fun `test subList`() {
        mDummyObserver.assertWas(7, 2, 0) {
            super.`test subList`()
        }
    }
}