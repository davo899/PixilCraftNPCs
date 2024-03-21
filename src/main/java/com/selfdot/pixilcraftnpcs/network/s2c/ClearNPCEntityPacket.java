package com.selfdot.pixilcraftnpcs.network.s2c;

import com.selfdot.pixilcraftnpcs.network.PixilCraftNPCsPacket;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ClearNPCEntityPacket extends PixilCraftNPCsPacket {

    public static final Identifier ID = new Identifier(
        DataKeys.MOD_NAMESPACE, "clear_human_npc_texture"
    );

    private final UUID npcEntityID;

    public ClearNPCEntityPacket(UUID npcEntityID) {
        this.npcEntityID = npcEntityID;
    }

    @Override
    protected Identifier id() {
        return ID;
    }

    @Override
    protected PacketByteBuf encode() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(npcEntityID);
        return buf;
    }

}
