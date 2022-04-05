/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IServer
import v1_18_2.net.minecraft.client.network.ServerInfo

class ServerImpl(private val serverInfo: ServerInfo) : IServer {
    override val ip: String
        get() = serverInfo.address
}