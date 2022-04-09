/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.ITextClickEvent;
import com.github.sorusclient.client.adapter.ITextHoverEvent;
import com.github.sorusclient.client.adapter.ITextStyle;
import com.github.sorusclient.client.adapter.TextFormatting;
import v1_8_9.net.minecraft.text.Style;
import v1_8_9.net.minecraft.util.Formatting;

public class TextStyleImpl implements ITextStyle {

    private final ITextClickEvent clickEvent;
    private final ITextHoverEvent hoverEvent;
    private final String insertion;
    private final TextFormatting color;
    private final boolean bold;
    private final boolean italic;
    private final boolean obfuscate;
    private final boolean strikethrough;
    private final boolean underline;

    public TextStyleImpl(Style style) {
        clickEvent = style.getClickEvent() != null ? new TextClickEventImpl(style.getClickEvent()) : null;
        hoverEvent = style.getHoverEvent() != null ? new TextHoverEventImpl(style.getHoverEvent()) : null;
        insertion = style.getInsertion();

        if (style.getColor() != null) {
            var formatting = Formatting.byName(style.getColor().getName());
            color = formatting != null ? Util.INSTANCE.formattingToTextFormatting(formatting) : null;
        } else {
            color = null;
        }

        bold = style.isBold();
        italic = style.isItalic();
        obfuscate = style.isObfuscated();
        strikethrough = style.isStrikethrough();
        underline = style.isUnderlined();
    }

    @Override
    public ITextClickEvent getClickEvent() {
        return clickEvent;
    }

    @Override
    public ITextHoverEvent getHoverEvent() {
        return hoverEvent;
    }

    @Override
    public String getInsertion() {
        return insertion;
    }

    @Override
    public TextFormatting getColor() {
        return color;
    }

    @Override
    public boolean isBold() {
        return bold;
    }

    @Override
    public boolean isItalic() {
        return italic;
    }

    @Override
    public boolean isObfuscate() {
        return obfuscate;
    }

    @Override
    public boolean isStrikethrough() {
        return strikethrough;
    }

    @Override
    public boolean isUnderline() {
        return underline;
    }

}
