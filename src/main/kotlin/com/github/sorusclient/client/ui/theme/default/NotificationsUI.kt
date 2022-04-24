/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.notification.Interaction
import com.github.sorusclient.client.notification.Notification
import com.github.sorusclient.client.notification.NotificationManager
import com.github.sorusclient.client.notification.close
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.List
import com.github.sorusclient.client.ui.framework.Text
import com.github.sorusclient.client.ui.framework.constraint.Relative
import com.github.sorusclient.client.ui.framework.constraint.Side
import kotlin.math.max

class NotificationsUI(private val defaultTheme: DefaultTheme): Container() {

    init {
        children += List(List.VERTICAL)
            .apply {
                x = Side.POSITIVE.toSide()
                width = 0.2.toRelative()
                setPadding(0.005.toRelative())

                val renderedNotifications = HashMap<Notification, Container>()

                onUpdate += {
                    for (notification in NotificationManager.notifications) {
                        if (!renderedNotifications.containsKey(notification)) {
                            val container = Container()
                                .apply {
                                    val contentSplit = ArrayList<String>()
                                    var currentLine = ""
                                    for (word in notification.content.split(" ")) {
                                        if (AdapterManager.adapter.renderer.getTextWidth("sorus/ui/font/Quicksand-Medium.ttf", "$currentLine$word") > 115) {
                                            contentSplit.add(currentLine)
                                            currentLine = "$word "
                                        } else {
                                            currentLine += "$word "
                                        }
                                    }
                                    contentSplit.add(currentLine)

                                    contentSplit.removeIf { it.isEmpty() }

                                    val height2 = max(2.2, 1.0 + contentSplit.size * 0.6 + if (notification.interactions.isNotEmpty()) { 1.25 } else { 0.0 })
                                    height = {
                                        (0.12 * height2).toCopy()
                                    }.toDependent()

                                    backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                                    borderColor = { defaultTheme.borderColor.value }.toDependent()
                                    borderThickness = 0.001.toRelative()
                                    backgroundCornerRadius = 0.025.toRelative()

                                    setPadding(0.01.toRelative())

                                    children += Container()
                                        .apply {
                                            x = Side.NEGATIVE.toSide()
                                            width = 0.275.toRelative()

                                            children += Container()
                                                .apply {
                                                    width = 0.7.toRelative()
                                                    height = 1.0.toCopy()

                                                    for (icon in notification.icons) {
                                                        children += Container()
                                                            .apply {
                                                                x = 0.0.toRelative()
                                                                y = 0.0.toRelative()

                                                                backgroundImage = icon.icon.toAbsolute()
                                                                backgroundImageBounds = icon.iconBounds
                                                                backgroundCornerRadius = 0.1.toRelative()
                                                            }
                                                    }

                                                    if (notification.subIcon != null) {
                                                        children += Container()
                                                            .apply {
                                                                x = 0.475.toRelative()
                                                                y = 0.475.toRelative()
                                                                width = 0.325.toRelative()
                                                                height = 0.325.toRelative()

                                                                backgroundImage = notification.subIcon!!.icon.toAbsolute()
                                                                backgroundImageBounds = notification.subIcon!!.iconBounds
                                                                backgroundCornerRadius = 0.1625.toRelative()
                                                            }
                                                    }
                                                }
                                        }

                                    children += List(List.VERTICAL)
                                        .apply {
                                            x = Side.POSITIVE.toSide()

                                            setPadding(0.02.toRelative())

                                            children += Container()
                                                .apply {
                                                    height = (1.0 / height2).toRelative()

                                                    children += Text()
                                                        .apply {
                                                            x = Side.NEGATIVE.toSide()

                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                            scale = 0.01.toRelative()
                                                            text = notification.title.toAbsolute()

                                                            textColor = { defaultTheme.elementColor.value }.toDependent()
                                                        }
                                                }

                                            for (content in contentSplit) {
                                                children += Container()
                                                    .apply {
                                                        height = (0.6 / height2).toRelative()

                                                        //backgroundColor = Color.fromRGB(0, 255, 0, 255).toAbsolute()
                                                        //setPadding(0.001.toAbsolute())

                                                        children += Text()
                                                            .apply {
                                                                x = Side.NEGATIVE.toSide()

                                                                setPadding(0.001.toRelative())

                                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                                scale = 0.0085.toRelative()
                                                                text = content.toAbsolute()

                                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                                            }
                                                    }
                                            }

                                            children += List(List.HORIZONTAL)
                                                .apply {
                                                    height = (1.25 / height2).toRelative()

                                                    //setPadding(0.001.toAbsolute())

                                                    for (interaction in notification.interactions) {
                                                        when (interaction) {
                                                            is Interaction.Button -> {
                                                                children += Container()
                                                                    .apply {
                                                                        width = 2.75.toCopy()

                                                                        paddingTop = 0.02.toRelative()
                                                                        paddingLeft = 0.02.toRelative()
                                                                        paddingRight = 0.02.toRelative()

                                                                        backgroundColor = { state: Map<String, Any> ->
                                                                            if (state["clicked"] as Boolean) {
                                                                                defaultTheme.selectedColor.value
                                                                            } else {
                                                                                defaultTheme.midgroundColor.value
                                                                            }
                                                                        }.toDependent()
                                                                        borderColor = { state: Map<String, Any> ->
                                                                            if (state["hovered"] as Boolean) {
                                                                                defaultTheme.selectedBorderColor.value
                                                                            } else {
                                                                                defaultTheme.borderColor.value
                                                                            }
                                                                        }.toDependent()
                                                                        borderThickness = 0.001.toRelative()
                                                                        backgroundCornerRadius = 0.025.toRelative()

                                                                        children += Text()
                                                                            .apply {
                                                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                                                scale = Relative(0.05, true)
                                                                                text = interaction.text.toAbsolute()

                                                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        onClick = {
                                                                            if (interaction.onClick != null) {
                                                                                interaction.onClick!!()
                                                                            }
                                                                            if (interaction.closeOnInteract) {
                                                                                notification.close()
                                                                            }
                                                                        }
                                                                    }
                                                            }
                                                        }
                                                    }
                                                }
                                        }
                                }

                            children += container
                            renderedNotifications[notification] = container
                        }
                    }

                    for ((notification, container) in HashMap(renderedNotifications).entries) {
                        if (!NotificationManager.notifications.contains(notification)) {
                            renderedNotifications.remove(notification)
                            children.remove(container)
                        }
                    }
                }
            }
    }

}