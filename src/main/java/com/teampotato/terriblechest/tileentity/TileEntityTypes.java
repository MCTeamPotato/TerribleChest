package com.teampotato.terriblechest.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class TileEntityTypes {
    @ObjectHolder("terrible_chest:terrible_chest")
    public static TileEntityType<TerribleChestTileEntity> TERRIBLE_CHEST;
    @ObjectHolder("terrible_chest:terrible_chest_2")
    public static TileEntityType<TerribleChestTileEntity2> TERRIBLE_CHEST_2;

    private TileEntityTypes() {
    }
}
