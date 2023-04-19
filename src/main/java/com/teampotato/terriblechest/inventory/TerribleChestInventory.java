package com.teampotato.terriblechest.inventory;

import com.teampotato.terriblechest.item.ItemStackContainer;
import com.teampotato.terriblechest.item.TerribleChestItemHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface TerribleChestInventory extends IInventory {
    static TerribleChestInventory dummy(int length) {
        final TerribleChestItemHandler itemHandler = TerribleChestItemHandler.dummy(length);
        return new TerribleChestInventory() {
            public TerribleChestItemHandler getItemHandler() {
                return itemHandler;
            }

            public void setChanged() {
            }

            public boolean stillValid(PlayerEntity player) {
                return true;
            }
        };
    }

    TerribleChestItemHandler getItemHandler();

    default ItemStackContainer getContainerInSlot(int index) {
        return this.getItemHandler().getContainerInSlot(index);
    }

    default IIntArray getItemCountAccessor() {
        return new IIntArray() {
            public int get(int index) {
                return index >= 0 && index < TerribleChestInventory.this.getContainerSize() ? (int) TerribleChestInventory.this.getContainerInSlot(index).getCount() : 0;
            }

            public void set(int index, int value) {
                if (index >= 0 && index < TerribleChestInventory.this.getContainerSize()) {
                    TerribleChestInventory.this.getContainerInSlot(index).setCount(Integer.toUnsignedLong(value));
                }

            }

            public int getCount() {
                return TerribleChestInventory.this.getContainerSize();
            }
        };
    }

    default int getContainerSize() {
        return this.getItemHandler().getSlots();
    }

    default boolean isEmpty() {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            if (!this.getContainerInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    default ItemStack getItem(int index) {
        return this.getContainerInSlot(index).getStack().copy();
    }

    default ItemStack removeItem(int index, int count) {
        return this.getItemHandler().extractItem(index, count, false);
    }

    default ItemStack removeItemNoUpdate(int index) {
        ItemStackContainer container = this.getItemHandler().removeContainerInSlot(index);
        if (container.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stack = container.getStack();
            int size = (int)Math.min(container.getCount(), stack.getMaxStackSize());
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }
    }

    default void setItem(int index, ItemStack stack) {
        this.getItemHandler().setStackInSlot(index, stack);
    }

    default int getMaxStackSize() {
        return this.getItemHandler().getSlotLimit(0);
    }

    default void clearContent() {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            this.getItemHandler().removeContainerInSlot(i);
        }

    }
}

