package com.teampotato.terriblechest.tileentity;

import com.teampotato.terriblechest.capability.Capabilities;
import com.teampotato.terriblechest.capability.TerribleChestCapability;
import com.teampotato.terriblechest.inventory.TerribleChestInventory;
import com.teampotato.terriblechest.inventory.container.TerribleChestContainer;
import com.teampotato.terriblechest.item.ItemStackContainer;
import com.teampotato.terriblechest.item.TerribleChestItemHandler;
import com.teampotato.terriblechest.settings.Config;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestTileEntity extends TileEntity implements INamedContainerProvider {
    static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.terrible_chest.terrible_chest");
    private UUID ownerId = null;
    private int page = 0;

    public TerribleChestTileEntity() {
        super(TileEntityTypes.TERRIBLE_CHEST);
    }

    private void dataFix(CompoundNBT compound) {
        if (compound.contains("OwnerIdMost") && compound.contains("OwnerIdLeast")) {
            UUID uuid = new UUID(compound.getLong("OwnerIdMost"), compound.getLong("OwnerIdLeast"));
            compound.putUUID("OwnerId", uuid);
        }

    }

    private TerribleChestItemHandler createItemHandler(Int2ObjectMap<ItemStackContainer> containers) {
        if (Config.COMMON.useSinglePageMode.get()) {
            return TerribleChestItemHandler.create(containers, 0, 133);
        } else {
            int slots = Config.COMMON.inventoryRows.get() * 9;
            return TerribleChestItemHandler.create(containers, () -> this.page * slots, () -> slots);
        }
    }

    private TerribleChestInventory createInventory(TerribleChestCapability chest) {
        final TerribleChestItemHandler itemHandler = this.createItemHandler(chest.getContainers());
        return new TerribleChestInventory() {
            public TerribleChestItemHandler getItemHandler() {
                return itemHandler;
            }

            public void setChanged() {
            }

            public boolean stillValid(PlayerEntity player) {
                World world = TerribleChestTileEntity.this.getLevel();
                BlockPos pos = TerribleChestTileEntity.this.getBlockPos();
                if (world != null && world.getBlockEntity(pos) == TerribleChestTileEntity.this) {
                    if (!TerribleChestTileEntity.this.isOwner(player)) {
                        return false;
                    } else {
                        return !(player.distanceToSqr((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) > 64.0);
                    }
                } else {
                    return false;
                }
            }
        };
    }

    private IIntArray createChestData(final TerribleChestCapability chest) {
        return new IIntArray() {
            public int get(int index) {
                if (index == 0) {
                    return chest.getMaxPage();
                } else {
                    return index == 1 ? TerribleChestTileEntity.this.page : 0;
                }
            }

            public void set(int index, int value) {
                if (index == 0) {
                    chest.setMaxPage(value);
                }

                if (index == 1) {
                    TerribleChestTileEntity.this.page = value;
                }

            }

            public int getCount() {
                return 2;
            }
        };
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isOwner(PlayerEntity player) {
        return Objects.equals(this.ownerId, player.getUUID());
    }

    public ITextComponent getDisplayName() {
        return CONTAINER_NAME;
    }

    @Nullable
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        TerribleChestCapability chest = playerEntity.getCapability(Capabilities.TERRIBLE_CHEST).orElseThrow(NoSuchElementException::new);
        return new TerribleChestContainer(id, playerInventory, this.createInventory(chest), this.createChestData(chest));
    }

    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.dataFix(compound);
        this.ownerId = null;
        if (compound.hasUUID("OwnerId")) {
            this.ownerId = compound.getUUID("OwnerId");
        }

        this.page = compound.getInt("Page");
    }

    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        if (this.ownerId != null) {
            compound.putUUID("OwnerId", this.ownerId);
        }

        compound.putInt("Page", this.page);
        return compound;
    }
}
