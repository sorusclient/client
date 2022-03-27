package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.ILiteralText
import v1_8_9.net.minecraft.text.LiteralText

class LiteralTextImpl(text: LiteralText) : com.github.sorusclient.client.adapter.v1_8_9.TextImpl<LiteralText>(text), ILiteralText {
    override val string: String?

    init {
        string = text.rawString
    }

}