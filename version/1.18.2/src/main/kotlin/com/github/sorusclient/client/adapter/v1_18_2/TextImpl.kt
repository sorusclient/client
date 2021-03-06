/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.adapter.ITextStyle
import v1_18_2.net.minecraft.text.BaseText
import v1_18_2.net.minecraft.text.Style

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