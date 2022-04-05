/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.InterfaceManager

object AdapterManager {

    val adapter: IAdapter
        get() = InterfaceManager.get()

}