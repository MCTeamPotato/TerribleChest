package com.teampotato.terriblechest.init;

import com.teampotato.terriblechest.capability.TerribleChestCapability;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityInitializer {
    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(TerribleChestCapability.class, TerribleChestCapability.storage(), TerribleChestCapability::new);
    }
}
