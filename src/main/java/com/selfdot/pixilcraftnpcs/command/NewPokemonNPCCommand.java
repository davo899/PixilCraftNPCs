package com.selfdot.pixilcraftnpcs.command;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import com.selfdot.pixilcraftnpcs.npc.PokemonNPC;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class NewPokemonNPCCommand extends NewNPCCommand {

    public NewPokemonNPCCommand(boolean withFacing) {
        super(withFacing);
    }

    @Override
    public int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String speciesStr = StringArgumentType.getString(context, "species");
        Species species = PokemonSpecies.INSTANCE.getByName(speciesStr);
        if (species == null) {
            context.getSource().sendError(Text.literal("Unknown species " + speciesStr));
            return -1;
        }
        NPCTracker.getInstance().add(id, new PokemonNPC(
            id, new MultiversePos(player.getPos(), player.getWorld().getRegistryKey().getValue()),
            pitch, yaw, species
        ));
        context.getSource().sendMessage(Text.literal("Created new " + speciesStr + " NPC " + id));
        return SINGLE_SUCCESS;
    }

}
