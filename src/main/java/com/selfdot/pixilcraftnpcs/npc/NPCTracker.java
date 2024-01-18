package com.selfdot.pixilcraftnpcs.npc;

import com.google.gson.*;
import com.selfdot.pixilcraftnpcs.util.DisableableMod;
import com.selfdot.pixilcraftnpcs.util.JsonFile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NPCTracker extends JsonFile {

    private final Map<String, NPC<?>> npcs = new HashMap<>();
    private MinecraftServer server;

    public NPCTracker(DisableableMod mod) {
        super(mod);
    }

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
        save();
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
        save();
        if (npc != null) npc.remove(server);
    }

    public void checkInteract(PlayerEntity player, Entity entity) {
        npcs.values().forEach(npc -> npc.checkInteract(player, entity));
    }

    public void onTick(ServerWorld world) {
        npcs.values().forEach(npc -> npc.tick(world));
    }

    @Override
    protected String filename() {
        return "pixilcraftnpcs/npcs.json";
    }

    @Override
    protected void setDefaults() {
        npcs.clear();
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        jsonElement.getAsJsonObject().entrySet().forEach(
            entry -> npcs.put(entry.getKey(), NPC.fromJson(entry.getKey(), entry.getValue()))
        );
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        npcs.forEach((id, npc) -> jsonObject.add(id, npc.toJson()));
        return jsonObject;
    }

}
