/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IEntity;
import v1_8_9.net.minecraft.entity.Entity;

public class EntityImpl<T extends Entity> implements IEntity {

    protected T entity;

    public EntityImpl(T entity) {
        this.entity = entity;
    }

    @Override
    public double getX() {
        return entity.x;
    }

    @Override
    public double getY() {
        return entity.y;
    }

    @Override
    public double getZ() {
        return entity.z;
    }

}
