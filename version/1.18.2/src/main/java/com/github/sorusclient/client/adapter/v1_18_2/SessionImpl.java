/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.ISession;
import v1_18_2.net.minecraft.client.MinecraftClient;

public class SessionImpl implements ISession {

    @Override
    public String getUUID() {
        return MinecraftClient.getInstance().getSession().getUuid();
    }

    @Override
    public String getAccessToken() {
        return MinecraftClient.getInstance().getSession().getAccessToken();
    }

    @Override
    public String getUsername() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

}
