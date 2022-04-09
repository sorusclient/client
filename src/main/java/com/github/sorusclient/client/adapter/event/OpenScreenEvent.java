/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event;

import com.github.sorusclient.client.adapter.ScreenType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class OpenScreenEvent {

    private @Getter final ScreenType screenType;

}
