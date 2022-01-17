package com.github.sorusclient.client.event

import java.util.function.Consumer

object EventManager {

    private val consumers: MutableMap<Class<Event>, MutableList<Consumer<Event>>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    fun <T : Event?> register(eventClass: Class<T>, consumer: Consumer<T>) {
        consumers.computeIfAbsent(eventClass as Class<Event>) { ArrayList() }
            .add(consumer as Consumer<Event>)
    }

    fun call(event: Event) {
        val consumers = consumers[event.javaClass]
            ?: return
        for (consumer in consumers) {
            consumer.accept(event)
        }
    }

}