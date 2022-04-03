package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.InterfaceManager

object AdapterManager {

    val adapter: IAdapter
        get() = InterfaceManager.get()

}