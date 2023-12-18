package com.selfdot.pixilcraftnpcs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PixilCraftNPCs implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    public static final EntityType<NPCEntity> NPC = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier("pixilcraft", "npc"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, NPCEntity::new)
            .dimensions(EntityDimensions.fixed(0.75f, 0.75f))
            .build()
    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(NPC, NPCEntity.createMobAttributes());
    }
}
