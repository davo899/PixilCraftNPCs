package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class NPC {

    private final String id;
    private NPCEntity entity;
    private String displayName;
    private final MultiversePos position;
    private final double pitch;
    private final double yaw;
    private List<String> commandList;
    private boolean nameplateEnabled;
    private long interactCooldownSeconds;

    public NPC(
        String id,
        String displayName,
        MultiversePos position,
        double pitch,
        double yaw,
        List<String> commandList,
        boolean nameplateEnabled,
        int interactCooldownSeconds
    ) {
        this.id = id;
        this.displayName = displayName;
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.commandList = commandList;
        this.nameplateEnabled = nameplateEnabled;
        this.interactCooldownSeconds = interactCooldownSeconds;
    }

    public long getInteractCooldownSeconds() {
        return interactCooldownSeconds;
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
        entity.setCommandList(commandList);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        entity.setDisplayName(displayName);
    }

    public void setNameplateEnabled(boolean nameplateEnabled) {
        this.nameplateEnabled = nameplateEnabled;
        entity.setNameplateEnabled(nameplateEnabled);
    }

    public void setInteractCooldownSeconds(long interactCooldownSeconds) {
        this.interactCooldownSeconds = interactCooldownSeconds;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.NPC_DISPLAY_NAME, displayName);
        jsonObject.add(DataKeys.NPC_POSITION, position.toJson());
        jsonObject.addProperty(DataKeys.NPC_PITCH, pitch);
        jsonObject.addProperty(DataKeys.NPC_YAW, yaw);
        JsonArray commandListJson = new JsonArray();
        commandList.forEach(commandListJson::add);
        jsonObject.add(DataKeys.NPC_COMMAND_LIST, commandListJson);
        jsonObject.addProperty(DataKeys.NPC_NAMEPLATE_ENABLED, nameplateEnabled);
        jsonObject.addProperty(DataKeys.NPC_INTERACT_COOLDOWN_SECONDS, interactCooldownSeconds);
        return jsonObject;
    }

    public static NPC fromJson(String id, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String displayName = jsonObject.get(DataKeys.NPC_DISPLAY_NAME).getAsString();
        MultiversePos position = MultiversePos.fromJson(jsonObject.get(DataKeys.NPC_POSITION));
        double pitch = jsonObject.get(DataKeys.NPC_PITCH).getAsDouble();
        double yaw = jsonObject.get(DataKeys.NPC_YAW).getAsDouble();
        List<String> commandList = new ArrayList<>();
        jsonObject.getAsJsonArray(DataKeys.NPC_COMMAND_LIST).forEach(command -> commandList.add(command.getAsString()));
        boolean nameplateEnabled = jsonObject.get(DataKeys.NPC_NAMEPLATE_ENABLED).getAsBoolean();
        int interactCooldownSeconds = jsonObject.get(DataKeys.NPC_INTERACT_COOLDOWN_SECONDS).getAsInt();
        return new NPC(
            id,
            displayName,
            position,
            pitch,
            yaw,
            commandList,
            nameplateEnabled,
            interactCooldownSeconds
        );
    }

    public void spawn(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey().getValue().equals(position.worldID())) {
                entity = PixilCraftNPCs.NPC.spawn(world, BlockPos.ORIGIN, SpawnReason.MOB_SUMMONED);
                if (entity == null) return;
                entity.setId(id);
                entity.setPosition(position.pos());
                entity.setPitch((float)pitch);
                entity.setBodyYaw((float)yaw);
                entity.setHeadYaw((float)yaw);
                entity.setCommandList(commandList);
                entity.setDisplayName(displayName);
                entity.setNameplateEnabled(nameplateEnabled);
            }
        }
    }

    public void remove() {
        if (entity != null) entity.remove(Entity.RemovalReason.KILLED);
    }

}
