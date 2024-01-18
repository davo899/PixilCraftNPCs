package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.util.DisableableMod;
import com.selfdot.pixilcraftnpcs.util.JsonFile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InteractCooldownTracker extends JsonFile {

    private final Map<UUID, Map<String, Long>> lastInteractedMap = new HashMap<>();

    public InteractCooldownTracker(DisableableMod mod) {
        super(mod);
    }

    private void putInteract(UUID player, String npcID, Long now) {
        if (!lastInteractedMap.containsKey(player)) lastInteractedMap.put(player, new HashMap<>());
        lastInteractedMap.get(player).put(npcID, now);
        save();
    }

    public void deleteNPC(String id) {
        lastInteractedMap.values().forEach(npcMap -> npcMap.remove(id));
        save();
    }

    public boolean attemptInteract(PlayerEntity player, String npcID, boolean shouldPrintCooldown) {
        NPC<?> npc = PixilCraftNPCs.getInstance().getNPCTracker().get(npcID);
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

    @Override
    protected String filename() {
        return "pixilcraftnpcs/lastInteracted.json";
    }

    @Override
    protected void setDefaults() {
        lastInteractedMap.clear();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry1 : jsonObject.entrySet()) {
            Map<String, Long> map = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry2 : entry1.getValue().getAsJsonObject().entrySet()) {
                map.put(entry2.getKey(), entry2.getValue().getAsLong());
            }
            lastInteractedMap.put(UUID.fromString(entry1.getKey()), map);
        }
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<UUID, Map<String, Long>> entry1 : lastInteractedMap.entrySet()) {
            JsonObject mapObject = new JsonObject();
            for (Map.Entry<String, Long> entry2 : entry1.getValue().entrySet()) {
                mapObject.addProperty(entry2.getKey(), entry2.getValue());
            }
            jsonObject.add(entry1.getKey().toString(), mapObject);
        }
        return jsonObject;
    }

}
