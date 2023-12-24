package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetFacesNearestPlayerCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        boolean facesNearestPlayer = BoolArgumentType.getBool(context, "facesNearestPlayer");
        npc.setFacesNearestPlayer(facesNearestPlayer);
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + " faces nearest player to " + facesNearestPlayer
        ));
        return SINGLE_SUCCESS;
    }

}
