package com.tm.calemicrime.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRadiationProjector;
import com.tm.calemicrime.client.screen.ScreenRadiationProjectorOptions;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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

public class BlockRadiationProjector extends BaseEntityBlock {

    public BlockRadiationProjector() {
        super(Properties.of(Material.STONE).sound(SoundType.WOOD).strength(-1.0F, 3600000.0F).noDrops());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        if (location.getBlockEntity() instanceof BlockEntityRadiationProjector radiationProjector) {

            if (player.isCreative()) {

                if (level.isClientSide()) openOptionsGui(player, hand, radiationProjector);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @OnlyIn(Dist.CLIENT)
    private void openOptionsGui(Player player, InteractionHand hand, BlockEntityRadiationProjector radiationProjector) {
        Minecraft.getInstance().setScreen(new ScreenRadiationProjectorOptions(player, hand, radiationProjector));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.RADIATION_PROJECTOR.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, InitBlockEntityTypes.RADIATION_PROJECTOR.get(), BlockEntityRadiationProjector::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
