package com.teampotato.terrible_chest.util;

import com.teampotato.terrible_chest.item.ItemStackContainer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class ItemStackContainerUtil {
    public static Int2ObjectMap<ItemStackContainer> newContainers() {
        Int2ObjectOpenHashMap<ItemStackContainer> containers = new Int2ObjectOpenHashMap<>();
        containers.defaultReturnValue(ItemStackContainer.EMPTY);
        return containers;
    }

    public static void saveAllItems(CompoundNBT nbt, Int2ObjectMap<ItemStackContainer> containers) {
        ListNBT list = new ListNBT();

        for (Int2ObjectMap.Entry<ItemStackContainer> itemStackContainerEntry : containers.int2ObjectEntrySet()) {
            int index = itemStackContainerEntry.getIntKey();
            ItemStackContainer item = itemStackContainerEntry.getValue();
            if (!item.isEmpty()) {
                CompoundNBT compound = item.serializeNBT();
                compound.putInt("Index", index);
                list.add(compound);
            }
        }

        nbt.put("Items", list);
    }

    public static void loadAllItems(CompoundNBT nbt, Int2ObjectMap<ItemStackContainer> containers) {
        ListNBT list = nbt.getList("Items", 10);

        for(int i = 0; i < list.size(); ++i) {
            CompoundNBT compound = list.getCompound(i);
            int index = compound.getInt("Index");
            ItemStackContainer item = ItemStackContainer.read(compound);
            if (!item.isEmpty()) {
                containers.put(index, item);
            }
        }

    }
}
