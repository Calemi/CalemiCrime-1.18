package com.tm.calemicrime.util;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RegionProfile {

    private Level level;

    private int priority;

    private boolean global;
    private Location origin;
    private Location offset;
    private Location size;

    private RegionRuleSet ruleSet;
    private BlockEntityRentAcceptor rentAcceptor;
    private Type type = Type.NONE;

    public RegionProfile(Level level, Location origin) {
        this.level = level;

        priority = 0;

        global = false;
        this.origin = origin;
        offset = new Location(level, 0, 1, 0);
        size = new Location(level, 1, 1, 1);

        ruleSet = new RegionRuleSet();
        rentAcceptor = null;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public Location getOffset() {
        return offset;
    }

    public void setOffset(Location offset) {
        this.offset = offset;
    }

    public Location getSize() {
        return size;
    }

    public void setSize(Location size) {
        this.size = size;
    }

    public RegionRuleSet getRuleSet() {
        return ruleSet;
    }

    public BlockEntityRentAcceptor getRentAcceptor() {
        return rentAcceptor;
    }

    public void setRentAcceptor(BlockEntityRentAcceptor rentAcceptor) {
        this.rentAcceptor = rentAcceptor;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public AABB getRegion() {

        Vec3 start = new Vec3(origin.x + getOffset().x, origin.y + getOffset().y, origin.z + getOffset().z);
        Vec3 end = new Vec3(origin.x + getOffset().x + getSize().x, origin.y + getOffset().y + getSize().y, origin.z + getOffset().z + getSize().z);

        return new AABB(start, end);
    }

    public void setRegion(AABB aabb) {

        Vec3i start = new Vec3i(aabb.minX, aabb.minY, aabb.minZ);
        Vec3i end = new Vec3i(aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1);

        end = end.subtract(start);
        start = start.subtract(new Vec3i(origin.x, origin.y, origin.z));

        setOffset(new Location(level, new BlockPos(start)));
        setSize(new Location(level, new BlockPos(end)));
    }

    public boolean isInRegion(Entity entity) {
        return getRegion().contains(entity.position());
    }

    public void loadFromNBT(CompoundTag tag) {

        offset = Location.readFromNBT(level, tag.getCompound("RegionOffset"));
        size = Location.readFromNBT(level, tag.getCompound("RegionEdge"));

        priority = tag.getInt("Priority");
        global = tag.getBoolean("Global");

        type = Type.fromIndex(tag.getInt("regionType"));

        ruleSet.loadFromNBT(tag);
    }

    public void saveToNBT(CompoundTag tag) {

        CompoundTag regionOffsetTag = new CompoundTag();
        offset.writeToNBT(regionOffsetTag);
        tag.put("RegionOffset", regionOffsetTag);

        CompoundTag regionEdgeTag = new CompoundTag();
        size.writeToNBT(regionEdgeTag);
        tag.put("RegionEdge", regionEdgeTag);

        tag.putInt("Priority", priority);
        tag.putBoolean("Global", global);

        tag.putInt("regionType", type.getIndex());

        ruleSet.saveToNBT(tag);
    }

    public enum Type {

        NONE(0, "none"),
        RESIDENTIAL(1, "residential"),
        COMMERCIAL(2, "commercial");

        private final int index;
        private final String name;

        Type(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public static Type fromIndex(int index) {
            return switch (index) {
                case 1 -> RESIDENTIAL;
                case 2 -> COMMERCIAL;
                default -> NONE;
            };
        }
    }
}
