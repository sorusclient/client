package com.github.sorusclient.client.feature.impl.enhancements.v1_18_1

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.v1_18_1.Util
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import com.github.sorusclient.client.feature.impl.enhancements.ISettingsLoader
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.option.GameOptions
import v1_18_1.net.minecraft.client.option.GraphicsMode
import v1_18_1.net.minecraft.client.option.KeyBinding
import v1_18_1.net.minecraft.client.util.InputUtil

class SettingsLoader: ISettingsLoader {

    override fun save(cached: Map<String, Any>): Map<String, Any> {
        val map = HashMap(cached)

        val options = MinecraftClient.getInstance().options

        map["sensitivity"] = options.mouseSensitivity
        map["graphics"] = Enhancements.Graphics.values()[options.graphicsMode.id]
        map["autoJump"] = options.autoJump

        for (i in 1..9) {
            map["hotbar_$i"] = getKey(options.keysHotbar[i - 1])
        }

        map["chat"] = getKey(options.keyChat)
        map["command"] = getKey(options.keyCommand)
        map["sprint"] = getKey(options.keySprint)
        map["sneak"] = getKey(options.keySneak)
        map["perspective"] = getKey(options.keyTogglePerspective)
        map["socialInteractions"] = getKey(options.keySocialInteractions)

        return map
    }

    private fun getKey(keyBinding: KeyBinding): Key {
        return Util.getKey(InputUtil.fromTranslationKey(keyBinding.boundKeyTranslationKey).code)
    }

    override fun load(map: Map<String, Any>) {
        val options = EnhancementsHook.options as GameOptions

        if (map.containsKey("sensitivity")) {
            options.mouseSensitivity = map["sensitivity"] as Double
        }

        if (map.containsKey("graphics")) {
            options.graphicsMode = GraphicsMode.byId((map["graphics"] as Enhancements.Graphics).ordinal)
        }
        if (map.containsKey("autoJump")) {
            options.autoJump = map["autoJump"] as Boolean
        }

        for (i in 1..9) {
            setKey(options.keysHotbar[i - 1], map, "hotbar_$i")
        }

        setKey(options.keyChat, map, "chat")
        setKey(options.keyCommand, map, "command")
        setKey(options.keySprint, map, "sprint")
        setKey(options.keySneak, map, "sneak")
        setKey(options.keyTogglePerspective, map, "perspective")
        setKey(options.keySocialInteractions, map, "socialInteractions")
    }

    private fun setKey(keyBinding: KeyBinding, settings: Map<String, Any>, id: String) {
        if (settings.containsKey(id)) {
            keyBinding.setBoundKey(InputUtil.fromKeyCode(Util.getKeyCode(settings[id] as Key), 0))
        }
    }

}