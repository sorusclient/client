package com.github.sorusclient.client.notification

data class Notification(var title: String = "", var content: String = "", var icons: List<Icon> = listOf(Icon("sorus/ui/sorus2.png")), var timeToLive: Long = 1000, val interactions: MutableList<Interaction> = ArrayList())