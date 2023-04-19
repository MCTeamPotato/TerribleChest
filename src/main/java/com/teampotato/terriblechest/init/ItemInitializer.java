package com.teampotato.terriblechest.init;

import com.teampotato.terriblechest.item.TerribleBangleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(
        modid = "terrible_chest",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ItemInitializer {
    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        Item.Properties prop = (new Item.Properties()).tab(ItemGroup.TAB_MISC);
        Item item = (new Item(prop)).setRegistryName("terrible_chest:diamond_sphere");
        registry.register(item);
        prop = (new Item.Properties()).tab(ItemGroup.TAB_MISC);
        item = (new TerribleBangleItem(prop)).setRegistryName("terrible_chest:terrible_bangle");
        registry.register(item);
    }
}