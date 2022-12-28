package com.example.lib

fun main() {
    val watcher = Watcher()
    var obj : Any? = Object()
    watcher.watch(obj)

    obj = null
    Runtime.getRuntime().gc()
    Thread.sleep(2000)
    watcher.getRetainedReferences().forEach { key, reference ->
        println("泄漏对象-key:$key, reference:$reference, obj:${reference.get()}")
    }
}