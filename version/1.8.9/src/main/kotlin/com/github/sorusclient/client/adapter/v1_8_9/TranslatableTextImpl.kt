/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.ITranslatableText
import v1_8_9.net.minecraft.text.Text
import v1_8_9.net.minecraft.text.TranslatableText

class TranslatableTextImpl(text: TranslatableText) : com.github.sorusclient.client.adapter.v1_8_9.TextImpl<TranslatableText>(text), ITranslatableText {
    override val arguments: List<Any>

    init {
        arguments = ArrayList()
        for (argument in text.args) {
            if (argument is Text) {
                arguments.add(com.github.sorusclient.client.adapter.v1_8_9.Util.textToApiText(argument))
            } else {
                arguments.add(argument)
            }
        }
    }

    override val key: String
        get() = text.key

}