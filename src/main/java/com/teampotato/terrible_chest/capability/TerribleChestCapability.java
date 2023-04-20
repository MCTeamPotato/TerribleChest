package com.teampotato.terrible_chest.capability;

import com.teampotato.terrible_chest.item.ItemStackContainer;
import com.teampotato.terrible_chest.settings.Config;
import com.teampotato.terrible_chest.util.ItemStackContainerUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestCapability implements INBTSerializable<CompoundNBT> {
    public static final ResourceLocation REGISTRY_KEY = new ResourceLocation("terrible_chest", "terrible_chest");
    private final Int2ObjectMap<ItemStackContainer> containers = ItemStackContainerUtil.newContainers();
    private int maxPage = 1;

    public Int2ObjectMap<ItemStackContainer> getContainers() {
        return this.containers;
    }

    public int getMaxPage() {
        return this.maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ItemStackContainerUtil.saveAllItems(nbt, this.containers);
        nbt.putInt("MaxPage", this.maxPage);
        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        this.containers.clear();
        ItemStackContainerUtil.loadAllItems(nbt, this.containers);
        this.maxPage = Math.max(nbt.getInt("MaxPage"), Config.COMMON.resetMaxPage.get());
    }

    public static Capability.IStorage<TerribleChestCapability> storage() {
        return new Capability.IStorage<TerribleChestCapability>() {
            public INBT writeNBT(Capability<TerribleChestCapability> capability, TerribleChestCapability instance, Direction side) {
                return instance.serializeNBT();
            }

            public void readNBT(Capability<TerribleChestCapability> capability, TerribleChestCapability instance, Direction side, INBT nbt) {
                if (nbt instanceof CompoundNBT) {
                    instance.deserializeNBT((CompoundNBT)nbt);
                }

            }
        };
    }

    public static ICapabilitySerializable<CompoundNBT> provider() {
        final Capability<TerribleChestCapability> _cap = Capabilities.TERRIBLE_CHEST;
        final TerribleChestCapability instance = _cap.getDefaultInstance();
        return new ICapabilitySerializable<CompoundNBT>() {
            @Nonnull
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return _cap.orEmpty(cap, LazyOptional.of(instance == null ? null : () -> instance));
            }

            public CompoundNBT serializeNBT() {
                INBT inbt = _cap.writeNBT(instance, null);
                return inbt instanceof CompoundNBT ? (CompoundNBT)inbt : new CompoundNBT();
            }

            public void deserializeNBT(CompoundNBT nbt) {
                _cap.readNBT(instance, null, nbt);
            }
        };
    }
}

