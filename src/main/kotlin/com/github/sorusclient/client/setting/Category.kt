package com.github.sorusclient.client.setting

class Category(val displayName: String): SettingComponent() {

    val components: MutableList<SettingComponent> = ArrayList()
    var parent: Category? = null

    fun <T: SettingComponent> add(displayed: T): T {
        this.components.add(displayed)

        if (displayed is Category) {
            displayed.parent = this
        }

        return displayed
    }

}