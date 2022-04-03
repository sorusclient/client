package com.github.sorusclient.client.event

fun Any.call() {
    EventManager.call(this)
}