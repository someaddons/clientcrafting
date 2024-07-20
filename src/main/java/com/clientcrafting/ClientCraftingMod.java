package com.clientcrafting;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class ClientCraftingMod implements ModInitializer {

    public static final String MOD_ID = "clientcrafting";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Random rand = new Random();

    @Override
    public void onInitialize() {
    }
}
