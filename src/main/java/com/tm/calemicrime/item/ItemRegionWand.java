package com.tm.calemicrime.item;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.SoundHelper;
import com.tm.calemicrime.blockentity.BlockEntityMineGenerator;
import com.tm.calemicrime.blockentity.BlockEntityRadiationProjector;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.main.CalemiCrime;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRegionWand extends Item {

    public ItemRegionWand() {
        super(new Item.Properties().tab(CalemiCrime.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag flag) {

        Location position_1 = getPosition(level, stack, 1);
        Location position_2 = getPosition(level, stack, 2);

        tooltipList.add(new TextComponent("Position 1").withStyle(ChatFormatting.GRAY).append(" ")
                .append(position_1 != null ? new TextComponent(position_1.toString()) : new TextComponent("Not set")).withStyle(ChatFormatting.AQUA));
        tooltipList.add(new TextComponent("Position 2").withStyle(ChatFormatting.GRAY).append(" ")
                .append(position_2 != null ? new TextComponent(position_2.toString()) : new TextComponent("Not set")).withStyle(ChatFormatting.AQUA));
    }

    public static Location getPosition(Level level, ItemStack stack, int index) {
        CompoundTag tag = stack.getOrCreateTag();
        return Location.readFromNBT(level, tag.getCompound("pos" + index));
    }

    public static void setPosition(ItemStack stack, int index, Location location) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag pos = new CompoundTag();
        location.writeToNBT(pos);
        tag.put("pos" + index, pos);
        stack.setTag(tag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos clickedPos = context.getClickedPos();
        Location location = new Location(level, clickedPos);

        Location position_1 = getPosition(level, context.getItemInHand(), 1);
        Location position_2 = getPosition(level, context.getItemInHand(), 2);

        if (position_1 != null && position_2 != null && location.getBlockEntity() instanceof BlockEntityRegionProtector regionProtector) {
            regionProtector.setRegion(new AABB(position_1.getBlockPos(), position_2.getBlockPos()));
            if (level.isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Transferred positions to Region Protector"), Util.NIL_UUID);
        }

        else if (position_1 != null && position_2 != null && location.getBlockEntity() instanceof BlockEntityMineGenerator mineGenerator) {
            mineGenerator.setRegion(new AABB(position_1.getBlockPos(), position_2.getBlockPos()));
            if (level.isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Transferred positions to Mine Generator"), Util.NIL_UUID);
        }

        else if (position_1 != null && position_2 != null && location.getBlockEntity() instanceof BlockEntityRadiationProjector radiationProjector) {
            radiationProjector.setRegion(new AABB(position_1.getBlockPos(), position_2.getBlockPos()));
            if (level.isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Transferred positions to Radiation Projector"), Util.NIL_UUID);
        }

        else if (player.isCrouching()) {
            setPosition(context.getItemInHand(), 2, location);
            if (level.isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Set Position 2 to " + location), Util.NIL_UUID);
        }

        else {
            setPosition(context.getItemInHand(), 1, location);
            if (level.isClientSide()) player.sendMessage(new TextComponent(ChatFormatting.GREEN + "Set Position 1 to " + location), Util.NIL_UUID);
        }

        SoundHelper.playSimple(player, SoundEvents.UI_BUTTON_CLICK);

        return InteractionResult.SUCCESS;
    }
}
