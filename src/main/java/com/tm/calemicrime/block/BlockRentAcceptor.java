package com.tm.calemicrime.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import org.jetbrains.annotations.Nullable;

public class BlockRentAcceptor extends BaseEntityBlock {

    public BlockRentAcceptor() {
        super(Properties.of(Material.STONE).sound(SoundType.WOOD).strength(-1.0F, 3600000.0F).noDrops());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        super.setPlacedBy(level, pos, state, placer, stack);

        Location location = new Location(level, pos);

        if (location.getBlockEntity() instanceof BlockEntityRentAcceptor rentAcceptor) {

            Team team = TeamManager.INSTANCE.getPlayerTeam(placer.getUUID());

            if (team != null) {
                rentAcceptor.setResidentTeam(team);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        if (location.getBlockEntity() instanceof BlockEntityRentAcceptor rentAcceptor) {

            if (player.isCreative()) {

                if (level.isClientSide()) {
                    openGui(player, hand, rentAcceptor);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock())) {

            BlockEntityRegionProtector.cleanRegionProtectorList();
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui (Player player, InteractionHand hand, BlockEntityRentAcceptor rentAcceptor) {
        //Minecraft.getInstance().setScreen(new ScreenRentAcceptorOptions(player, hand, rentAcceptor));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.RENT_ACCEPTOR.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, InitBlockEntityTypes.RENT_ACCEPTOR.get(), BlockEntityRentAcceptor::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
