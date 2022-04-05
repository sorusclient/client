/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.Identifier
import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.*
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.toIdentifier
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.gui.screen.ConnectScreen
import v1_18_2.net.minecraft.client.gui.screen.GameMenuScreen
import v1_18_2.net.minecraft.client.gui.screen.TitleScreen
import v1_18_2.net.minecraft.client.gui.screen.option.ControlsOptionsScreen
import v1_18_2.net.minecraft.client.gui.screen.option.OptionsScreen
import v1_18_2.net.minecraft.client.gui.screen.option.VideoOptionsScreen
import v1_18_2.net.minecraft.client.network.ServerAddress
import v1_18_2.net.minecraft.client.network.ServerInfo
import v1_18_2.net.minecraft.client.option.Perspective
import v1_18_2.net.minecraft.text.LiteralText
import v1_18_2.net.minecraft.client.option.KeyBinding
import java.lang.reflect.Field

class Adapter: IAdapter, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override val openScreen: ScreenType
        get() = Util.screenToScreenType(MinecraftClient.getInstance().currentScreen)

    override val screenDimensions: DoubleArray
        get() {
            val window = MinecraftClient.getInstance().window
            return doubleArrayOf(window.scaledWidth.toDouble(), window.scaledHeight.toDouble())
        }

    override val mouseLocation: DoubleArray
        get() {
            val mouse = MinecraftClient.getInstance().mouse
            val window = MinecraftClient.getInstance().window
            return doubleArrayOf(mouse.x / window.width * window.scaledWidth, mouse.y / window.height * window.scaledHeight)
        }

    override val player: IPlayerEntity
        get() = PlayerEntityImpl(MinecraftClient.getInstance().player!!)

    override val world: IWorld
        get() = WorldImpl(MinecraftClient.getInstance().world!!)

    override fun openScreen(screenType: ScreenType) {
        val screen = when (screenType) {
            ScreenType.DUMMY -> DummyScreen()
            ScreenType.SETTINGS -> createSettingsScreen()
            ScreenType.CONTROLS -> ControlsOptionsScreen(createSettingsScreen(), MinecraftClient.getInstance().options)
            ScreenType.VIDEO_SETTINGS -> VideoOptionsScreen(createSettingsScreen(), MinecraftClient.getInstance().options)
            else -> null
        }

        MinecraftClient.getInstance().setScreen(screen)
    }

    private fun createSettingsScreen(): OptionsScreen {
        val inGame = MinecraftClient.getInstance().world != null
        return OptionsScreen(if (inGame) { GameMenuScreen(true) } else { TitleScreen() }, MinecraftClient.getInstance().options)
    }

    override var perspective: PerspectiveMode
        get() {
            return when (MinecraftClient.getInstance().options.perspective!!) {
                Perspective.FIRST_PERSON -> PerspectiveMode.FIRST_PERSON
                Perspective.THIRD_PERSON_FRONT -> PerspectiveMode.THIRD_PERSON_FRONT
                Perspective.THIRD_PERSON_BACK -> PerspectiveMode.THIRD_PERSON_BACK
            }
        }
        set(value) {
            MinecraftClient.getInstance().options.perspective = when (value) {
                PerspectiveMode.FIRST_PERSON -> Perspective.FIRST_PERSON
                PerspectiveMode.THIRD_PERSON_FRONT -> Perspective.THIRD_PERSON_FRONT
                PerspectiveMode.THIRD_PERSON_BACK -> Perspective.THIRD_PERSON_BACK
                else -> null!!
            }
        }

    override val currentServer: IServer?
        get() {
            val serverInfo = MinecraftClient.getInstance().currentServerEntry
            return if (serverInfo != null) {
                ServerImpl(serverInfo)
            } else {
                null
            }
        }

    override fun getKeyBind(type: IKeyBind.KeyBindType): IKeyBind {
        val options = MinecraftClient.getInstance().options
        val keyBinding: KeyBinding = when (type) {
            IKeyBind.KeyBindType.SPRINT -> options.sprintKey
            IKeyBind.KeyBindType.SNEAK -> options.sneakKey
        }
        return KeyBindImpl(keyBinding)
    }

    override fun sendPlayerMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun setDisplayTitle(title: String) {
        MinecraftClient.getInstance().window.setTitle(title)
    }

    override fun setDisplayIcon(iconSmall: String, iconLarge: String) {
        MinecraftClient.getInstance().window.setIcon(Adapter::class.java.classLoader.getResourceAsStream(iconSmall), Adapter::class.java.classLoader.getResourceAsStream(iconLarge))
    }

    override fun leaveWorld() {
        if (MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world!!.disconnect()
        }
        MinecraftClient.getInstance().setScreen(TitleScreen())
    }

    override fun joinServer(ip: String) {
        val serverAddress = ServerAddress.parse(ServerAddress.parse(ip).address)
        val serverInfo = ServerInfo("", ip, false)
        MinecraftClient.getInstance().currentServerEntry = serverInfo
        ConnectScreen.connect(TitleScreen(), MinecraftClient.getInstance(), serverAddress, serverInfo)
    }

    override val gameMode: GameMode
        get() = when(MinecraftClient.getInstance().interactionManager!!.currentGameMode) {
            v1_18_2.net.minecraft.world.GameMode.SURVIVAL -> GameMode.SURVIVAL
            v1_18_2.net.minecraft.world.GameMode.CREATIVE -> GameMode.CREATIVE
            v1_18_2.net.minecraft.world.GameMode.ADVENTURE -> GameMode.ADVENTURE
            v1_18_2.net.minecraft.world.GameMode.SPECTATOR -> GameMode.SPECTATOR
            else -> GameMode.UNKNOWN
        }

    override val session: ISession
        get() = SessionImpl()

    override fun createText(string: String): IText {
        return Util.textToApiText(LiteralText(string))
    }

    override val version: String
        get() = "1.18.2"

    override val renderer: IRenderer = RendererImpl()

    private val currentFps: Field = run {
        val currentFps = "v1_18_2/net/minecraft/client/MinecraftClient#currentFps".toIdentifier()
        val field = MinecraftClient::class.java.getDeclaredField(currentFps.fieldName)
        field.isAccessible = true

        field
    }

    override val fps: Int
        get() = currentFps.getInt(MinecraftClient.getInstance())

}