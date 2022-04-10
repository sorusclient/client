/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IEntity
import v1_8_9.net.minecraft.entity.Entity

open class EntityImpl(protected val entity: Entity) : IEntity {

    override val x: Double
        get() = entity.x

    override val y: Double
        get() = entity.y

    override val z: Double
        get() = entity.z

}