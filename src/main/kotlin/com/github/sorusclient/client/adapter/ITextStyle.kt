package com.github.sorusclient.client.adapter

interface ITextStyle {
    val clickEvent: ITextClickEvent?
    val hoverEvent: ITextHoverEvent?
    val insertion: String?
    val color: TextFormatting?
    val bold: Boolean
    val italic: Boolean
    val obfuscated: Boolean
    val strikethrough: Boolean
    val underlined: Boolean
}