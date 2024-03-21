package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.pixilcraftnpcs.npc.HumanNPC;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SetHumanNPCSkinCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        if (!(npc instanceof HumanNPC humanNPC)) {
            context.getSource().sendError(Text.literal("NPC " + id + " is not a human NPC"));
            return -1;
        }
        String texture = StringArgumentType.getString(context, "texture");
        humanNPC.setTexture(new Identifier(DataKeys.MOD_NAMESPACE, texture), context.getSource().getServer());
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s texture file to " + texture
        ));
        return SINGLE_SUCCESS;
    }

}
