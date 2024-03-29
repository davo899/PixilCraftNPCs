package com.selfdot.pixilcraftnpcs.npc;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.network.s2c.ClearNPCEntityPacket;
import com.selfdot.pixilcraftnpcs.network.s2c.SetNPCVisibilityPacket;
import com.selfdot.pixilcraftnpcs.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class NPC<E extends MobEntity> {

    protected E entity;
    protected abstract void spawnEntityInWorld(ServerWorld world);
    protected abstract boolean faceNearestPlayer();

    private final String id;
    protected MultiversePos position;
    private double pitch;
    private double yaw;
    protected String displayName;
    private List<String> commandList = new ArrayList<>();
    protected boolean nameplateEnabled = true;
    private long interactCooldownSeconds = 0;
    private long questConditionID = -1;
    private long questAntiConditionID = -1;
    protected boolean globallyInvisible = false;
    protected boolean facesNearestPlayer = false;
    private double proximityTriggerRadius = 0;
    protected boolean entityLoaded = false;

    public NPC(String id, MultiversePos position, double pitch, double yaw) {
        this.id = id;
        this.displayName = id;
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public long getInteractCooldownSeconds() {
        return interactCooldownSeconds;
    }

    protected Text formattedDisplayName() {
        return Text.literal(ChatColourUtils.format(displayName));
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void setPosition(MultiversePos position, MinecraftServer server) {
        this.position = position;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
        remove(server);
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void setNameplateEnabled(boolean nameplateEnabled) {
        this.nameplateEnabled = nameplateEnabled;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void setInteractCooldownSeconds(long interactCooldownSeconds) {
        this.interactCooldownSeconds = interactCooldownSeconds;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void setQuestConditionID(long questConditionID, MinecraftServer server) {
        this.questConditionID = questConditionID;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
        server.getPlayerManager().getPlayerList().forEach(this::sendVisibilityUpdate);
    }

    public void setQuestAntiConditionID(long questAntiConditionID, MinecraftServer server) {
        this.questAntiConditionID = questAntiConditionID;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
        server.getPlayerManager().getPlayerList().forEach(this::sendVisibilityUpdate);
    }

    public void toggleGloballyInvisible(MinecraftServer server) {
        globallyInvisible = !globallyInvisible;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
        server.getPlayerManager().getPlayerList().forEach(this::sendVisibilityUpdate);
    }

    public void setFacesNearestPlayer(boolean facesNearestPlayer) {
        this.facesNearestPlayer = facesNearestPlayer;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void setProximityTriggerRadius(double proximityTriggerRadius) {
        this.proximityTriggerRadius = proximityTriggerRadius;
        PixilCraftNPCs.getInstance().getNPCTracker().save();
    }

    public void tick(ServerWorld world) {
        if (!world.getRegistryKey().getValue().equals(position.worldID())) return;
        checkShouldLoadEntity(world);
        checkProximityTrigger(world);
        if (entityLoaded) updateTracked();
    }

    protected void updateTracked() {
        if (!facesNearestPlayer || !faceNearestPlayer()) {
            entity.setBodyYaw((float)yaw);
            entity.setHeadYaw((float)yaw);
            entity.setPitch((float)pitch);
        }
        entity.setPosition(position.pos());
        updateDisplayName();
        updateNameplateEnabled();
    }

    protected void updateDisplayName() {
        if (nameplateEnabled) entity.setCustomName(formattedDisplayName());
    }

    protected void updateNameplateEnabled() {
        entity.setCustomNameVisible(nameplateEnabled);
        if (nameplateEnabled) entity.setCustomName(formattedDisplayName());
        else entity.setCustomName(null);
    }

    public void checkShouldLoadEntity(ServerWorld world) {
        Vec3d pos = position.pos();
        if (
            world.getClosestPlayer(
                pos.x, pos.y, pos.z, PixilCraftNPCs.getInstance().getConfig().getMinDespawnDistance(), null
            ) == null
        ) {
            if (entityLoaded) {
                remove(world.getServer());
            }
        } else {
            if (!entityLoaded) {
                spawn(world.getServer());
            }
        }
    }

    public void checkProximityTrigger(ServerWorld world) {
        if (proximityTriggerRadius == 0) return;
        world.getPlayers().stream()
            .filter(player -> player.squaredDistanceTo(entity) < (proximityTriggerRadius * proximityTriggerRadius))
            .forEach(player -> checkInteract(player, false));
    }

    public void spawn(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey().getValue().equals(position.worldID())) {
                spawnEntityInWorld(world);
                entityLoaded = entity != null;
                server.getPlayerManager().getPlayerList().forEach(this::sendClientUpdate);
                return;
            }
        }
    }

    public void remove(MinecraftServer server) {
        if (entity != null) entity.discard();
        entityLoaded = false;
        server.getPlayerManager().getPlayerList().forEach(this::sendClientUpdate);
    }

    public void checkInteract(PlayerEntity player, Entity entity) {
        if (this.entity != entity) return;
        checkInteract(player, true);
    }

    private boolean isVisibleFor(PlayerEntity player) {
        return !globallyInvisible &&
            (questConditionID == -1 || FTBUtils.completedQuest(player, questConditionID)) &&
            (questAntiConditionID == -1 || !FTBUtils.completedQuest(player, questAntiConditionID));
    }

    private void checkInteract(PlayerEntity player, boolean shouldPrintCooldown) {
        if (!isVisibleFor(player)) return;
        if (
            !PixilCraftNPCs.getInstance().getInteractCooldownTracker().attemptInteract(player, id, shouldPrintCooldown)
        ) {
            return;
        }

        commandList.forEach(
            command -> CommandUtils.executeCommandAsServer(
                ChatColourUtils.replaceTokens(command, player),
                Objects.requireNonNull(player.getServer())
            )
        );
    }

    public void sendClientUpdate(ServerPlayerEntity player) {
        if (entityLoaded) sendVisibilityUpdate(player);
        else if (entity != null) new ClearNPCEntityPacket(entity.getUuid()).sendS2C(player);
    }

    private void sendVisibilityUpdate(ServerPlayerEntity player) {
        new SetNPCVisibilityPacket(entity.getUuid(), isVisibleFor(player)).sendS2C(player);
    }

    public void onQuestCompleted(ServerPlayerEntity player, long questConditionID) {
        if (questConditionID != this.questConditionID && questConditionID != this.questAntiConditionID) return;
        sendVisibilityUpdate(player);
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
        jsonObject.addProperty(DataKeys.NPC_QUEST_ANTICONDITION_ID, questAntiConditionID);
        jsonObject.addProperty(DataKeys.NPC_GLOBALLY_INVISIBLE, globallyInvisible);
        jsonObject.addProperty(DataKeys.NPC_FACES_NEAREST_PLAYER, facesNearestPlayer);
        jsonObject.addProperty(DataKeys.NPC_PROXIMITY_TRIGGER_RADIUS, proximityTriggerRadius);
        return jsonObject;
    }

    public static NPC<?> fromJson(String id, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MultiversePos position = MultiversePos.fromJson(jsonObject.get(DataKeys.NPC_POSITION));
        double pitch = jsonObject.get(DataKeys.NPC_PITCH).getAsDouble();
        double yaw = jsonObject.get(DataKeys.NPC_YAW).getAsDouble();
        String type = jsonObject.get(DataKeys.NPC_TYPE).getAsString();
        NPC<?> npc = switch (type) {
            case DataKeys.NPC_HUMAN -> {
                String skinStr = jsonObject.get(DataKeys.NPC_HUMAN_SKIN).getAsString();
                Identifier skin = Identifier.tryParse(skinStr);
                if (skin == null) throw new IllegalArgumentException("Failed to parse skin ID: " + skinStr);
                SkinType skinType = SkinType.SLIM;
                if (jsonObject.has(DataKeys.NPC_HUMAN_SKIN_TYPE)) {
                    String skinTypeStr = jsonObject.get(DataKeys.NPC_HUMAN_SKIN_TYPE).getAsString();
                    skinType = switch (skinTypeStr) {
                        case DataKeys.SKIN_TYPE_CLASSIC -> SkinType.CLASSIC;
                        case DataKeys.SKIN_TYPE_SLIM -> SkinType.SLIM;
                        default -> throw new IllegalStateException("Unknown skin type: " + skinTypeStr);
                    };
                }
                yield new HumanNPC(id, position, pitch, yaw, skin, skinType);
            }
            case DataKeys.NPC_POKEMON -> {
                String speciesStr = jsonObject.get(DataKeys.NPC_POKEMON_SPECIES).getAsString();
                Identifier speciesIdentifier = Identifier.tryParse(speciesStr);
                if (speciesIdentifier == null) throw new IllegalArgumentException("Invalid species ID " + speciesStr);
                Species species = PokemonSpecies.INSTANCE.getByIdentifier(speciesIdentifier);
                if (species == null) throw new IllegalArgumentException("Unknown species " + speciesStr);
                yield new PokemonNPC(id, position, pitch, yaw, species);
            }
            default -> throw new IllegalArgumentException("NPC type was '" + type + "', must be: human, pokemon");
        };
        npc.displayName = jsonObject.get(DataKeys.NPC_DISPLAY_NAME).getAsString();
        npc.commandList = jsonObject.getAsJsonArray(DataKeys.NPC_COMMAND_LIST).asList().stream()
            .map(JsonElement::getAsString).collect(Collectors.toList());
        npc.nameplateEnabled = jsonObject.get(DataKeys.NPC_NAMEPLATE_ENABLED).getAsBoolean();
        npc.interactCooldownSeconds = jsonObject.get(DataKeys.NPC_INTERACT_COOLDOWN_SECONDS).getAsInt();
        npc.questConditionID = jsonObject.get(DataKeys.NPC_QUEST_CONDITION_ID).getAsLong();
        npc.globallyInvisible = jsonObject.get(DataKeys.NPC_GLOBALLY_INVISIBLE).getAsBoolean();
        npc.facesNearestPlayer = jsonObject.get(DataKeys.NPC_FACES_NEAREST_PLAYER).getAsBoolean();
        npc.proximityTriggerRadius = jsonObject.get(DataKeys.NPC_PROXIMITY_TRIGGER_RADIUS).getAsDouble();
        npc.questAntiConditionID = jsonObject.has(DataKeys.NPC_QUEST_ANTICONDITION_ID) ?
            jsonObject.get(DataKeys.NPC_QUEST_ANTICONDITION_ID).getAsLong() : -1;
        return npc;
    }

}
