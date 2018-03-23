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

import io.bluego.powercollections.observable.map.adapter.ObservableMapAdapter
import java.util.function.BiFunction

interface ObservableMap<K, out V>: Map<K, V>

/**
 * This [MutableMap] keeps track of adding, removing and replacing items in this map.
 * The following actions are tracked by the [MapObserver]:
 * * [MutableObservableMap.put]
 * * [MutableObservableMap.putIfAbsent]
 * * [MutableObservableMap.putAll]
 * * [MutableObservableMap.remove]
 * * [MutableObservableMap.replace]
 * * [MutableObservableMap.replaceAll]
 */
interface MutableObservableMap<K, V>: ObservableMap<K, V>, MutableMap<K, V>, Observable<K, V> {

    fun runSilentChanges(silentChanges: MutableObservableMap<K, V>.() -> Unit)
}

abstract class AbstractObservableMap<K: Any, V: Any> protected constructor(
        private val mMutableMap: MutableMap<K, V>,
        final override var observer: MapObserver<K, V>)
    : MutableObservableMap<K, V>, MutableMap<K, V> by mMutableMap
{

    private val mAdapter = ObservableMapAdapter(observer, mMutableMap)

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
            = mAdapter.getEntries(mMutableMap::entries)

    override val keys: MutableSet<K>
            = mAdapter.getKeys(mMutableMap::keys)

    override val values: MutableCollection<V>
            = mAdapter.getValues(mMutableMap::values)

    override fun runSilentChanges(silentChanges: MutableObservableMap<K, V>.() -> Unit) {
        mAdapter.runSilentChanges { silentChanges() }
    }

    override fun notifyDataChanged() = mAdapter.notifyDataChanged()

    override fun notifyDataChanged(key: K) = mAdapter.notifyDataChanged(key)

    override fun clear() = mAdapter.clear(mMutableMap::clear)

    override fun put(key: K, value: V): V? = mAdapter.put(key, value, mMutableMap::put)

    override fun putAll(from: Map<out K, V>) = mAdapter.putAll(from, mMutableMap::putAll)

    override fun remove(key: K): V? = mAdapter.remove(key, mMutableMap::remove)

    override fun remove(key: K, value: V): Boolean = mAdapter.remove(key, value, mMutableMap::remove)

    override fun replace(p0: K, p1: V, p2: V): Boolean
            = mAdapter.replace(p0, p1, p2, mMutableMap::replace)

    override fun putIfAbsent(p0: K, p1: V): V? = mAdapter.putIfAbsent(p0, p1, mMutableMap::putIfAbsent)

    override fun replace(p0: K, p1: V): V? = mAdapter.replace(p0, p1, mMutableMap::replace)

    override fun replaceAll(p0: BiFunction<in K, in V, out V>)
            = mAdapter.replaceAll(p0, mMutableMap::replaceAll)
}

class HashObservableMap<K: Any, V: Any>(observer: MapObserver<K, V>, capacity: Int = 16)
    : AbstractObservableMap<K, V>(HashMap(capacity), observer)
{
    companion object {
        fun <K: Any, V: Any> create(observer: MapObserver<K, V>, map: Map<K, V>)
                : HashObservableMap<K, V>
        {
            val observerMap = HashObservableMap(observer)
            observerMap.putAll(map)
            return observerMap
        }
    }
}

/**
 * Creates an empty [MutableObservableMap].
 *
 * @return Emtpy [MutableObservableMap]
 */
fun <K: Any, V: Any> mutableObservableMapOf(observer: MapObserver<K, V>): MutableObservableMap<K, V> =
        HashObservableMap(observer)

/**
 * @see [mutableObservableMapOf]
 * @see [MapObserverDSL]
 */
fun <K: Any, V: Any> mutableObservableMapOf(observer: MapObserverDSL<K, V>.() -> Unit): MutableObservableMap<K, V>
        = mutableObservableMapOf(MapObserverDSL.create(observer))

/**
 * Creates an [MutableObservableMap] with [elements]s.
 *
 * @return [MutableObservableMap] with [elements]s
 */
fun <K: Any, V: Any> mutableObservableMapOf(observer: MapObserver<K, V>,
                                            vararg elements: Pair<K, V>): MutableObservableMap<K, V>
        = HashObservableMap.create(observer, mapOf(*elements))

/**
 * @see [mutableObservableMapOf]
 * @see [MapObserverDSL]
 */
fun <K: Any, V: Any> mutableObservableMapOf(observer: MapObserverDSL<K, V>.() -> Unit,
                                            vararg elements: Pair<K, V>): MutableObservableMap<K, V>
        = mutableObservableMapOf(MapObserverDSL.create(observer), *elements)

/**
 * Creates an empty [ObservableMap].
 *
 * @return Empty [ObservableMap]
 */
fun <K: Any, V: Any> observableMapOf(observer: MapObserver<K, V>): ObservableMap<K, V>
        = mutableObservableMapOf(observer)

/**
 * @see [observableMapOf]
 * @see [MapObserverDSL]
 */
fun <K: Any, V: Any> observableMapOf(observer: MapObserverDSL<K, V>.() -> Unit): ObservableMap<K, V>
        = mutableObservableMapOf(observer)

/**
 * Creates an [ObservableMap] with [elements]s.
 *
 * @return [ObservableMap] with [elements]s
 */
fun <K: Any, V: Any> observableMapOf(observer: MapObserver<K, V>,
                                     vararg elements: Pair<K, V>): ObservableMap<K, V> =
        mutableObservableMapOf(observer, *elements)

/**
 * @see [observableMapOf]
 * @see [MapObserverDSL]
 */
fun <K: Any, V: Any> observableMapOf(observer: MapObserverDSL<K, V>.() -> Unit,
                                     vararg elements: Pair<K, V>): ObservableMap<K, V> =
        mutableObservableMapOf(observer, *elements)