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

package io.bluego.powercollections.weak

import io.bluego.powercollections.CollectionTest
import io.bluego.powercollections.assertSize
import org.junit.Test
import kotlin.test.assertEquals

@Suppress("PropertyName")
open class WeakCollectionTest: CollectionTest<String?>() {

    override val `collection of 3 different items`: Collection<String>
        get() = listOf("zero", "one", "two")

    override val `collection of 4 different items`: Collection<String>
        get() = listOf("_zero", "_one", "_two", "_three")

    override val `get collection to test`: () -> MutableCollection<String?>
        get() = { mutableWeakCollectionOf() }

    override fun `test addAll`() {
        super.`test addAll`()
    }

    @Test
    fun `test addAll gc`() {

        val collection = mutableWeakCollectionOf<DummyClass>()

        val persistentClasses = listOf(DummyClass("persistent_0"), DummyClass("persistent_1"))

        val notPersistentClasses = { listOf(DummyClass("not_persistent_0"), DummyClass("not_persistent_1")) }

        collection.addAll(persistentClasses)

        collection.addAll(notPersistentClasses())

        collection.assertSize(4)

        assertEquals(persistentClasses + notPersistentClasses(), collection.toList())

        System.gc()

        collection.assertSize(4)

        assertEquals(persistentClasses + listOf(null, null), collection.toList())

        collection.forceOptimize()

        assertEquals(persistentClasses, collection.toList())

    }

    override fun `test add`() {
        super.`test add`()
    }

    @Test
    fun `test add gc`() {

        val collection = mutableWeakCollectionOf<DummyClass>()

        val persistentClass = DummyClass("persistent")

        val notPersistentClass = { DummyClass("not_persistent") }

        collection.add(notPersistentClass())

        collection.add(persistentClass)

        collection.assertSize(2)

        assertEquals(listOf(notPersistentClass(), persistentClass), collection.toList())

        System.gc()

        collection.forceOptimize()

        assertEquals(listOf(persistentClass), collection.toList())
    }

    override fun `test iterator`() {
        super.`test iterator`()
    }
}