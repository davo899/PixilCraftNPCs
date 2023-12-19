package com.selfdot.pixilcraftnpcs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record MultiversePos(BlockPos blockPos, Identifier worldID) {

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.MV_POS_X, blockPos.getX());
        jsonObject.addProperty(DataKeys.MV_POS_Y, blockPos.getY());
        jsonObject.addProperty(DataKeys.MV_POS_Z, blockPos.getZ());
        jsonObject.addProperty(DataKeys.MV_POS_WORLD_ID, worldID.toString());
        return jsonObject;
    }

    public static MultiversePos fromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int x = jsonObject.get(DataKeys.MV_POS_X).getAsInt();
        int y = jsonObject.get(DataKeys.MV_POS_Y).getAsInt();
        int z = jsonObject.get(DataKeys.MV_POS_Z).getAsInt();
        Identifier worldID = new Identifier(jsonObject.get(DataKeys.MV_POS_WORLD_ID).getAsString());
        return new MultiversePos(new BlockPos(x, y, z), worldID);
    }

}
