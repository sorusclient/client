package com.github.sorusclient.client.feature.impl.enhancements.v1_18_2

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.v1_18_2.Util
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import com.github.sorusclient.client.feature.impl.enhancements.ISettingsLoader
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.option.AttackIndicator
import v1_18_2.net.minecraft.client.option.GameOptions
import v1_18_2.net.minecraft.client.option.GraphicsMode
import v1_18_2.net.minecraft.client.option.KeyBinding
import v1_18_2.net.minecraft.client.util.InputUtil

class SettingsLoader: ISettingsLoader {

    override fun save(cached: Map<String, Any>): Map<String, Any> {
        val map = HashMap(cached)

        val options = MinecraftClient.getInstance().options

        map["sensitivity"] = options.mouseSensitivity
        map["graphics"] = Enhancements.Graphics.values()[options.graphicsMode.id]
        map["autoJump"] = options.autoJump
        map["attackIndicator"] = Enhancements.AttackIndicator.values()[options.attackIndicator.id]
        map["skipMultiplayerWarning"] = options.skipMultiplayerWarning
        map["rawInput"] = options.rawMouseInput

        for (i in 1..9) {
            map["hotbar_$i"] = getKey(options.hotbarKeys[i - 1])
        }

        map["chat"] = getKey(options.chatKey)
        map["command"] = getKey(options.commandKey)
        map["sprint"] = getKey(options.sprintKey)
        map["sneak"] = getKey(options.sneakKey)
        map["perspective"] = getKey(options.togglePerspectiveKey)
        map["socialInteractions"] = getKey(options.socialInteractionsKey)

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
        if (map.containsKey("rawInput")) {
            options.rawMouseInput = map["rawInput"] as Boolean
        }
        if (map.containsKey("attackIndicator")) {
            options.attackIndicator = AttackIndicator.byId((map["attackIndicator"] as Enhancements.AttackIndicator).ordinal)
        }
        if (map.containsKey("skipMultiplayerWarning")) {
            options.skipMultiplayerWarning = map["skipMultiplayerWarning"] as Boolean
        }

        for (i in 1..9) {
            setKey(options.hotbarKeys[i - 1], map, "hotbar_$i")
        }

        setKey(options.chatKey, map, "chat")
        setKey(options.commandKey, map, "command")
        setKey(options.sprintKey, map, "sprint")
        setKey(options.sneakKey, map, "sneak")
        setKey(options.togglePerspectiveKey, map, "perspective")
        setKey(options.socialInteractionsKey, map, "socialInteractions")
    }

    private fun setKey(keyBinding: KeyBinding, settings: Map<String, Any>, id: String) {
        if (settings.containsKey(id)) {
            keyBinding.setBoundKey(InputUtil.fromKeyCode(Util.getKeyCode(settings[id] as Key), 0))
        }
    }

}