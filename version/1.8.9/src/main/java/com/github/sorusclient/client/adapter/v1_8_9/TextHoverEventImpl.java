/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.HoverEventAction;
import com.github.sorusclient.client.adapter.IText;
import com.github.sorusclient.client.adapter.ITextHoverEvent;
import v1_8_9.net.minecraft.text.HoverEvent;

public class TextHoverEventImpl implements ITextHoverEvent {

    private final IText value;
    private final HoverEventAction action;

    public TextHoverEventImpl(HoverEvent hoverEvent) {
        value = Util.textToApiText(hoverEvent.getValue());
        if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            action = HoverEventAction.SHOW_ENTITY;
        } else {
            action = HoverEventAction.SHOW_TEXT;
        }
    }

    @Override
    public IText getValue() {
        return value;
    }

    @Override
    public HoverEventAction getAction() {
        return action;
    }

}
