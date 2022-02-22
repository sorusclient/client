package com.github.sorusclient.client.setting.display

abstract class Displayed {
    abstract val name: String
    var parent: DisplayedCategory? = null
}