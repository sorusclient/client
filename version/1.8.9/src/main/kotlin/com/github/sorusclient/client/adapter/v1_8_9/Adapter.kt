/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.*
import com.github.sorusclient.client.adapter.IKeyBind.KeyBindType
import com.github.sorusclient.client.bootstrap.Initializer
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.screen.*
import v1_8_9.net.minecraft.client.gui.screen.options.ControlsOptionsScreen
import v1_8_9.net.minecraft.client.network.ServerInfo
import v1_8_9.net.minecraft.client.options.KeyBinding
import v1_8_9.net.minecraft.client.util.Window
import v1_8_9.net.minecraft.entity.Entity
import v1_8_9.net.minecraft.network.ServerAddress
import v1_8_9.net.minecraft.text.LiteralText
import v1_8_9.net.minecraft.world.level.LevelInfo
import v1_8_9.org.lwjgl.input.Mouse
import v1_8_9.org.lwjgl.opengl.Display
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class Adapter : IAdapter, Initializer {

    override fun initialize() {
        InterfaceManager.register(Adapter())
    }

    override val openScreen: ScreenType
        get() {
            return Util.screenToScreenType(MinecraftClient.getInstance().currentScreen)
        }

    override val screenDimensions: DoubleArray
        get() {
            val window = Window(MinecraftClient.getInstance())
            return doubleArrayOf(window.scaledWidth, window.scaledHeight)
        }

    override val mouseLocation: DoubleArray
        get() {
            val window = Window(MinecraftClient.getInstance())
            val x = Mouse.getX()
            val y = Mouse.getY()
            return doubleArrayOf(
                x / Display.getWidth().toDouble() * window.scaledWidth,
                window.scaledHeight - y / Display.getHeight().toDouble() * window.scaledHeight
            )
        }
    override val player: IPlayerEntity?
        get() {
            val player: Entity? = MinecraftClient.getInstance().player
            return player?.let { PlayerEntityImpl(it) }
        }
    override val world: IWorld
        get() = WorldImpl(MinecraftClient.getInstance().world)

    override fun openScreen(screenType: ScreenType) {
        val screen = when (screenType) {
            ScreenType.DUMMY -> DummyScreen()
            ScreenType.SETTINGS -> createSettingsScreen()
            ScreenType.CONTROLS -> ControlsOptionsScreen(createSettingsScreen(), MinecraftClient.getInstance().options)
            ScreenType.VIDEO_SETTINGS -> VideoOptionsScreen(createSettingsScreen(), MinecraftClient.getInstance().options)
            else -> null
        }

        MinecraftClient.getInstance().openScreen(screen)
    }

    private fun createSettingsScreen(): SettingsScreen {
        val inGame = MinecraftClient.getInstance().world != null
        return SettingsScreen(if (inGame) { GameMenuScreen() } else { TitleScreen() }, MinecraftClient.getInstance().options)
    }

    override var perspective: PerspectiveMode
        get() {
            return when (MinecraftClient.getInstance().options.perspective) {
                0 -> PerspectiveMode.FIRST_PERSON
                1 -> PerspectiveMode.THIRD_PERSON_BACK
                2 -> PerspectiveMode.THIRD_PERSON_FRONT
                else -> PerspectiveMode.UNKNOWN
            }
        }
        set(perspectiveMode) {
            val newPerspective: Int = when (perspectiveMode) {
                PerspectiveMode.FIRST_PERSON -> 0
                PerspectiveMode.THIRD_PERSON_BACK -> 1
                PerspectiveMode.THIRD_PERSON_FRONT -> 2
                else -> -1
            }
            MinecraftClient.getInstance().options.perspective = newPerspective
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

    override fun getKeyBind(type: KeyBindType): IKeyBind {
        val options = MinecraftClient.getInstance().options
        val keyBinding: KeyBinding = when (type) {
            KeyBindType.SPRINT -> options.keySprint
            KeyBindType.SNEAK -> options.keySneak
        }
        return KeyBindImpl(keyBinding)
    }

    override fun sendPlayerMessage(message: String) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendChatMessage(message)
        } else {
            error("Attempted to send player message but player was null")
        }
    }

    override fun setDisplayTitle(title: String) {
        Display.setTitle(title)
    }

    override fun setDisplayIcon(iconSmall: String, iconLarge: String) {
        Display.setIcon(arrayOf(getByteBuffer(iconSmall), getByteBuffer(iconLarge)))
    }

    private fun getByteBuffer(path: String): ByteBuffer {
        val bufferedImage = ImageIO.read(Adapter::class.java.classLoader.getResourceAsStream(path))
        val buffer = ByteBuffer.allocateDirect(bufferedImage.width * bufferedImage.height * 4)
        val rgba = IntArray(bufferedImage.width * bufferedImage.height)
        bufferedImage.getRGB(0, 0, bufferedImage.width, bufferedImage.height, rgba, 0, bufferedImage.width)
        for (pixelY in 0 until bufferedImage.height) {
            for (pixelX in 0 until bufferedImage.width) {
                val rgb = rgba[bufferedImage.width * pixelY + pixelX]
                val red = rgb shr 16 and 0xFF
                val green = rgb shr 8 and 0xFF
                val blue = rgb and 0xFF
                val alpha = rgb shr 24 and 0xFF
                buffer.put(red.toByte())
                buffer.put(green.toByte())
                buffer.put(blue.toByte())
                buffer.put(alpha.toByte())
            }
        }
        buffer.flip()

        return buffer
    }

    override fun leaveWorld() {
        if (MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.disconnect()
        }
        MinecraftClient.getInstance().connect(null)
    }

    override fun joinServer(ip: String) {
        val serverAddress = ServerAddress.parse(ServerAddress.parse(ip).address)
        MinecraftClient.getInstance().currentServerEntry = ServerInfo("", ip, false)
        MinecraftClient.getInstance().openScreen(ConnectScreen(TitleScreen(), MinecraftClient.getInstance(), serverAddress.address, serverAddress.port))
    }

    override val gameMode: GameMode
        get() = when(MinecraftClient.getInstance().interactionManager.currentGameMode) {
            LevelInfo.GameMode.SURVIVAL -> GameMode.SURVIVAL
            LevelInfo.GameMode.CREATIVE -> GameMode.CREATIVE
            LevelInfo.GameMode.ADVENTURE -> GameMode.ADVENTURE
            LevelInfo.GameMode.SPECTATOR -> GameMode.SPECTATOR
            else -> GameMode.UNKNOWN
        }
    override val session: ISession
        get() = SessionImpl()

    override fun createText(string: String): IText {
        return Util.textToApiText(LiteralText(string))
    }

    override val version = "1.8.9"

    override val renderer: IRenderer = RendererImpl()

    override val fps: Int
        get() = MinecraftClient.getCurrentFps()

}