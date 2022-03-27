package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IServer
import v1_18_2.net.minecraft.client.network.ServerInfo

class ServerImpl(private val serverInfo: ServerInfo) : IServer {
    override val ip: String
        get() = serverInfo.address
}