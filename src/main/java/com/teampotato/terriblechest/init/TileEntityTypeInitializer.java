package com.teampotato.terriblechest.init;

import com.teampotato.terriblechest.block.Blocks;
import com.teampotato.terriblechest.tileentity.TerribleChestTileEntity;
import com.teampotato.terriblechest.tileentity.TerribleChestTileEntity2;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(
        modid = "terrible_chest",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class TileEntityTypeInitializer {
    private static <T extends TileEntity> void registerTileEntity(IForgeRegistry<TileEntityType<?>> registry, String registryName, Supplier<T> factory, Block... blocks) {
        TileEntityType<T> type = TileEntityType.Builder.of(factory, blocks).build(null);
        type.setRegistryName(registryName);
        registry.register(type);
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
        registerTileEntity(event.getRegistry(), "terrible_chest:terrible_chest", TerribleChestTileEntity::new, Blocks.TERRIBLE_CHEST);
        registerTileEntity(event.getRegistry(), "terrible_chest:terrible_chest_2", TerribleChestTileEntity2::new, Blocks.TERRIBLE_CHEST_2);
    }
}

