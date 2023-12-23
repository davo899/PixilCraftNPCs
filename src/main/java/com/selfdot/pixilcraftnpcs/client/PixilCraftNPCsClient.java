package com.selfdot.pixilcraftnpcs.client;

import com.selfdot.pixilcraftnpcs.network.s2c.PixilCraftNPCsClientPacketHandler;
import com.selfdot.pixilcraftnpcs.network.s2c.SetHumanNPCTexturePacket;
import com.selfdot.pixilcraftnpcs.network.s2c.SetNPCVisibilityPacket;
import com.selfdot.pixilcraftnpcs.npc.HumanNPCEntity;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
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
                new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER_SLIM), false),
                1f
            ) {
                @Override
                public Identifier getTexture(HumanNPCEntity entity) {
                    return HumanNPCTextureTracker.getInstance().getTexture(entity.getUuid());
                }
            }
        );

        PixilCraftNPCsClientPacketHandler packetHandler = new PixilCraftNPCsClientPacketHandler();
        ClientPlayNetworking.registerGlobalReceiver(
            SetHumanNPCTexturePacket.ID, packetHandler::onSetHumanNPCTexture
        );
        ClientPlayNetworking.registerGlobalReceiver(
            SetNPCVisibilityPacket.ID, packetHandler::onSetNPCVisibility
        );
    }
}
