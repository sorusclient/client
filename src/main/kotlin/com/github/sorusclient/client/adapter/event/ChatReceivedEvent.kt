package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.adapter.IText
import com.github.sorusclient.client.event.Event

class ChatReceivedEvent(val message: String, val text: IText): Event()