package com.github.sorusclient.client.setting

interface SettingContainer {
    val id: String
    fun load(settings: Map<String, Any>)
    fun loadForced(settings: Map<String, Any>)
    fun removeForced()
    fun save(): Map<String, Any>
    var shared: Boolean
}