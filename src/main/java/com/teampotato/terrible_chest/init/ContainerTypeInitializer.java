package com.teampotato.terrible_chest.init;

import com.teampotato.terrible_chest.inventory.container.TerribleChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(
        modid = "terrible_chest",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ContainerTypeInitializer {
    @SubscribeEvent
    public static void registerContainerType(RegistryEvent.Register<ContainerType<?>> event) {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        ContainerType<?> containerType = (ContainerType)(new ContainerType(TerribleChestContainer::createContainer)).setRegistryName("terrible_chest:terrible_chest");
        registry.register(containerType);
        containerType = (ContainerType)(new ContainerType(TerribleChestContainer::createContainer)).setRegistryName("terrible_chest:terrible_chest_2");
        registry.register(containerType);
    }
}

