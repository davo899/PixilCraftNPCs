package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ClearCommandListCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        npc.setCommandList(new ArrayList<>());
        context.getSource().sendMessage(Text.literal("Cleared NPC " + id + "'s command list"));
        return SINGLE_SUCCESS;
    }

}
