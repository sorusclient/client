/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IServer
import v1_8_9.net.minecraft.client.network.ServerInfo

class ServerImpl(private val serverInfo: ServerInfo) : IServer {
    override val ip: String
        get() = serverInfo.address
}