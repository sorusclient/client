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
    override val bold: Boolean
    override val italic: Boolean
    override val obfuscated: Boolean
    override val strikethrough: Boolean
    override val underlined: Boolean

    init {
        if (style.clickEvent != null) {
            clickEvent = com.github.sorusclient.client.adapter.v1_8_9.TextClickEventImpl(style.clickEvent)
        }
        if (style.hoverEvent != null) {
            hoverEvent = com.github.sorusclient.client.adapter.v1_8_9.TextHoverEventImpl(style.hoverEvent)
        }
        insertion = style.insertion
        if (style.color != null) {
            color = com.github.sorusclient.client.adapter.v1_8_9.Util.formattingToTextFormatting(style.color)
        }
        bold = style.isBold
        italic = style.isItalic
        obfuscated = style.isObfuscated
        strikethrough = style.isStrikethrough
        underlined = style.isUnderlined
    }

}