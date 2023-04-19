package com.teampotato.terriblechest.tileentity;

import com.teampotato.terriblechest.inventory.TerribleChestInventory;
import com.teampotato.terriblechest.inventory.container.TerribleChestContainer;
import com.teampotato.terriblechest.item.ItemStackContainer;
import com.teampotato.terriblechest.item.TerribleChestItemHandler;
import com.teampotato.terriblechest.settings.Config;
import com.teampotato.terriblechest.util.ItemStackContainerUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestTileEntity2 extends LockableTileEntity implements INamedContainerProvider, TerribleChestInventory {
    private final IIntArray chestData = new IIntArray() {
        public int get(int index) {
            if (index == 0) {
                return TerribleChestTileEntity2.this.maxPage;
            } else {
                return index == 1 ? TerribleChestTileEntity2.this.page : 0;
            }
        }

        public void set(int index, int value) {
            if (index == 0) {
                TerribleChestTileEntity2.this.maxPage = value;
            }

            if (index == 1) {
                TerribleChestTileEntity2.this.page = value;
            }

        }

        public int getCount() {
            return 2;
        }
    };
    private final Int2ObjectMap<ItemStackContainer> containers = ItemStackContainerUtil.newContainers();
    private int maxPage = 1;
    private int page = 0;

    public TerribleChestTileEntity2() {
        super(TileEntityTypes.TERRIBLE_CHEST_2);
    }

    public void loadFromNbt(CompoundNBT compound) {
        this.containers.clear();
        ItemStackContainerUtil.loadAllItems(compound, this.containers);
        this.maxPage = compound.getInt("MaxPage");
        this.page = compound.getInt("Page");
    }

    public CompoundNBT saveToNbt(CompoundNBT compound) {
        ItemStackContainerUtil.saveAllItems(compound, this.containers);
        compound.putInt("MaxPage", this.maxPage);
        compound.putInt("Page", this.page);
        return compound;
    }

    protected IItemHandler createUnSidedHandler() {
        return this.getItemHandler();
    }

    public TerribleChestItemHandler getItemHandler() {
        if (Config.COMMON.useSinglePageMode.get()) {
            return TerribleChestItemHandler.create(this.containers, 0, 133);
        } else {
            int slots = Config.COMMON.inventoryRows.get() * 9;
            return TerribleChestItemHandler.create(this.containers, () -> this.page * slots, () -> slots);
        }
    }

    protected ITextComponent getDefaultName() {
        return TerribleChestTileEntity.CONTAINER_NAME;
    }

    protected Container createMenu(int id, PlayerInventory playerInventory) {
        return new TerribleChestContainer(id, playerInventory, this, this.chestData);
    }

    public boolean stillValid(PlayerEntity player) {
        World world = this.getLevel();
        BlockPos pos = this.getBlockPos();
        if (world != null && world.getBlockEntity(pos) == this) {
            return !(player.distanceToSqr((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) > 64.0);
        } else {
            return false;
        }
    }

    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.loadFromNbt(compound);
    }

    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        return this.saveToNbt(compound);
    }
}

