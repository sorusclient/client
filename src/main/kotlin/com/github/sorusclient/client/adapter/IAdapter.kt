/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.adapter.IKeyBind.KeyBindType

interface IAdapter {
    fun openScreen(screenType: ScreenType)
    val openScreen: ScreenType?

    val screenDimensions: DoubleArray
    val mouseLocation: DoubleArray

    val player: IPlayerEntity?
    val world: IWorld
    var perspective: PerspectiveMode
    val currentServer: IServer?
    val gameMode: GameMode
    val session: ISession
    val fps: Int

    fun getKeyBind(type: KeyBindType): IKeyBind
    fun sendPlayerMessage(message: String)
    fun createText(string: String): IText

    fun setDisplayTitle(title: String)
    fun setDisplayIcon(iconSmall: String, iconLarge: String)

    fun leaveWorld()
    fun joinServer(ip: String)

    val version: String

    val renderer: IRenderer
}