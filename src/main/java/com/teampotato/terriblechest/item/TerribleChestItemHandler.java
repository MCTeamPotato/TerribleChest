package com.teampotato.terriblechest.item;

import com.teampotato.terriblechest.settings.Config;
import com.teampotato.terriblechest.util.ItemStackContainerUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.IntSupplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TerribleChestItemHandler implements IItemHandlerModifiable {
    public TerribleChestItemHandler() {
    }

    public static TerribleChestItemHandler create(final Int2ObjectMap<ItemStackContainer> containers, final IntSupplier offset, final IntSupplier length) {
        return new TerribleChestItemHandler() {
            protected Int2ObjectMap<ItemStackContainer> getContainers() {
                return containers;
            }

            protected int getOffset() {
                return offset.getAsInt();
            }

            public int getSlots() {
                return length.getAsInt();
            }
        };
    }

    public static TerribleChestItemHandler create(final Int2ObjectMap<ItemStackContainer> containers, final int offset, final int length) {
        return new TerribleChestItemHandler() {
            protected Int2ObjectMap<ItemStackContainer> getContainers() {
                return containers;
            }

            protected int getOffset() {
                return offset;
            }

            public int getSlots() {
                return length;
            }
        };
    }

    public static TerribleChestItemHandler create(Int2ObjectMap<ItemStackContainer> containers) {
        IntIterator it = containers.keySet().iterator();

        int slots;
        for(slots = -1; it.hasNext(); slots = Math.max(slots, it.nextInt())) {
        }

        return create(containers, 0, slots + 1);
    }

    public static TerribleChestItemHandler dummy(final int length) {
        final Int2ObjectMap<ItemStackContainer> containers = ItemStackContainerUtil.newContainers();
        return new TerribleChestItemHandler() {
            protected Int2ObjectMap<ItemStackContainer> getContainers() {
                return containers;
            }

            protected int getOffset() {
                return 0;
            }

            public int getSlots() {
                return length;
            }
        };
    }

    protected abstract Int2ObjectMap<ItemStackContainer> getContainers();

    protected abstract int getOffset();

    public abstract int getSlots();

    public ItemStackContainer getContainerInSlot(int slot) {
        return this.getContainers().get(this.getOffset() + slot);
    }

    public void setContainerInSlot(int slot, ItemStackContainer container) {
        this.getContainers().put(this.getOffset() + slot, container);
    }

    public ItemStackContainer removeContainerInSlot(int slot) {
        return this.getContainers().remove(this.getOffset() + slot);
    }

    public long getSlotLimitLong(int slot) {
        return Config.COMMON.slotStackLimit.get();
    }

    public long getSlotFreeSpace(int slot) {
        return this.getSlotLimitLong(slot) - this.getContainerInSlot(slot).getCount();
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            this.removeContainerInSlot(slot);
        } else {
            int size = (int)Math.min(stack.getCount(), this.getSlotLimitLong(slot));
            this.setContainerInSlot(slot, ItemStackContainer.create(stack, size));
        }

    }

    public ItemStack getStackInSlot(int slot) {
        ItemStackContainer container = this.getContainerInSlot(slot);
        ItemStack stack = container.getStack();
        int size = (int)Math.min(container.getCount(), stack.getMaxStackSize());
        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.isItemValid(slot, stack)) {
            return stack;
        } else {
            ItemStackContainer container = this.getContainerInSlot(slot);
            ItemStack stackInSlot = container.getStack();
            long limit = this.getSlotLimitLong(slot);
            if (!container.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                    return stack;
                }

                limit -= container.getCount();
            }

            if (limit <= 0L) {
                return stack;
            } else {
                int toInsert = (int)Math.min(stack.getCount(), limit);
                if (!simulate) {
                    if (container.isEmpty()) {
                        this.setContainerInSlot(slot, ItemStackContainer.create(stack, toInsert));
                    } else {
                        container.growCount(toInsert);
                    }
                }

                return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toInsert);
            }
        }
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStackContainer container = this.getContainerInSlot(slot);
            if (container.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack stackInSlot = container.getStack();
                long stackCount = container.getCount();
                int toExtract = (int)Math.min(Math.min(amount, stackCount), stackInSlot.getMaxStackSize());
                if (!simulate) {
                    if (stackCount > (long)toExtract) {
                        container.shrinkCount(toExtract);
                    } else {
                        this.removeContainerInSlot(slot);
                    }
                }

                return ItemHandlerHelper.copyStackWithSize(stackInSlot, toExtract);
            }
        }
    }

    public int getSlotLimit(int slot) {
        return (int)Math.min(2147483647L, this.getSlotLimitLong(slot));
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}

