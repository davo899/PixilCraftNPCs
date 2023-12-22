package com.selfdot.pixilcraftnpcs.npc;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.util.CommandUtils;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class NPC<E extends MobEntity> {

    private final String id;
    protected E entity;
    protected String displayName;
    private final MultiversePos position;
    private final double pitch;
    private final double yaw;
    private List<String> commandList;
    protected boolean nameplateEnabled;
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
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        if (nameplateEnabled) entity.setCustomName(Text.literal(displayName));
    }

    public void setNameplateEnabled(boolean nameplateEnabled) {
        this.nameplateEnabled = nameplateEnabled;

        entity.setCustomNameVisible(nameplateEnabled);
        if (nameplateEnabled) entity.setCustomName(Text.literal(displayName));
        else entity.setCustomName(null);
    }

    public void setInteractCooldownSeconds(long interactCooldownSeconds) {
        this.interactCooldownSeconds = interactCooldownSeconds;
    }

    public void checkInteract(PlayerEntity player, Entity entity) {
        if (this.entity == entity) {
            if (InteractCooldownTracker.getInstance().attemptInteract(player, id)) {
                commandList.forEach(
                    command -> CommandUtils.executeCommandAsServer(command, Objects.requireNonNull(player.getServer()))
                );
                onInteract();
            }
        }
    }

    public void onInteract() { }

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

    public static NPC<?> fromJson(String id, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String displayName = jsonObject.get(DataKeys.NPC_DISPLAY_NAME).getAsString();
        MultiversePos position = MultiversePos.fromJson(jsonObject.get(DataKeys.NPC_POSITION));
        double pitch = jsonObject.get(DataKeys.NPC_PITCH).getAsDouble();
        double yaw = jsonObject.get(DataKeys.NPC_YAW).getAsDouble();
        List<String> commandList = new ArrayList<>();
        jsonObject.getAsJsonArray(DataKeys.NPC_COMMAND_LIST).forEach(command -> commandList.add(command.getAsString()));
        boolean nameplateEnabled = jsonObject.get(DataKeys.NPC_NAMEPLATE_ENABLED).getAsBoolean();
        int interactCooldownSeconds = jsonObject.get(DataKeys.NPC_INTERACT_COOLDOWN_SECONDS).getAsInt();
        String type = jsonObject.get(DataKeys.NPC_TYPE).getAsString();
        return switch (type) {
            case DataKeys.NPC_HUMAN -> {
                String skinStr = jsonObject.get(DataKeys.NPC_HUMAN_SKIN).getAsString();
                Identifier skin = Identifier.tryParse(skinStr);
                if (skin == null) throw new IllegalArgumentException("Failed to parse skin ID: " + skinStr);
                yield new HumanNPC(
                    id, displayName, position, pitch, yaw, commandList,
                    nameplateEnabled, interactCooldownSeconds, skin
                );
            }
            case DataKeys.NPC_POKEMON -> {
                String speciesStr = jsonObject.get(DataKeys.NPC_POKEMON_SPECIES).getAsString();
                Identifier speciesIdentifier = Identifier.tryParse(speciesStr);
                if (speciesIdentifier == null) throw new IllegalArgumentException("Invalid species ID " + speciesStr);
                Species species = PokemonSpecies.INSTANCE.getByIdentifier(speciesIdentifier);
                if (species == null) throw new IllegalArgumentException("Unknown species " + speciesStr);
                yield new PokemonNPC(
                    id, displayName, position, pitch, yaw, commandList,
                    nameplateEnabled, interactCooldownSeconds, species
                );
            }
            default -> throw new IllegalArgumentException("NPC type was '" + type + "', must be: human, pokemon");
        };
    }

    public void remove() {
        if (entity != null) entity.discard();
    }

    public abstract E getNewEntity(ServerWorld world);

    public void spawn(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey().getValue().equals(position.worldID())) {
                entity = getNewEntity(world);
                if (entity == null) return;
                entity.setPosition(position.pos());
                entity.setBodyYaw((float)yaw);
                entity.setHeadYaw((float)yaw);
                entity.setPitch((float)pitch);
                setDisplayName(displayName);
                setNameplateEnabled(nameplateEnabled);
                return;
            }
        }
    }

    public void discard() {
        entity.discard();
    }

    public void sendClientUpdate(ServerPlayerEntity player) { }

}
