package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.IServer
import v1_18_1.net.minecraft.client.network.ServerInfo

class ServerImpl(private val serverInfo: ServerInfo) : IServer {
    override val ip: String
        get() = serverInfo.address
}