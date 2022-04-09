/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.val;
import v1_8_9.net.minecraft.client.gui.screen.GameMenuScreen;
import v1_8_9.net.minecraft.client.gui.screen.Screen;
import v1_8_9.net.minecraft.item.Item;
import v1_8_9.net.minecraft.item.Items;
import v1_8_9.net.minecraft.text.*;
import v1_8_9.net.minecraft.util.Formatting;
import v1_8_9.org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class Util {

    public static Item getItemByItemType(ItemType itemType) {
        return switch (itemType) {
            case LEATHER_HELMET -> Items.LEATHER_HELMET;
            case LEATHER_CHESTPLATE -> Items.LEATHER_CHESTPLATE;
            case LEATHER_LEGGINGS -> Items.LEATHER_LEGGINGS;
            case LEATHER_BOOTS -> Items.LEATHER_BOOTS;
            case CHAIN_HELMET -> Items.CHAINMAIL_HELMET;
            case CHAIN_CHESTPLATE -> Items.CHAINMAIL_CHESTPLATE;
            case CHAIN_LEGGINGS -> Items.CHAINMAIL_LEGGINGS;
            case CHAIN_BOOTS -> Items.CHAINMAIL_BOOTS;
            case IRON_HELMET -> Items.IRON_HELMET;
            case IRON_CHESTPLATE -> Items.IRON_CHESTPLATE;
            case IRON_LEGGINGS -> Items.IRON_LEGGINGS;
            case IRON_BOOTS -> Items.IRON_BOOTS;
            case DIAMOND_HELMET -> Items.DIAMOND_HELMET;
            case DIAMOND_CHESTPLATE -> Items.DIAMOND_CHESTPLATE;
            case DIAMOND_LEGGINGS -> Items.DIAMOND_LEGGINGS;
            case DIAMOND_BOOTS -> Items.DIAMOND_BOOTS;
            case GOLD_HELMET -> Items.GOLDEN_HELMET;
            case GOLD_CHESTPLATE -> Items.GOLDEN_CHESTPLATE;
            case GOLD_LEGGINGS -> Items.GOLDEN_LEGGINGS;
            case GOLD_BOOTS -> Items.GOLDEN_BOOTS;
            default -> throw new IllegalStateException("Unexpected value: " + itemType);
        };
    }

    private static final BiMap<Integer, Key> keyMap = HashBiMap.create();

    static {
        keyMap.put(Keyboard.KEY_ESCAPE, Key.ESCAPE);
        keyMap.put(Keyboard.KEY_1, Key.ONE);
        keyMap.put(Keyboard.KEY_2, Key.TWO);
        keyMap.put(Keyboard.KEY_3, Key.THREE);
        keyMap.put(Keyboard.KEY_4, Key.FOUR);
        keyMap.put(Keyboard.KEY_5, Key.FIVE);
        keyMap.put(Keyboard.KEY_6, Key.SIX);
        keyMap.put(Keyboard.KEY_7, Key.SEVEN);
        keyMap.put(Keyboard.KEY_8, Key.EIGHT);
        keyMap.put(Keyboard.KEY_9, Key.NINE);
        keyMap.put(Keyboard.KEY_0, Key.ZERO);
        keyMap.put(Keyboard.KEY_BACK, Key.BACKSPACE);
        keyMap.put(Keyboard.KEY_Q, Key.Q);
        keyMap.put(Keyboard.KEY_W, Key.W);
        keyMap.put(Keyboard.KEY_E, Key.E);
        keyMap.put(Keyboard.KEY_R, Key.R);
        keyMap.put(Keyboard.KEY_T, Key.T);
        keyMap.put(Keyboard.KEY_Y, Key.Y);
        keyMap.put(Keyboard.KEY_U, Key.U);
        keyMap.put(Keyboard.KEY_I, Key.I);
        keyMap.put(Keyboard.KEY_O, Key.O);
        keyMap.put(Keyboard.KEY_P, Key.P);
        keyMap.put(Keyboard.KEY_RETURN, Key.ENTER);
        keyMap.put(Keyboard.KEY_LCONTROL, Key.CONTROL_LEFT);
        keyMap.put(Keyboard.KEY_A, Key.A);
        keyMap.put(Keyboard.KEY_S, Key.S);
        keyMap.put(Keyboard.KEY_D, Key.D);
        keyMap.put(Keyboard.KEY_F, Key.F);
        keyMap.put(Keyboard.KEY_G, Key.G);
        keyMap.put(Keyboard.KEY_H, Key.H);
        keyMap.put(Keyboard.KEY_J, Key.J);
        keyMap.put(Keyboard.KEY_K, Key.K);
        keyMap.put(Keyboard.KEY_L, Key.L);
        keyMap.put(Keyboard.KEY_LSHIFT, Key.SHIFT_LEFT);
        keyMap.put(Keyboard.KEY_Z, Key.Z);
        keyMap.put(Keyboard.KEY_X, Key.X);
        keyMap.put(Keyboard.KEY_C, Key.C);
        keyMap.put(Keyboard.KEY_V, Key.V);
        keyMap.put(Keyboard.KEY_B, Key.B);
        keyMap.put(Keyboard.KEY_N, Key.N);
        keyMap.put(Keyboard.KEY_M, Key.M);
        keyMap.put(Keyboard.KEY_SLASH, Key.SLASH);
        keyMap.put(Keyboard.KEY_RSHIFT, Key.SHIFT_RIGHT);
        keyMap.put(Keyboard.KEY_LMENU, Key.ALT_LEFT);
        keyMap.put(Keyboard.KEY_SPACE, Key.SPACE);
        keyMap.put(Keyboard.KEY_UP, Key.ARROW_UP);
        keyMap.put(Keyboard.KEY_DOWN, Key.ARROW_DOWN);
        keyMap.put(Keyboard.KEY_F1, Key.F1);
        keyMap.put(Keyboard.KEY_F2, Key.F2);
        keyMap.put(Keyboard.KEY_F3, Key.F3);
        keyMap.put(Keyboard.KEY_F4, Key.F4);
        keyMap.put(Keyboard.KEY_F5, Key.F5);
        keyMap.put(Keyboard.KEY_F6, Key.F6);
        keyMap.put(Keyboard.KEY_F7, Key.F7);
        keyMap.put(Keyboard.KEY_F8, Key.F8);
        keyMap.put(Keyboard.KEY_F9, Key.F9);
        keyMap.put(Keyboard.KEY_F10, Key.F10);
        keyMap.put(Keyboard.KEY_F11, Key.F11);
        keyMap.put(Keyboard.KEY_F12, Key.F12);
    }

    public static Key getKey(int id) {
        return keyMap.getOrDefault(id, Key.UNKNOWN);
    }

    public static int getKeyCode(Key key) {
        return keyMap.inverse().getOrDefault(key, -1);
    }

    private static final BiMap<Integer, Button> buttonMap = HashBiMap.create();

    static {
        buttonMap.put(0, Button.PRIMARY);
        buttonMap.put(-1, Button.NONE);
    }

    public static Button getButton(int id) {
        return buttonMap.getOrDefault(id, Button.UNKNOWN);
    }

    public static IText textToApiText(Text text) {
        if (text instanceof TranslatableText translatableText) {
            return new TranslatableTextImpl(translatableText);
        } else if (text instanceof LiteralText literalText) {
            return new LiteralTextImpl(literalText);
        }
        return new TextImpl<>((BaseText) text);
    }

    public static Text apiTextToText(IText text) {
        BaseText text1;
        if (text instanceof ITranslatableText translatableText) {
            val arguments = new ArrayList<>();
            for (val argument : translatableText.getArguments()) {
                if (argument instanceof IText iText) {
                    arguments.add(apiTextToText(iText));
                } else {
                    arguments.add(argument);
                }
            }
            
            text1 = new TranslatableText(translatableText.getKey(), arguments.toArray());
        } else if (text instanceof ILiteralText literalText) {
            text1 = new LiteralText(literalText.getString());
        } else {
            throw new IllegalStateException("Unexpected value: " + text);
        }
        
        if (text.getStyle() != null) {
            var style = new Style();
            if (text.getStyle().getClickEvent() != null) {
                val textClickEvent = text.getStyle().getClickEvent();
                
                val action = switch (textClickEvent.getAction()) {
                    case SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND;
                    case RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND;
                    case OPEN_URL -> ClickEvent.Action.OPEN_URL;
                };
                
                val clickEvent = new ClickEvent(action, textClickEvent.getValue());
                style.setClickEvent(clickEvent);
            }
            if (text.getStyle().getHoverEvent() != null) {
                val textHoverEvent = text.getStyle().getHoverEvent();
                
                val action= switch (textHoverEvent.getAction()) {
                    case SHOW_ENTITY -> HoverEvent.Action.SHOW_ENTITY;
                    case SHOW_TEXT -> HoverEvent.Action.SHOW_TEXT;
                    default -> throw new IllegalStateException("Unexpected value: " + textHoverEvent.getAction());
                };
                
                val hoverEvent = new HoverEvent(action, apiTextToText(textHoverEvent.getValue()));

                style.setHoverEvent(hoverEvent);
            }
            style.setInsertion(text.getStyle().getInsertion());

            if (text.getStyle().getColor() != null) {
                style.setFormatting(textFormattingToFormatting(text.getStyle().getColor()));
            }

            style = style
                    .setBold(text.getStyle().isBold())
                    .setItalic(text.getStyle().isItalic())
                    .setObfuscated(text.getStyle().isObfuscate())
                    .setStrikethrough(text.getStyle().isStrikethrough())
                    .setUnderline(text.getStyle().isUnderline());

            text1.setStyle(style);
        }
        
        for (val sibling : text.getSiblings()) {
            text1.getSiblings().add(apiTextToText(sibling));
        }

        return text1;
    }

    public static TextFormatting formattingToTextFormatting(Formatting formatting) {
        return switch (formatting) {
            case BLACK -> TextFormatting.BLACK;
            case DARK_BLUE -> TextFormatting.DARK_BLUE;
            case DARK_GREEN -> TextFormatting.DARK_GREEN;
            case DARK_AQUA -> TextFormatting.DARK_AQUA;
            case DARK_RED -> TextFormatting.DARK_RED;
            case DARK_PURPLE -> TextFormatting.DARK_PURPLE;
            case GOLD -> TextFormatting.GOLD;
            case GRAY -> TextFormatting.GRAY;
            case DARK_GRAY -> TextFormatting.DARK_GRAY;
            case BLUE -> TextFormatting.BLUE;
            case GREEN -> TextFormatting.GREEN;
            case AQUA -> TextFormatting.AQUA;
            case RED -> TextFormatting.RED;
            case LIGHT_PURPLE -> TextFormatting.LIGHT_PURPLE;
            case YELLOW -> TextFormatting.YELLOW;
            case WHITE -> TextFormatting.WHITE;
            case OBFUSCATED -> TextFormatting.OBFUSCATED;
            case BOLD -> TextFormatting.BOLD;
            case STRIKETHROUGH -> TextFormatting.STRIKETHROUGH;
            case UNDERLINE -> TextFormatting.UNDERLINE;
            case ITALIC -> TextFormatting.ITALIC;
            case RESET -> TextFormatting.RESET;
        };
    }

    private static Formatting textFormattingToFormatting(TextFormatting formatting) {
        return switch (formatting) {
            case BLACK -> Formatting.BLACK;
            case DARK_BLUE -> Formatting.DARK_BLUE;
            case DARK_GREEN -> Formatting.DARK_GREEN;
            case DARK_AQUA -> Formatting.DARK_AQUA;
            case DARK_RED -> Formatting.DARK_RED;
            case DARK_PURPLE -> Formatting.DARK_PURPLE;
            case GOLD -> Formatting.GOLD;
            case GRAY -> Formatting.GRAY;
            case DARK_GRAY -> Formatting.DARK_GRAY;
            case BLUE -> Formatting.BLUE;
            case GREEN -> Formatting.GREEN;
            case AQUA -> Formatting.AQUA;
            case RED -> Formatting.RED;
            case LIGHT_PURPLE -> Formatting.LIGHT_PURPLE;
            case YELLOW -> Formatting.YELLOW;
            case WHITE -> Formatting.WHITE;
            case OBFUSCATED -> Formatting.OBFUSCATED;
            case BOLD -> Formatting.BOLD;
            case STRIKETHROUGH -> Formatting.STRIKETHROUGH;
            case UNDERLINE -> Formatting.UNDERLINE;
            case ITALIC -> Formatting.ITALIC;
            case RESET -> Formatting.RESET;
        };
    }

    public static ScreenType screenToScreenType(Screen screen) {
        if (screen instanceof GameMenuScreen) {
            return ScreenType.GAME_MENU;
        } else if (screen instanceof DummyScreen) {
            return ScreenType.DUMMY;
        } else if (screen == null) {
            return ScreenType.IN_GAME;
        } else {
            return ScreenType.UNKNOWN;
        }
    }
    
}
