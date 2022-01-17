package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.event.Event

class SorusCustomPacketEvent(val channel: String, val contents: String) : Event()