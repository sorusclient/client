package com.github.sorusclient.client.setting

abstract class Displayed {

    abstract val id: String

    abstract fun save(): Any?
    abstract fun load(any: Any, isPrimary: Boolean)

}