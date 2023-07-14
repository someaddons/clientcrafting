package com.clientcrafting.config;

import com.clientcrafting.ClientCraftingMod;
import com.google.gson.JsonObject;

public class CommonConfiguration {

    public boolean skipWeatherOnSleep = false;

    protected CommonConfiguration() {
    }

    public JsonObject serialize() {
        final JsonObject root = new JsonObject();

        final JsonObject entry = new JsonObject();
        entry.addProperty("desc:", "Whether to skip weather after sleeping: default:false");
        entry.addProperty("skipWeatherOnSleep", skipWeatherOnSleep);
        root.add("skipWeatherOnSleep", entry);

        return root;
    }

    public void deserialize(JsonObject data) {
        if (data == null) {
            ClientCraftingMod.LOGGER.error("Config file was empty!");
            return;
        }

        skipWeatherOnSleep = data.get("skipWeatherOnSleep").getAsJsonObject().get("skipWeatherOnSleep").getAsBoolean();
    }
}
