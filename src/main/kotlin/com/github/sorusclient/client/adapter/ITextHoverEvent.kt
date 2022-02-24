package com.github.sorusclient.client.adapter

interface ITextHoverEvent {
    val value: IText?
    val action: HoverEventAction
}