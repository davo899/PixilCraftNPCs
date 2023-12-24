package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public class SetCommandListCommand extends NPCEditCommand {

    @Override
    public int runSubCommand(CommandContext<ServerCommandSource> context) {
        List<String> commandList = CommandListArgumentType.getCommands(context, "commandList");
        npc.setCommandList(commandList);
        context.getSource().sendMessage(Text.literal("Set NPC " + id + "'s command list to:"));
        for (String command : commandList) {
            context.getSource().sendMessage(Text.literal("  " + command));
        }
        return SINGLE_SUCCESS;
    }

}
