package com.github.sorusclient.client.feature.impl.enhancements.v1_18_1

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.v1_18_1.Util
import com.github.sorusclient.client.feature.impl.enhancements.ISettingsLoader
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.option.GameOptions
import v1_18_1.net.minecraft.client.util.InputUtil

class SettingsLoader: ISettingsLoader {

    override fun save(cached: Map<String, Any>): Map<String, Any> {
        val map = HashMap(cached)

        val options = MinecraftClient.getInstance().options

        map["sensitivity"] = options.mouseSensitivity

        for (i in 1..9) {
            map["hotbar_$i"] = Util.getKey(InputUtil.fromTranslationKey(options.keysHotbar[i - 1].boundKeyTranslationKey).code)
        }

        map["chat"] = Util.getKey(InputUtil.fromTranslationKey(options.keyChat.boundKeyTranslationKey).code)
        map["command"] = Util.getKey(InputUtil.fromTranslationKey(options.keyCommand.boundKeyTranslationKey).code)

        return map
    }

    override fun load(map: Map<String, Any>) {
        val options = EnhancementsHook.options as GameOptions

        if (map.containsKey("sensitivity")) {
            options.mouseSensitivity = map["sensitivity"] as Double
        }

        for (i in 1..9) {
            if (map.containsKey("hotbar_$i")) {
                options.keysHotbar[i - 1].setBoundKey(InputUtil.fromKeyCode(Util.getKeyCode(map["hotbar_$i"] as Key), 0))
            }
        }

        if (map.containsKey("chat")) {
            options.keyChat.setBoundKey(InputUtil.fromKeyCode(Util.getKeyCode(map["chat"] as Key), 0))
        }
        if (map.containsKey("command")) {
            options.keyCommand.setBoundKey(InputUtil.fromKeyCode(Util.getKeyCode(map["command"] as Key), 0))
        }
    }

}