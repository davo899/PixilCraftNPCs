package com.selfdot.pixilcraftnpcs.client;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HumanNPCTextureTracker {

    private static final HumanNPCTextureTracker INSTANCE = new HumanNPCTextureTracker();
    private HumanNPCTextureTracker() { }
    public static HumanNPCTextureTracker getInstance() { return INSTANCE; }

    private final Map<UUID, Identifier> textureMap = new HashMap<>();

    @NotNull
    public Identifier getTexture(UUID entityID) {
        Identifier texture = textureMap.get(entityID);
        if (texture == null) texture = new Identifier("textures/entity/player/slim/steve.png");
        return texture;
    }

    public void putTexture(UUID entityID, Identifier texture) {
        System.out.println(entityID + " " + texture);
        textureMap.put(entityID, texture);
    }

}
