package com.github.sorusclient.client.module

import com.github.sorusclient.client.module.impl.autogg.AutoGG
import com.github.sorusclient.client.module.impl.blockoverlay.BlockOverlay
import com.github.sorusclient.client.module.impl.enhancements.Enhancements
import com.github.sorusclient.client.module.impl.environmentchanger.EnvironmentChanger
import com.github.sorusclient.client.module.impl.fullbright.FullBright
import com.github.sorusclient.client.module.impl.itemphysics.ItemPhysics
import com.github.sorusclient.client.module.impl.oldanimations.OldAnimations
import com.github.sorusclient.client.module.impl.perspective.Perspective
import com.github.sorusclient.client.module.impl.togglesprintsneak.ToggleSprintSneak
import com.github.sorusclient.client.module.impl.zoom.Zoom
import com.github.sorusclient.client.setting.SettingManager

object ModuleManager {
    val modules: MutableMap<Class<out Module>, ModuleData> = HashMap()

    fun initialize() {
        registerInternalModules()
    }

    private fun registerInternalModules() {
        register(AutoGG(), "AutoGG", "test")
        register(BlockOverlay(), "Block Overlay", "test")
        register(Enhancements(), "Enhancements", "test")
        register(EnvironmentChanger(), "Environment Changer", "test")
        register(FullBright(), "Fullbright", "test")
        register(ItemPhysics(), "Item Physics", "test")
        register(OldAnimations(), "Old Animations", "test")
        register(Perspective(), "Perspective", "test")
        register(ToggleSprintSneak(), "Toggle Sprint & Sneak", "test")
        register(Zoom(), "Zoom", "test")
    }

    fun register(module: Module, name: String, description: String) {
        modules[module.javaClass] =
            ModuleData(module, name, description)
        SettingManager.register(module)
    }

    inline fun <reified T: Module> get(): T {
        return modules[T::class.java]!!.module as T
    }

    operator fun get(id: String): Module? {
        val optionalModuleData = modules.values.stream().filter { moduleData: ModuleData -> moduleData.module.id == id }
            .findFirst()
        return optionalModuleData.map { obj: ModuleData -> obj.module }.orElse(null)
    }

}