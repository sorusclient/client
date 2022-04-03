package com.github.sorusclient.client.bootstrap

import com.github.sorusclient.client.Sorus
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class Launcher {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val version = args[args.indexOf("--minecraftVersion") + 1]
            BootstrapManager.loadJson("sorus.json", version)

            Sorus.initialize()

            val main = Class.forName("net.minecraft.client.main.Main")
            val mainMethod = main.getMethod("main", Array<String>::class.java)
            mainMethod.invoke(null, args)
        }
    }

}