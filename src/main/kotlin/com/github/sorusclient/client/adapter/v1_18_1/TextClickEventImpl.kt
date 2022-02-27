package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.ClickEventAction
import com.github.sorusclient.client.adapter.ITextClickEvent
import v1_18_1.net.minecraft.text.ClickEvent

class TextClickEventImpl(clickEvent: ClickEvent) : ITextClickEvent {
    override val value: String
    override val action: ClickEventAction

    init {
        value = clickEvent.value
        this.action = when (clickEvent.action) {
            ClickEvent.Action.SUGGEST_COMMAND -> ClickEventAction.SUGGEST_COMMAND
            ClickEvent.Action.RUN_COMMAND -> ClickEventAction.RUN_COMMAND
            ClickEvent.Action.OPEN_URL -> ClickEventAction.OPEN_URL
            else -> null!!
        }
    }

}