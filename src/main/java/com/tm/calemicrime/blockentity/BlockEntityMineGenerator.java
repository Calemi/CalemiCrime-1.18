package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.menu.MenuMineGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockEntityMineGenerator extends BlockEntityContainerBase {

    public static final int MAX_TIME_TO_FILL = 20 * 60 * 5;

    private Location regionOffset = new Location(getLevel(), 0, 0, 0);
    private Location regionSize = new Location(getLevel(), 16, 16, 16);

    private int remainingTimeToFill;

    public BlockEntityMineGenerator(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.MINE_GENERATOR.get(), pos, state);
    }

    public Location getRegionOffset() {
        return regionOffset;
    }

    public Location getRegionSize() {
        return regionSize;
    }

    public AABB getRegion() {

        Vec3 start = new Vec3(getLocation().x + getRegionOffset().x, getLocation().y + getRegionOffset().y, getLocation().z + getRegionOffset().z);
        Vec3 end = new Vec3(getLocation().x + getRegionOffset().x + getRegionSize().x, getLocation().y + getRegionOffset().y + getRegionSize().y, getLocation().z + getRegionOffset().z + getRegionSize().z);

        return new AABB(start, end);
    }

    public void setRegion(AABB aabb) {

        Vec3i start = new Vec3i(aabb.minX, aabb.minY, aabb.minZ);
        Vec3i end = new Vec3i(aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1);

        end = end.subtract(start);
        start = start.subtract(new Vec3i(getLocation().x, getLocation().y, getLocation().z));

        setRegionOffset(new Location(level, new BlockPos(start)));
        setRegionSize(new Location(level, new BlockPos(end)));
    }

    public void setRegionOffset(Location value) {
        regionOffset = value;
    }

    public void setRegionSize(Location value) {
        regionSize = value;
    }

    public int getRemainingTimeToFill() {
        return remainingTimeToFill;
    }

    public void setRemainingTimeToFill(int value) {
        remainingTimeToFill = value;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityMineGenerator mineGenerator) {

        if (level.isClientSide()) {
            return;
        }

        if (mineGenerator.getRemainingTimeToFill() > 0) {

            switch (mineGenerator.getRemainingTimeToFill()) {
                case (20 * 30) -> mineGenerator.sendWarning(level, 30);
                case (20 * 10) -> mineGenerator.sendWarning(level, 10);
                case (20 * 5) -> mineGenerator.sendWarning(level, 5);
                case (20 * 3) -> mineGenerator.sendWarning(level, 3);
                case (20 * 2) -> mineGenerator.sendWarning(level, 2);
                case (20) -> mineGenerator.sendWarning(level, 1);
            }

            mineGenerator.setRemainingTimeToFill(mineGenerator.getRemainingTimeToFill() - 1);
        }

        else {
            mineGenerator.fillRegion();
            mineGenerator.setRemainingTimeToFill(MAX_TIME_TO_FILL);
        }

        if (level.getGameTime() % 20 * 10 == 0) {

            if (mineGenerator.isRegionClear()) {
                mineGenerator.fillRegion();
            }
        }
    }

    public void sendWarning(Level level, int seconds) {

        List<? extends Player> playerList = level.players();

        for (Player player : playerList) {

            if (getRegion().contains(player.position().add(0, 1, 0))) {
                player.sendMessage(new TextComponent(ChatFormatting.RED + "This area will regenerate in " + seconds + " second(s). Please move out or you will suffocate!"), Util.NIL_UUID);
            }
        }
    }

    private void fillRegion() {

        if (selectRandomBlock() == null) {
            return;
        }

        for (int x = (int) getRegion().minX; x < (int) getRegion().maxX; x++) {

            for (int y = (int) getRegion().minY; y < (int) getRegion().maxY; y++) {

                for (int z = (int) getRegion().minZ; z < (int) getRegion().maxZ; z++) {

                    level.setBlock(new BlockPos(x, y, z), selectRandomBlock().defaultBlockState(), 3);
                }
            }
        }
    }

    private Block selectRandomBlock() {

        int totalWeight = 0;

        for (ItemStack item : items) {
            if (!item.isEmpty() && item.getItem() instanceof BlockItem) totalWeight += item.getCount();
        }

        if (totalWeight > 0) {

            int randomNumber = level.random.nextInt(totalWeight) + 1;

            for (ItemStack item : items) {

                if (!item.isEmpty() && item.getItem() instanceof BlockItem) {

                    if (randomNumber > item.getCount()) {
                        randomNumber -= item.getCount();
                    }

                    else return Block.byItem(item.getItem());
                }
            }
        }

        return null;
    }

    private boolean isRegionClear() {

        for (int x = (int) getRegion().minX; x < (int) getRegion().maxX; x++) {
            for (int y = (int) getRegion().minY; y < (int) getRegion().maxY; y++) {
                for (int z = (int) getRegion().minZ; z < (int) getRegion().maxZ; z++) {

                    if (!new Location(level, x, y, z).isAirBlock()) return false;
                }
            }
        }

        return true;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return getRegion();
    }

    /**
     * Container Methods
     */

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.mine_generator");
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory playerInv, Player player) {
        return new MenuMineGenerator(containerID, playerInv, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        regionOffset = Location.readFromNBT(level, tag.getCompound("RegionOffset"));
        regionSize = Location.readFromNBT(level, tag.getCompound("RegionEdge"));

        remainingTimeToFill = tag.getInt("Time");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        CompoundTag regionOffsetTag = new CompoundTag();
        regionOffset.writeToNBT(regionOffsetTag);
        tag.put("RegionOffset", regionOffsetTag);

        CompoundTag regionEdgeTag = new CompoundTag();
        regionSize.writeToNBT(regionEdgeTag);
        tag.put("RegionEdge", regionEdgeTag);

        tag.putInt("Time", remainingTimeToFill);
    }
}
