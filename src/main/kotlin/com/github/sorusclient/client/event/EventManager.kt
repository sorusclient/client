package com.github.sorusclient.client.event

object EventManager {

    val consumers: MutableMap<Class<*>, MutableList<(Any) -> Unit>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> register(noinline consumer: (T) -> Unit) {
        consumers.computeIfAbsent(T::class.java as Class<*>) { ArrayList() }
            .add(consumer as (Any) -> Unit)
    }

    fun call(event: Any) {
        val consumers = consumers[event.javaClass] ?: return
        for (consumer in consumers) {
            consumer(event)
        }
    }

}