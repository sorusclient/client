/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.notification

data class Notification(var title: String = "", var content: String = "", var icons: List<Icon> = listOf(Icon("sorus/ui/sorus2.png")), var subIcon: Icon? = null, var timeToLive: Long = 1000, val interactions: MutableList<Interaction> = ArrayList(), var initTime: Long = System.currentTimeMillis(), private var initTimeNanos: Long = System.nanoTime())