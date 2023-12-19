package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.DataKeys;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class NPC {

    private NPCEntity entity;
    private final String displayName;
    private final double x;
    private final double y;
    private final double z;

    public NPC(String displayName, double x, double y, double z) {
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.NPC_DISPLAY_NAME, displayName);
        jsonObject.addProperty(DataKeys.NPC_X, x);
        jsonObject.addProperty(DataKeys.NPC_Y, y);
        jsonObject.addProperty(DataKeys.NPC_Z, z);
        return jsonObject;
    }

    public static NPC fromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String displayName = jsonObject.get(DataKeys.NPC_DISPLAY_NAME).getAsString();
        double x = jsonObject.get(DataKeys.NPC_X).getAsDouble();
        double y = jsonObject.get(DataKeys.NPC_Y).getAsDouble();
        double z = jsonObject.get(DataKeys.NPC_Z).getAsDouble();
        return new NPC(displayName, x, y, z);
    }

    public boolean spawn(ServerWorld world) {
        entity = PixilCraftNPCs.NPC.spawn(world, new BlockPos((int)x, (int)y, (int)z), SpawnReason.MOB_SUMMONED);
        if (entity == null) return false;
        entity.setPosition(x, y, z);
        return true;
    }

    public void remove() {
        if (entity != null) entity.remove(Entity.RemovalReason.KILLED);
    }

}
