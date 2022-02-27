package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.ITranslatableText
import v1_18_1.net.minecraft.text.Text
import v1_18_1.net.minecraft.text.TranslatableText
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