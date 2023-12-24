package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.network.s2c.SetHumanNPCTexturePacket;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class HumanNPC extends NPC<HumanNPCEntity> {

    private Identifier texture;

    public HumanNPC(String id, MultiversePos position, double pitch, double yaw, Identifier texture) {
        super(id, position, pitch, yaw);
        this.texture = texture;
    }

    public HumanNPC(String id, MultiversePos position, double pitch, double yaw) {
        this(id, position, pitch, yaw, new Identifier("textures/entity/player/slim/steve.png"));
    }

    public void setTexture(Identifier texture, MinecraftServer server) {
        this.texture = texture;
        server.getPlayerManager().getPlayerList().forEach(this::sendTextureUpdate);
    }

    @Override
    public HumanNPCEntity getNewEntity(ServerWorld world) {
        return PixilCraftNPCs.NPC_HUMAN.spawn(world, BlockPos.ORIGIN, SpawnReason.MOB_SUMMONED);
    }

    @Override
    protected boolean faceNearestPlayer() {
        PlayerEntity nearestPlayer = entity.getWorld().getClosestPlayer(entity, 5);
        if (nearestPlayer == null) return false;
        entity.getLookControl().lookAt(nearestPlayer);
        return true;
    }

    private void sendTextureUpdate(ServerPlayerEntity player) {
        new SetHumanNPCTexturePacket(entity.getUuid(), texture).sendS2C(player);
    }

    @Override
    public void sendClientUpdate(ServerPlayerEntity player) {
        super.sendClientUpdate(player);
        if (entityLoaded) sendTextureUpdate(player);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(DataKeys.NPC_TYPE, DataKeys.NPC_HUMAN);
        jsonObject.addProperty(DataKeys.NPC_HUMAN_SKIN, texture.toString());
        return jsonObject;
    }

}
