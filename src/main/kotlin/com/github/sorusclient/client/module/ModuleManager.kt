package com.github.sorusclient.client.module

import com.github.sorusclient.client.setting.SettingManager

object ModuleManager {
    val modules: MutableMap<Class<out Module>, ModuleData> = HashMap()

    fun initialize() {
        registerInternalModules()
    }

    private fun registerInternalModules() {
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