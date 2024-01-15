package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.context.CommandContext;
import com.selfdot.pixilcraftnpcs.npc.HumanNPC;
import com.selfdot.pixilcraftnpcs.npc.SkinType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetHumanNPCSkinTypeCommand extends NPCEditCommand {

    private final SkinType skinType;

    public SetHumanNPCSkinTypeCommand(SkinType skinType) {
        this.skinType = skinType;
    }

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) {
        if (!(npc instanceof HumanNPC humanNPC)) {
            context.getSource().sendError(Text.literal("NPC " + id + " is not a human NPC"));
            return -1;
        }
        humanNPC.setSkinType(skinType, context.getSource().getServer());
        context.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s skin type to " + switch (skinType) {
                case CLASSIC -> "Classic";
                case SLIM -> "Slim";
            }
        ));
        return SINGLE_SUCCESS;
    }

}
