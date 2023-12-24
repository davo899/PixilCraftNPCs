package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ClearQuestConditionCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        npc.setQuestConditionID(-1, context.getSource().getServer());
        context.getSource().sendMessage(Text.literal(
            "Cleared NPC " + id + "'s quest condition"
        ));
        return SINGLE_SUCCESS;
    }

}
