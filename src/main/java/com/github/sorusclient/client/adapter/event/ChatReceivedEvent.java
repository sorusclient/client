/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event;

import com.github.sorusclient.client.adapter.IText;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ChatReceivedEvent {

    private @Getter final String message;
    private @Getter final IText text;

}
