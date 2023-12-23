package com.selfdot.pixilcraftnpcs.npc;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.network.s2c.SetNPCVisibilityPacket;
import com.selfdot.pixilcraftnpcs.util.CommandUtils;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.FTBUtils;
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

    protected E entity;
    public abstract E getNewEntity(ServerWorld world);
    protected abstract boolean faceNearestPlayer();

    private final String id;
    protected String displayName;
    private final MultiversePos position;
    private final double pitch;
    private final double yaw;
    private List<String> commandList;
    protected boolean nameplateEnabled;
    private long interactCooldownSeconds;
    private long questConditionID;
    private boolean globallyInvisible;
    protected boolean facesNearestPlayer;
    private double proximityTriggerRadius;

    public NPC(
        String id,
        String displayName,
        MultiversePos position,
        double pitch,
        double yaw,
        List<String> commandList,
        boolean nameplateEnabled,
        int interactCooldownSeconds,
        long questConditionID,
        boolean globallyInvisible,
        boolean facesNearestPlayer,
        double proximityTriggerRadius
    ) {
        this.id = id;
        this.displayName = displayName;
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.commandList = commandList;
        this.nameplateEnabled = nameplateEnabled;
        this.interactCooldownSeconds = interactCooldownSeconds;
        this.questConditionID = questConditionID;
        this.globallyInvisible = globallyInvisible;
        this.facesNearestPlayer = facesNearestPlayer;
        this.proximityTriggerRadius = proximityTriggerRadius;
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

    public void setQuestConditionID(long questConditionID, MinecraftServer server) {
        this.questConditionID = questConditionID;
        server.getPlayerManager().getPlayerList().forEach(this::sendQuestUpdate);
    }

    public void toggleGloballyInvisible() {
        globallyInvisible = !globallyInvisible;
        entity.setInvisible(globallyInvisible);
    }

    public void setFacesNearestPlayer(boolean facesNearestPlayer) {
        this.facesNearestPlayer = facesNearestPlayer;
        updateFacing();
    }

    public void setProximityTriggerRadius(double proximityTriggerRadius) {
        this.proximityTriggerRadius = proximityTriggerRadius;
    }

    public void tick(ServerWorld world) {
        updateFacing();

        if (proximityTriggerRadius == 0) return;
        if (world != entity.getWorld()) return;
        world.getPlayers().stream()
            .filter(player -> player.squaredDistanceTo(entity) < (proximityTriggerRadius * proximityTriggerRadius))
            .forEach(player -> checkInteract(player, false));
    }

    protected void updateFacing() {
        if (!facesNearestPlayer || !faceNearestPlayer()) {
            entity.setBodyYaw((float)yaw);
            entity.setHeadYaw((float)yaw);
            entity.setPitch((float)pitch);
        }
    }

    public void spawn(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey().getValue().equals(position.worldID())) {
                entity = getNewEntity(world);
                if (entity == null) return;
                entity.setPosition(position.pos());
                updateFacing();
                setDisplayName(displayName);
                setNameplateEnabled(nameplateEnabled);
                return;
            }
        }
    }

    public void remove() {
        if (entity != null) entity.discard();
    }

    public void checkInteract(PlayerEntity player, Entity entity) {
        if (this.entity != entity) return;
        checkInteract(player, true);
    }

    private void checkInteract(PlayerEntity player, boolean shouldPrintCooldown) {
        if (globallyInvisible) return;
        if (questConditionID != -1 && !FTBUtils.completedQuest(player, questConditionID)) return;
        if (!InteractCooldownTracker.getInstance().attemptInteract(player, id, shouldPrintCooldown)) return;

        commandList.forEach(
            command -> CommandUtils.executeCommandAsServer(command, Objects.requireNonNull(player.getServer()))
        );
    }

    public void sendClientUpdate(ServerPlayerEntity player) {
        sendQuestUpdate(player);
    }

    private void sendQuestUpdate(ServerPlayerEntity player) {
        new SetNPCVisibilityPacket(
            entity.getUuid(),
            (questConditionID == -1 || FTBUtils.completedQuest(player, questConditionID))
        ).sendS2C(player);
    }

    public void onQuestCompleted(ServerPlayerEntity player, long questConditionID) {
        if (questConditionID != this.questConditionID) return;
        sendQuestUpdate(player);
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
        jsonObject.addProperty(DataKeys.NPC_QUEST_CONDITION_ID, questConditionID);
        jsonObject.addProperty(DataKeys.NPC_GLOBALLY_INVISIBLE, globallyInvisible);
        jsonObject.addProperty(DataKeys.NPC_FACES_NEAREST_PLAYER, facesNearestPlayer);
        jsonObject.addProperty(DataKeys.NPC_PROXIMITY_TRIGGER_RADIUS, proximityTriggerRadius);
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
        long questConditionID = jsonObject.get(DataKeys.NPC_QUEST_CONDITION_ID).getAsLong();
        String type = jsonObject.get(DataKeys.NPC_TYPE).getAsString();
        boolean globallyInvisible = jsonObject.get(DataKeys.NPC_GLOBALLY_INVISIBLE).getAsBoolean();
        boolean facesNearestPlayer = jsonObject.get(DataKeys.NPC_FACES_NEAREST_PLAYER).getAsBoolean();
        double proximityTriggerRadius = jsonObject.get(DataKeys.NPC_PROXIMITY_TRIGGER_RADIUS).getAsDouble();
        return switch (type) {
            case DataKeys.NPC_HUMAN -> {
                String skinStr = jsonObject.get(DataKeys.NPC_HUMAN_SKIN).getAsString();
                Identifier skin = Identifier.tryParse(skinStr);
                if (skin == null) throw new IllegalArgumentException("Failed to parse skin ID: " + skinStr);
                yield new HumanNPC(
                    id, displayName, position, pitch, yaw, commandList,
                    nameplateEnabled, interactCooldownSeconds, questConditionID,
                    globallyInvisible, facesNearestPlayer, proximityTriggerRadius, skin
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
                    nameplateEnabled, interactCooldownSeconds, questConditionID,
                    globallyInvisible, facesNearestPlayer, proximityTriggerRadius, species
                );
            }
            default -> throw new IllegalArgumentException("NPC type was '" + type + "', must be: human, pokemon");
        };
    }

}
