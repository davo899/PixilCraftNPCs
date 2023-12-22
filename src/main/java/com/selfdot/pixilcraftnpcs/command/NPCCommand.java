package com.selfdot.pixilcraftnpcs.command;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.selfdot.pixilcraftnpcs.npc.HumanNPC;
import com.selfdot.pixilcraftnpcs.npc.NPC;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import com.selfdot.pixilcraftnpcs.npc.PokemonNPC;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.MultiversePos;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class NPCCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("npc")
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("new")
                .then(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("human")
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("id", string())
                        .executes((ctx) -> newNPC(ctx, DataKeys.NPC_HUMAN, false))
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("facing", string())
                            .executes((ctx) -> newNPC(ctx, DataKeys.NPC_HUMAN, true))
                        )
                    )
                )
                .then(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("pokemon")
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("species", string())
                        .suggests((ctx, builder) -> {
                            PokemonSpecies.INSTANCE.getSpecies().forEach(
                                species -> builder.suggest(species.getName().toLowerCase())
                            );
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("id", string())
                            .executes((ctx) -> newNPC(ctx, DataKeys.NPC_POKEMON, false))
                            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                                argument("facing", string())
                                .executes((ctx) -> newNPC(ctx, DataKeys.NPC_POKEMON, true))
                            )
                        )
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("toggle")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("id", string())
                    .suggests(this::npcIDs)
                    .executes(this::toggleNPCGloballyInvisible)
                )
            )
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("id", string())
                .suggests(this::npcIDs)
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
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("nameplateEnabled")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Boolean>
                            argument("nameplateEnabled", bool())
                            .executes(this::setNPCNameplateEnabled)
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("interactCooldownSeconds")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Long>
                            argument("interactCooldownSeconds", longArg())
                            .executes(this::setNPCInteractCooldownSeconds)
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("texture")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("texture", string())
                            .executes(this::setHumanNPCSkin)
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("questCondition")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("questID", string())
                            .executes(this::setNPCQuestCondition)
                        )
                        .then(LiteralArgumentBuilder.<ServerCommandSource>
                            literal("none")
                            .executes(this::clearNPCQuestCondition)
                        )
                    )
                )
            )
        );
    }

    private CompletableFuture<Suggestions> npcIDs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        NPCTracker.getInstance().getAllIDs().forEach(builder::suggest);
        return builder.buildFuture();
    }

    private int newNPC(
        CommandContext<ServerCommandSource> ctx, String type, boolean withFacing
    ) throws CommandSyntaxException {
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

        if (type.equals(DataKeys.NPC_POKEMON)) {
            String speciesStr = StringArgumentType.getString(ctx, "species");
            Species species = PokemonSpecies.INSTANCE.getByName(speciesStr);
            if (species == null) {
                ctx.getSource().sendError(Text.literal("Unknown species " + speciesStr));
                return -1;
            }
            NPCTracker.getInstance().add(id, new PokemonNPC(
                id,
                id,
                new MultiversePos(player.getPos(), player.getWorld().getRegistryKey().getValue()),
                pitch, yaw,
                new ArrayList<>(),
                true,
                0,
                -1,
                false,
                species
            ));

        } else if (type.equals(DataKeys.NPC_HUMAN)) {
            NPCTracker.getInstance().add(id, new HumanNPC(
                id,
                id,
                new MultiversePos(player.getPos(), player.getWorld().getRegistryKey().getValue()),
                pitch, yaw,
                new ArrayList<>(),
                true,
                0,
                -1,
                false,
                new Identifier("textures/entity/player/slim/steve.png")
            ));
        }
        ctx.getSource().sendMessage(Text.literal("Created new NPC " + id));
        return 1;
    }

    private static Optional<NPC<?>> getNPC(CommandContext<ServerCommandSource> ctx) {
        String id = StringArgumentType.getString(ctx, "id");
        NPC<?> npc = NPCTracker.getInstance().get(id);
        if (npc == null) {
            ctx.getSource().sendError(Text.literal("NPC " + id + " does not exist"));
            return Optional.empty();
        }
        return Optional.of(npc);
    }

    private int toggleNPCGloballyInvisible(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC<?>> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        npc.get().toggleGloballyInvisible();
        String id = StringArgumentType.getString(ctx, "id");
        ctx.getSource().sendMessage(Text.literal("Toggled NPC " + id));
        return 1;
    }

    private int setNPCCommandList(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC<?>> npc = getNPC(ctx);
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
        Optional<NPC<?>> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        String displayName = StringArgumentType.getString(ctx, "displayName");
        npc.get().setDisplayName(displayName);
        String id = StringArgumentType.getString(ctx, "id");
        ctx.getSource().sendMessage(Text.literal("Set NPC " + id + "'s display name to " + displayName));
        return 1;
    }

    private int setNPCNameplateEnabled(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC<?>> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        boolean nameplateEnabled = BoolArgumentType.getBool(ctx, "nameplateEnabled");
        npc.get().setNameplateEnabled(nameplateEnabled);
        String id = StringArgumentType.getString(ctx, "id");
        ctx.getSource().sendMessage(Text.literal("Set NPC " + id + " nameplate enabled to " + nameplateEnabled));
        return 1;
    }

    private int setNPCInteractCooldownSeconds(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC<?>> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        long interactCooldownSeconds = LongArgumentType.getLong(ctx, "interactCooldownSeconds");
        npc.get().setInteractCooldownSeconds(interactCooldownSeconds);
        String id = StringArgumentType.getString(ctx, "id");
        ctx.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s interact cooldown to " + interactCooldownSeconds + " seconds"
        ));
        return 1;
    }

    private int setHumanNPCSkin(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC<?>> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        String id = StringArgumentType.getString(ctx, "id");
        if (!(npc.get() instanceof HumanNPC humanNPC)) {
            ctx.getSource().sendError(Text.literal("NPC " + id + " is not a human NPC"));
            return -1;
        }
        String texture = StringArgumentType.getString(ctx, "texture");
        humanNPC.setTexture(new Identifier(DataKeys.PIXILCRAFT_NAMESPACE, texture), ctx.getSource().getServer());
        ctx.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s texture file to " + texture
        ));
        return 1;
    }

    private int setNPCQuestCondition(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC<?>> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        String questIDString = StringArgumentType.getString(ctx, "questID");
        long questID = Long.decode("0x" + questIDString);
        String id = StringArgumentType.getString(ctx, "id");
        npc.get().setQuestConditionID(questID, ctx.getSource().getServer());
        ctx.getSource().sendMessage(Text.literal(
            "Set NPC " + id + "'s quest condition ID to " + questIDString
        ));
        return 1;
    }

    private int clearNPCQuestCondition(CommandContext<ServerCommandSource> ctx) {
        Optional<NPC<?>> npc = getNPC(ctx);
        if (npc.isEmpty()) return -1;
        String id = StringArgumentType.getString(ctx, "id");
        npc.get().setQuestConditionID(-1, ctx.getSource().getServer());
        ctx.getSource().sendMessage(Text.literal(
            "Cleared NPC " + id + "'s quest condition"
        ));
        return 1;
    }

}
