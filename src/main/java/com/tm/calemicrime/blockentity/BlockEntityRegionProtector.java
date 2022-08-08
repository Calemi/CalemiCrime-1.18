package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class BlockEntityRegionProtector extends BlockEntityBase {

    private static final ArrayList<BlockEntityRegionProtector> regionProtectors = new ArrayList<>();

    private Location regionOffset = new Location(getLevel(), 0, 0, 0);
    private Location regionSize = new Location(getLevel(), 16, 16, 16);
    private int priority = 0;

    private final RegionRuleSet regionRuleSet = new RegionRuleSet();

    private BlockEntityRentAcceptor rentAcceptor;

    public BlockEntityRegionProtector(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.REGION_PROTECTOR.get(), pos, state);
    }

    public static ArrayList<BlockEntityRegionProtector> getRegionProtectors() {
        return regionProtectors;
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

    public int getPriority() {
        return priority;
    }

    public RegionRuleSet getRegionRuleSet() {
        return regionRuleSet;
    }

    public void setRegionOffset(Location value) {
        regionOffset = value;
    }

    public void setRegionSize(Location value) {
        regionSize = value;
    }

    public void setPriority(int value) {
        priority = value;
    }

    public BlockEntityRentAcceptor getRentAcceptor() {
        return rentAcceptor;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRegionProtector regionProtector) {

        if (level.getGameTime() % 20 == 0) {

            if (!level.isClientSide()) {
                addRegionProtectorToList(regionProtector);
                cleanRegionProtectorList();
                regionProtector.checkForRentAcceptors();
            }
        }
    }

    private static void addRegionProtectorToList(BlockEntityRegionProtector regionProtector) {

        if (!regionProtectors.contains(regionProtector)) {
            regionProtectors.add(regionProtector);
        }
    }

    public static void cleanRegionProtectorList() {
        regionProtectors.removeIf(BlockEntity::isRemoved);
    }

    public void checkForRentAcceptors() {

        rentAcceptor = null;

        for (Direction direction : Direction.values()) {

            if (new Location(getLocation(), direction, 1).getBlockEntity() instanceof BlockEntityRentAcceptor rentAcceptor) {
                this.rentAcceptor = rentAcceptor;
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return getRegion();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        regionOffset = Location.readFromNBT(level, tag.getCompound("RegionOffset"));
        regionSize = Location.readFromNBT(level, tag.getCompound("RegionEdge"));

        priority = tag.getInt("Priority");
        regionRuleSet.loadFromNBT(tag);
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

        tag.putInt("Priority", priority);
        regionRuleSet.saveToNBT(tag);
    }
}
