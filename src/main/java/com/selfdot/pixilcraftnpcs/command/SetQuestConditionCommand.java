package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetQuestConditionCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String questIDString = StringArgumentType.getString(context, "questID");
        long questID = Long.decode("0x" + questIDString);
        npc.setQuestConditionID(questID, context.getSource().getServer());
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s quest condition ID to " + questIDString
        ));
        return SINGLE_SUCCESS;
    }

}
