package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.adapter.ITextStyle
import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.adapter.ITextClickEvent
import com.github.sorusclient.client.adapter.ITextHoverEvent
import com.github.sorusclient.client.adapter.TextFormatting

interface ILiteralText : IText {
    val string: String?
}