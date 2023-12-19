package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.selfdot.pixilcraftnpcs.npc.NPC;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class NPCCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("npc")
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("new")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("id", string())
                    .executes(this::newNPC)
                )
            )
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("id", string())
                .then(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("setCommandList")
                    .then(RequiredArgumentBuilder.<ServerCommandSource, List<String>>
                        argument("commandList", new CommandListArgumentType())
                        .executes(this::setNPCCommandList)
                    )
                )
            )
        );
    }

    private int newNPC(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        if (!source.isExecutedByPlayer()) {
            source.sendError(Text.literal("Must be executed by a player"));
            return -1;
        }
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        String id = StringArgumentType.getString(ctx, "id");
        NPCTracker.getInstance().add(id, new NPC(
            "",
            new MultiversePos(player.getBlockPos(), player.getWorld().getRegistryKey().getValue()),
            new ArrayList<>()
        ));
        return 1;
    }

    private int setNPCCommandList(CommandContext<ServerCommandSource> ctx) {
        String id = StringArgumentType.getString(ctx, "id");
        NPC npc = NPCTracker.getInstance().get(id);
        if (npc == null) {
            ctx.getSource().sendError(Text.literal("NPC " + id + " does not exist"));
            return -1;
        }
        npc.setCommandList(CommandListArgumentType.getCommands(ctx, "commandList"));
        return 1;
    }

}
