package com.selfdot.pixilcraftnpcs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record MultiversePos(Vec3d pos, Identifier worldID) {

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(DataKeys.MV_POS_X, pos.getX());
        jsonObject.addProperty(DataKeys.MV_POS_Y, pos.getY());
        jsonObject.addProperty(DataKeys.MV_POS_Z, pos.getZ());
        jsonObject.addProperty(DataKeys.MV_POS_WORLD_ID, worldID.toString());
        return jsonObject;
    }

    public static MultiversePos fromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        double x = jsonObject.get(DataKeys.MV_POS_X).getAsDouble();
        double y = jsonObject.get(DataKeys.MV_POS_Y).getAsDouble();
        double z = jsonObject.get(DataKeys.MV_POS_Z).getAsDouble();
        Identifier worldID = new Identifier(jsonObject.get(DataKeys.MV_POS_WORLD_ID).getAsString());
        return new MultiversePos(new Vec3d(x, y, z), worldID);
    }

}
