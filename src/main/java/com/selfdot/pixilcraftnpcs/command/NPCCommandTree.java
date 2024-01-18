package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.npc.SkinType;
import com.selfdot.pixilcraftnpcs.util.CommandUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.List;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.argument.IdentifierArgumentType.identifier;

public class NPCCommandTree {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>
            literal("npc")
            .requires(source -> !PixilCraftNPCs.getInstance().isDisabled())
            .requires(source -> CommandUtils.hasPermission(source, "selfdot.op.npcs"))
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("new")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .then(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("human")
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("id", string())
                        .executes(new NewHumanNPCCommand(false))
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("facing", string())
                            .executes(new NewHumanNPCCommand(true))
                        )
                    )
                )
                .then(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("pokemon")
                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                        argument("species", string())
                        .suggests(new PokemonSpeciesSuggestionProvider())
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("id", string())
                            .executes(new NewPokemonNPCCommand(false))
                            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                                argument("facing", string())
                                .executes(new NewPokemonNPCCommand(true))
                            )
                        )
                    )
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("toggle")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("id", string())
                    .suggests(new NPCIDSuggestionProvider())
                    .executes(new ToggleGloballyVisibleCommand())
                )
            )
            .then(LiteralArgumentBuilder.<ServerCommandSource>
                literal("delete")
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                    argument("id", string())
                    .suggests(new NPCIDSuggestionProvider())
                    .executes(new DeleteNPCCommand())
                )
            )
            .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                argument("id", string())
                .suggests(new NPCIDSuggestionProvider())
                .then(LiteralArgumentBuilder.<ServerCommandSource>
                    literal("set")
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("commandList")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, List<String>>
                            argument("commandList", new CommandListArgumentType())
                            .executes(new SetCommandListCommand())
                        )
                        .then(LiteralArgumentBuilder.<ServerCommandSource>
                            literal("none")
                            .executes(new ClearCommandListCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("displayName")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("displayName", string())
                            .executes(new SetDisplayNameCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("nameplateEnabled")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Boolean>
                            argument("nameplateEnabled", bool())
                            .executes(new SetNameplateEnabledCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("interactCooldownSeconds")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Long>
                            argument("interactCooldownSeconds", longArg())
                            .executes(new SetInteractCooldownSecondsCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("texture")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("texture", string())
                            .executes(new SetHumanNPCSkinCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("questCondition")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>
                            argument("questID", string())
                            .executes(new SetQuestConditionCommand())
                        )
                        .then(LiteralArgumentBuilder.<ServerCommandSource>
                            literal("none")
                            .executes(new ClearQuestConditionCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("facesNearestPlayer")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Boolean>
                            argument("facesNearestPlayer", bool())
                            .executes(new SetFacesNearestPlayerCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("proximityTriggerRadius")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Double>
                            argument("proximityTriggerRadius", doubleArg())
                            .executes(new SetProximityTriggerRadiusCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("position")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Double>
                            argument("x", doubleArg())
                            .then(RequiredArgumentBuilder.<ServerCommandSource, Double>
                                argument("y", doubleArg())
                                .then(RequiredArgumentBuilder.<ServerCommandSource, Double>
                                    argument("z", doubleArg())
                                    .then(RequiredArgumentBuilder.<ServerCommandSource, Identifier>
                                        argument("worldID", identifier())
                                        .suggests(new WorldIDSuggestionProvider())
                                        .executes(new SetPositionCommand())
                                    )
                                )
                            )
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("pitch")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Double>
                            argument("pitch", doubleArg())
                            .executes(new SetPitchCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("yaw")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Double>
                            argument("yaw", doubleArg())
                            .executes(new SetYawCommand())
                        )
                    )
                    .then(LiteralArgumentBuilder.<ServerCommandSource>
                        literal("skinType")
                        .then(LiteralArgumentBuilder.<ServerCommandSource>
                            literal("classic")
                            .executes(new SetHumanNPCSkinTypeCommand(SkinType.CLASSIC))
                        )
                        .then(LiteralArgumentBuilder.<ServerCommandSource>
                            literal("slim")
                            .executes(new SetHumanNPCSkinTypeCommand(SkinType.SLIM))
                        )
                    )
                )
            )
        );
    }

}
