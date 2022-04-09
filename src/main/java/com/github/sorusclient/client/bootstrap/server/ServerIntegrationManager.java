/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap.server;

import com.github.sorusclient.client.adapter.AdapterManager;
import com.github.sorusclient.client.adapter.event.GameJoinEvent;
import com.github.sorusclient.client.adapter.event.GameLeaveEvent;
import com.github.sorusclient.client.adapter.event.SorusCustomPacketEvent;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.util.AssetUtil;
import lombok.val;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ServerIntegrationManager {

    private static final Map<String, Consumer<Object>> joinListeners = new HashMap<>();
    private static final List<Runnable> leaveListeners = new ArrayList<>();

    public static void registerJoinListener(String section, Consumer<Object> consumer) {
        joinListeners.put(section, consumer);
    }

    public static void registerLeaveListener(Runnable runnable) {
        leaveListeners.add(runnable);
    }

    private static void onGameJoin() {
        val server = AdapterManager.getAdapter().getCurrentServer();
        if (server != null) {
            new Thread(() -> {
                val json = AssetUtil.getJsonForServer(server.getIP());
                if (json != null) {
                    applyServerConfiguration(json);
                }
            }).start();
        }
    }

    private static void onGameLeave() {
        for (val listener : leaveListeners) {
            listener.run();
        }
    }

    private static void onCustomPacket(SorusCustomPacketEvent event) {
        if (event.getChannel().equals("integration")) {
            applyServerConfiguration(event.getContent());
        }
    }

    private static void applyServerConfiguration(String json) {
        val jsonObject = new JSONObject(json).toMap();
        for (var entry : jsonObject.entrySet()) {
            if (joinListeners.get(entry.getKey()) != null) {
                joinListeners.get(entry.getKey()).accept(entry.getValue());
            }
        }
    }

    static {
        EventManager.register(GameJoinEvent.class, (event) -> onGameJoin());
        EventManager.register(GameLeaveEvent.class, (event) -> onGameLeave());
        EventManager.register(SorusCustomPacketEvent.class, ServerIntegrationManager::onCustomPacket);
    }

}
