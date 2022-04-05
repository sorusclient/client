/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.notification

sealed class Interaction {

    data class Button(var text: String = "", var onClick: (() -> Unit)? = null, var closeOnInteract: Boolean = true): Interaction()

}