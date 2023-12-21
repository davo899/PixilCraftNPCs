package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.npc.NPC;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class NPCCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("npc")
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("new")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("id", string())
                    .executes((ctx) -> newNPC(ctx, false))
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("facing", string())
                        .executes((ctx) -> newNPC(ctx, true))
                    )
                )
            )
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("id", string())
                .then(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("set")
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("commandList")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, List<String>>
                            argument("commandList", new CommandListArgumentType())
                            .executes(this::setNPCCommandList)
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("displayName")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("displayName", string())
                            .executes(this::setNPCDisplayName)
                        )
                    )
                )
            )
        );
    }

    private int newNPC(CommandContext<ServerCommandSource> ctx, boolean withFacing) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();
        if (!source.isExecutedByPlayer()) {
            source.sendError(Text.literal("Must be executed by a player"));
            return -1;
        }
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        double pitch = 0;
        double yaw;
        if (withFacing) {
            String facing = StringArgumentType.getString(ctx, "facing");
            yaw = 360 * switch (facing.toUpperCase()) {
                case "S" -> 0/8d;
                case "SW" -> 1/8d;
                case "W" -> 2/8d;
                case "NW" -> 3/8d;
                case "N" -> 4/8d;
                case "NE" -> 5/8d;
                case "E" -> 6/8d;
                case "SE" -> 7/8d;
                default -> throw new CommandSyntaxException(
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(),
                    () -> "Facing direction must be one of N,NE,E,SE,S,SW,W,NW"
                );
            };
        } else {
            pitch = player.getPitch();
            yaw = player.getHeadYaw();
        }
        String id = StringArgumentType.getString(ctx, "id");
        if (NPCTracker.getInstance().exists(id)) {
            ctx.getSource().sendError(Text.literal("NPC " + id + " already exists"));
            return -1;
        }
        NPCTracker.getInstance().add(id, new NPC(
            id,
            new MultiversePos(player.getPos(), player.getWorld().getRegistryKey().getValue()),
            pitch, yaw,
            new ArrayList<>()
        ));
        ctx.getSource().sendMessage(Text.literal("Created NPC " + id));
        return 1;
    }

    private static Optional<NPC> getNPC(CommandContext<ServerCommandSource> ctx) {
        String id = StringArgumentType.getString(ctx, "id");
        NPC npc = NPCTracker.getInstance().get(id);
        if (npc == null) {
            ctx.getSource().sendError(Text.literal("NPC " + id + " does not exist"));
            return Optional.empty();
        }
        return Optional.of(npc);
    }

    private int setNPCCommandList(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        List<String> commandList = CommandListArgumentType.getCommands(ctx, "commandList");
        npc.get().setCommandList(commandList);
        String id = StringArgumentType.getString(ctx, "id");
        ctx.getSource().sendMessage(Text.literal("Set NPC " + id + "'s command list to:"));
        for (String command : commandList) {
            ctx.getSource().sendMessage(Text.literal("  " + command));
        }
        return 1;
    }

    private int setNPCDisplayName(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        String displayName = StringArgumentType.getString(ctx, "displayName");
        npc.get().setDisplayName(displayName);
        String id = StringArgumentType.getString(ctx, "id");
        ctx.getSource().sendMessage(Text.literal("Set NPC " + id + "'s display name to " + displayName));
        return 1;
    }

}
