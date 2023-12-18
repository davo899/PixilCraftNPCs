package com.selfdot.pixilcraftnpcs;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class NPCEntity extends MobEntity {

    protected NPCEntity(World world) {
        super(PixilCraftNPCs.NPC, world);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

}
