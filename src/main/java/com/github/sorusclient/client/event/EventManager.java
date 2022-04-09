/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.event;

import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager {

    private static final Map<Class<?>, List<Consumer<Object>>> consumers = new HashMap<>();

    public static <T> void register(Class<T> clazz, Consumer<T> consumer) {
        consumers.computeIfAbsent(clazz, k -> new ArrayList<>()).add((Consumer<Object>) consumer);
    }

    public static void call(Object event) {
        val consumers1 = consumers.get(event.getClass());
        if (consumers1 == null) return;

        for (val consumer : consumers1) {
            consumer.accept(event);
        }
    }

}
