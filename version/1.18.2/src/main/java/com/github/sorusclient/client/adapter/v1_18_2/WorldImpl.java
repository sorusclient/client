/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.IdentifierKt;
import com.github.sorusclient.client.adapter.IBossBar;
import com.github.sorusclient.client.adapter.IScoreboard;
import com.github.sorusclient.client.adapter.IWorld;
import v1_18_2.net.minecraft.client.MinecraftClient;
import v1_18_2.net.minecraft.client.gui.hud.BossBarHud;
import v1_18_2.net.minecraft.client.gui.hud.ClientBossBar;
import v1_18_2.net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldImpl implements IWorld {

    private final World world;
    private final Field bossBarsField;

    public WorldImpl(World world) {
        this.world = world;

        var bossBars = IdentifierKt.toIdentifier("v1_18_2/net/minecraft/client/gui/hud/BossBarHud#bossBars");
        try {
            Field field = BossBarHud.class.getDeclaredField(bossBars.getFieldName());
            field.setAccessible(true);
            bossBarsField = field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public IScoreboard getScoreboard() {
        return new ScoreboardImpl(world.getScoreboard());
    }

    @Override
    public List<IBossBar> getBossBars() {
        try {
            Map<UUID, ClientBossBar> bossBars = (Map<UUID, ClientBossBar>) bossBarsField.get(MinecraftClient.getInstance().inGameHud.getBossBarHud());

            List<IBossBar> bossBarsList = new ArrayList<>();
            for (var bossBar : bossBars.entrySet()) {
                bossBarsList.add(new BossBarImpl(bossBar.getValue()));
            }

            return bossBarsList;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
