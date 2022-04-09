/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.IText;
import com.github.sorusclient.client.adapter.ITextStyle;
import v1_18_2.net.minecraft.text.BaseText;

import java.util.ArrayList;
import java.util.List;

public class TextImpl<T extends BaseText> implements IText {

    protected final T text;
    protected final ITextStyle style;
    protected final List<IText> siblings;

    public TextImpl(T text) {
        this.text = text;
        style = new TextStyleImpl(text.getStyle());
        siblings = new ArrayList<>();
        for (var sibling : text.getSiblings()) {
            siblings.add(Util.textToApiText(sibling));
        }
    }

    @Override
    public ITextStyle getStyle() {
        return style;
    }

    @Override
    public List<IText> getSiblings() {
        return siblings;
    }

}
