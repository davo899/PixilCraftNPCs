package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.network.s2c.SetHumanNPCTexturePacket;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class HumanNPC extends NPC<HumanNPCEntity> {

    private Identifier texture;

    public HumanNPC(
        String id, String displayName, MultiversePos position, double pitch, double yaw,
        List<String> commandList, boolean nameplateEnabled, int interactCooldownSeconds,
        Identifier texture
    ) {
        super(id, displayName, position, pitch, yaw, commandList, nameplateEnabled, interactCooldownSeconds);
        this.texture = texture;
    }

    public void setTexture(Identifier texture, MinecraftServer server) {
        this.texture = texture;
        server.getPlayerManager().getPlayerList().forEach(this::sendClientUpdate);
    }

    @Override
    public HumanNPCEntity getNewEntity(ServerWorld world) {
        return PixilCraftNPCs.NPC_HUMAN.spawn(world, BlockPos.ORIGIN, SpawnReason.MOB_SUMMONED);
    }

    @Override
    public void sendClientUpdate(ServerPlayerEntity player) {
        super.sendClientUpdate(player);
        new SetHumanNPCTexturePacket(entity.getUuid(), texture).sendS2C(player);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(DataKeys.NPC_TYPE, DataKeys.NPC_HUMAN);
        jsonObject.addProperty(DataKeys.NPC_HUMAN_SKIN, texture.toString());
        return jsonObject;
    }

}
