package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class SetPitchCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        npc.setPitch(DoubleArgumentType.getDouble(context, "pitch"));
        return SINGLE_SUCCESS;
    }

}
