package com.github.sorusclient.client.event;

import java.util.*;
import java.util.function.Consumer;

public class EventManager {

    private final Map<Class<Event>, List<Consumer<Event>>> consumers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> void register(Class<T> eventClass, Consumer<T> consumer) {
        this.consumers.computeIfAbsent((Class<Event>) eventClass, k -> new ArrayList<>()).add((Consumer<Event>) consumer);
    }

    public void call(Event event) {
        List<Consumer<Event>> consumers = this.consumers.get(event.getClass());
        if (consumers == null) {
            return;
        }

        for (Consumer<Event> consumer : consumers) {
            consumer.accept(event);
        }
    }

}
