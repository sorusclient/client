package com.github.sorusclient.client.adapter

import com.github.glassmc.loader.GlassLoader

object AdapterManager {

    fun getAdapter(): IAdapter {
        return GlassLoader.getInstance().getInterface(IAdapter::class.java)
    }

}