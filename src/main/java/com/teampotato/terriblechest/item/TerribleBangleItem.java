package com.teampotato.terriblechest.item;

import com.teampotato.terriblechest.capability.Capabilities;
import com.teampotato.terriblechest.capability.TerribleChestCapability;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.NoSuchElementException;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleBangleItem extends Item {
    public TerribleBangleItem(Item.Properties properties) {
        super(properties);
    }

    private void moveInventoryItems(IItemHandler src, IItemHandler dest) {
        ItemStack stackInSlot;
        for(int i = 0; i < src.getSlots(); ++i) {
            while(!(stackInSlot = src.getStackInSlot(i)).isEmpty()) {
                ItemStack stack = src.extractItem(i, stackInSlot.getMaxStackSize(), false);
                IntList emptySlots = new IntArrayList();

                for(int j = 0; j < dest.getSlots(); ++j) {
                    if (dest.getStackInSlot(j).isEmpty()) {
                        emptySlots.add(j);
                    } else {
                        stack = dest.insertItem(j, stack, false);
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }

                if (!stack.isEmpty()) {
                    IntListIterator it = emptySlots.iterator();

                    while(it.hasNext()) {
                        int emptySlot = it.nextInt();
                        stack = dest.insertItem(emptySlot, stack, false);
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }

                if (!stack.isEmpty()) {
                    src.insertItem(i, stack, false);
                    break;
                }
            }
        }

    }

    private void collectItems(PlayerEntity playerEntity, TileEntity tileEntity) {
        IItemHandler src = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(EmptyHandler.INSTANCE);
        IItemHandler dest = playerEntity.getCapability(Capabilities.TERRIBLE_CHEST).map(TerribleChestCapability::getContainers).map(TerribleChestItemHandler::create).orElseThrow(NoSuchElementException::new);
        this.moveInventoryItems(src, dest);
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack heldItem = playerEntity.getItemInHand(hand);
        BlockRayTraceResult rayTrace = getPlayerPOVHitResult(world, playerEntity, RayTraceContext.FluidMode.NONE);
        if (rayTrace.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(heldItem);
        } else {
            BlockPos pos = rayTrace.getBlockPos();
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null) {
                playerEntity.startUsingItem(hand);
                playerEntity.swing(hand);
                playerEntity.playSound(SoundEvents.UI_BUTTON_CLICK, 0.25F, 1.0F);
                if (!world.isClientSide()) {
                    this.collectItems(playerEntity, tileEntity);
                }

                return ActionResult.success(heldItem);
            } else {
                return ActionResult.fail(heldItem);
            }
        }
    }
}
