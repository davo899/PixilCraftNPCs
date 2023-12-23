package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InteractCooldownTracker {

    private static final InteractCooldownTracker INSTANCE = new InteractCooldownTracker();
    public static InteractCooldownTracker getInstance() { return INSTANCE; }
    private InteractCooldownTracker() { }

    private static final String FILENAME = "pixilcraftnpcs/lastInteracted.json";
    private final Map<UUID, Map<String, Long>> lastInteractedMap = new HashMap<>();

    private void putInteract(UUID player, String npcID, Long now) {
        if (!lastInteractedMap.containsKey(player)) lastInteractedMap.put(player, new HashMap<>());
        lastInteractedMap.get(player).put(npcID, now);
    }

    public boolean attemptInteract(PlayerEntity player, String npcID, boolean shouldPrintCooldown) {
        NPC<?> npc = NPCTracker.getInstance().get(npcID);
        if (npc == null) return false;
        if (npc.getInteractCooldownSeconds() == 0) return true;

        Long now = System.currentTimeMillis();
        if (!lastInteractedMap.containsKey(player.getUuid())) {
            putInteract(player.getUuid(), npcID, now);
            return true;
        }

        Map<String, Long> interactions = lastInteractedMap.get(player.getUuid());
        if (!interactions.containsKey(npcID)) {
            putInteract(player.getUuid(), npcID, now);
            return true;
        }

        Long lastInteraction = interactions.get(npcID);
        if (now - lastInteraction >= npc.getInteractCooldownSeconds() * 1000) {
            putInteract(player.getUuid(), npcID, now);
            return true;

        } else {
            if (shouldPrintCooldown) {
                player.sendMessage(Text.literal("Cannot interact with this NPC for another " + (
                    npc.getInteractCooldownSeconds() - ((now - lastInteraction) / 1000)
                ) + " seconds"));
            }
            return false;
        }
    }

    public void load() {
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(FILENAME));
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry1 : jsonObject.entrySet()) {
                    Map<String, Long> map = new HashMap<>();
                    for (Map.Entry<String, JsonElement> entry2 : entry1.getValue().getAsJsonObject().entrySet()) {
                        map.put(entry2.getKey(), entry2.getValue().getAsLong());
                    }
                    lastInteractedMap.put(UUID.fromString(entry1.getKey()), map);
                }
                PixilCraftNPCs.LOGGER.info("Last interacted data loaded");

            } catch (Exception e) {
                PixilCraftNPCs.DISABLED = true;
                PixilCraftNPCs.LOGGER.error("An exception occurred when loading last interacted data:");
                PixilCraftNPCs.LOGGER.error(e.getMessage());
            }

        } catch (FileNotFoundException e) {
            LogUtils.getLogger().warn("Last interacted data file not found, attempting to generate");
            try {
                Files.createDirectories(Paths.get(FILENAME).getParent());
                FileWriter writer = new FileWriter(FILENAME);
                PixilCraftNPCs.GSON.toJson(new JsonObject(), writer);
                writer.close();

            } catch (IOException ex) {
                PixilCraftNPCs.DISABLED = true;
                PixilCraftNPCs.LOGGER.error("Unable to generate last interacted data file");
            }
        }
    }

    public void save() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<UUID, Map<String, Long>> entry1 : lastInteractedMap.entrySet()) {
            JsonObject mapObject = new JsonObject();
            for (Map.Entry<String, Long> entry2 : entry1.getValue().entrySet()) {
                mapObject.addProperty(entry2.getKey(), entry2.getValue());
            }
            jsonObject.add(entry1.getKey().toString(), mapObject);
        }
        try {
            Files.createDirectories(Paths.get(FILENAME).getParent());
            FileWriter writer = new FileWriter(FILENAME);
            PixilCraftNPCs.GSON.toJson(jsonObject, writer);
            writer.close();

        } catch (IOException e) {
            LogUtils.getLogger().error("Unable to store last interacted data to " + FILENAME);
        }
    }

}
