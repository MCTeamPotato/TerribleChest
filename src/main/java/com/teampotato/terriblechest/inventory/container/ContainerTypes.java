package com.teampotato.terriblechest.inventory.container;


import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

public class ContainerTypes {
    @ObjectHolder("terrible_chest:terrible_chest")
    public static ContainerType<TerribleChestContainer> TERRIBLE_CHEST;
    @ObjectHolder("terrible_chest:terrible_chest_2")
    public static ContainerType<TerribleChestContainer> TERRIBLE_CHEST_2;

    private ContainerTypes() {
    }
}
