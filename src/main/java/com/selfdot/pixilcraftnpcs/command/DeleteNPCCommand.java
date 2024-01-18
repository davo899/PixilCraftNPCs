package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.npc.InteractCooldownTracker;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class DeleteNPCCommand extends NPCEditCommand {

    @Override
    protected int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PixilCraftNPCs.getInstance().getInteractCooldownTracker().deleteNPC(id);
        PixilCraftNPCs.getInstance().getNPCTracker().delete(id);
        context.getSource().sendMessage(Text.literal("Deleted NPC " + id));
        return SINGLE_SUCCESS;
    }

}
