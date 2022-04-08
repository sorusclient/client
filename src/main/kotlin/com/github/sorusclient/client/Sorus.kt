/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.GetClientBrandEvent
import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.hud.HUDManager
import com.github.sorusclient.client.plugin.PluginManager
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.ui.UserInterface
import com.github.sorusclient.client.ui.framework.ContainerRenderer
import com.github.sorusclient.client.ui.theme.ThemeManager
import com.github.sorusclient.client.websocket.WebSocketManager

object Sorus {

    fun initialize() {
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

        EventManager.register<GetClientBrandEvent> {
            it.brand = "sorus"
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            SettingManager.saveCurrent()
        })

        WebSocketManager
    }

}