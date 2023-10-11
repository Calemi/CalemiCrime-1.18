package com.tm.calemicrime.item;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.pulley.PulleyContraption;
import com.tm.calemicrime.client.screen.ScreenLootBox;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTest extends Item {

    public ItemTest() {
        super(new Properties().tab(CalemiCrime.TAB));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.FAIL;
        }

        PulleyContraption contraption = new PulleyContraption();

        try {
            if (!contraption.assemble(context.getLevel(), context.getClickedPos())) {
                return InteractionResult.FAIL;
            }
        }

        catch (AssemblyException e) {
            e.printStackTrace();
        }

        if (contraption.getBlocks().isEmpty()) {
            return InteractionResult.FAIL;
        }

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);

        AbstractContraptionEntity contraptionEntity = OrientedContraptionEntity.create(level, contraption, Direction.NORTH);
        contraptionEntity.setPos(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
        level.addFreshEntity(contraptionEntity);

        return InteractionResult.SUCCESS;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20 * 2;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @OnlyIn(Dist.CLIENT)
    private void openOptionsGui(Player player, String pool) {
        Minecraft.getInstance().setScreen(new ScreenLootBox(player, pool));
    }
}
