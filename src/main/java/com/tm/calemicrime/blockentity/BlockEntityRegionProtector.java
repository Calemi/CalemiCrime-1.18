package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.main.CCConfig;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Optional;

public class BlockEntityRegionProtector extends BlockEntityBase {

    public static final ArrayList<BlockEntityRegionProtector> regionProtectors = new ArrayList<>();

    public Location regionOffset = new Location(getLevel(), 0, 0, 0);
    public Location regionSize = new Location(getLevel(), 16, 16, 16);
    public int priority = 0;
    public boolean global;
    public RegionType regionType = RegionType.NONE;
    public final RegionRuleSet regionRuleSet = new RegionRuleSet();

    public long lastSaveTime = 0;

    public BlockEntityRentAcceptor rentAcceptor;

    public BlockEntityRegionProtector(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.REGION_PROTECTOR.get(), pos, state);
    }

    public AABB getRegion() {

        Vec3 start = new Vec3(getLocation().x + regionOffset.x, getLocation().y + regionOffset.y, getLocation().z + regionOffset.z);
        Vec3 end = new Vec3(getLocation().x + regionOffset.x + regionSize.x, getLocation().y + regionOffset.y + regionSize.y, getLocation().z + regionOffset.z + regionSize.z);

        return new AABB(start, end);
    }

    public void setRegion(AABB aabb) {

        Vec3i start = new Vec3i(aabb.minX, aabb.minY, aabb.minZ);
        Vec3i end = new Vec3i(aabb.maxX + 1, aabb.maxY + 1, aabb.maxZ + 1);

        end = end.subtract(start);
        start = start.subtract(new Vec3i(getLocation().x, getLocation().y, getLocation().z));

        regionOffset = new Location(level, new BlockPos(start));
        regionSize = new Location(level, new BlockPos(end));
    }

    private ResourceLocation getPlotResourceLocation() {
        return new ResourceLocation(CCReference.MOD_ID, "plots/x" + getLocation().x + "y" + getLocation().y + "z" + getLocation().z);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRegionProtector regionProtector) {

        if (level.getGameTime() % 20 == 0) {

            if (!level.isClientSide()) {
                addRegionProtectorToList(regionProtector);
                cleanRegionProtectorList();
            }

            regionProtector.checkForRentAcceptors();
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

    public boolean savePlot() {

        if (level.isClientSide) {
            return false;
        }

        if (getRegion().getXsize() * getRegion().getYsize() * getRegion().getZsize() > CCConfig.server.maximumPlotSaveVolume.get()) {
            return false;
        }

        Location location = getLocation();
        ResourceLocation structureName = getPlotResourceLocation();

        BlockPos blockpos = getBlockPos().offset(regionOffset.getBlockPos());
        ServerLevel serverlevel = (ServerLevel) level;

        StructureManager structuremanager = serverlevel.getStructureManager();
        StructureTemplate structuretemplate;

        try {
            structuretemplate = structuremanager.getOrCreate(structureName);
        }

        catch (ResourceLocationException resourcelocationexception1) {
            return false;
        }

        Vec3i regionSizeVec = new Vec3i(regionSize.x, regionSize.y, regionSize.z);

        structuretemplate.fillFromWorld(level, blockpos, regionSizeVec, true, Blocks.STRUCTURE_VOID);
        structuretemplate.setAuthor("Admin");

        try {
            return structuremanager.save(structureName);
        }

        catch (ResourceLocationException resourcelocationexception) {
            return false;
        }
    }

    public boolean loadPlot(ServerLevel serverLevel) {

        ResourceLocation structureName = getPlotResourceLocation();
        StructureManager structuremanager = serverLevel.getStructureManager();

        Optional<StructureTemplate> optional;
        try {
            optional = structuremanager.get(structureName);
        }

        catch (ResourceLocationException resourcelocationexception) {
            return false;
        }

        return optional.isPresent() && loadPlot(serverLevel, optional.get());
    }

    private boolean loadPlot(ServerLevel serverLevel, StructureTemplate structureTemplate) {

        BlockPos blockpos = getBlockPos();
        Vec3i regionSizeVec = new Vec3i(regionSize.x, regionSize.y, regionSize.z);
        Vec3i templateSize = structureTemplate.getSize();

        if (!regionSizeVec.equals(templateSize)) {
            return false;
        }

        StructurePlaceSettings structureplacesettings = new StructurePlaceSettings();

        BlockPos blockpos1 = blockpos.offset(regionOffset.getBlockPos());

        for (BlockPos pos : BlockPos.betweenClosed((int) getRegion().minX, (int) getRegion().minY, (int) getRegion().minZ, (int) getRegion().maxX, (int) getRegion().maxY, (int) getRegion().maxZ)) {

            BlockEntity blockentity = serverLevel.getBlockEntity(pos);
            Clearable.tryClear(blockentity);
        }

        serverLevel.getAllEntities().forEach((entity) -> {
            if (entity != null && getRegion().contains(entity.position()) && !(entity instanceof Player)) {
                entity.kill();
            }
        });

        structureTemplate.placeInWorld(serverLevel, blockpos1, blockpos1, structureplacesettings, serverLevel.getRandom(), 2);

        return true;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        regionOffset = Location.readFromNBT(level, tag.getCompound("RegionOffset"));
        regionSize = Location.readFromNBT(level, tag.getCompound("RegionEdge"));

        priority = tag.getInt("Priority");
        global = tag.getBoolean("Global");

        regionType = RegionType.fromIndex(tag.getInt("regionType"));

        lastSaveTime = tag.getLong("LastSaveTime");

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

        tag.putLong("LastSaveTime", lastSaveTime);

        regionRuleSet.saveToNBT(tag);
    }

    public enum RegionType {

        NONE(0, "none"),
        RESIDENTIAL(1, "residential"),
        COMMERCIAL(2, "commercial");

        private final int index;
        private final String name;

        RegionType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
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
