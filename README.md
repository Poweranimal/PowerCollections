# PowerCollections [![](https://jitpack.io/v/Poweranimal/PowerCollections.svg)](https://jitpack.io/#Poweranimal/PowerCollections)
**PowerCollections** is a library with a set of useful `Collections`, `Sets`, `Lists` and `Maps` primary designed for the SDK / 
API development. It's entirely written in Kotlin.

## Contents
| Name | Description |
|------|-------------|
| [BoundedList](#boundedlist) | `List` with a max size of elements
| [BoundedMap](#boundedmap) | `Map` with a max size of entries
| [ObservableList](#observablelist) | `List` that observes all content altering commands
| [ObservableMap](#observablemap) | `Map` that observes all content altering commands
| [WeakCollection](#weakcollection) | `Collection` that wraps all elements in `WeakReferences`
| [WeakSet](#weakset) | `Set` that wraps all elements in `WeakReferences`

## Samples
### BoundedList
A `List` with a max size of `n`-elements.
```kotlin
 /*
 Creates a list with a max size of 2 elements.
 */
 val boundedList: MutableBoundedList<String> by PowerCollections.boundedList(2)

 boundedList.addAll(listOf("hello", "world"))

 /*
 If the max capacity is going to be exceeded, an IndexOutOfBoundsException will be thrown.
 */
 boundedList.add("foo")

 /*
 If the max capacity is reached, additional elements can be added in the following way.
 This will remove the element at the head of the list (the eldest element) and adds the new element to the tail.  
 */
 boundedList.forceAdd("bar")
 
 println(boundedList) // [world, bar]
 
 boundedList.resize(3) // Changes the max size to 3 elements.
 
 boundedList.resize(1) // Resizing would loose elements. An IllegalStateException will be thrown.
 
 /*
 If the new max size is smaller than the current size, all elements at the head are going to be removed, until 
 the new max size is equal to the list's size.
 */
 boundedList.forceResize(1)
 
 println(boundedList) // [bar]
```
`BoundedList` extends from `List`. You can do:
```kotlin
 val mutableBoundedList: MutableBoundedList<String> by PowerCollections.boundedList(2)
  
 val boundedList: BoundedList<String> by PowerCollections.boundedList(2)
  
 val mutableList: MutableList<String> by PowerCollections.boundedList(2)
  
 val list: List<String> by PowerCollections.boundedList(2)
```
`BoundedList` supports "kotlin-like" list initialization.
```kotlin
 val mutableBoundedList = mutableBoundedList(2)
  
 val boundedList = boundedList(2)
```

### BoundedMap
A `Map` with a max size of `n`-entries.
```kotlin
 /*
 Creates a map with a max size of 2 entries.
 */
 val boundedMap: MutableBoundedMap<Int, String> by PowerCollections.boundedMap(2)

 boundedMap.putAll(mapOf(0 to "hello", 1 to "world"))

 /*
 If the max capacity is going to be exceeded, an IndexOutOfBoundsException will be thrown.
 */
 boundedMap.put(2 to "foo")

 /*
 If the max capacity is reached, additional entries can be added in the following way.
 This will remove the entry at the head of the map (the eldest entry) and adds the new entry to the tail.  
 */
 boundedMap.forcePut(3 to "bar")
 
 println(boundedMap) // {0="world", 3="bar"}
 
 boundedMap.resize(3) // Changes the max size to 3 entries.
 
 boundedMap.resize(1) // Resizing would loose entries. An IllegalStateException will be thrown.
 
 /*
 If the new max size is smaller than the current size, all entries at the head are going to be removed, until 
 the new max size is equal to the map's size.
 */
 boundedMap.forceResize(1)
 
 println(boundedMap) // {3="bar"}
```
`BoundedMap` extends from `Map`. You can do:
```kotlin
 val mutableBoundedMap: MutableBoundedMap<Int, String> by PowerCollections.boundedMap(2)
  
 val boundedMap: BoundedMap<Int, String> by PowerCollections.boundedMap(2)
  
 val mutableMap: MutableMap<Int, String> by PowerCollections.boundedMap(2)
  
 val map: Map<Int, String> by PowerCollections.boundedList(2)
```
`BoundedMap` supports "kotlin-like" map initialization.
```kotlin
 val mutableBoundedMap = mutableBoundedMap(2)
  
 val boundedMap = boundedMap(2)
```

### ObservableList
A `List` that observes all content altering commands.
```kotlin
/*
 Creates a list with a delegate that listens to any altering commands.
 You don't have to implement all listening methods. However, if a not implemented method gets triggered, 
 a NotImplementedException gets thrown. To avoid this exception, set 'safetyMode' to 'false'.
 */
 val observableList: MutableObservableList<String> by PowerCollections.observableList {
    
    //If false, no NotImplementedException gets thrown.
    safetyMode = false
    
    // Triggered for all 'add' commands.
    wasAdded { index, element ->
        println("added $element at $index")
    }
    // Triggered for all 'addAll' commands.
    wasAdded { elements ->
        println("added $elements")
    }
    // Triggered for all 'remove' commands.
    wasRemoved { index, element ->
        println("removed $element at $index")
    }
    // Triggered for all 'removeAll' commands.
    wasRemoved { elements ->
        println("removed $elements")
    }
    // Triggered for all 'set' commands.
    wasReplaced { index, oldElement, newElement ->
        println("replaced $oldElement with $newElement at $index")
    }
    // Triggered for all 'setAll' commands.
    wasReplaced { map ->
        println("Replaced $map")
    }
 }

 observableList.addAll(listOf("hello", "world")) // prints: "added [hello, world]"

 observableList.add("foo") // prints "added foo at 2"
```
You can manually inform `ObservableList`, if changes are made inside a class.
```kotlin

 data class MyClass(var tag: String)

 val observableList: MutableObservableList<MyClass> by PowerCollections.observableList {
    
    safetyMode = false
    
    notifyDataChanged {
        println("data changed")
    }
    notifyDataChangedByIndex { index ->
        println("data changed at $index")
    }
 }
 
 val myClass = MyClass("Hello World")
 
 observableList.add(myClass)
 
 myClass.tag = "foo bar"
 
 observableList.notifyDataChanged(0) // prints: "data changed at 0"
```
`ObservableList` extends from `List`. You can do:
```kotlin
 val mutableObservableList: MutableObservableList<String> by PowerCollections.observableList { }
  
 val observableList: ObservableList<String> by PowerCollections.observableList { }
  
 val mutableList: MutableList<String> by PowerCollections.observableList { }
  
 val list: List<String> by PowerCollections.observableList { }
```
`ObservableList` supports "kotlin-like" list initialization.
```kotlin
 val mutableObservableList = mutableObservableList { }
  
 val observableList = observableList { }
```
### ObservableMap
A `Map` that observes all content altering commands.
```kotlin
/*
 Creates a map with a delegate that listens to any altering commands.
 You don't have to implement all listening methods. However, if a not implemented method gets triggered, 
 a NotImplementedException gets thrown. To avoid this exception, set 'safetyMode' to 'false'.
 */
 val observableMap: MutableObservableMap<Int, String> by PowerCollections.observableMap {
    
    //If false, no NotImplementedException gets thrown.
    safetyMode = false
    
    // Triggered for all 'put' commands.
    wasAdded { key, value ->
        println("added $value at $key")
    }
    // Triggered for all 'putAll' commands.
    wasAdded { entries ->
        println("added $entries")
    }
    // Triggered for all 'remove' commands.
    wasRemoved { key, value ->
        println("removed $value at $key")
    }
    // Triggered for all 'removeAll' commands.
    wasRemoved { entries ->
        println("removed $entries")
    }
    // Triggered for all 'put' commands that are replacing.
    wasReplaced { key, oldValue, newValue ->
        println("replaced $oldValue with $newValue at $key")
    }
    // Triggered for all 'putAll' commands that are replacing.
    wasReplaced { entries ->
        println("Replaced $entries")
    }
 }

 observableMap.putAll(mapOf(0 to "hello", 1 to "world")) // prints: "added {0=hello, 1=world}"

 observableMap.put(1 to "foo") // prints "replaced world with foo at 2"
```
You can manually inform `ObservableMap`, if changes are made inside a class.
```kotlin

 data class MyClass(var tag: String)

 val observableMap: MutableObservableMap<Int, MyClass> by PowerCollections.observableMap {
    
    safetyMode = false
    
    notifyDataChanged {
        println("data changed")
    }
    notifyDataChangedByKey { key ->
        println("data changed at $key")
    }
 }
 
 val myClass = MyClass("Hello World")
 
 observableMap.put(10 to myClass)
 
 myClass.tag = "foo bar"
 
 observableMap.notifyDataChanged(10) // prints: "data changed at 10"
```
`ObservableMap` extends from `Map`. You can do:
```kotlin
 val mutableObservableMap: MutableObservableMap<Int, String> by PowerCollections.observableMap { }
  
 val observableMap: ObservableMap<Int, String> by PowerCollections.observableMap { }
  
 val mutableMap: MutableMap<Int, String> by PowerCollections.observableMap { }
  
 val map: Map<Int, String> by PowerCollections.observableMap { }
```
`ObservableMap` supports "kotlin-like" map initialization.
```kotlin
 val mutableObservableMap = mutableObservableMap { }
  
 val observableMap = observableMap { }
```

### WeakCollection
A `Collection` that wraps all elements in `WeakReferences`.
```kotlin

object MyGlobalClient {
    /*
    All classes that registered their 'MyListener' to 'MyGlobalClient' can be garbage collected, 
    if not needed anymore.
    Hence, 'MyGlobalClient' doesn't prevent the garbage collector from collecting.
    */
    val myListeners: MutableWeakCollection<MyListener> by PowerCollections.weakCollection()
    
    fun processMyInterfaces() {
    
        /*
        Only processes not garbage collected elements. 
        Garbage collected items are temporarily stored in the collections as null-values.
        Those will be automatically removed out of the collection.
        */
        myListeners.forEach { it?.doSomething() }
        
        /*
        Manually deletes all garbage collected elements (stored as null-value) from
        the collection. Normally it's not needed to call this method.
        */
        myListeners.optimze()
        
        /*
        Manually forces to delete all garbage collected elements (stored as null-value) from
        the collection. Normally it's not needed to call this method.
        */
        myListeners.forceOptimze()
    }
}
```
`WeakCollection` extends from `Collection`. You can do:
```kotlin
 val mutableWeakCollection: MutableWeakCollection<MyListener> by PowerCollections.weakCollection()
  
 val weakCollection: WeakCollection<MyListener> by PowerCollections.weakCollection()
  
 val mutableCollection: MutableCollection<MyListener> by PowerCollections.weakCollection()
  
 val collection: Collection<MyListener> by PowerCollections.weakCollection()
```
`WeakCollection` supports "kotlin-like" initialization.
```kotlin
 val mutableWeakCollection = mutableWeakCollection()
  
 val weakCollection = weakCollection()
```

### WeakSet
A `Set` that wraps all elements in `WeakReferences`.
```kotlin

object MyGlobalClient {
    /*
    All classes that registered their 'MyListener' to 'MyGlobalClient' can be garbage collected, 
    if not needed anymore.
    Hence, 'MyGlobalClient' doesn't prevent the garbage collector from collecting.
    */
    val myListeners: MutableWeakSet<MyListener> by PowerCollections.weakSet()
    
    fun processMyInterfaces() {
    
        /*
        Only processes not garbage collected elements. 
        Garbage collected items are temporarily stored in the collections as null-values.
        Those will be automatically removed out of the collection.
        */
        myListeners.forEach { it?.doSomething() }
        
        /*
        Manually deletes all garbage collected elements (stored as null-value) from
        the set. Normally it's not needed to call this method.
        */
        myListeners.optimze()
        
        /*
        Manually forces to delete all garbage collected elements (stored as null-value) from
        the set. Normally it's not needed to call this method.
        */
        myListeners.forceOptimze()
    }
}
```
`WeakSet` extends from `Set`. You can do:
```kotlin
 val mutableWeakSet: MutableWeakSet<MyListener> by PowerCollections.weakSet()
  
 val weakSet: WeakSet<MyListener> by PowerCollections.weakSet()
  
 val mutableSet: MutableSet<MyListener> by PowerCollections.weakSet()
  
 val set: Set<MyListener> by PowerCollections.weakSet()
```
`WeakSet` supports "kotlin-like" set initialization.
```kotlin
 val mutableWeakSet = mutableWeakSet()
  
 val weakSet = weakSet()
```

## Download
Add it in your root build.gradle at the end of repositories:

**Step 1.** Add the JitPack repository to your build file
```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```
**Step 2.** Add the dependency
```groovy
dependencies {
	implementation 'com.github.Poweranimal:PowerCollections:0.1.0'
}
```

## License
See [here](LICENSE.md).
```
 MIT License
 
 Copyright (c) 2018 Felix Pröhl
 
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
```