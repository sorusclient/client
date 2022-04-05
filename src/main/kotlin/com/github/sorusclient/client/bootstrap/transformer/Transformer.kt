/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap.transformer

interface Transformer {

    fun canTransform(name: String): Boolean
    fun transform(name: String, data: ByteArray): ByteArray

}