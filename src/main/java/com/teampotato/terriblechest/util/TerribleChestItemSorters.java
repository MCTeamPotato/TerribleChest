package com.teampotato.terriblechest.util;

import com.teampotato.terriblechest.item.ItemStackContainer;
import net.minecraft.util.ResourceLocation;

import java.util.Comparator;

public class TerribleChestItemSorters {
    public static final Comparator<ItemStackContainer> DEFAULT_1;
    public static final Comparator<ItemStackContainer> DEFAULT_2;
    public static final Comparator<ItemStackContainer> DEFAULT_3;
    public static final Comparator<ItemStackContainer> ITEM_REGISTRY_NAME = (item1, item2) -> {
        ResourceLocation registryName1 = item1.getStack().getItem().getRegistryName();
        ResourceLocation registryName2 = item2.getStack().getItem().getRegistryName();
        if (registryName1 != null && registryName2 != null) {
            return registryName1.getPath().compareTo(registryName2.getPath());
        } else if (registryName1 == null && registryName2 == null) {
            return 0;
        } else {
            return registryName1 == null ? 1 : -1;
        }
    };
    public static final Comparator<ItemStackContainer> ITEM_COUNT = Comparator.comparingLong(ItemStackContainer::getCount);
    public static final Comparator<ItemStackContainer> MOD_ID = (item1, item2) -> {
        ResourceLocation registryName1 = item1.getStack().getItem().getRegistryName();
        ResourceLocation registryName2 = item2.getStack().getItem().getRegistryName();
        if (registryName1 != null && registryName2 != null) {
            return registryName1.getNamespace().compareTo(registryName2.getNamespace());
        } else if (registryName1 == null && registryName2 == null) {
            return 0;
        } else {
            return registryName1 == null ? 1 : -1;
        }
    };
    public static final Comparator<ItemStackContainer> ITEM_NAME = (item1, item2) -> {
        String name1 = item1.getStack().getDisplayName().getString();
        String name2 = item2.getStack().getDisplayName().getString();
        return name1.compareTo(name2);
    };

    static {
        DEFAULT_1 = ITEM_REGISTRY_NAME.thenComparing(MOD_ID).thenComparing(ITEM_COUNT.reversed());
        DEFAULT_2 = ITEM_NAME.thenComparing(ITEM_COUNT.reversed());
        DEFAULT_3 = ITEM_COUNT.reversed().thenComparing(ITEM_NAME);
    }
}

