/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client;

import java.util.HashMap;
import java.util.Map;

public class InterfaceManager {

    private static final Map<Class<?>, Object> interfaces = new HashMap<>();

    public static void register(Object implementor) {
        interfaces.put(implementor.getClass().getInterfaces()[0], implementor);
    }

    public static <T> T get(Class<T> clazz) {
        return (T) interfaces.get(clazz);
    }

}
