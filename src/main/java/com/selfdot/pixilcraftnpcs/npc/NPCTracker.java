package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NPCTracker {

    private static final NPCTracker INSTANCE = new NPCTracker();
    public static NPCTracker getInstance() { return INSTANCE; }
    private NPCTracker() { }

    private static final String FILENAME = "pixilcraftnpcs/npcs.json";
    private final Map<String, NPC<?>> npcs = new HashMap<>();
    private MinecraftServer server;

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public Set<String> getAllIDs() {
        return npcs.keySet();
    }

    public void summonAllNPCEntities() {
        npcs.values().forEach(npc -> npc.spawn(server));
    }

    public void sendClientUpdates(ServerPlayerEntity player) {
        npcs.values().forEach(npc -> npc.sendClientUpdate(player));
    }

    public void onQuestCompleted(ServerPlayerEntity player, long questID) {
        npcs.values().forEach(npc -> npc.onQuestCompleted(player, questID));
    }

    public void add(String id, NPC<?> npc) {
        npcs.put(id, npc);
        npc.spawn(server);
    }

    public NPC<?> get(String id) {
        return npcs.get(id);
    }

    public boolean exists(String id) {
        return npcs.containsKey(id);
    }

    public void delete(String id) {
        NPC<?> npc = npcs.remove(id);
        if (npc != null) npc.remove(server);
    }

    public void checkInteract(PlayerEntity player, Entity entity) {
        npcs.values().forEach(npc -> npc.checkInteract(player, entity));
    }

    public void onTick(ServerWorld world) {
        npcs.values().forEach(npc -> npc.tick(world));
    }

    public void load() {
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(FILENAME));
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    npcs.put(entry.getKey(), NPC.fromJson(entry.getKey(), entry.getValue()));
                }
                PixilCraftNPCs.LOGGER.info("NPC data loaded");

            } catch (Exception e) {
                PixilCraftNPCs.DISABLED = true;
                npcs.values().forEach(npc -> npc.remove(server));
                PixilCraftNPCs.LOGGER.error("An exception occurred when loading NPC data:");
                PixilCraftNPCs.LOGGER.error(e.getMessage());
            }

        } catch (FileNotFoundException e) {
            LogUtils.getLogger().warn("NPC data file not found, attempting to generate");
            try {
                Files.createDirectories(Paths.get(FILENAME).getParent());
                FileWriter writer = new FileWriter(FILENAME);
                PixilCraftNPCs.GSON.toJson(new JsonObject(), writer);
                writer.close();

            } catch (IOException ex) {
                PixilCraftNPCs.DISABLED = true;
                PixilCraftNPCs.LOGGER.error("Unable to generate NPC data file");
            }
        }
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, NPC<?>> entry : npcs.entrySet()) {
            jsonObject.add(entry.getKey(), entry.getValue().toJson());
        }
        try {
            Files.createDirectories(Paths.get(FILENAME).getParent());
            FileWriter writer = new FileWriter(FILENAME);
            PixilCraftNPCs.GSON.toJson(jsonObject, writer);
            writer.close();

        } catch (IOException e) {
            LogUtils.getLogger().error("Unable to store NPC data to " + FILENAME);
        }
    }

}
