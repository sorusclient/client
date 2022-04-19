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
            val minecraftVersionIndex = args.indexOf("--minecraftVersion") + 1
            val version = args[minecraftVersionIndex]
            BootstrapManager.loadJson(Launcher::class.java.classLoader.getResource("sorus.json")!!, version)

            Sorus.initialize()

            val main = Class.forName("net.minecraft.client.main.Main")
            val mainMethod = main.getMethod("main", Array<String>::class.java)

            val args2 = Array<String?>(args.size - 2) { null }
            System.arraycopy(args, 0, args2, 0, minecraftVersionIndex - 1)
            System.arraycopy(args, minecraftVersionIndex + 1, args2, minecraftVersionIndex - 1, args.size - minecraftVersionIndex - 1)
            mainMethod.invoke(null, args2)
        }

    }

}