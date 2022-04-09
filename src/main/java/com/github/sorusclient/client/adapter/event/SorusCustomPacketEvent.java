/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SorusCustomPacketEvent {

    private @Getter final String channel;
    private @Getter final String content;

}
