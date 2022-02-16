package com.github.sorusclient.client.adapter

interface ITextStyle {
    val clickEvent: ITextClickEvent?
    val hoverEvent: ITextHoverEvent?
    val insertion: String?
    val color: TextFormatting?
}