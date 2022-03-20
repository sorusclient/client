package com.github.sorusclient.client.notification

import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager

object NotificationManager {

    val notifications: MutableList<Notification> = ArrayList()

    private val notificationTimes: MutableMap<Notification, Long> = HashMap()

    init {
        EventManager.register<TickEvent> {
            for (notification in notifications) {
                if (!notificationTimes.containsKey(notification)) {
                    notificationTimes[notification] = System.currentTimeMillis()
                }
            }

            for ((notification, startTime) in HashMap(notificationTimes)) {
                if (System.currentTimeMillis() - startTime > 5000L) {
                    notifications.remove(notification)
                    notificationTimes.remove(notification)
                }
            }
        }
    }

}