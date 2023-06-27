package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.util.HazardHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRadiationProjector extends BlockEntityBase {

    private Location regionOffset = new Location(getLevel(), 0, 0, 0);
    private Location regionSize = new Location(getLevel(), 16, 16, 16);

    private float radiationStrength = 1F;

    public BlockEntityRadiationProjector(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.RADIATION_PROJECTOR.get(), pos, state);
    }

    public Location getRegionOffset() {
        return regionOffset;
    }

    public Location getRegionSize() {
        return regionSize;
    }

    public float getRadiationStrength() {
        return radiationStrength;
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

    public boolean isInRegion(Entity entity) {
        return getRegion().contains(entity.position());
    }

    public void setRegionOffset(Location value) {
        regionOffset = value;
    }

    public void setRegionSize(Location value) {
        regionSize = value;
    }

    public void setRadiationStrength(float value) {
        radiationStrength = value;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRadiationProjector radiationProjector) {

        if (level.getGameTime() % 20 == 0) {

            for (Player player : level.players()) {

                if (radiationProjector.isInRegion(player)) {

                    HazardHelper.attemptRadiationDamage(player, radiationProjector.getRadiationStrength());
                }
            }
        }

        if (level.getGameTime() % (20 * 6) == 0) {

            for (Player player : level.players()) {

                if (radiationProjector.isInRegion(player)) {

                    HazardHelper.playGeigerSound(player, radiationProjector.getRadiationStrength());
                }
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

        radiationStrength = tag.getFloat("RadiationStrength");
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

        tag.putFloat("RadiationStrength", radiationStrength);
    }
}
