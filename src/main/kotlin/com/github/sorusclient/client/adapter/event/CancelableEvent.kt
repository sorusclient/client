package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.event.Event

open class CancelableEvent: Event() {
    var canceled = false
}