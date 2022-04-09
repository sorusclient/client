/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IBossBar;
import com.github.sorusclient.client.adapter.IScoreboard;
import com.github.sorusclient.client.adapter.IWorld;
import v1_8_9.net.minecraft.entity.boss.BossBar;
import v1_8_9.net.minecraft.world.World;

import java.util.List;

public class WorldImpl implements IWorld {

    private final World world;

    public WorldImpl(World world) {
        this.world = world;
    }

    @Override
    public IScoreboard getScoreboard() {
        return new ScoreboardImpl(world.getScoreboard());
    }

    @Override
    public List<IBossBar> getBossBars() {
        if (BossBar.framesToLive > 0) {
            return List.of(new BossBarImpl());
        } else {
            return List.of();
        }
    }

}
