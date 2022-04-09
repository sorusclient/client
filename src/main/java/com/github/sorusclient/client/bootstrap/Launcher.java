/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap;

import com.github.sorusclient.client.Sorus;

import java.util.List;

public class Launcher {

    public static void main(String[] args) throws Exception {
        String version = args[List.of(args).indexOf("--minecraftVersion") + 1];
        BootstrapManager.loadJson(Launcher.class.getClassLoader().getResource("sorus.json"), version);

        Sorus.INSTANCE.initialize();

        var main = Class.forName("net.minecraft.client.main.Main");
        var mainMethod = main.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) args);
    }

}
