package com.example.lib

import java.lang.ref.ReferenceQueue
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author AlexisYin
 */
class Watcher {
    //监控列表
    private val watchedReferences = mutableMapOf<String, KeyWeakReference<Any?>>()

    //保留列表，保留列表中出现的引用，说明它是泄漏的对象
    private val retainedReferences = mutableMapOf<String, KeyWeakReference<Any?>>()

    //当被监控的对象被GC回收后，对应的弱引用会被加入到引用队列
    private val queue = ReferenceQueue<Any?>()

    fun watch(obj: Any?) {
        //生成一个UUID key，便于从列表中取出相应的引用
        val key = UUID.randomUUID().toString()
        //建立弱引用关系，并关联引用队列
        val reference = KeyWeakReference(obj, queue, key)
        //加入到监控列表做登记
        watchedReferences[key] = reference
        println("监控:$reference")

        //过1s，去引用列表中查看，如果在引用列表中找到了，说明GC回收成功
        //如果没有，将引用加入到保留列表
        Executors.newSingleThreadExecutor().execute{
            Thread.sleep(1000)
            moveToRetained(key)
        }
    }

    private fun moveToRetained(key: String) {
        var ref: KeyWeakReference<Any?>? = null
        do {
            queue.poll()?.also {
                ref = it as KeyWeakReference<Any?> }
            //回收成功，没有发生内存泄漏
            ref?.key?.let {
                watchedReferences.remove(it)
                retainedReferences.remove(it)
                println("回收成功:$ref")
            }
            ref = null
        } while (ref != null)
        //如果仍然存在于监控列表，将弱引用转入保留列表
        //回收不成功，发生了内存泄漏

        watchedReferences.remove(key)?.also {
            println("发生了内存泄漏:$it")
            retainedReferences[key] = it
        }
    }

    fun getRetainedReferences(): MutableMap<String, KeyWeakReference<Any?>>{
        return retainedReferences
    }
}