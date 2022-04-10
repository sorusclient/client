/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

interface IScoreboard {
    fun getObjective(slot: Slot): IScoreboardObjective?

    enum class Slot {
        SIDEBAR
    }
}