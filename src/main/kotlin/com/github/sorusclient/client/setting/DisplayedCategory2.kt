package com.github.sorusclient.client.setting

class DisplayedCategory2(override val id: String): Displayed() {

    val displayed: MutableList<Displayed> = ArrayList()
    var parent: DisplayedCategory2? = null

    fun <T: Displayed> registerDisplayed(displayed: T): T {
        this.displayed.add(displayed)

        if (displayed is DisplayedCategory2) {
            displayed.parent = this
        }

        return displayed
    }

    override fun save(): Any {
        val displayedList: MutableMap<String, Any> = HashMap()
        for (displayed in this.displayed) {
            val saved = displayed.save()
            if (saved != null) {
                displayedList[displayed.id] = saved
            }
        }

        return displayedList
    }

    override fun load(any: Any, isPrimary: Boolean) {
        val loadedMap = any as Map<String, Any>
        for (displayed in this.displayed) {
            loadedMap[displayed.id]?.let { displayed.load(it, isPrimary) }
        }
    }

    override fun loadForced(any: Any) {
        val loadedMap = any as Map<String, Any>
        for (displayed in this.displayed) {
            loadedMap[displayed.id]?.let { displayed.loadForced(it) }
        }
    }

    override fun clearForced() {
        for (displayed in this.displayed) {
            displayed.clearForced()
        }
    }

}