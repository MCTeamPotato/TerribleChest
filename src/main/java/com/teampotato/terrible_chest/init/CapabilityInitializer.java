package com.teampotato.terrible_chest.init;

import com.teampotato.terrible_chest.capability.TerribleChestCapability;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityInitializer {
    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(TerribleChestCapability.class, TerribleChestCapability.storage(), TerribleChestCapability::new);
    }
}
