package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetProximityTriggerRadiusCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double proximityTriggerRadius = DoubleArgumentType.getDouble(context, "proximityTriggerRadius");
        npc.setProximityTriggerRadius(proximityTriggerRadius);
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s proximity trigger radius to " + proximityTriggerRadius
        ));
        return SINGLE_SUCCESS;
    }

}
