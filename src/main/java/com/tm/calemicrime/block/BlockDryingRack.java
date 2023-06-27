package com.tm.calemicrime.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicrime.blockentity.BlockEntityDryingRack;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockDryingRack extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected static final VoxelShape NORTH_AABB = Block.box(0, 13, 13, 16, 16, 16);
    protected static final VoxelShape SOUTH_AABB = Block.box(0, 13, 0, 16, 16, 3);
    protected static final VoxelShape EAST_AABB = Block.box(0, 13, 0, 3, 16, 16);
    protected static final VoxelShape WEST_AABB = Block.box(13, 13, 0, 16, 16, 16);

    public BlockDryingRack() {
        super(Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1, 1).noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        if (location.getBlockEntity() instanceof BlockEntityDryingRack rack) {

            if (rack.dryingStack.isEmpty()) {

                ItemStack heldStack = player.getItemInHand(hand);

                if (!heldStack.isEmpty()) {

                    ItemStack stack = heldStack.copy();
                    stack.setCount(1);
                    rack.dryingStack = stack;
                    heldStack.shrink(1);
                    rack.markUpdated();

                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.PASS;
            }

            else {
                ItemHelper.spawnStackAtLocation(level, rack.getLocation(), rack.dryingStack);
                rack.dryingStack = ItemStack.EMPTY;
                rack.markUpdated();
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock())) {

            BlockEntity blockentity = level.getBlockEntity(pos);

            if (blockentity instanceof BlockEntityDryingRack rack) {

                if (!rack.dryingStack.isEmpty()) {
                    ItemHelper.spawnStackAtLocation(level, rack.getLocation(), rack.dryingStack);
                }

                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityDryingRack(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, InitBlockEntityTypes.DRYING_RACK.get(), BlockEntityDryingRack::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
