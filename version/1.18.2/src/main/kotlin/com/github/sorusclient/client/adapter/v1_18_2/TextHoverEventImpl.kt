/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.HoverEventAction
import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.adapter.ITextHoverEvent
import v1_18_2.net.minecraft.text.HoverEvent
import v1_18_2.net.minecraft.text.Text

class TextHoverEventImpl(hoverEvent: HoverEvent) : ITextHoverEvent {
    override val value: IText
    override val action: HoverEventAction

    init {
        value = Util.textToApiText(hoverEvent.getValue(hoverEvent.action) as Text)
        this.action = when (hoverEvent.action) {
            HoverEvent.Action.SHOW_ENTITY -> HoverEventAction.SHOW_ENTITY
            HoverEvent.Action.SHOW_TEXT -> HoverEventAction.SHOW_TEXT
            else -> null!!
        }
    }

}