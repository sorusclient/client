package com.github.sorusclient.client

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IAdapter
import com.github.sorusclient.client.adapter.event.GetClientBrandEvent
import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.hud.HUDManager
import com.github.sorusclient.client.plugin.PluginManager
import com.github.sorusclient.client.server.ServerIntegrationManager
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.ui.UserInterface
import com.github.sorusclient.client.ui.framework.ContainerRenderer
import com.github.sorusclient.client.ui.theme.ThemeManager
import com.github.sorusclient.client.util.keybind.KeyBindManager
import java.util.*

class Sorus : Listener {

    override fun run() {
        GlassLoader.getInstance().runHooks("post-initialize")
        val sorus = Sorus()
        GlassLoader.getInstance().registerAPI(sorus)
        sorus.initialize()
    }

    private val components: MutableMap<Class<*>, Any> = HashMap()

    private fun initialize() {
        this.register(ContainerRenderer)
        this.register(EventManager)
        this.register(FeatureManager)
        this.register(HUDManager)
        this.register(IAdapter::class.java, GlassLoader.getInstance().getInterface(IAdapter::class.java))
        this.register(KeyBindManager)
        this.register(PluginManager)
        this.register(ServerIntegrationManager)
        this.register(SettingManager)
        this.register(ThemeManager)
        this.register(UserInterface)

        PluginManager.findPlugins()

        HUDManager.initialize()
        ContainerRenderer.initialize()

        EventManager.register<InitializeEvent> {
            val adapter = AdapterManager.getAdapter()
            adapter.setDisplayTitle("Sorus | " + AdapterManager.getAdapter().version)
            adapter.setDisplayIcon("sorus/icon_16x.png", "sorus/icon_32x.png")
        }

        UserInterface.initialize()
        ThemeManager.initialize()

        SettingManager.initialize()

        EventManager.register<GetClientBrandEvent> { event ->
            event.brand = "sorus"
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            SettingManager.saveCurrent()
        })
    }

    fun register(component: Any) {
        this.register(component.javaClass, component)
    }

    fun register(clazz: Class<*>, component: Any) {
        components[clazz] = component
    }

    companion object {
        val instance: Sorus
            get() = Objects.requireNonNull(GlassLoader.getInstance().getAPI(Sorus::class.java))
    }

}