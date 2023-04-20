package com.teampotato.terrible_chest.block;

import com.teampotato.terrible_chest.tileentity.TerribleChestTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestBlock extends Block implements IWaterLoggable {
    private static final ITextComponent MESSAGE_AUTH_ERROR = new TranslationTextComponent("message.terrible_chest.auth_error");
    static final VoxelShape TERRIBLE_CHEST_SHAPE = VoxelShapes.or(Block.box(0.0, 0.0, 0.0, 4.0, 16.0, 4.0), Block.box(0.0, 0.0, 12.0, 4.0, 16.0, 16.0), Block.box(0.0, 0.0, 4.0, 4.0, 4.0, 12.0), Block.box(12.0, 0.0, 4.0, 16.0, 4.0, 12.0), Block.box(0.0, 12.0, 4.0, 4.0, 16.0, 12.0), Block.box(12.0, 12.0, 4.0, 16.0, 16.0, 12.0), Block.box(12.0, 0.0, 0.0, 16.0, 16.0, 4.0), Block.box(12.0, 0.0, 12.0, 16.0, 16.0, 16.0), Block.box(4.0, 0.0, 0.0, 12.0, 4.0, 4.0), Block.box(4.0, 12.0, 0.0, 12.0, 16.0, 4.0), Block.box(4.0, 0.0, 12.0, 12.0, 4.0, 16.0), Block.box(4.0, 12.0, 12.0, 12.0, 16.0, 16.0));
    public static final BooleanProperty WATERLOGGED;

    public TerribleChestBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return TERRIBLE_CHEST_SHAPE;
    }

    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TerribleChestTileEntity();
    }

    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TerribleChestTileEntity) {
            TerribleChestTileEntity chestTileEntity = (TerribleChestTileEntity)tileentity;
            if (placer instanceof PlayerEntity) {
                chestTileEntity.setOwnerId(placer.getUUID());
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
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof TerribleChestTileEntity) {
                if (((TerribleChestTileEntity)tileEntity).isOwner(player)) {
                    player.openMenu(state.getMenuProvider(worldIn, pos));
                } else {
                    player.sendMessage(MESSAGE_AUTH_ERROR, Util.NIL_UUID);
                }
            }

            return ActionResultType.CONSUME;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = super.getStateForPlacement(context);
        return state != null ? state.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER) : null;
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
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
