package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.menu.MenuMineGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

public class BlockEntityMineGenerator extends BlockEntityContainerBase {

    private Location regionOffset = new Location(getLevel(), 0, 0, 0);
    private Location regionSize = new Location(getLevel(), 16, 16, 16);

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

    public void setRegionOffset(Location value) {
        regionOffset = value;
    }

    public void setRegionSize(Location value) {
        regionSize = value;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityMineGenerator mineGenerator) {

        if (level.getGameTime() % 20 * 10 == 0) {

            if (mineGenerator.isRegionClear() && mineGenerator.selectRandomBlock() != null) {
                mineGenerator.fillRegion();
            }
        }
    }

    private void fillRegion() {

        for (int x = (int)getRegion().minX; x < (int)getRegion().maxX; x++) {
            for (int y = (int)getRegion().minY; y < (int)getRegion().maxY; y++) {
                for (int z = (int)getRegion().minZ; z < (int)getRegion().maxZ; z++) {

                    new Location(level, x, y, z).setBlock(selectRandomBlock());
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

        for (int x = (int)getRegion().minX; x < (int)getRegion().maxX; x++) {
            for (int y = (int)getRegion().minY; y < (int)getRegion().maxY; y++) {
                for (int z = (int)getRegion().minZ; z < (int)getRegion().maxZ; z++) {

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
    }
}
