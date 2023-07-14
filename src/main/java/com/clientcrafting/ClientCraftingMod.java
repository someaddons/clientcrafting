package com.clientcrafting;

import com.clientcrafting.config.Configuration;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class ClientCraftingMod implements ModInitializer {

    public static final String MOD_ID = "clientcrafting";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static Configuration config = null;
    public static Random rand = new Random();

    @Override
    public void onInitialize() {
    }

    public static Configuration getConfig() {
        if (config == null) {
            config = new Configuration();
            config.load();
        }

        return config;
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
