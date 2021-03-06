/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.setting.data

import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.Util
import java.util.*

class SettingData(val setting: Setting<*>): AbstractData() {

    override fun loadForced(json: Any) {
        val javaData = Util.toJava(setting.type, json)
        if (javaData is List<*>) {
            javaData.let { setting.setForcedValueRaw(it as List<Any>) }
        } else {
            javaData?.let { setting.setForcedValueRaw(ArrayList(Collections.singletonList(javaData))) }
        }
    }

    override fun clearForced() {
        setting.setForcedValueRaw(null)
    }

    override fun load(json: Any, isPrimary: Boolean) {
        Util.toJava(setting.type, json)?.let { setting.setValueRaw(it, isPrimary) }
    }

    override fun save(): Any? {
        return if (setting.overriden) {
            Util.toData(setting.realValue!!)
        } else {
            null
        }
    }

}