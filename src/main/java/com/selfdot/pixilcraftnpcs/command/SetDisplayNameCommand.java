package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.pixilcraftnpcs.util.ChatColourUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetDisplayNameCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        String displayName = StringArgumentType.getString(context, "displayName");
        npc.setDisplayName(displayName);
        context.getSource().sendMessage(Text.literal(
            ChatColourUtils.format("Set NPC " + id + "'s display name to " + displayName)
        ));
        return SINGLE_SUCCESS;
    }

}
