package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.ITranslatableText
import v1_8_9.net.minecraft.text.Text
import v1_8_9.net.minecraft.text.TranslatableText
import java.util.ArrayList

class TranslatableTextImpl(text: TranslatableText) : TextImpl<TranslatableText>(text), ITranslatableText {
    override val arguments: List<Any>

    init {
        arguments = ArrayList()
        for (argument in text.args) {
            if (argument is Text) {
                arguments.add(Util.textToApiText(argument))
            } else {
                arguments.add(argument)
            }
        }
    }

    override val key: String
        get() = text.key

}