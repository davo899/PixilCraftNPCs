package com.selfdot.pixilcraftnpcs.npc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class HumanNPCEntity extends MobEntity {

    public HumanNPCEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        setNoGravity(true);
        setPersistent();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void pushAway(Entity entity) { }

    @Override
    public void pushAwayFrom(Entity entity) { }

    @Override
    public boolean shouldSave() {
        return false;
    }

}
