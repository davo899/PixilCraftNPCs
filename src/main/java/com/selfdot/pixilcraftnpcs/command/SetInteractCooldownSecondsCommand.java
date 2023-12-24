package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetInteractCooldownSecondsCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        long interactCooldownSeconds = LongArgumentType.getLong(context, "interactCooldownSeconds");
        npc.setInteractCooldownSeconds(interactCooldownSeconds);
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s interact cooldown to " + interactCooldownSeconds + " seconds"
        ));
        return SINGLE_SUCCESS;
    }

}
