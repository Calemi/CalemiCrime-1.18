package com.tm.calemicrime.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public abstract class BlockBushPlant extends CropBlock implements BonemealableBlock {

    public static final BooleanProperty ROOT = BooleanProperty.create("root");
    public static final int MAX_AGE = 7;

    private static final VoxelShape SHAPE = Block.box(2.0D, 2.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public BlockBushPlant() {
        super(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP));
        registerDefaultState(stateDefinition.any().setValue(ROOT, true).setValue(AGE, 0));
    }

    public abstract ItemStack getHarvest();
    public abstract ItemLike getSeeds();
    public abstract boolean canUseBonemeal();
    public abstract int growTime();

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

    private void grow(Level level, BlockPos pos, BlockState state, int growth) {

        if (canPlacePlantAbove(level, pos)) {
            level.setBlock(pos.above(), state.setValue(ROOT, false), 2);
        }

        else if (!isMaxAge(state)) {

            int newAge = getAge(state) + growth;
            newAge = Math.min(newAge, getMaxAge());

            level.setBlock(pos, state.setValue(getAgeProperty(), newAge), 2);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {

        if (!level.isAreaLoaded(pos, 1)) return;

        if (level.getRawBrightness(pos, 0) >= 9) {

            float f = getGrowthSpeed(this, level, pos);

            if (random.nextInt((int)(growTime() / f) + 1) == 0) {
                grow(level, pos, state, 1);
            }
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        grow(level, pos, state, random.nextInt(4) + 3);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        if (isMaxAge(state)) {

            if (player.getItemInHand(hand).getItem() instanceof ShearsItem) {

                ItemHelper.spawnStackAtLocation(level, location, getHarvest());
                level.setBlock(pos, state.setValue(AGE, 0), 2);

                level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                level.playSound(player, pos, SoundEvents.AZALEA_LEAVES_BREAK, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);

                player.getItemInHand(hand).hurtAndBreak(1, player, (livingEntity) -> {
                    livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos placedOnPos = pos.below();
        BlockState placedOnState = level.getBlockState(placedOnPos);

        if (!placedOnState.is(Blocks.FARMLAND)) {
            return false;
        }

        return (level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingStage, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        BlockPos placedOnPos = currentPos.below();
        BlockState placedOnState = level.getBlockState(placedOnPos);
        boolean isPlacedOnFarmland = placedOnState.is(Blocks.FARMLAND);
        boolean isPlacedOnItself = placedOnState.is(this);
        return !isPlacedOnFarmland && !isPlacedOnItself ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return getSeeds();
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return !isMaxAge(state) && canUseBonemeal();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROOT);
        builder.add(AGE);
    }
}
