package com.selfdot.pixilcraftnpcs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class NPCEntity extends MobEntity {

    protected NPCEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void pushAway(Entity entity) { }

    @Override
    public void pushAwayFrom(Entity entity) { }

}
