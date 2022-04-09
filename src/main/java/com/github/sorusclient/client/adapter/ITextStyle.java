/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

public interface ITextStyle {
    ITextClickEvent getClickEvent();
    ITextHoverEvent getHoverEvent();
    String getInsertion();
    TextFormatting getColor();
    boolean isBold();
    boolean isItalic();
    boolean isObfuscate();
    boolean isStrikethrough();
    boolean isUnderline();
}
