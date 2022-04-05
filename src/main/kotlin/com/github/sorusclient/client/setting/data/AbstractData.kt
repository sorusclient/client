/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.setting.data

abstract class AbstractData {

    abstract fun loadForced(json: Any)
    abstract fun clearForced()
    abstract fun load(json: Any, isPrimary: Boolean)
    abstract fun save(): Any?

}