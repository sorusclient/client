package com.github.sorusclient.client.notification

import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import kotlinx.collections.immutable.persistentListOf

object NotificationManager {

    var notifications: List<Notification> = persistentListOf()
        private set

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
                    notifications -= notification
                    notificationTimes.remove(notification)
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