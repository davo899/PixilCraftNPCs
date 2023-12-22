package com.selfdot.pixilcraftnpcs.client;

import com.selfdot.pixilcraftnpcs.npc.HumanNPCEntity;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

public class PixilCraftNPCsClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
            PixilCraftNPCs.NPC_HUMAN, context -> new MobEntityRenderer<>(
                context,
                new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false),
                1f
            ) {
                @Override
                public Identifier getTexture(HumanNPCEntity entity) {
                    return DefaultSkinHelper.getTexture();
                }
            }
        );
        //EntityRendererRegistry.register(PixilCraftNPCs.NPC_POKEMON, PokemonNPCEntityRenderer::new);
    }
}
