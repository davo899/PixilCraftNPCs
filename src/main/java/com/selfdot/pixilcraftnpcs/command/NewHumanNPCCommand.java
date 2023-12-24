package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.npc.HumanNPC;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class NewHumanNPCCommand extends NewNPCCommand {
    public NewHumanNPCCommand(boolean withFacing) {
        super(withFacing);
    }

    @Override
    public int runSubCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        NPCTracker.getInstance().add(id, new HumanNPC(
            id, new MultiversePos(player.getPos(), player.getWorld().getRegistryKey().getValue()), pitch, yaw
        ));
        context.getSource().sendMessage(Text.literal("Created new human NPC " + id));
        return SINGLE_SUCCESS;
    }

}
