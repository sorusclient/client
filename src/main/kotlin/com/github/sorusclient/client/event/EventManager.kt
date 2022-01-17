package com.github.sorusclient.client.event

import java.util.function.Consumer

object EventManager {

    val consumers: MutableMap<Class<Event>, MutableList<Consumer<Event>>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Event> register(consumer: Consumer<T>) {
        consumers.computeIfAbsent(T::class.java as Class<Event>) { ArrayList() }
            .add(consumer as Consumer<Event>)
    }

    fun call(event: Event) {
        val consumers = consumers[event.javaClass] ?: return
        for (consumer in consumers) {
            consumer.accept(event)
        }
    }

}