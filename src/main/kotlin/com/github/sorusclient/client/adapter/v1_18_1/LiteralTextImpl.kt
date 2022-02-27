package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.ILiteralText
import v1_18_1.net.minecraft.text.LiteralText

class LiteralTextImpl(text: LiteralText) : TextImpl<LiteralText>(text), ILiteralText {
    override val string: String?

    init {
        string = text.rawString
    }

}