/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event;

import com.github.sorusclient.client.adapter.Key;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class KeyEvent {

    private @Getter final Key key;
    private @Getter final boolean pressed, repeat;

}
