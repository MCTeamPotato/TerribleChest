package com.teampotato.terrible_chest.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "terrible_chest",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ItemInitializer {
    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register((new Item((new Item.Properties()).tab(ItemGroup.TAB_MISC))).setRegistryName("terrible_chest:diamond_sphere"));
    }
}