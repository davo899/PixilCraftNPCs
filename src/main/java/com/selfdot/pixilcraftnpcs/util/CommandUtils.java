package com.selfdot.pixilcraftnpcs.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandUtils {

    public static void executeCommandAsServer(String command, MinecraftServer server) {
        try {
            server.getCommandManager().getDispatcher().execute(command, server.getCommandSource());

        } catch (CommandSyntaxException e) {
            PixilCraftNPCs.LOGGER.error("Could not run: " + command);
            PixilCraftNPCs.LOGGER.error(e.getMessage());
        }
    }

    public static boolean hasPermission(ServerCommandSource source, String permission) {
        if (source.hasPermissionLevel(4)) return true;
        if (!source.isExecutedByPlayer()) return false;
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return false;
        if (!isLuckPermsPresent()) return false;
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUuid());
        if (user == null) return false;
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    private static boolean isLuckPermsPresent() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
