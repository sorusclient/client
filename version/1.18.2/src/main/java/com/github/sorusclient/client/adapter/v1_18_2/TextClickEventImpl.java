/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.ClickEventAction;
import com.github.sorusclient.client.adapter.ITextClickEvent;
import v1_18_2.net.minecraft.text.ClickEvent;

public class TextClickEventImpl implements ITextClickEvent {

    private final String value;
    private final ClickEventAction action;

    public TextClickEventImpl(ClickEvent clickEvent) {
        value = clickEvent.getValue();
        action = switch (clickEvent.getAction()) {
            case SUGGEST_COMMAND -> ClickEventAction.SUGGEST_COMMAND;
            case RUN_COMMAND -> ClickEventAction.RUN_COMMAND;
            case OPEN_URL -> ClickEventAction.OPEN_URL;
            default -> null;
        };
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public ClickEventAction getAction() {
        return action;
    }

}
