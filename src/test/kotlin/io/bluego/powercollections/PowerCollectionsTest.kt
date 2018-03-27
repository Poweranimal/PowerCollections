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
import io.bluego.powercollections.observable.list.MutableObservableList
import io.bluego.powercollections.observable.list.ObservableList
import io.bluego.powercollections.observable.list.mutableObservableListOf
import io.bluego.powercollections.observable.list.observableListOf
import io.bluego.powercollections.observable.map.MutableObservableMap
import io.bluego.powercollections.observable.map.ObservableMap
import io.bluego.powercollections.observable.map.mutableObservableMapOf
import io.bluego.powercollections.observable.map.observableMapOf
import io.bluego.powercollections.weak.*
import org.junit.Test
import kotlin.test.assertEquals

class PowerCollectionsTest {


    @Test
    fun `test biMap`() {

        val sampleMap0 = mapOf(0 to "zero")
        val sampleMap1 = mapOf(10 to "ten", 11 to "eleven")

        var mutableBiMap: MutableBiMap<Int, String> by PowerCollections.biMap()
        var biMap: BiMap<Int, String> by PowerCollections.biMap()
        var mutableMap: MutableMap<Int, String> by PowerCollections.biMap()
        var map: Map<Int, String> by PowerCollections.biMap()

        mutableBiMap.putAll(sampleMap0)
        mutableMap.putAll(sampleMap0)
        assertEquals(sampleMap0, mutableBiMap)
        assertEquals(sampleMap0, mutableMap)

        mutableBiMap = mutableBiMapOf(sampleMap1)
        biMap = biMapOf(sampleMap1)
        mutableMap = mutableMapOf(*sampleMap1.toList().toTypedArray())
        map = mapOf(*sampleMap1.toList().toTypedArray())

        assertEquals(sampleMap1, mutableBiMap)
        assertEquals(sampleMap1, biMap)
        assertEquals(sampleMap1, mutableMap)
        assertEquals(sampleMap1, map)

    }

    @Test
    fun `test boundedList`() {

        val sampleList0 = listOf("zero")
        val sampleList1 = listOf("ten", "eleven")

        var mutableBoundedList: MutableBoundedList<String> by PowerCollections.boundedList(2)
        var boundedList: BoundedList<String> by PowerCollections.boundedList(2)
        var mutableList: MutableList<String> by PowerCollections.boundedList(2)
        var list: List<String> by PowerCollections.boundedList(2)

        mutableBoundedList.addAll(sampleList0)
        mutableList.addAll(sampleList0)
        assertEquals(sampleList0, mutableBoundedList)
        assertEquals(sampleList0, mutableList)

        mutableBoundedList = mutableBoundedListOf(2, *sampleList1.toTypedArray())
        boundedList = boundedListOf(2, *sampleList1.toTypedArray())
        mutableList = mutableListOf(*sampleList1.toTypedArray())
        list = listOf(*sampleList1.toTypedArray())

        assertEquals(sampleList1, mutableBoundedList)
        assertEquals(sampleList1, boundedList)
        assertEquals(sampleList1, mutableList)
        assertEquals(sampleList1, list)
    }

    @Test
    fun `test boundedMap`() {

        val sampleMap0 = mapOf(0 to "zero")
        val sampleMap1 = mapOf(10 to "ten", 11 to "eleven")

        var mutableBoundedMap: MutableBoundedMap<Int, String> by PowerCollections.boundedMap(2)
        var boundedMap: BoundedMap<Int, String> by PowerCollections.boundedMap(2)
        var mutableMap: MutableMap<Int, String> by PowerCollections.boundedMap(2)
        var map: Map<Int, String> by PowerCollections.boundedMap(2)

        mutableBoundedMap.putAll(sampleMap0)
        mutableMap.putAll(sampleMap0)
        assertEquals(sampleMap0, mutableBoundedMap)
        assertEquals(sampleMap0, mutableMap)

        mutableBoundedMap = mutableBoundedMapOf(2, sampleMap1)
        boundedMap = boundedMapOf(2, sampleMap1)
        mutableMap = mutableMapOf(*sampleMap1.toList().toTypedArray())
        map = mapOf(*sampleMap1.toList().toTypedArray())

        assertEquals(sampleMap1, mutableBoundedMap)
        assertEquals(sampleMap1, boundedMap)
        assertEquals(sampleMap1, mutableMap)
        assertEquals(sampleMap1, map)
    }

    @Test
    fun `test observableList`() {

        val sampleList0 = listOf("zero")
        val sampleList1 = listOf("ten", "eleven")

        var mutableObservableList: MutableObservableList<String> by PowerCollections.observableList { safetyMode = false }
        var observableList: ObservableList<String> by PowerCollections.observableList { safetyMode = false }
        var mutableList: MutableList<String> by PowerCollections.observableList { safetyMode = false }
        var list: List<String> by PowerCollections.observableList { safetyMode = false }

        mutableObservableList.addAll(sampleList0)
        mutableList.addAll(sampleList0)
        assertEquals(sampleList0, mutableObservableList)
        assertEquals(sampleList0, mutableList)

        mutableObservableList = mutableObservableListOf({ safetyMode = false }, *sampleList1.toTypedArray())
        observableList = observableListOf({ safetyMode = false }, *sampleList1.toTypedArray())
        mutableList = mutableListOf(*sampleList1.toList().toTypedArray())
        list = listOf(*sampleList1.toList().toTypedArray())

        assertEquals(sampleList1, mutableObservableList)
        assertEquals(sampleList1, observableList)
        assertEquals(sampleList1, mutableList)
        assertEquals(sampleList1, list)
    }

    @Test
    fun `test observableMap`() {

        val sampleMap0 = mapOf(0 to "zero")
        val sampleMap1 = mapOf(10 to "ten", 11 to "eleven")

        var mutableObservableMap: MutableObservableMap<Int, String> by PowerCollections.observableMap { safetyMode = false }
        var observableMap: ObservableMap<Int, String> by PowerCollections.observableMap { safetyMode = false }
        var mutableMap: MutableMap<Int, String> by PowerCollections.observableMap { safetyMode = false }
        var map: Map<Int, String> by PowerCollections.observableMap { safetyMode = false }

        mutableObservableMap.putAll(sampleMap0)
        mutableMap.putAll(sampleMap0)
        assertEquals(sampleMap0, mutableObservableMap)
        assertEquals(sampleMap0, mutableMap)

        mutableObservableMap = mutableObservableMapOf({ safetyMode = false }, sampleMap1)
        observableMap = observableMapOf({ safetyMode = false }, sampleMap1)
        mutableMap = mutableMapOf(*sampleMap1.toList().toTypedArray())
        map = mapOf(*sampleMap1.toList().toTypedArray())

        assertEquals(sampleMap1, mutableObservableMap)
        assertEquals(sampleMap1, observableMap)
        assertEquals(sampleMap1, mutableMap)
        assertEquals(sampleMap1, map)
    }

    @Test
    fun `test weakCollection`() {

        val sampleList0: Collection<String?> = listOf("zero")
        val sampleList1: Collection<String?> = listOf("ten", "eleven")

        var mutableWeakCollection: MutableWeakCollection<String?> by PowerCollections.weakCollection()
        var weakCollection: WeakCollection<String?> by PowerCollections.weakCollection()
        var mutableCollection: MutableCollection<String?> by PowerCollections.weakCollection()
        var collection: Collection<String?> by PowerCollections.weakCollection()

        mutableWeakCollection.addAll(sampleList0)
        mutableCollection.addAll(sampleList0)

        assertEquals(sampleList0, mutableWeakCollection.toList())
        assertEquals(sampleList0, mutableCollection.toList())

        mutableWeakCollection = mutableWeakCollectionOf(*sampleList1.toTypedArray())
        weakCollection = weakCollectionOf(*sampleList1.toTypedArray())
        mutableCollection = mutableListOf(*sampleList1.toTypedArray())
        collection = listOf(*sampleList1.toTypedArray())

        assertEquals(sampleList1, mutableWeakCollection.toList())
        assertEquals(sampleList1, weakCollection.toList())
        assertEquals(sampleList1, mutableCollection.toList())
        assertEquals(sampleList1, collection.toList())
    }

    @Test
    fun `test weakSet`() {

        val sampleList0: Set<String?> = setOf("zero")
        val sampleList1: Set<String?> = setOf("ten", "eleven")

        var mutableWeakSet: MutableWeakSet<String?> by PowerCollections.weakSet()
        var weakSet: WeakSet<String?> by PowerCollections.weakSet()
        var mutableSet: MutableSet<String?> by PowerCollections.weakSet()
        var set: Set<String?> by PowerCollections.weakSet()

        mutableWeakSet.addAll(sampleList0)
        mutableSet.addAll(sampleList0)

        assertEquals(sampleList0, mutableWeakSet)
        assertEquals(sampleList0, mutableSet)

        mutableWeakSet = mutableWeakSetOf(*sampleList1.toTypedArray())
        weakSet = weakSetOf(*sampleList1.toTypedArray())
        mutableSet = mutableSetOf(*sampleList1.toTypedArray())
        set = setOf(*sampleList1.toTypedArray())

        assertEquals(sampleList1, mutableWeakSet.toSet())
        assertEquals(sampleList1, weakSet.toSet())
        assertEquals(sampleList1, mutableSet)
        assertEquals(sampleList1, set)
    }
}