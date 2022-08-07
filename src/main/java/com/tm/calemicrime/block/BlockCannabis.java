package com.tm.calemicrime.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicrime.init.InitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class BlockCannabis extends CropBlock implements BonemealableBlock {

    public static final BooleanProperty MATURE = BooleanProperty.create("mature");

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockCannabis() {
        super(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP));
        registerDefaultState(stateDefinition.any().setValue(MATURE, false));
    }

    private boolean canPlacePlantAbove(Level level, BlockPos pos) {

        Location location = new Location(level, pos);
        int height = 0;

        for (int i = 0; i < 3; i++) {

            Location check = new Location(location, Direction.DOWN, i);

            if (check.getBlock() == this) {
                height++;
            }

            else break;
        }

        return (height < 3 && new Location(location, Direction.UP, 1).isAirBlock());
    }

    private void grow(Level level, BlockPos pos, BlockState state) {

        if (canPlacePlantAbove(level, pos)) {
            level.setBlock(pos.above(), state, 2);
        }

        else if (!state.getValue(MATURE)) {
            level.setBlock(pos, state.setValue(MATURE, true), 2);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {

        if (!level.isAreaLoaded(pos, 1)) return;

        if (level.getRawBrightness(pos, 0) >= 9) {

            float f = getGrowthSpeed(this, level, pos);

            if (random.nextInt((int)(25F / f) + 1) == 0) {
                grow(level, pos, state);
            }
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        grow(level, pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        if (state.getValue(MATURE)) {

            if (player.getItemInHand(hand).getItem() == Items.SHEARS) {

                ItemHelper.spawnStackAtLocation(level, location, new ItemStack(InitItems.CANNABIS_LEAF.get()));
                level.setBlock(pos, state.setValue(MATURE, false), 2);

                level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                level.playSound(player, pos, SoundEvents.AZALEA_LEAVES_BREAK, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {

        return state.is(Blocks.FARMLAND) || (state.is(this));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(MATURE);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return InitItems.CANNABIS_SEEDS.get();
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return !state.getValue(MATURE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MATURE);
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
