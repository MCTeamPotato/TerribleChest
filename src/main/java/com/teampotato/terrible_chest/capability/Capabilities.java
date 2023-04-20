package com.teampotato.terrible_chest.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {
    @CapabilityInject(TerribleChestCapability.class)
    public static Capability<TerribleChestCapability> TERRIBLE_CHEST;

}
