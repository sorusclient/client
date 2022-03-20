package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.*
import com.github.sorusclient.client.adapter.IItem.ItemType
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.screen.GameMenuScreen
import v1_8_9.net.minecraft.client.gui.screen.Screen
import v1_8_9.net.minecraft.text.*
import v1_8_9.net.minecraft.util.Formatting
import v1_8_9.org.lwjgl.input.Keyboard

object Util {

    fun getIdByItemType(itemType: ItemType?): Int {
        return when (itemType) {
            ItemType.LEATHER_HELMET -> 298
            ItemType.LEATHER_CHESTPLATE -> 299
            ItemType.LEATHER_LEGGINGS -> 300
            ItemType.LEATHER_BOOTS -> 301
            ItemType.CHAIN_HELMET -> 302
            ItemType.CHAIN_CHESTPLATE -> 303
            ItemType.CHAIN_LEGGINGS -> 304
            ItemType.CHAIN_BOOTS -> 305
            ItemType.IRON_HELMET -> 306
            ItemType.IRON_CHESTPLATE -> 307
            ItemType.IRON_LEGGINGS -> 308
            ItemType.IRON_BOOTS -> 309
            ItemType.DIAMOND_HELMET -> 310
            ItemType.DIAMOND_CHESTPLATE -> 311
            ItemType.DIAMOND_LEGGINGS -> 312
            ItemType.DIAMOND_BOOTS -> 313
            ItemType.GOLD_HELMET -> 314
            ItemType.GOLD_CHESTPLATE -> 315
            ItemType.GOLD_LEGGINGS -> 316
            ItemType.GOLD_BOOTS -> 317
            else -> -1
        }
    }

    private val keyMap: BiMap<Int, Key> = HashBiMap.create()

    init {
        keyMap[Keyboard.KEY_ESCAPE] = Key.ESCAPE
        keyMap[Keyboard.KEY_1] = Key.ONE
        keyMap[Keyboard.KEY_2] = Key.TWO
        keyMap[Keyboard.KEY_3] = Key.THREE
        keyMap[Keyboard.KEY_4] = Key.FOUR
        keyMap[Keyboard.KEY_5] = Key.FIVE
        keyMap[Keyboard.KEY_6] = Key.SIX
        keyMap[Keyboard.KEY_7] = Key.SEVEN
        keyMap[Keyboard.KEY_8] = Key.EIGHT
        keyMap[Keyboard.KEY_9] = Key.NINE
        keyMap[Keyboard.KEY_0] = Key.ZERO
        keyMap[Keyboard.KEY_BACK] = Key.BACKSPACE
        keyMap[Keyboard.KEY_Q] = Key.Q
        keyMap[Keyboard.KEY_W] = Key.W
        keyMap[Keyboard.KEY_E] = Key.E
        keyMap[Keyboard.KEY_R] = Key.R
        keyMap[Keyboard.KEY_T] = Key.T
        keyMap[Keyboard.KEY_Y] = Key.Y
        keyMap[Keyboard.KEY_U] = Key.U
        keyMap[Keyboard.KEY_I] = Key.I
        keyMap[Keyboard.KEY_O] = Key.O
        keyMap[Keyboard.KEY_P] = Key.P
        keyMap[Keyboard.KEY_RETURN] = Key.ENTER
        keyMap[Keyboard.KEY_LCONTROL] = Key.CONTROL_LEFT
        keyMap[Keyboard.KEY_A] = Key.A
        keyMap[Keyboard.KEY_S] = Key.S
        keyMap[Keyboard.KEY_D] = Key.D
        keyMap[Keyboard.KEY_F] = Key.F
        keyMap[Keyboard.KEY_G] = Key.G
        keyMap[Keyboard.KEY_H] = Key.H
        keyMap[Keyboard.KEY_J] = Key.J
        keyMap[Keyboard.KEY_K] = Key.K
        keyMap[Keyboard.KEY_L] = Key.L
        keyMap[Keyboard.KEY_LSHIFT] = Key.SHIFT_LEFT
        keyMap[Keyboard.KEY_Z] = Key.Z
        keyMap[Keyboard.KEY_X] = Key.X
        keyMap[Keyboard.KEY_C] = Key.C
        keyMap[Keyboard.KEY_V] = Key.V
        keyMap[Keyboard.KEY_B] = Key.B
        keyMap[Keyboard.KEY_N] = Key.N
        keyMap[Keyboard.KEY_M] = Key.M
        keyMap[Keyboard.KEY_SLASH] = Key.SLASH
        keyMap[Keyboard.KEY_RSHIFT] = Key.SHIFT_RIGHT
        keyMap[Keyboard.KEY_LMENU] = Key.ALT_LEFT
        keyMap[Keyboard.KEY_SPACE] = Key.SPACE
        keyMap[Keyboard.KEY_UP] = Key.ARROW_UP
        keyMap[Keyboard.KEY_DOWN] = Key.ARROW_DOWN
        keyMap[Keyboard.KEY_F1] = Key.F1
        keyMap[Keyboard.KEY_F2] = Key.F2
        keyMap[Keyboard.KEY_F3] = Key.F3
        keyMap[Keyboard.KEY_F4] = Key.F4
        keyMap[Keyboard.KEY_F5] = Key.F5
        keyMap[Keyboard.KEY_F6] = Key.F6
        keyMap[Keyboard.KEY_F7] = Key.F7
        keyMap[Keyboard.KEY_F8] = Key.F8
        keyMap[Keyboard.KEY_F9] = Key.F9
        keyMap[Keyboard.KEY_F10] = Key.F10
        keyMap[Keyboard.KEY_F11] = Key.F11
        keyMap[Keyboard.KEY_F12] = Key.F12
    }

    fun getKey(id: Int): Key {
        return keyMap.getOrDefault(id, Key.UNKNOWN)
    }

    fun getKeyCode(key: Key): Int {
        return keyMap.inverse().getOrDefault(key, -1)
    }

    private val buttonMap: BiMap<Int, Button> = HashBiMap.create()

    init {
        buttonMap[0] = Button.PRIMARY
        buttonMap[-1] = Button.NONE
    }

    fun getButton(id: Int): Button {
        return buttonMap.getOrDefault(id, Button.UNKNOWN)
    }

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
            val style = Style()
            if (text.style?.clickEvent != null) {
                val textClickEvent: ITextClickEvent = text.style?.clickEvent!!
                val action: ClickEvent.Action? = when (textClickEvent.action) {
                    ClickEventAction.SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND
                    ClickEventAction.RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND
                    ClickEventAction.OPEN_URL -> ClickEvent.Action.OPEN_URL
                }
                val clickEvent = ClickEvent(action, textClickEvent.value)
                style.clickEvent = clickEvent
            }
            if (text.style?.hoverEvent != null) {
                val textHoverEvent: ITextHoverEvent = text.style?.hoverEvent!!
                val action: HoverEvent.Action? = when (textHoverEvent.action) {
                    HoverEventAction.SHOW_ENTITY -> HoverEvent.Action.SHOW_ENTITY
                    HoverEventAction.SHOW_ACHIEVEMENT -> HoverEvent.Action.SHOW_ACHIEVEMENT
                    HoverEventAction.SHOW_TEXT -> HoverEvent.Action.SHOW_TEXT
                }
                val hoverEvent = HoverEvent(action, apiTextToText(textHoverEvent.value!!))
                style.hoverEvent = hoverEvent
            }
            style.insertion = text.style?.insertion
            if (text.style?.color != null) {
                style.setFormatting(textFormattingToFormatting(text.style?.color!!))
            }
            style.isBold = text.style!!.bold
            style.isItalic = text.style!!.italic
            style.isObfuscated = text.style!!.obfuscated
            style.isStrikethrough = text.style!!.strikethrough
            style.setUnderline(text.style!!.underlined)

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

    fun screenToScreenType(screen: Screen?): ScreenType {
        return when (screen) {
            is GameMenuScreen -> {
                ScreenType.GAME_MENU
            }
            is DummyScreen -> {
                ScreenType.DUMMY
            }
            null -> {
                ScreenType.IN_GAME
            }
            else -> {
                ScreenType.UNKNOWN
            }
        }
    }

}