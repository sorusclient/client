package com.github.sorusclient.client.setting.data

class CategoryData: AbstractData() {
    val data: MutableMap<String, AbstractData> = HashMap()

    override fun loadForced(json: Any) {
        val json = json as Map<*, *>
        for (data in data) {
            json[data.key]?.let { data.value.loadForced(it) }
        }
    }

    override fun clearForced() {
        for (data in data) {
            data.value.clearForced()
        }
    }

    override fun load(json: Any, isPrimary: Boolean) {
        for (entry in data) {
            (json as Map<*, *>)[entry.key]?.let { entry.value.load(it, isPrimary) }
        }
    }

    override fun save(): Map<String, Any> {
        val map: HashMap<String, Any> = HashMap()
        for (entry in data) {
            entry.value.save()?.let { map[entry.key] = it }
        }
        return map
    }

}