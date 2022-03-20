package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.ISession
import v1_8_9.net.minecraft.client.MinecraftClient

class SessionImpl: ISession {

    override fun getUUID(): String {
        return MinecraftClient.getInstance().session.uuid
    }

    override fun getAccessToken(): String {
        return MinecraftClient.getInstance().session.accessToken
    }

    override fun getUsername(): String {
        return MinecraftClient.getInstance().session.username
    }

}

