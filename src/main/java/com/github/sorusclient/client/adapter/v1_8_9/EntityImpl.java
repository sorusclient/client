package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IEntity;
import v1_8_9.net.minecraft.entity.Entity;

public class EntityImpl implements IEntity {

    protected final Entity entity;

    public EntityImpl(Entity entity) {
        this.entity = entity;
    }

    @Override
    public double getX() {
        return this.entity.x;
    }

    @Override
    public double getY() {
        return this.entity.y;
    }

    @Override
    public double getZ() {
        return this.entity.z;
    }

}
