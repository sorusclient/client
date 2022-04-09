/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9;

import com.github.sorusclient.client.Identifier;
import com.github.sorusclient.client.IdentifierKt;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.adapter.v1_8_9.Util;
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements;
import com.github.sorusclient.client.feature.impl.enhancements.ISettingsLoader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.options.GameOptions;
import v1_8_9.net.minecraft.client.options.KeyBinding;
import v1_8_9.net.minecraft.util.collection.IntObjectStorage;

public class SettingsLoader implements ISettingsLoader {

    private final Field keyMap;

    public SettingsLoader() {
        Identifier keyMap = IdentifierKt.toIdentifier("v1_8_9/net/minecraft/client/options/KeyBinding#KEY_MAP");
        try {
            Field field = KeyBinding.class.getDeclaredField(keyMap.getFieldName());
            field.setAccessible(true);
            this.keyMap = field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    public Map<String, Object> save(Map<String, Object> cached) {
        var map = new HashMap<>(cached);
        var options = MinecraftClient.getInstance().options;

        map.put("sensitivity", options.sensitivity);
        map.put("chat", getKey(options.keyChat));
        map.put("command", getKey(options.keyCommand));
        map.put("sneak", getKey(options.keySneak));
        map.put("perspective", getKey(options.keyTogglePerspective));

        if (!(options.fancyGraphics && (!map.containsKey("graphics") || (((Enhancements.Graphics) map.get("graphics")).ordinal()) > 0))) {
            map.put("graphics", options.fancyGraphics ? Enhancements.Graphics.FANCY : Enhancements.Graphics.FAST);
        }

        for (var i = 1; i < 9; i++) {
            map.put("hotbar_" + i, getKey(options.keysHotbar[i - 1]));
        }

        return map;
    }

    private Key getKey(KeyBinding keyBinding) {
        return Util.getKey(keyBinding.getCode());
    }

    public void load(Map<String, Object> map) {
        var options = (GameOptions) EnhancementsHook.options;

        if (map.containsKey("sensitivity")) {
            options.sensitivity = (float) (double) map.get("sensitivity");
        }

        if (map.containsKey("graphics")) {
            options.fancyGraphics = ((Enhancements.Graphics) map.get("graphics")).ordinal() > 0;
        }

        for (var i = 1; i < 9; i++) {
            this.setKey(options.keysHotbar[i - 1], map, "hotbar_" + i);
        }

        this.setKey(options.keyChat, map, "chat");
        this.setKey(options.keyCommand, map, "command");
        this.setKey(options.keySprint, map, "sprint");
        this.setKey(options.keySneak, map, "sneak");
        this.setKey(options.keyTogglePerspective, map, "perspective");
    }

    private void setKey(KeyBinding keyBinding, Map<String, ?> settings, String id) {
        if (settings.containsKey(id)) {
            keyBinding.setCode(Util.getKeyCode((Key) settings.get(id)));
            try {
                ((IntObjectStorage) this.keyMap.get(null)).set(keyBinding.getCode(), keyBinding);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
