package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class NPC {

    private NPCEntity entity;
    private final String displayName;
    private final double x;
    private final double y;
    private final double z;
    private List<String> commandList;

    public NPC(String displayName, double x, double y, double z, List<String> commandList) {
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.commandList = commandList;
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
        entity.setCommandList(commandList);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.NPC_DISPLAY_NAME, displayName);
        jsonObject.addProperty(DataKeys.NPC_X, x);
        jsonObject.addProperty(DataKeys.NPC_Y, y);
        jsonObject.addProperty(DataKeys.NPC_Z, z);
        JsonArray commandListJson = new JsonArray();
        commandList.forEach(commandListJson::add);
        jsonObject.add(DataKeys.NPC_COMMAND_LIST, commandListJson);
        return jsonObject;
    }

    public static NPC fromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String displayName = jsonObject.get(DataKeys.NPC_DISPLAY_NAME).getAsString();
        double x = jsonObject.get(DataKeys.NPC_X).getAsDouble();
        double y = jsonObject.get(DataKeys.NPC_Y).getAsDouble();
        double z = jsonObject.get(DataKeys.NPC_Z).getAsDouble();
        List<String> commandList = new ArrayList<>();
        jsonObject.getAsJsonArray(DataKeys.NPC_COMMAND_LIST).forEach(command -> commandList.add(command.getAsString()));
        return new NPC(displayName, x, y, z, commandList);
    }

    public void spawn(ServerWorld world) {
        entity = PixilCraftNPCs.NPC.spawn(world, new BlockPos((int)x, (int)y, (int)z), SpawnReason.MOB_SUMMONED);
        if (entity == null) return;
        entity.setPosition(x, y, z);
        entity.setCommandList(commandList);
    }

    public void remove() {
        if (entity != null) entity.remove(Entity.RemovalReason.KILLED);
    }

}
