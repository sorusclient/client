package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.ITextClickEvent
import com.github.sorusclient.client.adapter.ITextHoverEvent
import com.github.sorusclient.client.adapter.ITextStyle
import com.github.sorusclient.client.adapter.TextFormatting
import v1_8_9.net.minecraft.text.Style

class TextStyleImpl(style: Style) : ITextStyle {
    override var clickEvent: ITextClickEvent? = null
    override var hoverEvent: ITextHoverEvent? = null
    override val insertion: String?
    override var color: TextFormatting? = null

    init {
        if (style.clickEvent != null) {
            clickEvent = TextClickEventImpl(style.clickEvent)
        }
        if (style.hoverEvent != null) {
            hoverEvent = TextHoverEventImpl(style.hoverEvent)
        }
        insertion = style.insertion
        if (style.color != null) {
            color = Util.formattingToTextFormatting(style.color)
        }
    }

}