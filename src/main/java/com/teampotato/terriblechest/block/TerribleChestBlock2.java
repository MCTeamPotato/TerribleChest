package com.teampotato.terriblechest.block;

import com.teampotato.terriblechest.item.ItemStackContainer;
import com.teampotato.terriblechest.tileentity.TerribleChestTileEntity2;
import com.teampotato.terriblechest.util.ItemStackContainerUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestBlock2 extends Block implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED;

    public TerribleChestBlock2(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT compound = stack.getTagElement("BlockEntityTag");
        if (compound != null && compound.contains("Items", 9)) {
            Int2ObjectMap<ItemStackContainer> containers = ItemStackContainerUtil.newContainers();
            ItemStackContainerUtil.loadAllItems(compound, containers);
            int i = 0;
            int j = 0;

            for (ItemStackContainer next : containers.values()) {
                if (!next.isEmpty()) {
                    ++j;
                    if (i <= 4) {
                        ++i;
                        IFormattableTextComponent textComponent = next.getStack().getHoverName().copy();
                        textComponent.append(" x").append(String.format("%,d", next.getCount()));
                        tooltip.add(textComponent);
                    }
                }
            }

            if (j - i > 0) {
                tooltip.add((new TranslationTextComponent("container.shulkerBox.more", j - i)).withStyle(TextFormatting.ITALIC));
            }
        }

    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return TerribleChestBlock.TERRIBLE_CHEST_SHAPE;
    }

    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TerribleChestTileEntity2();
    }

    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TerribleChestTileEntity2) {
            TerribleChestTileEntity2 chestTileEntity = (TerribleChestTileEntity2)tileentity;
            if (stack.hasCustomHoverName()) {
                chestTileEntity.setCustomName(stack.getHoverName());
            }
        }

    }

    @Nullable
    public INamedContainerProvider getMenuProvider(BlockState state, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity instanceof INamedContainerProvider ? (INamedContainerProvider)tileEntity : null;
    }

    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isClientSide()) {
            return ActionResultType.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(worldIn, pos));
            return ActionResultType.CONSUME;
        }
    }

    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isClientSide() && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof TerribleChestTileEntity2) {
                TerribleChestTileEntity2 chestTileEntity = (TerribleChestTileEntity2)tileEntity;
                ItemStack stack = new ItemStack(this);
                CompoundNBT compound = chestTileEntity.saveToNbt(new CompoundNBT());
                if (!compound.isEmpty()) {
                    stack.addTagElement("BlockEntityTag", compound);
                }

                if (chestTileEntity.hasCustomName()) {
                    stack.setHoverName(chestTileEntity.getCustomName());
                }

                popResource(worldIn, pos, stack);
            }
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = super.getStateForPlacement(context);
        return state != null ? state.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER) : null;
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}

