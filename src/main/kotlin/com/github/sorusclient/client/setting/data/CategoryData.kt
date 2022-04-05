/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.setting.data

class CategoryData: AbstractData() {

    val data: MutableMap<String, AbstractData> = HashMap()

    fun <T: AbstractData> add(string: String, data: T): T {
        if (this.data.containsKey(string)) {
            return this.data[string] as T
        }

        this.data[string] = data
        return data
    }

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
            json[entry.key]?.let { (json as MutableMap<*, *>).remove(entry.key) }
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