/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.BossBarColor;
import com.github.sorusclient.client.adapter.IBossBar;
import v1_8_9.net.minecraft.entity.boss.BossBar;

public class BossBarImpl implements IBossBar {

    @Override
    public String getName() {
        return BossBar.name;
    }

    @Override
    public double getPercentage() {
        return BossBar.percent;
    }

    @Override
    public BossBarColor getColor() {
        return BossBarColor.PURPLE;
    }

}
