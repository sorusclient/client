/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.experience

interface IExperienceRenderer {
    fun renderExperienceBar(x: Double, y: Double, scale: Double, percent: Double)
}