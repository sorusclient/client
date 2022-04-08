/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.BossBarColor;
import com.github.sorusclient.client.adapter.IBossBar;
import v1_18_2.net.minecraft.entity.boss.BossBar;

public class BossBarImpl implements IBossBar {

    private BossBar bossBar;

    public BossBarImpl(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    @Override
    public String getName() {
        return bossBar.getName().getString();
    }

    @Override
    public double getPercentage() {
        return bossBar.getPercent();
    }

    @Override
    public BossBarColor getColor() {
        return switch (bossBar.getColor()) {
            case PINK -> BossBarColor.PINK;
            case BLUE -> BossBarColor.BLUE;
            case RED -> BossBarColor.RED;
            case GREEN -> BossBarColor.GREEN;
            case YELLOW -> BossBarColor.YELLOW;
            case PURPLE -> BossBarColor.PURPLE;
            case WHITE -> BossBarColor.WHITE;
        };
    }

}
