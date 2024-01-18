package com.selfdot.pixilcraftnpcs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import com.selfdot.pixilcraftnpcs.util.DisableableMod;
import com.selfdot.pixilcraftnpcs.util.JsonFile;
import com.selfdot.pixilcraftnpcs.util.ReadOnlyJsonFile;
import net.minecraft.server.MinecraftServer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PixilCraftNPCsConfig extends JsonFile {

    private double minDespawnDistance;
    private double maxFacesPlayerDistance;

    public PixilCraftNPCsConfig(DisableableMod mod) {
        super(mod);
    }

    public double getMinDespawnDistance() {
        return minDespawnDistance;
    }

    public double getMaxFacesPlayerDistance() {
        return maxFacesPlayerDistance;
    }

    @Override
    protected String filename() {
        return "config/pixilcraftnpcsConfig.json";
    }

    @Override
    protected void setDefaults() {
        minDespawnDistance = 512;
        maxFacesPlayerDistance = 5;
    }

    @Override
    protected void loadFromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has(DataKeys.CONFIG_MIN_DESPAWN_DISTANCE)) {
            minDespawnDistance = jsonObject.get(DataKeys.CONFIG_MIN_DESPAWN_DISTANCE).getAsDouble();
        }
        if (jsonObject.has(DataKeys.CONFIG_MAX_FACES_PLAYER_DISTANCE)) {
            maxFacesPlayerDistance = jsonObject.get(DataKeys.CONFIG_MAX_FACES_PLAYER_DISTANCE).getAsDouble();
        }
    }

    @Override
    protected JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.CONFIG_MIN_DESPAWN_DISTANCE, minDespawnDistance);
        jsonObject.addProperty(DataKeys.CONFIG_MAX_FACES_PLAYER_DISTANCE, maxFacesPlayerDistance);
        return jsonObject;
    }

}
