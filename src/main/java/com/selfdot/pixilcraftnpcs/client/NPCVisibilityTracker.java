package com.selfdot.pixilcraftnpcs.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCVisibilityTracker {

    private static final NPCVisibilityTracker INSTANCE = new NPCVisibilityTracker();
    private NPCVisibilityTracker() { }
    public static NPCVisibilityTracker getInstance() { return INSTANCE; }

    private final Map<UUID, Boolean> visibilityMap = new HashMap<>();

    public boolean notVisible(UUID npcEntity) {
        Boolean visible = visibilityMap.get(npcEntity);
        if (visible == null) return false;
        return !visible;
    }

    public void putVisibility(UUID entityID, boolean visible) {
        visibilityMap.put(entityID, visible);
    }

}
