package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ToggleGloballyVisibleCommand extends NPCEditCommand {

    @Override
    public int runSubCommand(CommandContext<ServerCommandSource> context) {
        npc.toggleGloballyInvisible();
        context.getSource().sendMessage(Text.literal("Toggled NPC " + id));
        return SINGLE_SUCCESS;
    }

}
