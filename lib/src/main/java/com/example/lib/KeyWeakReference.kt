package com.example.lib

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * @author AlexisYin
 */
class KeyWeakReference<T> : WeakReference<T> {
    var key: String

    constructor(referent: T, key: String) : super(referent) {
        this.key  = key
    }

    constructor(referent: T, queue: ReferenceQueue<in T?>, key: String) : super(referent, queue) {
        this.key  = key
    }

    override fun toString(): String {
        return "KeyWeakReference(key=$key)"
    }
}