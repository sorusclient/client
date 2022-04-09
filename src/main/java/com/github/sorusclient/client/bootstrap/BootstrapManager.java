/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap;

import com.github.sorusclient.client.transform.Transformer;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BootstrapManager {

    @SneakyThrows
    public static JSONObject loadJson(URL path, String minecraftVersion) {
        var rawString = IOUtils.toString(path, StandardCharsets.UTF_8);
        var json = new JSONObject(rawString);

        if (json.has("versions")) {
            for (var entry : json.getJSONObject("versions").toMap().entrySet()) {
                if (entry.getKey().equals(minecraftVersion)) {
                    loadJson(BootstrapManager.class.getClassLoader().getResource((String) entry.getValue()), minecraftVersion);
                }
            }
        }

        var namespace = json.has("namespace") ? json.getString("namespace") : null;

        if (json.has("transformers")) {
            for (var transformer : json.getJSONArray("transformers")) {
                var transformer2 = (Class<? extends Transformer>) Class.forName((namespace != null ? namespace + "." : "") + transformer);
                addTransformer(transformer2);
            }
        }

        if (json.has("initializers")) {
            for (var initializer : json.getJSONArray("initializers")) {
                var initializer2 = ((Class<? extends Initializer>) Class.forName((namespace != null ? namespace + "." : "") + initializer)).getConstructor().newInstance();
                initializer2.initialize();
            }
        }

        return json;
    }

    public static void addTransformer(Class<? extends Transformer> transformer) {
        var classLoader = BootstrapManager.class.getClassLoader();
        try {
            classLoader.getClass().getMethod("addTransformer", Class.class).invoke(classLoader, transformer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void addURL(URL url) {
        var classLoader = BootstrapManager.class.getClassLoader();
        try {
            classLoader.getClass().getMethod("addURL", Class.class).invoke(classLoader, url);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void removeURL(URL url) {
        var classLoader = BootstrapManager.class.getClassLoader();
        try {
            classLoader.getClass().getMethod("removeURL", Class.class).invoke(classLoader, url);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

}
