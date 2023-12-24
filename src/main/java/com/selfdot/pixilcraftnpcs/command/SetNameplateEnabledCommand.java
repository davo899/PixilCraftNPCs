package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetNameplateEnabledCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        boolean nameplateEnabled = BoolArgumentType.getBool(context, "nameplateEnabled");
        npc.setNameplateEnabled(nameplateEnabled);
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s nameplate enabled to " + nameplateEnabled
        ));
        return SINGLE_SUCCESS;
    }

}
