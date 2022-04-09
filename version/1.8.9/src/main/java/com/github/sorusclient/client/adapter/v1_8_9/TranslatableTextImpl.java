/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.ITranslatableText;
import v1_8_9.net.minecraft.text.Text;
import v1_8_9.net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class TranslatableTextImpl extends TextImpl<TranslatableText> implements ITranslatableText {

    protected List<Object> arguments;

    public TranslatableTextImpl(TranslatableText text) {
        super(text);

        arguments = new ArrayList<>();
        for (var argument : text.getArgs()) {
            if (argument instanceof Text text1) {
                arguments.add(Util.textToApiText(text1));
            } else {
                arguments.add(argument);
            }
        }
    }

    @Override
    public String getKey() {
        return text.getKey();
    }

    @Override
    public List<Object> getArguments() {
        return arguments;
    }

}
