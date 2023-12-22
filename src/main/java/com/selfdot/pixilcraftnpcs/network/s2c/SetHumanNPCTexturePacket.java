package com.selfdot.pixilcraftnpcs.network.s2c;

import com.selfdot.pixilcraftnpcs.network.PixilCraftNPCsPacket;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class SetHumanNPCTexturePacket extends PixilCraftNPCsPacket {

    public static final Identifier ID = new Identifier(
        DataKeys.PIXILCRAFT_NAMESPACE, "set_human_npc_texture"
    );

    private final UUID npcEntityID;
    private final Identifier texture;

    public SetHumanNPCTexturePacket(UUID npcEntityID, Identifier texture) {
        this.npcEntityID = npcEntityID;
        this.texture = texture;
    }

    @Override
    protected Identifier id() {
        return ID;
    }

    public PacketByteBuf encode() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(npcEntityID);
        buf.writeIdentifier(texture);
        return buf;
    }

}
