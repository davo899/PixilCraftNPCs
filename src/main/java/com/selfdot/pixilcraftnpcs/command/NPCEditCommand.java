package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.pixilcraftnpcs.npc.NPC;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public abstract class NPCEditCommand extends TwoLayerCommand {

    protected String id;
    protected NPC<?> npc;

    @Override
    public int runSuperCommand(CommandContext<ServerCommandSource> context) {
        id = StringArgumentType.getString(context, "id");
        npc = NPCTracker.getInstance().get(id);
        if (npc == null) {
            context.getSource().sendError(Text.literal("NPC " + id + " does not exist"));
            return -1;
        }
        return SINGLE_SUCCESS;
    }

}
