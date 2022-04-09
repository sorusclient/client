/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap.loader;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class ClassLoader extends URLClassLoader {

    private final List<Object> transformers = new ArrayList<>();
    private final Method canTransformMethod;
    private final Method transformMethod;
    private final List<String> exclusions = new ArrayList<>();
    private final List<URL> urls = new ArrayList<>();
    private final java.lang.ClassLoader realParent = ClassLoader.class.getClassLoader();

    @SneakyThrows
    public ClassLoader() {
        super(new URL[0], null);

        exclusions.add("java.");
        exclusions.add("jdk.");
        exclusions.add("javax.");
        exclusions.add("sun.");
        exclusions.add("com.sun.");
        exclusions.add("org.xml.");
        exclusions.add("org.w3c.");
        exclusions.add("org.apache.");
        exclusions.add("org.slf4j.");
        exclusions.add("com.mojang.blocklist.");
        exclusions.add("com.github.sorusclient.client.bootstrap.transformer.Transformer");

        canTransformMethod = this.loadClass("com.github.sorusclient.client.bootstrap.transformer.Transformer").getMethod("canTransform", String.class);
        transformMethod = this.loadClass("com.github.sorusclient.client.bootstrap.transformer.Transformer").getMethod("transform", String.class, byte[].class);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (val exclusion : exclusions) {
            if (name.startsWith(exclusion)) {
                try {
                    return realParent.loadClass(name);
                } catch (Exception ignored) {

                }
            }
        }

        var clazz = super.findLoadedClass(name);
        if (clazz == null) {
            val data = getModifiedBytes(name);
            clazz = super.defineClass(name, data, 0, data.length);
        }
        return clazz;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        val clazz = this.loadClass(name);
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }

    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    private byte[] getModifiedBytes(String name) throws ClassNotFoundException {
            var data = loadClassData(name);

            if (data.length == 0) {
                throw new ClassNotFoundException(name);
            }

            for (val transformer : getTransformers()) {
                val formattedName = name.replace(".", "/");

                if ((boolean) canTransformMethod.invoke(transformer, formattedName)) {
                    data = (byte[]) transformMethod.invoke(transformer, formattedName, data);
                }
            }

            if (data.length == 0) {
                throw new ClassNotFoundException(name);
            }
            return data;
    }

    private List<Object> getTransformers() {
        return transformers;
    }

    @SneakyThrows
    private byte[] loadClassData(String className) {
        val resources = getResources(className.replace(".", "/") + ".class");
        final List<byte[]> datas = new ArrayList<>();

        while (resources.hasMoreElements()) {
            val resource = resources.nextElement();
            datas.add(IOUtils.toByteArray(resource));
        }

        if (datas.size() > 0) {
            return datas.toArray(new byte[0][])[0];
        } else {
            return new byte[0];
        }
    }

    @SneakyThrows
    @Override
    public Enumeration<URL> getResources(String name) {
        final List<URL> parentResources  = Collections.list(realParent.getResources(name));
        final List<URL> filteredURLs = new ArrayList<>(parentResources);
        for (var pathUrl : Collections.list(findResources(name))) {
            for (var url : urls) {
                if (pathUrl.getFile().contains(url.getFile())) {
                    filteredURLs.add(pathUrl);
                }
            }
        }

        return Collections.enumeration(filteredURLs);
    }

    @SneakyThrows
    @Override
    public URL getResource(String name) {
        val resources = getResources(name);
        if (resources.hasMoreElements()) {
            return resources.nextElement();
        }
        return realParent.getResource(name);
    }

    @Override
    public void addURL(URL url) {
        urls.add(url);
        super.addURL(url);
    }

    public void removeURL(URL url) {
        urls.remove(url);
    }

    @SneakyThrows
    public void addTransformer(Class<?> transformer) {
        this.transformers.add(transformer.getConstructor().newInstance());
    }

}