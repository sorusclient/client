package com.github.sorusclient.client.adapter

interface IText {
    val style: ITextStyle?
    val siblings: List<IText>
}