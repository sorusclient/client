/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

public interface IPotionEffect {
    String getDuration();
    String getName();
    int getAmplifier();
    PotionType getType();
}
