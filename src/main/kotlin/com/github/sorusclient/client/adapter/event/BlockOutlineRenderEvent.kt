package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.adapter.Box
import com.github.sorusclient.client.event.EventCancelable

class BlockOutlineRenderEvent(val box: Box) : EventCancelable()