/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

interface ITextStyle {
    val clickEvent: ITextClickEvent?
    val hoverEvent: ITextHoverEvent?
    val insertion: String?
    val color: TextFormatting?
    val bold: Boolean
    val italic: Boolean
    val obfuscated: Boolean
    val strikethrough: Boolean
    val underlined: Boolean
}