/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.notification

import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import kotlinx.collections.immutable.persistentListOf

object NotificationManager {

    var notifications: List<Notification> = persistentListOf()
        private set

    init {
        EventManager.register<TickEvent> {
            for (notification in notifications) {
                if (System.currentTimeMillis() - notification.initTime > notification.timeToLive) {
                    notifications -= notification
                }
            }
        }
    }

    fun display(notification: Notification) {
        notifications += notification
    }

    fun close(notification: Notification) {
        notifications -= notification
    }

}