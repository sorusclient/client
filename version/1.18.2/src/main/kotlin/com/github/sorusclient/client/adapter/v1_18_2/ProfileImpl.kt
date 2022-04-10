/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IProfile
import v1_18_2.com.mojang.authlib.GameProfile
import java.util.*

class ProfileImpl(private val gameProfile: GameProfile): IProfile {

    override val uuid: UUID
        get() = gameProfile.id

}