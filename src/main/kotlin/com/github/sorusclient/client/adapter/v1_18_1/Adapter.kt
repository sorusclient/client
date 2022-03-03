package com.github.sorusclient.client.adapter.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.*
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.gui.screen.ConnectScreen
import v1_18_1.net.minecraft.client.gui.screen.GameMenuScreen
import v1_18_1.net.minecraft.client.gui.screen.TitleScreen
import v1_18_1.net.minecraft.client.gui.screen.option.ControlsOptionsScreen
import v1_18_1.net.minecraft.client.gui.screen.option.OptionsScreen
import v1_18_1.net.minecraft.client.gui.screen.option.VideoOptionsScreen
import v1_18_1.net.minecraft.client.network.ServerAddress
import v1_18_1.net.minecraft.client.network.ServerInfo
import v1_18_1.net.minecraft.client.option.Perspective
import v1_18_1.net.minecraft.text.LiteralText
import v1_18_1.net.minecraft.client.option.KeyBinding

class Adapter: Listener, IAdapter {

    override fun run() {
        GlassLoader.getInstance().registerInterface(IAdapter::class.java, this)
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

    override val currentServer: IServer
        get() = TODO("Not yet implemented")

    override fun getKeyBind(type: IKeyBind.KeyBindType): IKeyBind {
        val options = MinecraftClient.getInstance().options
        val keyBinding: KeyBinding = when (type) {
            IKeyBind.KeyBindType.SPRINT -> options.keySprint
            IKeyBind.KeyBindType.SNEAK -> options.keySneak
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
        MinecraftClient.getInstance().world!!.disconnect()
        MinecraftClient.getInstance().setScreen(TitleScreen())
    }

    override fun joinServer(ip: String) {
        val serverAddress = ServerAddress.parse(ServerAddress.parse(ip).address)
        val serverInfo = ServerInfo("", ip, false)
        MinecraftClient.getInstance().currentServerEntry = serverInfo
        ConnectScreen.connect(TitleScreen(), MinecraftClient.getInstance(), serverAddress, serverInfo)
        //MinecraftClient.getInstance().setScreen(ConnectScreen(v1_8_9.net.minecraft.client.gui.screen.TitleScreen(), v1_8_9.net.minecraft.client.MinecraftClient.getInstance(), serverAddress.address, serverAddress.port))
    }

    override fun createText(string: String): IText {
        return Util.textToApiText(LiteralText(string))
    }

    override val version: String
        get() = "1.18.1"

    override val renderer: IRenderer = RendererImpl()

    override val fps: Int
        get() = TODO("Not yet implemented")

}