package com.tm.calemicrime.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityMineGenerator;
import com.tm.calemicrime.client.screen.ScreenMineGeneratorOptions;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class BlockMineGenerator extends BaseEntityBlock {

    public BlockMineGenerator() {
        super(Properties.of(Material.STONE).sound(SoundType.WOOD).strength(-1.0F, 3600000.0F).noDrops());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        if (location.getBlockEntity() instanceof BlockEntityMineGenerator mineGenerator) {

            if (player.isCreative()) {

                if (player.isCrouching()) {
                    if (!level.isClientSide()) NetworkHooks.openGui((ServerPlayer) player, mineGenerator, pos);
                }

                else if (level.isClientSide()) openOptionsGui(player, hand, mineGenerator);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @OnlyIn(Dist.CLIENT)
    private void openOptionsGui(Player player, InteractionHand hand, BlockEntityMineGenerator mineGenerator) {
        Minecraft.getInstance().setScreen(new ScreenMineGeneratorOptions(player, hand, mineGenerator));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.MINE_GENERATOR.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, InitBlockEntityTypes.MINE_GENERATOR.get(), BlockEntityMineGenerator::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
