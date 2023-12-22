package com.selfdot.pixilcraftnpcs.network.s2c;

import com.selfdot.pixilcraftnpcs.client.HumanNPCTextureTracker;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class PixilCraftNPCsClientPacketHandler {

    public void onSetHumanNPCTexture(
        MinecraftClient client,
        ClientPlayNetworkHandler handler,
        PacketByteBuf buf,
        PacketSender responseSender
    ) {
        UUID npcEntityID = buf.readUuid();
        Identifier texture = buf.readIdentifier();
        HumanNPCTextureTracker.getInstance().putTexture(npcEntityID, texture);
    }

}
