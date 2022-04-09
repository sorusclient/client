/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.HoverEventAction;
import com.github.sorusclient.client.adapter.IText;
import com.github.sorusclient.client.adapter.ITextHoverEvent;
import v1_18_2.net.minecraft.text.HoverEvent;
import v1_18_2.net.minecraft.text.Text;

import java.util.Objects;

public class TextHoverEventImpl implements ITextHoverEvent {

    private final IText value;
    private final HoverEventAction action;

    public TextHoverEventImpl(HoverEvent hoverEvent) {
        value = Util.INSTANCE.textToApiText((Text) Objects.requireNonNull(hoverEvent.getValue(hoverEvent.getAction())));
        if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            action = HoverEventAction.SHOW_ENTITY;
        } else {
            action = HoverEventAction.SHOW_TEXT;
        }
    }

    @Override
    public IText getValue() {
        return null;
    }

    @Override
    public HoverEventAction getAction() {
        return null;
    }

}
