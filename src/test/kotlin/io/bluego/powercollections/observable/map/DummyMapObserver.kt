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

import io.bluego.powercollections.observable.AbstractDummyObserver


class DummyMapObserver<K, V> : AbstractDummyObserver<K, V>, MapObserver<K, V> {

    constructor() : super()
    constructor(isDebug: Boolean) : super(isDebug)

    override fun wasAdded(key: K, value: V) {
        if (isDebug) println("Added: key: $key value: $value")
        addedList.add(key to value)
    }

    override fun wasAdded(entries: Map<K, V>) {
        if (isDebug) println("Added: $entries")
        addedList.addAll(entries.map(Map.Entry<K, V>::toPair))
    }

    override fun wasRemoved(key: K, value: V) {
        if (isDebug) println("Removed: key: $key value: $value")
        removedList.add(key to value)
    }

    override fun wasRemoved(entries: Map<K, V>) {
        if (isDebug) println("Removed: $entries")
        removedList.addAll(entries.map(Map.Entry<K, V>::toPair))
    }

    override fun wasReplaced(key: K, lastValue: V, newValue: V) {
        if (isDebug) println("Replaced key: $key lastValue: $lastValue with newValue $newValue")
        replacedList.add(key to (lastValue to newValue))
    }

    override fun wasReplaced(entries: Map<K, Pair<V, V>>) {
        if (isDebug) println("Replaced: $entries")
        replacedList.addAll(entries.map(Map.Entry<K, Pair<V, V>>::toPair))
    }

    override fun notifyDataChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun notifyDataChanged(key: K) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}