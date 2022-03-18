package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.v1_8_9.Util
import com.github.sorusclient.client.feature.impl.enhancements.ISettingsLoader
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.options.GameOptions

class SettingsLoader: ISettingsLoader {

    override fun save(cached: Map<String, Any>): Map<String, Any> {
        val map = HashMap(cached)

        /*val optionsTxt = File("options.txt")
        val lines = FileUtils.readLines(optionsTxt, StandardCharsets.UTF_8)
        for (line in lines) {
            val split = line.split(":")
            when (split[0]) {
                "mouseSensitivity" -> {
                    map["sensitivity"] = split[1].toDouble()
                }
                "key_key.hotbar.1" -> {
                    map["hotbar_1"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.2" -> {
                    map["hotbar_2"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.3" -> {
                    map["hotbar_3"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.4" -> {
                    map["hotbar_4"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.5" -> {
                    map["hotbar_5"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.6" -> {
                    map["hotbar_6"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.7" -> {
                    map["hotbar_7"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.8" -> {
                    map["hotbar_8"] = Util.getKey(split[1].toInt())
                }
                "key_key.hotbar.9" -> {
                    map["hotbar_9"] = Util.getKey(split[1].toInt())
                }
                "key_key.chat" -> {
                    map["chat"] = Util.getKey(split[1].toInt())
                }
                "key_key.command" -> {
                    map["command"] = Util.getKey(split[1].toInt())
                }
            }
        }*/
        val options = MinecraftClient.getInstance().options

        map["sensitivity"] = options.sensitivity

        for (i in 1..9) {
            map["hotbar_$i"] = Util.getKey(options.keysHotbar[i - 1].code)
        }

        map["chat"] = Util.getKey(options.keyChat.code)
        map["command"] = Util.getKey(options.keyCommand.code)

        return map
    }

    override fun load(map: Map<String, Any>) {
        val options = EnhancementsHook.options as GameOptions

        if (map.containsKey("sensitivity")) {
            options.sensitivity = (map["sensitivity"] as Double).toFloat()
        }

        for (i in 1..9) {
            if (map.containsKey("hotbar_$i")) {
                options.keysHotbar[i - 1].code = Util.getKeyCode(map["hotbar_$i"] as Key)
            }
        }

        if (map.containsKey("chat")) {
            options.keyChat.code = Util.getKeyCode(map["chat"] as Key)
        }
        if (map.containsKey("command")) {
            options.keyCommand.code = Util.getKeyCode(map["command"] as Key)
        }
    }

}