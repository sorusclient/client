package com.github.sorusclient.client

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IAdapter
import com.github.sorusclient.client.adapter.event.GetClientBrandEvent
import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.hud.HUDManager
import com.github.sorusclient.client.notification.NotificationManager
import com.github.sorusclient.client.plugin.PluginManager
import com.github.sorusclient.client.server.ServerIntegrationManager
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.social.SocialManager
import com.github.sorusclient.client.ui.UserInterface
import com.github.sorusclient.client.ui.framework.ContainerRenderer
import com.github.sorusclient.client.ui.theme.ThemeManager
import com.github.sorusclient.client.util.keybind.KeyBindManager
import com.github.sorusclient.client.websocket.WebSocketManager
import java.util.*

object Sorus {

    private val components: MutableMap<Class<*>, Any> = HashMap()

    fun initialize() {
        this.register(ContainerRenderer)
        this.register(EventManager)
        this.register(FeatureManager)
        this.register(HUDManager)
        this.register(IAdapter::class.java, InterfaceManager.get<IAdapter>())
        this.register(KeyBindManager)
        this.register(NotificationManager)
        this.register(PluginManager)
        this.register(ServerIntegrationManager)
        this.register(SettingManager)
        this.register(SocialManager)
        this.register(ThemeManager)
        this.register(UserInterface)
        this.register(WebSocketManager)

        PluginManager.findPlugins()

        HUDManager.initialize()
        ContainerRenderer.initialize()

        EventManager.register<InitializeEvent> {
            val adapter = AdapterManager.adapter
            adapter.setDisplayTitle("Sorus | " + AdapterManager.adapter.version)
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

}