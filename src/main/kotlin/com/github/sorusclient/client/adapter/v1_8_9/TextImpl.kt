package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.adapter.ITextStyle
import v1_8_9.net.minecraft.text.BaseText
import v1_8_9.net.minecraft.text.Style
import java.util.ArrayList

open class TextImpl<T : BaseText>(protected val text: T) : IText {

    final override var style: ITextStyle? = null
    final override val siblings: List<IText>

    init {
        siblings = ArrayList<IText>()
        val style: Style = text.style
        this.style = TextStyleImpl(style)
        for (text1 in text.siblings) {
            siblings.add(Util.textToApiText(text1))
        }
    }

}