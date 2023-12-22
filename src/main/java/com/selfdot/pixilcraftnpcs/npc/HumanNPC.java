package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class HumanNPC extends NPC<HumanNPCEntity> {

    public HumanNPC(
        String id, String displayName, MultiversePos position, double pitch, double yaw,
        List<String> commandList, boolean nameplateEnabled, int interactCooldownSeconds
    ) {
        super(id, displayName, position, pitch, yaw, commandList, nameplateEnabled, interactCooldownSeconds);
    }

    @Override
    public HumanNPCEntity getNewEntity(ServerWorld world) {
        return PixilCraftNPCs.NPC_HUMAN.spawn(world, BlockPos.ORIGIN, SpawnReason.MOB_SUMMONED);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(DataKeys.NPC_TYPE, DataKeys.NPC_HUMAN);
        return jsonObject;
    }

}
