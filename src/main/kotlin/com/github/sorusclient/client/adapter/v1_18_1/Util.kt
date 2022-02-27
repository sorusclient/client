package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.*
import v1_18_1.net.minecraft.text.*
import v1_18_1.net.minecraft.util.Formatting

object Util {

    fun textToApiText(text: Text): IText {
        if (text is TranslatableText) {
            return TranslatableTextImpl(text)
        } else if (text is LiteralText) {
            return LiteralTextImpl(text)
        }
        return TextImpl(text as BaseText)
    }

    fun apiTextToText(text: IText): Text {
        val text1 = when (text) {
            is ITranslatableText -> {
                val translatableText: ITranslatableText = text
                val arguments: MutableList<Any> = ArrayList()
                for (argument in translatableText.arguments) {
                    if (argument is IText) {
                        arguments.add(apiTextToText(argument))
                    } else {
                        arguments.add(argument)
                    }
                }
                TranslatableText(translatableText.key, *arguments.toTypedArray())
            }
            is ILiteralText -> {
                LiteralText(text.string)
            }
            else -> {
                LiteralText("bad")
            }
        }
        if (text.style != null) {
            var style = Style.EMPTY
            if (text.style?.clickEvent != null) {
                val textClickEvent: ITextClickEvent = text.style?.clickEvent!!
                val action: ClickEvent.Action? = when (textClickEvent.action) {
                    ClickEventAction.SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND
                    ClickEventAction.RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND
                    ClickEventAction.OPEN_URL -> ClickEvent.Action.OPEN_URL
                }
                val clickEvent = ClickEvent(action, textClickEvent.value)
                style = style.withClickEvent(clickEvent)
            }
            if (text.style?.hoverEvent != null) {
                val textHoverEvent: ITextHoverEvent = text.style?.hoverEvent!!
                val action: HoverEvent.Action<*>? = when (textHoverEvent.action) {
                    HoverEventAction.SHOW_ENTITY -> HoverEvent.Action.SHOW_ENTITY
                    HoverEventAction.SHOW_TEXT -> HoverEvent.Action.SHOW_TEXT
                    else -> null
                }
                val hoverEvent = HoverEvent(action as HoverEvent.Action<Any>, apiTextToText(textHoverEvent.value!!))

                style = style.withHoverEvent(hoverEvent)
            }
            style = style.withInsertion(text.style?.insertion)

            if (text.style?.color != null) {
                style = style.withFormatting(textFormattingToFormatting(text.style?.color!!))
            }

            style = style
                .withBold(text.style!!.bold)
                .withItalic(text.style!!.italic)
                .withObfuscated(text.style!!.obfuscated)
                .withStrikethrough(text.style!!.strikethrough)
                .withUnderline(text.style!!.underlined)

            text1.style = style
        }
        for (text2 in text.siblings) {
            text1.siblings.add(apiTextToText(text2))
        }

        return text1
    }

    fun formattingToTextFormatting(formatting: Formatting): TextFormatting {
        when (formatting) {
            Formatting.BLACK -> return TextFormatting.BLACK
            Formatting.DARK_BLUE -> return TextFormatting.DARK_BLUE
            Formatting.DARK_GREEN -> return TextFormatting.DARK_GREEN
            Formatting.DARK_AQUA -> return TextFormatting.DARK_AQUA
            Formatting.DARK_RED -> return TextFormatting.DARK_RED
            Formatting.DARK_PURPLE -> return TextFormatting.DARK_PURPLE
            Formatting.GOLD -> return TextFormatting.GOLD
            Formatting.GRAY -> return TextFormatting.GRAY
            Formatting.DARK_GRAY -> return TextFormatting.DARK_GRAY
            Formatting.BLUE -> return TextFormatting.BLUE
            Formatting.GREEN -> return TextFormatting.GREEN
            Formatting.AQUA -> return TextFormatting.AQUA
            Formatting.RED -> return TextFormatting.RED
            Formatting.LIGHT_PURPLE -> return TextFormatting.LIGHT_PURPLE
            Formatting.YELLOW -> return TextFormatting.YELLOW
            Formatting.WHITE -> return TextFormatting.WHITE
            Formatting.OBFUSCATED -> return TextFormatting.OBFUSCATED
            Formatting.BOLD -> return TextFormatting.BOLD
            Formatting.STRIKETHROUGH -> return TextFormatting.STRIKETHROUGH
            Formatting.UNDERLINE -> return TextFormatting.UNDERLINE
            Formatting.ITALIC -> return TextFormatting.ITALIC
            Formatting.RESET -> return TextFormatting.RESET
        }
    }

    private fun textFormattingToFormatting(formatting: TextFormatting): Formatting {
        when (formatting) {
            TextFormatting.BLACK -> return Formatting.BLACK
            TextFormatting.DARK_BLUE -> return Formatting.DARK_BLUE
            TextFormatting.DARK_GREEN -> return Formatting.DARK_GREEN
            TextFormatting.DARK_AQUA -> return Formatting.DARK_AQUA
            TextFormatting.DARK_RED -> return Formatting.DARK_RED
            TextFormatting.DARK_PURPLE -> return Formatting.DARK_PURPLE
            TextFormatting.GOLD -> return Formatting.GOLD
            TextFormatting.GRAY -> return Formatting.GRAY
            TextFormatting.DARK_GRAY -> return Formatting.DARK_GRAY
            TextFormatting.BLUE -> return Formatting.BLUE
            TextFormatting.GREEN -> return Formatting.GREEN
            TextFormatting.AQUA -> return Formatting.AQUA
            TextFormatting.RED -> return Formatting.RED
            TextFormatting.LIGHT_PURPLE -> return Formatting.LIGHT_PURPLE
            TextFormatting.YELLOW -> return Formatting.YELLOW
            TextFormatting.WHITE -> return Formatting.WHITE
            TextFormatting.OBFUSCATED -> return Formatting.OBFUSCATED
            TextFormatting.BOLD -> return Formatting.BOLD
            TextFormatting.STRIKETHROUGH -> return Formatting.STRIKETHROUGH
            TextFormatting.UNDERLINE -> return Formatting.UNDERLINE
            TextFormatting.ITALIC -> return Formatting.ITALIC
            TextFormatting.RESET -> return Formatting.RESET
        }
    }

}