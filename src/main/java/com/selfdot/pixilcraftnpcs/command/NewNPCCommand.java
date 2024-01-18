package com.selfdot.pixilcraftnpcs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.selfdot.pixilcraftnpcs.PixilCraftNPCs;
import com.selfdot.pixilcraftnpcs.npc.NPCTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public abstract class NewNPCCommand extends TwoLayerCommand {

    private boolean withFacing;
    protected ServerPlayerEntity player;
    protected String id;
    protected double pitch;
    protected double yaw;

    public NewNPCCommand(boolean withFacing) {
        this.withFacing = withFacing;
    }

    @Override
    public int runSuperCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        if (!source.isExecutedByPlayer()) {
            source.sendError(Text.literal("Must be executed by a player"));
            return -1;
        }
        player = source.getPlayer();
        if (player == null) return 0;

        pitch = 0;
        if (withFacing) {
            String facing = StringArgumentType.getString(context, "facing");
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

        id = StringArgumentType.getString(context, "id");
        if (PixilCraftNPCs.getInstance().getNPCTracker().exists(id)) {
            context.getSource().sendError(Text.literal("NPC " + id + " already exists"));
            return -1;
        }
        return SINGLE_SUCCESS;
    }

}
