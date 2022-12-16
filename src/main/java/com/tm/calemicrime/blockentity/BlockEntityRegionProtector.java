package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
    private boolean global;
    private RegionType regionType = RegionType.NONE;

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

    public void setRegion(AABB aabb) {

        Vec3i start = new Vec3i(aabb.minX, aabb.minY, aabb.minZ);
        Vec3i end = new Vec3i(aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1);

        end = end.subtract(start);
        start = start.subtract(new Vec3i(getLocation().x, getLocation().y, getLocation().z));

        setRegionOffset(new Location(level, new BlockPos(start)));
        setRegionSize(new Location(level, new BlockPos(end)));
    }

    public int getPriority() {
        return priority;
    }

    public boolean isGlobal() {
        return global;
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

    public void setGlobal(boolean value) {
        global = value;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType value) {
        regionType = value;
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
        global = tag.getBoolean("Global");

        regionType = RegionType.fromIndex(tag.getInt("regionType"));

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
        tag.putBoolean("Global", global);

        tag.putInt("regionType", regionType.getIndex());

        regionRuleSet.saveToNBT(tag);
    }

    public enum RegionType {

        NONE(0, "none", 999),
        RESIDENTIAL(1, "residential", 3),
        COMMERCIAL(2, "commercial", 5);

        private final int index;
        private final String name;
        private final int rentMax;

        RegionType(int index, String name, int rentMax) {
            this.index = index;
            this.name = name;
            this.rentMax = rentMax;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public int getRentMax() {
            return rentMax;
        }

        public static RegionType fromIndex(int index) {
            return switch (index) {
                case 1 -> RESIDENTIAL;
                case 2 -> COMMERCIAL;
                default -> NONE;
            };
        }
    }
}
