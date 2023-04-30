package com.teampotato.terrible_chest;

import com.teampotato.terrible_chest.init.BlockInitializer;
import com.teampotato.terrible_chest.init.CapabilityInitializer;
import com.teampotato.terrible_chest.init.NetworkInitializer;
import com.teampotato.terrible_chest.init.ScreenInitializer;
import com.teampotato.terrible_chest.settings.Config;
import com.teampotato.terrible_chest.settings.KeyBindings;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("terrible_chest")
public class TerribleChestMod {
    public TerribleChestMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    private void setup(FMLCommonSetupEvent event) {
        CapabilityInitializer.registerCapability();
        NetworkInitializer.registerMessage();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ScreenInitializer.registerScreen();
        KeyBindings.getAll().forEach(ClientRegistry::registerKeyBinding);
        BlockInitializer.registerRenderType();
    }
}

