package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class SetPositionCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier worldID = IdentifierArgumentType.getIdentifier(context, "worldID");
        Vec3d pos = new Vec3d(
            DoubleArgumentType.getDouble(context, "x"),
            DoubleArgumentType.getDouble(context, "y"),
            DoubleArgumentType.getDouble(context, "z")
        );
        npc.setPosition(new MultiversePos(pos, worldID), context.getSource().getServer());
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s position to " + pos + " in " + worldID
        ));
        return SINGLE_SUCCESS;
    }

}
