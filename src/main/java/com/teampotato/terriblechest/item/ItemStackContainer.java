package com.teampotato.terriblechest.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemStackContainer {
    public static final ItemStackContainer EMPTY;
    private boolean empty;
    private final ItemStack stack;
    private long count;

    private ItemStackContainer(ItemStack stack, long count) {
        this.stack = stack;
        this.count = count;
    }

    private ItemStackContainer(CompoundNBT nbt) {
        this.stack = ItemStack.of(nbt.getCompound("Stack"));
        this.count = Integer.toUnsignedLong(nbt.getInt("Count"));
    }

    public static ItemStackContainer create(ItemStack stack, int count) {
        if (!stack.isEmpty() && count != 0) {
            ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
            ItemStackContainer container = new ItemStackContainer(copy, count);
            container.updateEmptyState();
            return container;
        } else {
            return EMPTY;
        }
    }

    public static ItemStackContainer create(ItemStack stack) {
        return create(stack, stack.getCount());
    }

    public static ItemStackContainer read(CompoundNBT nbt) {
        ItemStackContainer container = new ItemStackContainer(nbt);
        container.updateEmptyState();
        return container;
    }

    private void updateEmptyState() {
        this.empty = this.isEmpty();
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        } else if (this.stack.isEmpty()) {
            return true;
        } else {
            return this.count <= 0L;
        }
    }

    public ItemStack getStack() {
        return this.empty ? ItemStack.EMPTY : this.stack;
    }

    public long getCount() {
        return this.empty ? 0L : this.count;
    }

    public void setCount(long count) {
        this.count = count;
        this.updateEmptyState();
    }

    public void growCount(long count) {
        this.setCount(this.count + count);
    }

    public void shrinkCount(long count) {
        this.growCount(-count);
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Stack", this.stack.serializeNBT());
        nbt.putInt("Count", (int)this.count);
        return nbt;
    }

    static {
        EMPTY = new ItemStackContainer(ItemStack.EMPTY, 0L);
    }
}

