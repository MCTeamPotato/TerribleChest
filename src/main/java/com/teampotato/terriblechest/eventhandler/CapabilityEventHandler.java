package com.teampotato.terriblechest.eventhandler;

import com.teampotato.terriblechest.capability.Capabilities;
import com.teampotato.terriblechest.capability.TerribleChestCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "terrible_chest")
public class CapabilityEventHandler {
    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        PlayerEntity original = event.getOriginal();
        original.revive();
        original.getCapability(Capabilities.TERRIBLE_CHEST).ifPresent((chest) -> {
            PlayerEntity clone = event.getPlayer();
            clone.getCapability(Capabilities.TERRIBLE_CHEST).ifPresent((cloneChest) -> cloneChest.deserializeNBT(chest.serializeNBT()));
        });
        original.remove(true);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof PlayerEntity) {
            event.addCapability(TerribleChestCapability.REGISTRY_KEY, TerribleChestCapability.provider());
        }

    }
}

