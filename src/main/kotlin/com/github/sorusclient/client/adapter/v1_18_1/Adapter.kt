package com.github.sorusclient.client.adapter.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.*
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.text.LiteralText

class Adapter: Listener, IAdapter {

    override fun run() {
        GlassLoader.getInstance().registerInterface(IAdapter::class.java, this)
    }

    override val openScreen: ScreenType?
        get() = TODO("Not yet implemented")

    override val screenDimensions: DoubleArray
        get() {
            val window = MinecraftClient.getInstance().window
            return doubleArrayOf(window.scaledWidth.toDouble(), window.scaledHeight.toDouble())
        }

    override val mouseLocation: DoubleArray
        get() {
            val mouse = MinecraftClient.getInstance().mouse
            return doubleArrayOf(mouse.x, mouse.y)
        }

    override val player: IPlayerEntity?
        get() = PlayerEntityImpl(MinecraftClient.getInstance().player!!)

    override val world: IWorld
        get() = WorldImpl(MinecraftClient.getInstance().world!!)

    override fun openScreen(screenType: ScreenType) {
        TODO("Not yet implemented")
    }

    override var perspective: PerspectiveMode
        get() = TODO("Not yet implemented")
        set(value) {}

    override val currentServer: IServer?
        get() = TODO("Not yet implemented")

    override fun getKeyBind(type: IKeyBind.KeyBindType): IKeyBind {
        TODO("Not yet implemented")
    }

    override fun sendPlayerMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun setDisplayTitle(title: String) {
        TODO("Not yet implemented")
    }

    override fun setDisplayIcon(iconSmall: String, iconLarge: String) {
        TODO("Not yet implemented")
    }

    override fun joinServer(ip: String) {
        TODO("Not yet implemented")
    }

    override fun createText(string: String): IText {
        return Util.textToApiText(LiteralText(string))
    }

    override val version: String
        get() = TODO("Not yet implemented")

    override val renderer: IRenderer = RendererImpl()

    override val fps: Int
        get() = TODO("Not yet implemented")

}