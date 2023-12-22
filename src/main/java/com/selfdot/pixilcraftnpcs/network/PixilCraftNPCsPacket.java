package com.selfdot.pixilcraftnpcs.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public abstract class PixilCraftNPCsPacket {

    protected abstract Identifier id();
    protected abstract PacketByteBuf encode();

    public void sendS2C(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, id(), encode());
    }

    public void sendC2S() {
        ClientPlayNetworking.send(id(), encode());
    }

}
