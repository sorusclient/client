/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.notification

fun Notification.close() {
    NotificationManager.close(this)
}

fun Notification.display() {
    NotificationManager.display(this)
}