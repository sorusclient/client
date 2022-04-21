/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.v1_8_9.Util
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import com.github.sorusclient.client.feature.impl.enhancements.ISettingsLoader
import com.github.sorusclient.client.toIdentifier
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.options.GameOptions
import v1_8_9.net.minecraft.client.options.KeyBinding
import v1_8_9.net.minecraft.util.collection.IntObjectStorage
import java.lang.reflect.Field

@Suppress("UNUSED")
class SettingsLoader: ISettingsLoader {

    override fun save(cached: Map<String, Any>): Map<String, Any> {
        val map = HashMap(cached)
        val options = MinecraftClient.getInstance().options

        map["sensitivity"] = options.sensitivity

        if (!(options.fancyGraphics && (!map.containsKey("graphics") || (map["graphics"] as Enhancements.Graphics).ordinal > 0))) {
            map["graphics"] = if (options.fancyGraphics) { Enhancements.Graphics.FANCY } else { Enhancements.Graphics.FAST }
        }

        for (i in 1..9) {
            map["hotbar_$i"] = getKey(options.keysHotbar[i - 1])
        }

        map["chat"] = getKey(options.keyChat)
        map["command"] = getKey(options.keyCommand)
        map["sprint"] = getKey(options.keySprint)
        map["sneak"] = getKey(options.keySneak)
        map["perspective"] = getKey(options.keyTogglePerspective)

        return map
    }

    private fun getKey(keyBinding: KeyBinding): Key {
        return Util.getKey(keyBinding.code)
    }

    override fun load(map: Map<String, Any>) {
        val options = EnhancementsHook.options as GameOptions

        if (map.containsKey("sensitivity")) {
            options.sensitivity = (map["sensitivity"] as Double).toFloat()
        }

        if (map.containsKey("graphics")) {
            options.fancyGraphics = (map["graphics"] as Enhancements.Graphics).ordinal > 0
        }

        for (i in 1..9) {
            setKey(options.keysHotbar[i - 1], map, "hotbar_$i")
        }

        setKey(options.keyChat, map, "chat")
        setKey(options.keyCommand, map, "command")
        setKey(options.keySprint, map, "sprint")
        setKey(options.keySneak, map, "sneak")
        setKey(options.keyTogglePerspective, map, "perspective")
    }

    private val keyMap: Field = run {
        val keyMap = "v1_8_9/net/minecraft/client/options/KeyBinding#KEY_MAP".toIdentifier()
        val field = KeyBinding::class.java.getDeclaredField(keyMap.fieldName)
        field.isAccessible =  true
        field
    }

    private fun setKey(keyBinding: KeyBinding, settings: Map<String, Any>, id: String) {
        if (settings.containsKey(id)) {
            keyBinding.code = Util.getKeyCode(settings[id] as Key)
            (keyMap.get(null) as IntObjectStorage<KeyBinding>).set(keyBinding.code, keyBinding)
        }
    }

}