package com.selfdot.pixilcraftnpcs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.selfdot.pixilcraftnpcs.util.DataKeys;
import net.minecraft.server.MinecraftServer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PixilCraftNPCsConfig {

    private static final String FILENAME = "config/pixilcraftnpcsConfig.json";

    private double minDespawnDistance = 512;

    public double getMinDespawnDistance() {
        return minDespawnDistance;
    }

    public void reload(MinecraftServer server) {
        try {
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(FILENAME));
            try {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                minDespawnDistance = jsonObject.get(DataKeys.CONFIG_MIN_DESPAWN_DISTANCE).getAsDouble();
                PixilCraftNPCs.LOGGER.info("Loaded NPCs config");

            } catch (Exception e) {
                PixilCraftNPCs.DISABLED = true;
                LogUtils.getLogger().error("An exception occurred when loading NPCs config:");
                LogUtils.getLogger().error(e.getMessage());
            }

        } catch (FileNotFoundException e) {
            PixilCraftNPCs.LOGGER.warn("NPCs config file not found, attempting to generate");
        }

        try {
            Files.createDirectories(Paths.get(FILENAME).getParent());
            FileWriter writer = new FileWriter(FILENAME);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(DataKeys.CONFIG_MIN_DESPAWN_DISTANCE, minDespawnDistance);
            PixilCraftNPCs.GSON.toJson(jsonObject, writer);
            writer.close();

        } catch (IOException ex) {
            PixilCraftNPCs.DISABLED = true;
            LogUtils.getLogger().error("Unable to save NPCs config file");
        }
    }

}
