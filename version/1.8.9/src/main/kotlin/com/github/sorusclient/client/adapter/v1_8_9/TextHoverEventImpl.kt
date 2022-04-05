/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.HoverEventAction
import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.adapter.ITextHoverEvent
import v1_8_9.net.minecraft.text.HoverEvent

class TextHoverEventImpl(hoverEvent: HoverEvent) : ITextHoverEvent {
    override val value: IText
    override val action: HoverEventAction

    init {
        value = com.github.sorusclient.client.adapter.v1_8_9.Util.textToApiText(hoverEvent.value)
        this.action = when (hoverEvent.action) {
            HoverEvent.Action.SHOW_ENTITY -> HoverEventAction.SHOW_ENTITY
            HoverEvent.Action.SHOW_ACHIEVEMENT -> HoverEventAction.SHOW_ACHIEVEMENT
            HoverEvent.Action.SHOW_TEXT -> HoverEventAction.SHOW_TEXT
            else -> null!!
        }
    }

}