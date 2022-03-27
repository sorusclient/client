package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IEntity
import v1_18_2.net.minecraft.entity.Entity

open class EntityImpl(protected val entity: Entity) : IEntity {

    override val x: Double
        get() = entity.x

    override val y: Double
        get() = entity.y

    override val z: Double
        get() = entity.z

}