package com.tm.calemicrime.item;

import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Random;

public abstract class ItemWashable extends Item {

    public ItemWashable() {
        super(new Item.Properties().tab(CalemiCrime.TAB));
    }

    public abstract LiquidBlock GetWashLiquid();
    public abstract ItemStack GetResult();
    public abstract boolean consumeLiquid(Random rand);

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack heldStack = player.getItemInHand(hand);

        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (isLookingAtLiquid(level, player)) {
            player.startUsingItem(hand);
        }

        return InteractionResultHolder.pass(heldStack);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity livingEntity, int count) {

        if (livingEntity instanceof Player player) {

            if (!isLookingAtLiquid(player.getLevel(), player)) {
                player.stopUsingItem();
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {

        if (livingEntity instanceof Player player) {

            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

            if (blockHitResult.getType() == HitResult.Type.BLOCK) {

                BlockPos blockPos = blockHitResult.getBlockPos();

                if (consumeLiquid(level.getRandom())) {
                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                }

                ItemHelper.spawnStackAtEntity(level, player, GetResult());
                stack.shrink(1);
            }
        }

        return stack;
    }

    private boolean isLookingAtLiquid(Level level, Player player) {

        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (player instanceof FakePlayer) {
            return false;
        }

        if (blockHitResult.getType() == HitResult.Type.BLOCK) {

            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = level.getBlockState(blockPos);

            return blockState.getBlock() == GetWashLiquid();
        }

        return false;
    }


    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }
}
