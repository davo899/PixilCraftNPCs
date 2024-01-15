package com.selfdot.pixilcraftnpcs.client;

import com.selfdot.pixilcraftnpcs.network.s2c.ClearNPCEntityPacket;
import com.selfdot.pixilcraftnpcs.network.s2c.PixilCraftNPCsClientPacketHandler;
import com.selfdot.pixilcraftnpcs.network.s2c.SetHumanNPCTexturePacket;
import com.selfdot.pixilcraftnpcs.network.s2c.SetNPCVisibilityPacket;
import com.selfdot.pixilcraftnpcs.npc.HumanNPCEntity;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.npc.SkinType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PixilCraftNPCsClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
            PixilCraftNPCs.NPC_HUMAN_SLIM, context -> new HumanNPCRenderer(context, SkinType.SLIM)
        );
        EntityRendererRegistry.register(
            PixilCraftNPCs.NPC_HUMAN_CLASSIC, context -> new HumanNPCRenderer(context, SkinType.CLASSIC)
        );

        PixilCraftNPCsClientPacketHandler packetHandler = new PixilCraftNPCsClientPacketHandler();
        ClientPlayNetworking.registerGlobalReceiver(
            SetHumanNPCTexturePacket.ID, packetHandler::onSetHumanNPCTexture
        );
        ClientPlayNetworking.registerGlobalReceiver(
            SetNPCVisibilityPacket.ID, packetHandler::onSetNPCVisibility
        );
        ClientPlayNetworking.registerGlobalReceiver(
            ClearNPCEntityPacket.ID, packetHandler::onClearNPCEntity
        );
    }
}
