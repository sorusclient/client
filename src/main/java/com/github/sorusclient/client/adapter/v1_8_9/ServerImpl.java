package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IServer;
import v1_8_9.net.minecraft.client.network.ServerInfo;

public class ServerImpl implements IServer {

    private final ServerInfo serverInfo;

    public ServerImpl(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public String getIp() {
        return this.serverInfo.address;
    }

}
