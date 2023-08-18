package com.clientcrafting;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.clientcrafting.ClientCraftingMod.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class ClientCraftingMod
{
    public static final String MOD_ID = "clientcrafting";
    public static final Logger LOGGER = LogManager.getLogger();
    public static       Random rand   = new Random();

    public ClientCraftingMod()
    {
    }
}
