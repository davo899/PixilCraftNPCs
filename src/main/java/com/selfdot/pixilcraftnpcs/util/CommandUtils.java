package com.selfdot.pixilcraftnpcs.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.minecraft.server.MinecraftServer;

public class CommandUtils {

    public static void executeCommandAsServer(String command, MinecraftServer server) {
        try {
            server.getCommandManager().getDispatcher().execute(command, server.getCommandSource());

        } catch (CommandSyntaxException e) {
            PixilCraftNPCs.LOGGER.error("Could not run: " + command);
            PixilCraftNPCs.LOGGER.error(e.getMessage());
        }
    }

}
