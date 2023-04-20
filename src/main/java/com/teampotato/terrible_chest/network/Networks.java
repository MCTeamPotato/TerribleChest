package com.teampotato.terrible_chest.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Networks {
    private static final String PROTOCOL_VERSION;
    private static final SimpleChannel CHANNEL;

    private Networks() {
    }

    public static SimpleChannel getChannel() {
        return CHANNEL;
    }

    static {
        ResourceLocation channelName = new ResourceLocation("terrible_chest", "terrible_chest");
        PROTOCOL_VERSION = "1";
        Supplier var10001 = () -> PROTOCOL_VERSION;
        Predicate var10002 = PROTOCOL_VERSION::equals;
        CHANNEL = NetworkRegistry.newSimpleChannel(channelName, var10001, var10002, PROTOCOL_VERSION::equals);
    }
}