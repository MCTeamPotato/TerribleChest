package com.teampotato.terriblechest.network.message.gui;

import com.teampotato.terriblechest.inventory.container.TerribleChestContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public enum UnlockMaxPage {
    INSTANCE;

    public static void encode(UnlockMaxPage message, PacketBuffer buffer) {}

    public static UnlockMaxPage decode(PacketBuffer buffer) {
        return INSTANCE;
    }

    public static void handle(UnlockMaxPage message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context _ctx = ctx.get();
        _ctx.enqueueWork(() -> {
            ServerPlayerEntity player = _ctx.getSender();
            if (player != null && player.containerMenu instanceof TerribleChestContainer) {
                ((TerribleChestContainer)player.containerMenu).unlockMaxPage();
            }

        });
        _ctx.setPacketHandled(true);
    }
}
