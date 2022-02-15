package com.github.sorusclient.client.adapter.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.*
import com.github.sorusclient.client.adapter.IKeyBind.KeyBindType
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.screen.GameMenuScreen
import v1_8_9.net.minecraft.client.gui.screen.Screen
import v1_8_9.net.minecraft.client.options.KeyBinding
import v1_8_9.net.minecraft.client.util.Window
import v1_8_9.net.minecraft.entity.Entity

class Adapter : Listener, IAdapter {
    override fun run() {
        GlassLoader.getInstance().registerInterface(IAdapter::class.java, Adapter())
    }

    override val openScreen: ScreenType
        get() {
            return when (MinecraftClient.getInstance().currentScreen) {
                is GameMenuScreen -> {
                    ScreenType.GAME_MENU
                }
                is DummyScreen -> {
                    ScreenType.DUMMY
                }
                null -> {
                    ScreenType.IN_GAME
                }
                else -> {
                    ScreenType.UNKNOWN
                }
            }
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
        var screen: Screen? = null
        when (screenType) {
            ScreenType.DUMMY -> screen = DummyScreen()
            ScreenType.IN_GAME -> {}
            else -> {}
        }
        MinecraftClient.getInstance().openScreen(screen)
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

    override val renderer: IRenderer
        get() = RendererImpl()
}