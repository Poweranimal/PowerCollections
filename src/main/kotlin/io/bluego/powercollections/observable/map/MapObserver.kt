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

import io.bluego.powercollections.observable.list.ListObserver
import io.bluego.powercollections.observable.list.ListObserverDSL


interface MapObserver<K, in V> {

    /**
     * Manually called, if changes in an [Map.Entry.value] has been made.
     */
    fun notifyDataChanged()

    /**
     * Manually called, if changes in a class in an [Map.Entry.value] has been made.
     * Specifies the class that has been changed by its [key].
     *
     * @param key of the item's content that has been changed
     */
    fun notifyDataChanged(key: K)

    /**
     * Called when new values are added.
     */
    fun wasAdded(key: K, value: V)

    /**
     * Called when new values are added.
     */
    fun wasAdded(entries: Map<K, V>)

    /**
     * Called when values are removed.
     */
    fun wasRemoved(key: K, value: V)

    /**
     * Called when values are removed.
     */
    fun wasRemoved(entries: Map<K, V>)

    /**
     * Called when a value is replaced with a new value.
     */
    fun wasReplaced(key: K, lastValue: V, newValue: V)

    /**
     * Called when a value is replaced with a new value.
     * 'Pair.first = oldValue; Pair.second = newValue'
     */
    fun wasReplaced(entries: Map<K, Pair<V, V>>)
}

@Suppress("AddVarianceModifier")
class MapObserverDSL<K, V> private constructor() {

    companion object {

        private const val ERROR_MSG = "Set safetyMode to false in order to disable this error"

        fun <K, V> create(mapObserver: MapObserverDSL<K, V>.() -> Unit): MapObserver<K, V> {

            return MapObserverDSL<K, V>().apply(mapObserver).run {

                object : MapObserver<K, V> {

                    override fun notifyDataChanged() = mNotifyDataChanged()

                    override fun notifyDataChanged(key: K) = mNotifyDataChangedKey(key)

                    override fun wasAdded(key: K, value: V) = mWasAdded(key, value)

                    override fun wasAdded(entries: Map<K, V>) = mWasAddedMultiple(entries)

                    override fun wasRemoved(key: K, value: V) = mWasRemoved(key, value)

                    override fun wasRemoved(entries: Map<K, V>) = mWasRemovedMultiple(entries)

                    override fun wasReplaced(key: K, lastValue: V, newValue: V) = mWasReplaced(key, lastValue, newValue)

                    override fun wasReplaced(entries: Map<K, Pair<V, V>>) = mWasReplacedMultiple(entries)

                }
            }
        }
    }

    private var mNotifyDataChanged: () -> Unit = { default() }
    private var mNotifyDataChangedKey: (K) -> Unit = { default() }
    private var mWasAdded: (K, V) -> Unit = { _, _ -> default() }
    private var mWasAddedMultiple: (Map<K, V>) -> Unit = { default() }
    private var mWasRemoved: (K, V) -> Unit = { _, _ -> default() }
    private var mWasRemovedMultiple: (Map<K, V>) -> Unit = { default() }
    private var mWasReplaced: (K, V, V) -> Unit = { _, _, _ -> default() }
    private var mWasReplacedMultiple: (Map<K, Pair<V, V>>) -> Unit = { default() }

    private fun default() {
        if (safetyMode) throw NotImplementedError(ERROR_MSG)
    }

    /**
     * If true, all methods of [ListObserver] that has not been added by [ListObserverDSL] will throw [NotImplementedError].
     */
    var safetyMode: Boolean = true

    /**
     * @see [MapObserver.notifyDataChanged]
     */
    fun notifyDataChanged(action: () -> Unit) {
        mNotifyDataChanged = action
    }

    /**
     * @see [MapObserver.notifyDataChanged]
     */
    fun notifyDataChangedByKey(action: (key: K) -> Unit) {
        mNotifyDataChangedKey = action
    }

    /**
     * @see [MapObserver.wasAdded]
     */
    fun addedSingleItem(action: (key: K, value: V) -> Unit) {
        mWasAdded = action
    }

    /**
     * @see [MapObserver.wasAdded]
     */
    fun addedMultipleItems(action: (entries: Map<K, V>) -> Unit) {
        mWasAddedMultiple = action
    }

    /**
     * @see [MapObserver.wasRemoved]
     */
    fun removedSingleItem(action: (key: K, value: V) -> Unit) {
        mWasRemoved = action
    }

    /**
     * @see [MapObserver.wasRemoved]
     */
    fun removedMultipleItems(action: (Map<K, V>) -> Unit) {
        mWasRemovedMultiple = action
    }

    /**
     * @see [MapObserver.wasReplaced]
     */
    fun replacedSingleItem(action: (key: K, lastValue: V, newValue: V) -> Unit) {
        mWasReplaced = action
    }

    /**
     * @see [MapObserver.wasReplaced]
     */
    fun replacedMultipleItems(action: (Map<K, Pair<V, V>>) -> Unit) {
        mWasReplacedMultiple = action
    }
}