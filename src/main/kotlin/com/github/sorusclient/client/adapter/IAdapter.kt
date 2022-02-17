package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.adapter.IKeyBind.KeyBindType

interface IAdapter {
    val openScreen: ScreenType?
    val screenDimensions: DoubleArray
    val mouseLocation: DoubleArray
    val player: IPlayerEntity?
    val world: IWorld
    fun openScreen(screenType: ScreenType)
    var perspective: PerspectiveMode
    val currentServer: IServer?
    fun getKeyBind(type: KeyBindType): IKeyBind
    fun sendPlayerMessage(message: String)
    fun setDisplayTitle(title: String)
    val version: String

    val renderer: IRenderer
}