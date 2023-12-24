package com.selfdot.pixilcraftnpcs.npc;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.imixin.IPokemonEntityMixin;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import kotlin.Unit;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PokemonNPC extends NPC<PokemonEntity> {

    private final Species species;

    public PokemonNPC(
        String id, String displayName, MultiversePos position, double pitch, double yaw,
        List<String> commandList, boolean nameplateEnabled, int interactCooldownSeconds,
        long questConditionID, boolean globallyInvisible, boolean facesNearestPlayer,
        double proximityTriggerRadius, Species species
    ) {
        super(
            id, displayName, position, pitch, yaw, commandList,
            nameplateEnabled, interactCooldownSeconds, questConditionID,
            globallyInvisible, facesNearestPlayer, proximityTriggerRadius
        );
        this.species = species;
    }

    @Override
    public PokemonEntity getNewEntity(ServerWorld world) {
        PokemonEntity entity = CobblemonEntities.POKEMON.spawn(world, BlockPos.ORIGIN, SpawnReason.MOB_SUMMONED);
        if (entity == null) return null;
        Pokemon pokemon = new Pokemon();
        pokemon.setSpecies(species);
        entity.setPokemon(pokemon);
        entity.setAiDisabled(true);
        entity.setPersistent();
        entity.setInvulnerable(true);
        entity.getLabelLevel$common().set(0);
        entity.getUnbattleable().set(true);
        entity.setCustomName(Text.literal(displayName));
        entity.getNicknameVisible().set(true);
        ((IPokemonEntityMixin)(Object)entity).pixilCraftNPCs$setNPC(true);
        entity.getLabelLevel$common().subscribe(
            Priority.HIGHEST,
            n -> {
                if (n != 0) entity.getLabelLevel$common().set(0);
                return Unit.INSTANCE;
            }
        );
        entity.getUnbattleable().subscribe(
            Priority.HIGHEST,
            b -> {
                if (!b) entity.getUnbattleable().set(true);
                return Unit.INSTANCE;
            }
        );
        return entity;
    }

    @Override
    protected boolean faceNearestPlayer() {
        PlayerEntity nearestPlayer = entity.getWorld().getClosestPlayer(entity, 5);
        if (nearestPlayer == null) return false;
        Vec3d diff = nearestPlayer.getEyePos().subtract(entity.getEyePos());
        Vec3d xzDir = diff.withAxis(Direction.Axis.Y, 0).normalize();
        float yaw = (float)(Math.acos(xzDir.z) * (180d / Math.PI));
        if (xzDir.x > 0) yaw = -yaw;
        entity.setBodyYaw(yaw);
        entity.setHeadYaw(yaw);
        float pitch = (float)(Math.acos(diff.normalize().dotProduct(xzDir)) * (180d / Math.PI));
        if (diff.y > 0) pitch = -pitch;
        entity.setPitch(pitch);
        return true;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        entity.setCustomName(Text.literal(displayName));
    }

    @Override
    public void setNameplateEnabled(boolean nameplateEnabled) {
        this.nameplateEnabled = nameplateEnabled;
        entity.getHideLabel().set(!nameplateEnabled);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(DataKeys.NPC_TYPE, DataKeys.NPC_POKEMON);
        jsonObject.addProperty(DataKeys.NPC_POKEMON_SPECIES, species.getResourceIdentifier().toString());
        return jsonObject;
    }

}
