package com.github.sorusclient.client.setting.data

abstract class AbstractData {

    abstract fun loadForced(json: Any)
    abstract fun clearForced()
    abstract fun load(json: Any, isPrimary: Boolean)
    abstract fun save(): Any?

}