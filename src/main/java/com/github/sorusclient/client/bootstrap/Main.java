/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap;

import com.github.sorusclient.client.bootstrap.loader.ClassLoader;

public class Main {

    public static void main(String[] args) throws Exception {
        var classLoader = new ClassLoader();
        var wrapperClass = classLoader.loadClass("com.github.sorusclient.client.bootstrap.Launcher");
        var mainMethod = wrapperClass.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) args);
    }

}
