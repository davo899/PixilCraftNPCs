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
    private SkinType skinType;

    public HumanNPC(
        String id,
        MultiversePos position,
        double pitch,
        double yaw,
        Identifier texture,
        SkinType skinType
    ) {
        super(id, position, pitch, yaw);
        this.texture = texture;
        this.skinType = skinType;
    }

    public HumanNPC(String id, MultiversePos position, double pitch, double yaw) {
        this(id, position, pitch, yaw, new Identifier("textures/entity/player/slim/steve.png"), SkinType.SLIM);
    }

    public void setTexture(Identifier texture, MinecraftServer server) {
        this.texture = texture;
        server.getPlayerManager().getPlayerList().forEach(this::sendTextureUpdate);
    }

    public void setSkinType(SkinType skinType, MinecraftServer server) {
        this.skinType = skinType;
        remove(server);
    }

    @Override
    protected void spawnEntityInWorld(ServerWorld world) {
        entity = switch (skinType) {
            case CLASSIC -> PixilCraftNPCs.NPC_HUMAN_CLASSIC.spawn(world, BlockPos.ORIGIN, SpawnReason.MOB_SUMMONED);
            case SLIM -> PixilCraftNPCs.NPC_HUMAN_SLIM.spawn(world, BlockPos.ORIGIN, SpawnReason.MOB_SUMMONED);
        };
    }

    @Override
    protected boolean faceNearestPlayer() {
        PlayerEntity nearestPlayer = entity.getWorld().getClosestPlayer(
            entity, PixilCraftNPCs.getInstance().getConfig().getMaxFacesPlayerDistance()
        );
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
        jsonObject.addProperty(DataKeys.NPC_HUMAN_SKIN_TYPE, switch (skinType) {
            case CLASSIC -> DataKeys.SKIN_TYPE_CLASSIC;
            case SLIM -> DataKeys.SKIN_TYPE_SLIM;
        });
        return jsonObject;
    }

}
