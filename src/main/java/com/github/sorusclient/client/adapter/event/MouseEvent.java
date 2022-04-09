/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event;

import com.github.sorusclient.client.adapter.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MouseEvent {

    private @Getter final Button button;
    private @Getter final boolean pressed;
    private @Getter final double x, y;
    private @Getter final double wheel;

}
