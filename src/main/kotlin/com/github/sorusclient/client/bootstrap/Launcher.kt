/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap

import com.github.sorusclient.client.Sorus

class Launcher {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val version = args[args.indexOf("--minecraftVersion") + 1]
            BootstrapManager.loadJson(Launcher::class.java.classLoader.getResource("sorus.json")!!, version)

            Sorus.initialize()

            val main = Class.forName("net.minecraft.client.main.Main")
            val mainMethod = main.getMethod("main", Array<String>::class.java)
            mainMethod.invoke(null, args)
        }
    }

}