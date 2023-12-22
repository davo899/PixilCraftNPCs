package com.selfdot.pixilcraftnpcs.network.s2c;

import com.selfdot.pixilcraftnpcs.network.PixilCraftNPCsPacket;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class SetNPCVisibilityPacket extends PixilCraftNPCsPacket {

    public static final Identifier ID = new Identifier(
        DataKeys.PIXILCRAFT_NAMESPACE, "set_npc_visibility"
    );

    private final UUID npcEntityID;
    private final boolean visible;

    public SetNPCVisibilityPacket(UUID npcEntityID, boolean visible) {
        this.npcEntityID = npcEntityID;
        this.visible = visible;
    }

    @Override
    protected Identifier id() {
        return ID;
    }

    public PacketByteBuf encode() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(npcEntityID);
        buf.writeBoolean(visible);
        return buf;
    }
    
}
