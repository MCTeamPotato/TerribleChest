package com.teampotato.terrible_chest.init;

import com.teampotato.terrible_chest.block.Blocks;
import com.teampotato.terrible_chest.block.TerribleChestBlock;
import com.teampotato.terrible_chest.block.TerribleChestBlock2;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(
        modid = "terrible_chest",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class BlockInitializer {
    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        AbstractBlock.Properties prop = AbstractBlock.Properties.of(Material.METAL, MaterialColor.DIAMOND).strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion();
        Block block = (new TerribleChestBlock(prop)).setRegistryName("terrible_chest:terrible_chest");
        registry.register(block);
        prop = AbstractBlock.Properties.of(Material.METAL, MaterialColor.EMERALD).strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion();
        block = (new TerribleChestBlock2(prop)).setRegistryName("terrible_chest:terrible_chest_2");
        registry.register(block);
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        Item.Properties prop = (new Item.Properties()).tab(ItemGroup.TAB_DECORATIONS);
        Item item = (new BlockItem(Blocks.TERRIBLE_CHEST, prop)).setRegistryName("terrible_chest:terrible_chest");
        registry.register(item);
        prop = (new Item.Properties()).tab(ItemGroup.TAB_DECORATIONS);
        item = (new BlockItem(Blocks.TERRIBLE_CHEST_2, prop)).setRegistryName("terrible_chest:terrible_chest_2");
        registry.register(item);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderType() {
        RenderTypeLookup.setRenderLayer(Blocks.TERRIBLE_CHEST, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(Blocks.TERRIBLE_CHEST_2, RenderType.cutout());
    }
}
