/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements

interface ISettingsLoader {

     fun save(cached: Map<String, Any>): Map<String, Any>

     fun load(map: Map<String, Any>)

}