/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.*
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
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
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_ESCAPE] = Key.ESCAPE
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_1] = Key.ONE
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_2] = Key.TWO
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_3] = Key.THREE
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_4] = Key.FOUR
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_5] = Key.FIVE
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_6] = Key.SIX
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_7] = Key.SEVEN
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_8] = Key.EIGHT
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_9] = Key.NINE
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_0] = Key.ZERO
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_BACK] = Key.BACKSPACE
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_Q] = Key.Q
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_W] = Key.W
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_E] = Key.E
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_R] = Key.R
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_T] = Key.T
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_Y] = Key.Y
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_U] = Key.U
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_I] = Key.I
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_O] = Key.O
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_P] = Key.P
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_RETURN] = Key.ENTER
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_LCONTROL] = Key.CONTROL_LEFT
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_A] = Key.A
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_S] = Key.S
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_D] = Key.D
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F] = Key.F
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_G] = Key.G
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_H] = Key.H
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_J] = Key.J
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_K] = Key.K
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_L] = Key.L
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_LSHIFT] = Key.SHIFT_LEFT
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_Z] = Key.Z
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_X] = Key.X
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_C] = Key.C
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_V] = Key.V
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_B] = Key.B
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_N] = Key.N
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_M] = Key.M
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_SLASH] = Key.SLASH
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_RSHIFT] = Key.SHIFT_RIGHT
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_LMENU] = Key.ALT_LEFT
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_SPACE] = Key.SPACE
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_UP] = Key.ARROW_UP
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_DOWN] = Key.ARROW_DOWN
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F1] = Key.F1
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F2] = Key.F2
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F3] = Key.F3
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F4] = Key.F4
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F5] = Key.F5
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F6] = Key.F6
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F7] = Key.F7
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F8] = Key.F8
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F9] = Key.F9
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F10] = Key.F10
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F11] = Key.F11
        com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap[Keyboard.KEY_F12] = Key.F12
    }

    fun getKey(id: Int): Key {
        return com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap.getOrDefault(id, Key.UNKNOWN)
    }

    fun getKeyCode(key: Key): Int {
        return com.github.sorusclient.client.adapter.v1_8_9.Util.keyMap.inverse().getOrDefault(key, -1)
    }

    private val buttonMap: BiMap<Int, Button> = HashBiMap.create()

    init {
        com.github.sorusclient.client.adapter.v1_8_9.Util.buttonMap[0] = Button.PRIMARY
        com.github.sorusclient.client.adapter.v1_8_9.Util.buttonMap[-1] = Button.NONE
    }

    fun getButton(id: Int): Button {
        return com.github.sorusclient.client.adapter.v1_8_9.Util.buttonMap.getOrDefault(id, Button.UNKNOWN)
    }

    fun textToApiText(text: Text): IText {
        if (text is TranslatableText) {
            return com.github.sorusclient.client.adapter.v1_8_9.TranslatableTextImpl(text)
        } else if (text is LiteralText) {
            return com.github.sorusclient.client.adapter.v1_8_9.LiteralTextImpl(text)
        }
        return com.github.sorusclient.client.adapter.v1_8_9.TextImpl(text as BaseText)
    }

    fun apiTextToText(text: IText): Text {
        val text1 = when (text) {
            is ITranslatableText -> {
                val translatableText: ITranslatableText = text
                val arguments: MutableList<Any> = ArrayList()
                for (argument in translatableText.arguments) {
                    if (argument is IText) {
                        arguments.add(com.github.sorusclient.client.adapter.v1_8_9.Util.apiTextToText(argument))
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
                val hoverEvent = HoverEvent(action,
                    com.github.sorusclient.client.adapter.v1_8_9.Util.apiTextToText(textHoverEvent.value!!)
                )
                style.hoverEvent = hoverEvent
            }
            style.insertion = text.style?.insertion
            if (text.style?.color != null) {
                style.setFormatting(com.github.sorusclient.client.adapter.v1_8_9.Util.textFormattingToFormatting(text.style?.color!!))
            }
            style.isBold = text.style!!.isBold
            style.isItalic = text.style!!.isItalic
            style.isObfuscated = text.style!!.isObfuscate
            style.isStrikethrough = text.style!!.isStrikethrough
            style.setUnderline(text.style!!.isUnderline)

            text1.style = style
        }
        for (text2 in text.siblings) {
            text1.siblings.add(com.github.sorusclient.client.adapter.v1_8_9.Util.apiTextToText(text2))
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
            is com.github.sorusclient.client.adapter.v1_8_9.DummyScreen -> {
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