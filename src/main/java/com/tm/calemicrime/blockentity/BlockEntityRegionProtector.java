package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.core.BlockPos;
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
    private Location regionEdge = new Location(getLevel(), 16, 16, 16);
    private int priority = 0;

    private final RegionRuleSet regionRuleSet = new RegionRuleSet();

    public BlockEntityRegionProtector(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.REGION_PROTECTOR.get(), pos, state);
    }

    public static ArrayList<BlockEntityRegionProtector> getRegionProtectors() {
        return regionProtectors;
    }

    public Location getRegionOffset() {
        return regionOffset;
    }

    public Location getRegionEdge() {
        return regionEdge;
    }

    public AABB getRegion() {

        Vec3 start = new Vec3(getLocation().x + getRegionOffset().x, getLocation().y + getRegionOffset().y, getLocation().z + getRegionOffset().z);
        Vec3 end = new Vec3(getLocation().x + getRegionOffset().x + getRegionEdge().x, getLocation().y + getRegionOffset().y + getRegionEdge().y, getLocation().z + getRegionOffset().z + getRegionEdge().z);

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

    public void setRegionEdge(Location value) {
        regionEdge = value;
    }

    public void setPriority(int value) {
        priority = value;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRegionProtector regionProtector) {

        if (level.getGameTime() % 20 == 0) {

            if (!level.isClientSide()) {
                addRegionProtectorToList(regionProtector);
                cleanRegionProtectorList();
            }

            //LogHelper.logCommon(CEReference.MOD_NAME, level,"BREAKING " + regionProtector.getRegionRuleSet().ruleSets[0]);
            //LogHelper.logCommon(CEReference.MOD_NAME, level,"PLACING " + regionProtector.getRegionRuleSet().ruleSets[1]);
            //LogHelper.logCommon(CEReference.MOD_NAME, level,"USING " + regionProtector.getRegionRuleSet().ruleSets[2]);
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

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        regionOffset = Location.readFromNBT(level, tag.getCompound("RegionOffset"));
        regionEdge = Location.readFromNBT(level, tag.getCompound("RegionEdge"));

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
        regionEdge.writeToNBT(regionEdgeTag);
        tag.put("RegionEdge", regionEdgeTag);

        tag.putInt("Priority", priority);
        regionRuleSet.saveToNBT(tag);
    }
}
