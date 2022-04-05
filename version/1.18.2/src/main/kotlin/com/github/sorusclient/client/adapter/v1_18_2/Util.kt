/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.*
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import v1_18_2.org.lwjgl.glfw.GLFW
import v1_18_2.net.minecraft.client.gui.screen.GameMenuScreen
import v1_18_2.net.minecraft.client.gui.screen.Screen
import v1_18_2.net.minecraft.item.Item
import v1_18_2.net.minecraft.item.Items
import v1_18_2.net.minecraft.text.*
import v1_18_2.net.minecraft.util.Formatting

object Util {

    fun getItemByItemType(itemType: IItem.ItemType?): Item? {
        return when (itemType) {
            IItem.ItemType.LEATHER_HELMET -> Items.LEATHER_HELMET
            IItem.ItemType.LEATHER_CHESTPLATE -> Items.LEATHER_CHESTPLATE
            IItem.ItemType.LEATHER_LEGGINGS -> Items.LEATHER_LEGGINGS
            IItem.ItemType.LEATHER_BOOTS -> Items.LEATHER_BOOTS
            IItem.ItemType.CHAIN_HELMET -> Items.CHAINMAIL_HELMET
            IItem.ItemType.CHAIN_CHESTPLATE -> Items.CHAINMAIL_CHESTPLATE
            IItem.ItemType.CHAIN_LEGGINGS -> Items.CHAINMAIL_LEGGINGS
            IItem.ItemType.CHAIN_BOOTS -> Items.CHAINMAIL_BOOTS
            IItem.ItemType.IRON_HELMET -> Items.IRON_HELMET
            IItem.ItemType.IRON_CHESTPLATE -> Items.IRON_CHESTPLATE
            IItem.ItemType.IRON_LEGGINGS -> Items.IRON_LEGGINGS
            IItem.ItemType.IRON_BOOTS -> Items.IRON_BOOTS
            IItem.ItemType.DIAMOND_HELMET -> Items.DIAMOND_HELMET
            IItem.ItemType.DIAMOND_CHESTPLATE -> Items.DIAMOND_CHESTPLATE
            IItem.ItemType.DIAMOND_LEGGINGS -> Items.DIAMOND_LEGGINGS
            IItem.ItemType.DIAMOND_BOOTS -> Items.DIAMOND_BOOTS
            IItem.ItemType.GOLD_HELMET -> Items.GOLDEN_HELMET
            IItem.ItemType.GOLD_CHESTPLATE -> Items.GOLDEN_CHESTPLATE
            IItem.ItemType.GOLD_LEGGINGS -> Items.GOLDEN_LEGGINGS
            IItem.ItemType.GOLD_BOOTS -> Items.GOLDEN_BOOTS
            else -> null
        }
    }

    private val keyMap: BiMap<Int, Key> = HashBiMap.create()

    init {
        keyMap[GLFW.GLFW_KEY_ESCAPE] = Key.ESCAPE
        keyMap[GLFW.GLFW_KEY_BACKSPACE] = Key.BACKSPACE
        keyMap[GLFW.GLFW_KEY_Q] = Key.Q
        keyMap[GLFW.GLFW_KEY_W] = Key.W
        keyMap[GLFW.GLFW_KEY_E] = Key.E
        keyMap[GLFW.GLFW_KEY_R] = Key.R
        keyMap[GLFW.GLFW_KEY_T] = Key.T
        keyMap[GLFW.GLFW_KEY_Y] = Key.Y
        keyMap[GLFW.GLFW_KEY_U] = Key.U
        keyMap[GLFW.GLFW_KEY_I] = Key.I
        keyMap[GLFW.GLFW_KEY_O] = Key.O
        keyMap[GLFW.GLFW_KEY_P] = Key.P
        keyMap[GLFW.GLFW_KEY_ENTER] = Key.ENTER
        keyMap[GLFW.GLFW_KEY_LEFT_CONTROL] = Key.CONTROL_LEFT
        keyMap[GLFW.GLFW_KEY_A] = Key.A
        keyMap[GLFW.GLFW_KEY_S] = Key.S
        keyMap[GLFW.GLFW_KEY_D] = Key.D
        keyMap[GLFW.GLFW_KEY_F] = Key.F
        keyMap[GLFW.GLFW_KEY_G] = Key.G
        keyMap[GLFW.GLFW_KEY_H] = Key.H
        keyMap[GLFW.GLFW_KEY_J] = Key.J
        keyMap[GLFW.GLFW_KEY_K] = Key.K
        keyMap[GLFW.GLFW_KEY_L] = Key.L
        keyMap[GLFW.GLFW_KEY_LEFT_SHIFT] = Key.SHIFT_LEFT
        keyMap[GLFW.GLFW_KEY_Z] = Key.Z
        keyMap[GLFW.GLFW_KEY_X] = Key.X
        keyMap[GLFW.GLFW_KEY_C] = Key.C
        keyMap[GLFW.GLFW_KEY_V] = Key.V
        keyMap[GLFW.GLFW_KEY_B] = Key.B
        keyMap[GLFW.GLFW_KEY_N] = Key.N
        keyMap[GLFW.GLFW_KEY_M] = Key.M
        keyMap[GLFW.GLFW_KEY_RIGHT_SHIFT] = Key.SHIFT_RIGHT
        keyMap[GLFW.GLFW_KEY_LEFT_ALT] = Key.ALT_LEFT
        keyMap[GLFW.GLFW_KEY_SPACE] = Key.SPACE
        keyMap[GLFW.GLFW_KEY_UP] = Key.ARROW_UP
        keyMap[GLFW.GLFW_KEY_DOWN] = Key.ARROW_DOWN
        keyMap[GLFW.GLFW_KEY_1] = Key.ONE
        keyMap[GLFW.GLFW_KEY_2] = Key.TWO
        keyMap[GLFW.GLFW_KEY_3] = Key.THREE
        keyMap[GLFW.GLFW_KEY_4] = Key.FOUR
        keyMap[GLFW.GLFW_KEY_5] = Key.FIVE
        keyMap[GLFW.GLFW_KEY_6] = Key.SIX
        keyMap[GLFW.GLFW_KEY_7] = Key.SEVEN
        keyMap[GLFW.GLFW_KEY_8] = Key.EIGHT
        keyMap[GLFW.GLFW_KEY_9] = Key.NINE
        keyMap[GLFW.GLFW_KEY_0] = Key.ZERO
        keyMap[GLFW.GLFW_KEY_SLASH] = Key.SLASH
        keyMap[GLFW.GLFW_KEY_F1] = Key.F1
        keyMap[GLFW.GLFW_KEY_F2] = Key.F2
        keyMap[GLFW.GLFW_KEY_F3] = Key.F3
        keyMap[GLFW.GLFW_KEY_F4] = Key.F4
        keyMap[GLFW.GLFW_KEY_F5] = Key.F5
        keyMap[GLFW.GLFW_KEY_F6] = Key.F6
        keyMap[GLFW.GLFW_KEY_F7] = Key.F7
        keyMap[GLFW.GLFW_KEY_F8] = Key.F8
        keyMap[GLFW.GLFW_KEY_F9] = Key.F9
        keyMap[GLFW.GLFW_KEY_F10] = Key.F10
        keyMap[GLFW.GLFW_KEY_F11] = Key.F11
        keyMap[GLFW.GLFW_KEY_F12] = Key.F12
    }

    fun getKey(id: Int): Key {
        return keyMap.getOrDefault(id, Key.UNKNOWN)
    }

    fun getKeyCode(key: Key): Int {
        return keyMap.inverse().getOrDefault(key, -1);
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