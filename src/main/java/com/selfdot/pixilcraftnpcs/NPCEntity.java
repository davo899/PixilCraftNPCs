package com.selfdot.pixilcraftnpcs;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class NPCEntity extends MobEntity {

    protected NPCEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

}
