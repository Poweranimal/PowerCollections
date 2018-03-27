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

import io.bluego.powercollections.bounded.*
import io.bluego.powercollections.observable.list.ListObserverDSL
import io.bluego.powercollections.observable.list.MutableObservableList
import io.bluego.powercollections.observable.list.mutableObservableListOf
import io.bluego.powercollections.observable.map.MapObserverDSL
import io.bluego.powercollections.observable.map.MutableObservableMap
import io.bluego.powercollections.observable.map.mutableObservableMapOf
import io.bluego.powercollections.weak.MutableWeakCollection
import io.bluego.powercollections.weak.MutableWeakSet
import io.bluego.powercollections.weak.mutableWeakCollectionOf
import io.bluego.powercollections.weak.mutableWeakSetOf
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object PowerCollections {

    inline fun <reified T: List<*>> observableList(noinline observer: ListObserverDSL<*>.() -> Unit): ReadWriteProperty<Any?, T>
            = object : ReadWriteProperty<Any?, T> {

        private var mList: MutableObservableList<*> = mutableObservableListOf<Any>(observer)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return mList as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            mList = mutableObservableListOf(observer, *value.toTypedArray())
        }
    }

    inline fun <reified T: Map<*, *>> observableMap(noinline observer: MapObserverDSL<*, *>.() -> Unit): ReadWriteProperty<Any?, T>
            = object : ReadWriteProperty<Any?, T> {

        private var mMap: MutableObservableMap<*, *> = mutableObservableMapOf<Any, Any>(observer)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return mMap as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            mMap = mutableObservableMapOf(observer, value)
        }
    }

    inline fun <reified T: List<*>> boundedList(size: Int): ReadWriteProperty<Any?, T>
            = object : ReadWriteProperty<Any?, T> {

        private var mList: MutableBoundedList<*> = mutableBoundedListOf<Any>(size)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return mList as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            mList = mutableBoundedListOf(size, *value.toTypedArray())
        }
    }

    inline fun <reified T: Map<*, *>> boundedMap(size: Int): ReadWriteProperty<Any?, T>
            = object : ReadWriteProperty<Any?, T> {

        private var mMap: MutableBoundedMap<*, *> = mutableBoundedMapOf<Any, Any>(size)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return mMap as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            mMap = mutableBoundedMapOf(size, value)
        }
    }

    inline fun <reified T: Collection<*>> weakCollection(): ReadWriteProperty<Any?, T>
            = object : ReadWriteProperty<Any?, T> {

        private var mCollection: MutableWeakCollection<*> = mutableWeakCollectionOf<Any>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return mCollection as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            mCollection = mutableWeakCollectionOf(*value.toTypedArray())
        }
    }

    inline fun <reified T: Set<*>> weakSet(): ReadWriteProperty<Any?, T>
            = object : ReadWriteProperty<Any?, T> {

        private var mSet: MutableWeakSet<*> = mutableWeakSetOf<Any>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return mSet as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            mSet = mutableWeakSetOf(*value.toTypedArray())
        }
    }

    inline fun <K: Any, V: Any, reified T: Map<K, V>> biMap(): ReadWriteProperty<Any?, T>
            = object : ReadWriteProperty<Any?, T> {

        private var mMap: MutableBiMap<K, V> = mutableBiMapOf()

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return mMap as T
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            mMap = mutableBiMapOf(value)
        }
    }
}
