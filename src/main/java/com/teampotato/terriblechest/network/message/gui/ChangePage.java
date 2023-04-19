package com.teampotato.terriblechest.network.message.gui;


import com.teampotato.terriblechest.inventory.container.TerribleChestContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class ChangePage {
    private final int page;

    public ChangePage(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }

    public static void encode(ChangePage message, PacketBuffer buffer) {
        buffer.writeInt(message.page);
    }

    public static ChangePage decode(PacketBuffer buffer) {
        return new ChangePage(buffer.readInt());
    }

    public static void handle(ChangePage message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context _ctx = ctx.get();
        _ctx.enqueueWork(() -> {
            ServerPlayerEntity player = _ctx.getSender();
            if (player != null && player.containerMenu instanceof TerribleChestContainer) {
                ((TerribleChestContainer)player.containerMenu).changePage(message.getPage());
            }

        });
        _ctx.setPacketHandled(true);
    }
}