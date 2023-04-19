package com.teampotato.terriblechest.inventory.container;

import com.teampotato.terriblechest.inventory.TerribleChestInventory;
import com.teampotato.terriblechest.item.ItemStackContainer;
import com.teampotato.terriblechest.item.Items;
import com.teampotato.terriblechest.item.TerribleChestItemHandler;
import com.teampotato.terriblechest.settings.Config;
import com.teampotato.terriblechest.util.TerribleChestItemSorters;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestContainer extends Container {
    public static final int DATA_MAX_PAGE = 0;
    public static final int DATA_CURRENT_PAGE = 1;
    public static final int TYPE_LEFT_CLICK = 0;
    public static final int TYPE_RIGHT_CLICK = 1;
    public static final int TYPE_SWAP = 2;
    public static final int TYPE_ONE_BY_ONE = 3;
    public static final int TYPE_THROW_MAX_STACK = 1;
    public static final int TYPE_MOVE_THE_SAME = 2;
    private final PlayerInventory playerInventory;
    private final TerribleChestInventory chestInventory;
    private final IInventory unlockInventory;
    private final IIntArray chestData;
    private final IIntArray itemCounts;
    private final int mainInventorySize;
    private int swapIndex;

    public TerribleChestContainer(int id, PlayerInventory playerInventory, TerribleChestInventory chestInventory, final IIntArray chestData) {
        super(ContainerTypes.TERRIBLE_CHEST, id);
        this.playerInventory = playerInventory;
        this.chestInventory = chestInventory;
        this.unlockInventory = new Inventory(1);
        this.chestData = chestData;
        this.itemCounts = chestInventory.getItemCountAccessor();
        this.mainInventorySize = playerInventory.items.size() + chestInventory.getContainerSize();
        this.swapIndex = -1;
        if (Config.COMMON.useSinglePageMode.get()) {
            this.slotInitializeSinglePage();
        } else {
            this.slotInitializeMultiPage();
        }

        this.addDataSlots(this.chestData);

        for(int i = 0; i < this.itemCounts.getCount(); ++i) {
            int finalI = i;
            this.addDataSlot(new IntReferenceHolder() {
                private int lastKnownPage = -1;

                public int get() {
                    return TerribleChestContainer.this.itemCounts.get(finalI);
                }

                public void set(int value) {
                    TerribleChestContainer.this.itemCounts.set(finalI, value);
                }

                public boolean checkAndClearUpdateFlag() {
                    if (super.checkAndClearUpdateFlag()) {
                        return true;
                    } else {
                        int page = chestData.get(1);
                        boolean flag = page != this.lastKnownPage;
                        this.lastKnownPage = page;
                        return flag;
                    }
                }
            });
        }

    }

    public static TerribleChestContainer createContainer(int id, PlayerInventory playerInventory) {
        int slots = 133;
        if (!(Boolean)Config.COMMON.useSinglePageMode.get()) {
            slots = Config.COMMON.inventoryRows.get() * 9;
        }

        return new TerribleChestContainer(id, playerInventory, TerribleChestInventory.dummy(slots), new IntArray(2));
    }

    private void slotInitializeMultiPage() {
        int rows = Config.COMMON.inventoryRows.get();
        int offset = (rows - 4) * 18;

        int i;
        int j;
        for(i = 0; i < rows; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.chestInventory, j + i * 9, 8 + j * 18, 36 + i * 18));
            }
        }

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.playerInventory, j + i * 9 + 9, 8 + j * 18, 122 + i * 18 + offset));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(this.playerInventory, i, 8 + i * 18, 180 + offset));
        }

        this.addSlot(new Slot(this.unlockInventory, 0, 183, 35));
    }

    private void slotInitializeSinglePage() {
        int i;
        int j;
        for(i = 0; i < 7; ++i) {
            for(j = 0; j < 19; ++j) {
                this.addSlot(new Slot(this.chestInventory, j + i * 19, 8 + j * 18, 17 + i * 18));
            }
        }

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.playerInventory, j + i * 9 + 9, 98 + j * 18, 156 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(this.playerInventory, i, 98 + i * 18, 214));
        }

    }

    private void swap(int index1, int index2) {
        TerribleChestItemHandler itemHandler = this.chestInventory.getItemHandler();
        ItemStackContainer container1 = itemHandler.removeContainerInSlot(index1);
        ItemStackContainer container2 = itemHandler.removeContainerInSlot(index2);
        if (!container2.isEmpty()) {
            itemHandler.setContainerInSlot(index1, container2);
        }

        if (!container1.isEmpty()) {
            itemHandler.setContainerInSlot(index2, container1);
        }

    }

    private void sort(Comparator<ItemStackContainer> comparator, int index1, int index2) {
        if (index1 >= 0 && index2 >= 0 && index1 < index2) {
            TerribleChestItemHandler itemHandler = this.chestInventory.getItemHandler();
            int length = index2 - index1;
            int h = length;
            boolean loop = false;

            while(h > 1 || loop) {
                if (h > 1) {
                    h = h * 10 / 13;
                }

                loop = false;

                for(int i = 0; i < length - h; ++i) {
                    int j = index1 + i;
                    int k = j + h;
                    ItemStackContainer container1 = itemHandler.getContainerInSlot(j);
                    ItemStackContainer container2 = itemHandler.getContainerInSlot(k);
                    boolean swap = container1.isEmpty() && !container2.isEmpty();
                    if (!container1.isEmpty() && !container2.isEmpty()) {
                        if (ItemHandlerHelper.canItemStacksStack(container1.getStack(), container2.getStack())) {
                            long freeSpace = itemHandler.getSlotFreeSpace(j);
                            if (freeSpace > 0L) {
                                long size = Math.min(container2.getCount(), freeSpace);
                                container1.growCount(size);
                                container2.shrinkCount(size);
                                loop = true;
                            }
                        }

                        swap = comparator.compare(container1, container2) > 0;
                    }

                    if (swap) {
                        this.swap(j, k);
                        loop = true;
                    }
                }
            }
        }

    }

    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        IntStream stream = IntStream.range(startIndex, endIndex);
        if (reverseDirection) {
            stream = stream.map((i) -> endIndex - i + startIndex - 1);
        }

        PrimitiveIterator.OfInt it = stream.filter((i) -> i >= 0 && i < this.slots.size()).iterator();
        boolean result = false;
        IntList emptySlots = new IntArrayList(endIndex - startIndex);

        while(it.hasNext() && !stack.isEmpty()) {
            int index = it.nextInt();
            Slot slot = this.getSlot(index);
            ItemStack stackInSlot = slot.getItem();
            if (stackInSlot.isEmpty()) {
                emptySlots.add(index);
            } else if (ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                int size;
                if (index < this.chestInventory.getContainerSize()) {
                    long freeSpace = this.chestInventory.getItemHandler().getSlotFreeSpace(index);
                    if (freeSpace > 0L) {
                        size = (int)Math.min(stack.getCount(), freeSpace);
                        this.chestInventory.getContainerInSlot(index).growCount(size);
                        stack.shrink(size);
                        result = true;
                    }
                } else {
                    int count = stackInSlot.getCount();
                    int freeSpace = Math.min(stackInSlot.getMaxStackSize(), slot.getMaxStackSize()) - count;
                    if (freeSpace > 0) {
                        size = Math.min(stack.getCount(), freeSpace);
                        stackInSlot.grow(size);
                        stack.shrink(size);
                        result = true;
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            IntListIterator emptiesIt = emptySlots.iterator();

            while(emptiesIt.hasNext()) {
                int emptySlot = emptiesIt.nextInt();
                Slot slot = this.getSlot(emptySlot);
                slot.set(stack.split(slot.getMaxStackSize()));
                result = true;
                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        return result;
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxPage() {
        return this.chestData.get(0);
    }

    @OnlyIn(Dist.CLIENT)
    public int getCurrentPage() {
        return this.chestData.get(1);
    }

    @OnlyIn(Dist.CLIENT)
    public long getItemCount(int slot) {
        return Integer.toUnsignedLong(this.itemCounts.get(slot));
    }

    @OnlyIn(Dist.CLIENT)
    public IInventory getPlayerInventory() {
        return this.playerInventory;
    }

    @OnlyIn(Dist.CLIENT)
    public IInventory getChestInventory() {
        return this.chestInventory;
    }

    @OnlyIn(Dist.CLIENT)
    public int getSwapIndex() {
        return this.swapIndex;
    }

    public void changePage(int page) {
        int currentPage = this.chestData.get(1);
        int maxPage = this.chestData.get(0);
        int nextPage = MathHelper.clamp(page, 0, maxPage - 1);
        if (nextPage != currentPage) {
            this.chestData.set(1, nextPage);
        }

    }

    public void unlockMaxPage() {
        ItemStack stack = this.unlockInventory.getItem(0);
        if (!stack.isEmpty() && stack.getItem() == Items.DIAMOND_SPHERE) {
            int maxPage = this.chestData.get(0);
            int nextMaxPage = MathHelper.clamp(maxPage + 1, 0, Config.COMMON.maxPageLimit.get());
            if (nextMaxPage > maxPage) {
                stack.shrink(1);
                this.chestData.set(0, nextMaxPage);
            }
        }

    }

    public boolean stillValid(PlayerEntity playerIn) {
        return this.chestInventory.stillValid(playerIn);
    }

    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        ItemStack stack = this.unlockInventory.removeItemNoUpdate(0);
        if (!stack.isEmpty() && !playerIn.addItem(stack)) {
            playerIn.drop(stack, true);
        }

    }

    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        Slot slot = slotId >= 0 && slotId < this.mainInventorySize ? this.getSlot(slotId) : null;
        if (slot == null) {
            return super.clicked(slotId, dragType, clickTypeIn, player);
        } else {
            ItemStack grabbedStack;
            ItemStack stackInSlot;
            ItemStack copy;
            int i;
            if (slotId < this.chestInventory.getContainerSize()) {
                this.swapIndex = clickTypeIn == ClickType.PICKUP ? this.swapIndex : -1;
                int size;
                if (clickTypeIn == ClickType.PICKUP) {
                    grabbedStack = this.playerInventory.getCarried().copy();
                    stackInSlot = slot.getItem();
                    if (this.swapIndex != -1) {
                        dragType = 2;
                    }

                    if (dragType == 0) {
                        if (grabbedStack.isEmpty()) {
                            if (!stackInSlot.isEmpty()) {
                                copy = slot.remove(stackInSlot.getMaxStackSize());
                                this.playerInventory.setCarried(copy);
                                return copy;
                            }
                        } else {
                            if (stackInSlot.isEmpty()) {
                                slot.set(grabbedStack);
                                this.playerInventory.setCarried(ItemStack.EMPTY);
                                return ItemStack.EMPTY;
                            }

                            if (ItemHandlerHelper.canItemStacksStack(grabbedStack, stackInSlot) && this.moveItemStackTo(grabbedStack, slotId, slotId + 1, false)) {
                                this.playerInventory.setCarried(grabbedStack);
                                return grabbedStack;
                            }
                        }
                    } else if (dragType == 1) {
                        if (grabbedStack.isEmpty()) {
                            if (!stackInSlot.isEmpty()) {
                                long count = this.chestInventory.getContainerInSlot(slotId).getCount();
                                size = (int)Math.min(count, stackInSlot.getMaxStackSize()) / 2;
                                ItemStack result = slot.remove(size);
                                this.playerInventory.setCarried(result);
                                return result;
                            }
                        } else {
                            if (stackInSlot.isEmpty()) {
                                slot.set(grabbedStack.split(1));
                                this.playerInventory.setCarried(grabbedStack);
                                return grabbedStack;
                            }

                            if (ItemHandlerHelper.canItemStacksStack(grabbedStack, stackInSlot) && this.chestInventory.getItemHandler().getSlotFreeSpace(slotId) > 0L) {
                                grabbedStack.shrink(1);
                                this.chestInventory.getContainerInSlot(slotId).growCount(1L);
                                this.playerInventory.setCarried(grabbedStack);
                                return grabbedStack;
                            }
                        }
                    } else if (dragType == 2) {
                        if (this.swapIndex >= 0) {
                            this.swap(this.swapIndex, slotId);
                            this.swapIndex = -1;
                        } else {
                            this.swapIndex = slotId;
                        }
                    } else if (dragType == 3 && !stackInSlot.isEmpty()) {
                        copy = ItemHandlerHelper.copyStackWithSize(stackInSlot, 1);
                        if (this.moveItemStackTo(copy, this.chestInventory.getContainerSize(), this.mainInventorySize, true)) {
                            this.chestInventory.getContainerInSlot(slotId).shrinkCount(1L);
                            if (this.chestInventory.getContainerInSlot(slotId).isEmpty()) {
                                slot.set(ItemStack.EMPTY);
                            }
                        }
                    }
                } else if (clickTypeIn == ClickType.THROW) {
                    grabbedStack = this.playerInventory.getCarried();
                    if (grabbedStack.isEmpty()) {
                        stackInSlot = slot.getItem();
                        if (!stackInSlot.isEmpty()) {
                            ItemStackContainer container = this.chestInventory.getContainerInSlot(slotId);
                            int stackSize = dragType == 1 ? stackInSlot.getMaxStackSize() : 1;
                            size = (int)Math.min(stackSize, container.getCount());
                            player.drop(ItemHandlerHelper.copyStackWithSize(stackInSlot, size), false);
                            container.shrinkCount(size);
                            if (container.isEmpty()) {
                                slot.set(ItemStack.EMPTY);
                            }
                        }
                    }
                } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                    if (dragType == 2) {
                        grabbedStack = slot.getItem().copy();
                        if (!grabbedStack.isEmpty()) {
                            for(i = 0; i < this.chestInventory.getContainerSize(); ++i) {
                                copy = this.getSlot(i).getItem();
                                if (ItemHandlerHelper.canItemStacksStack(grabbedStack, copy)) {
                                    while(!this.quickMoveStack(player, i).isEmpty()) {
                                    }
                                }
                            }
                        }
                    } else {
                        this.quickMoveStack(player, slotId);
                    }
                } else if (clickTypeIn == ClickType.CLONE) {
                    Comparator<ItemStackContainer> comparator = TerribleChestItemSorters.DEFAULT_1;
                    if (dragType == 2) {
                        comparator = TerribleChestItemSorters.DEFAULT_2;
                    } else if (dragType == 3) {
                        comparator = TerribleChestItemSorters.DEFAULT_3;
                    }

                    this.sort(comparator, 0, this.chestInventory.getContainerSize());
                }

                return ItemStack.EMPTY;
            } else {
                this.swapIndex = -1;
                if (clickTypeIn == ClickType.PICKUP) {
                    if (dragType == 3) {
                        grabbedStack = slot.getItem();
                        if (!grabbedStack.isEmpty()) {
                            stackInSlot = ItemHandlerHelper.copyStackWithSize(grabbedStack, 1);
                            if (this.moveItemStackTo(stackInSlot, 0, this.chestInventory.getContainerSize(), false)) {
                                grabbedStack.shrink(1);
                            }
                        }
                    }
                } else if (clickTypeIn == ClickType.QUICK_MOVE && dragType == 2) {
                    grabbedStack = slot.getItem().copy();
                    if (!grabbedStack.isEmpty()) {
                        for(i = this.chestInventory.getContainerSize(); i < this.mainInventorySize; ++i) {
                            copy = this.getSlot(i).getItem();
                            if (ItemHandlerHelper.canItemStacksStack(grabbedStack, copy)) {
                                this.quickMoveStack(player, i);
                            }
                        }
                    }

                    return ItemStack.EMPTY;
                }

                return super.clicked(slotId, dragType, clickTypeIn, player);
            }
        }
    }

    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.slots.size() ? this.slots.get(index) : null;
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();
            if (index < this.chestInventory.getContainerSize()) {
                ItemStackContainer container = this.chestInventory.getContainerInSlot(index);
                int size = (int)Math.min(container.getCount(), stackInSlot.getMaxStackSize());
                ItemStack copy = ItemHandlerHelper.copyStackWithSize(stackInSlot, size);
                if (!this.moveItemStackTo(copy, this.chestInventory.getContainerSize(), this.mainInventorySize, true)) {
                    return ItemStack.EMPTY;
                }

                container.shrinkCount(size - copy.getCount());
                if (container.isEmpty()) {
                    stackInSlot.setCount(0);
                }
            } else if (index < this.mainInventorySize) {
                if (!this.moveItemStackTo(stackInSlot, 0, this.chestInventory.getContainerSize(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, this.chestInventory.getContainerSize(), this.mainInventorySize, true)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    public boolean canDragTo(Slot slotIn) {
        return Objects.equals(slotIn.container, this.playerInventory);
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return Objects.equals(slotIn.container, this.playerInventory);
    }
}

