package com.selfdot.pixilcraftnpcs.npc;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.google.gson.JsonObject;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.imixin.IPokemonEntityMixin;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import kotlin.Unit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PokemonNPC extends NPC<PokemonEntity> {

    private final Species species;
    private final Pokemon pokemon;

    public PokemonNPC(String id, MultiversePos position, double pitch, double yaw, Species species) {
        super(id, position, pitch, yaw);
        this.species = species;
        pokemon = new Pokemon();
        pokemon.setSpecies(species);
        pokemon.getCustomProperties().add(UncatchableProperty.INSTANCE.uncatchable());
    }

    @Override
    protected void spawnEntityInWorld(ServerWorld world) {
        entity = new PokemonEntity(world, pokemon, CobblemonEntities.POKEMON);
        updateTracked();
        world.spawnEntity(entity);
    }

    @Override
    protected void updateTracked() {
        super.updateTracked();
        entity.getLabelLevel$common().set(0);
        entity.getUnbattleable().set(true);
        entity.getNicknameVisible().set(true);
        entity.getHideLabel().set(!nameplateEnabled);
        ((IPokemonEntityMixin)(Object)entity).pixilCraftNPCs$setNPC(true);
        entity.setAiDisabled(true);
        entity.setPersistent();
        entity.setInvulnerable(true);
        entity.setPokemon(pokemon);
    }

    @Override
    protected boolean faceNearestPlayer() {
        PlayerEntity nearestPlayer = entity.getWorld().getClosestPlayer(
            entity, PixilCraftNPCs.CONFIG.getMaxFacesPlayerDistance()
        );
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
    public void updateDisplayName() {
        entity.setCustomName(formattedDisplayName());
    }

    @Override
    public void updateNameplateEnabled() {
        entity.getHideLabel().set(!nameplateEnabled);
    }

    @Override
    public void remove(MinecraftServer server) {
        ((IPokemonEntityMixin)(Object)entity).pixilCraftNPCs$setDiscardable(true);
        super.remove(server);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(DataKeys.NPC_TYPE, DataKeys.NPC_POKEMON);
        jsonObject.addProperty(DataKeys.NPC_POKEMON_SPECIES, species.getResourceIdentifier().toString());
        return jsonObject;
    }

}
