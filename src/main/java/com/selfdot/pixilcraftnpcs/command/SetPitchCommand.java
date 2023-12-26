package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetPitchCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double pitch = DoubleArgumentType.getDouble(context, "pitch");
        npc.setPitch(pitch);
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s pitch to " + pitch
        ));
        return SINGLE_SUCCESS;
    }

}
