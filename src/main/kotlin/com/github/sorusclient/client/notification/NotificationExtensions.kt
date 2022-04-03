package com.github.sorusclient.client.notification

fun Notification.close() {
    NotificationManager.close(this)
}

fun Notification.display() {
    NotificationManager.display(this)
}