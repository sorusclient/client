package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IBossBar;
import com.github.sorusclient.client.adapter.IScoreboard;
import com.github.sorusclient.client.adapter.IWorld;
import v1_8_9.net.minecraft.world.World;

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
    public IBossBar getBossBar() {
        return new BossBarImpl();
    }

}
