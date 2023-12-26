package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetYawCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double yaw = DoubleArgumentType.getDouble(context, "yaw");
        npc.setYaw(yaw);
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s yaw to " + yaw
        ));
        return SINGLE_SUCCESS;
    }

}
