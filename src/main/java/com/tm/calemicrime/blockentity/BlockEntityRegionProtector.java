package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.accessor.CorpseAccessor;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.init.InitItems;
import com.tm.calemicrime.main.CCConfig;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.NotifyHelper;
import com.tm.calemicrime.util.RegionHelper;
import com.tm.calemicrime.util.RegionProfile;
import com.tm.calemicrime.util.RegionRuleSet;
import de.maxhenkel.corpse.Main;
import de.maxhenkel.corpse.entities.CorpseEntity;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public class BlockEntityRegionProtector extends BlockEntityBase {

    public RegionProfile profile = new RegionProfile(getLevel(), getLocation());

    public long lastSaveTime = 0;

    public BlockEntityRegionProtector(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.REGION_PROTECTOR.get(), pos, state);
    }

    private ResourceLocation getPlotResourceLocation() {
        return new ResourceLocation(CCReference.MOD_ID, "plots/x" + getLocation().x + "y" + getLocation().y + "z" + getLocation().z);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRegionProtector regionProtector) {

        if (level.getGameTime() % 20 == 0) {

            if (!level.isClientSide()) {
                RegionHelper.allProfiles.put(regionProtector.getBlockPos(), regionProtector.profile);
            }

            regionProtector.checkForRentAcceptors();

            //IF PVP
            if (regionProtector.profile.getRuleSet().ruleSets[5] == RegionRuleSet.RuleOverrideType.ALLOW) {

                for (CorpseEntity entity : level.getEntitiesOfClass(CorpseEntity.class, regionProtector.profile.getRegion())) {

                    if (entity instanceof CorpseAccessor mixin) {

                        if (mixin.getAge() < Main.SERVER_CONFIG.corpseSkeletonTime.get()) {
                            mixin.setAge(Main.SERVER_CONFIG.corpseSkeletonTime.get());
                        }
                    }
                }

                for (Player player : level.players()) {

                    if (regionProtector.profile.isInRegion(player)) {

                        NotifyHelper.notifyHotbar(player, ChatFormatting.RED, "[PVP Zone] Disconnecting will kill you!");

                        InventoryCosArmor cosArmorInventory = ModObjects.invMan.getCosArmorInventory(player.getUUID());

                        for (int i = 0; i < 4; i++) {

                            ItemStack stack = cosArmorInventory.getItem(i);

                            cosArmorInventory.setSkinArmor(i, false);

                            if (stack.isEmpty()) {
                                continue;
                            }

                            ItemHelper.spawnStackAtEntity(level, player, stack);
                            cosArmorInventory.setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    public void checkForRentAcceptors() {

        for (Direction direction : Direction.values()) {

            if (new Location(getLocation(), direction, 1).getBlockEntity() instanceof BlockEntityRentAcceptor rentAcceptor) {
                profile.setRentAcceptor(rentAcceptor);
                rentAcceptor.regionProtector = this;
                return;
            }
        }

        profile.setRentAcceptor(null);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return profile.getRegion();
    }

    public boolean savePlot() {

        if (level.isClientSide) {
            return false;
        }

        if (profile.getType() == RegionProfile.Type.NONE) {
            return false;
        }

        if (profile.getRegion().getXsize() * profile.getRegion().getYsize() * profile.getRegion().getZsize() > CCConfig.server.maximumPlotSaveVolume.get()) {
            return false;
        }

        for (int x = 0; x < profile.getRegion().getXsize(); x++) {
            for (int y = 0; y < profile.getRegion().getYsize(); y++) {
                for (int z = 0; z < profile.getRegion().getZsize(); z++) {

                    int xRelative = (int)profile.getRegion().minX + x;
                    int yRelative = (int)profile.getRegion().minY + y;
                    int zRelative = (int)profile.getRegion().minZ + z;

                    BlockState state = level.getBlockState(new BlockPos(xRelative, yRelative, zRelative));

                    if (state.is(InitItems.REGION_PROJECTOR.get()) || state.is(InitItems.RENT_ACCEPTOR.get())) {
                        LogHelper.log(CCReference.MOD_NAME, "FOUND " + state);
                        return false;
                    }
                }
            }
        }

        Location location = getLocation();
        ResourceLocation structureName = getPlotResourceLocation();

        BlockPos blockpos = getBlockPos().offset(profile.getOffset().getBlockPos());
        ServerLevel serverlevel = (ServerLevel) level;

        StructureManager structuremanager = serverlevel.getStructureManager();
        StructureTemplate structuretemplate;

        try {
            structuretemplate = structuremanager.getOrCreate(structureName);
        }

        catch (ResourceLocationException resourcelocationexception1) {
            return false;
        }

        Vec3i regionSizeVec = new Vec3i(profile.getSize().x, profile.getSize().y, profile.getSize().z);

        structuretemplate.fillFromWorld(level, blockpos, regionSizeVec, true, Blocks.STRUCTURE_VOID);
        structuretemplate.setAuthor("Admin");

        try {

            boolean value = structuremanager.save(structureName);

            if (value) {
                lastSaveTime = System.currentTimeMillis();
                markUpdated();
            }

            return value;
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
        Vec3i regionSizeVec = new Vec3i(profile.getSize().x, profile.getSize().y, profile.getSize().z);
        Vec3i templateSize = structureTemplate.getSize();

        if (!regionSizeVec.equals(templateSize)) {
            return false;
        }

        StructurePlaceSettings structureplacesettings = new StructurePlaceSettings();

        BlockPos blockpos1 = blockpos.offset(profile.getOffset().getBlockPos());

        if (!serverLevel.isLoaded(new BlockPos(profile.getRegion().minX, profile.getRegion().minY, profile.getRegion().minZ))) {
            return false;
        }

        if (!serverLevel.isLoaded(new BlockPos(profile.getRegion().maxX, profile.getRegion().maxY, profile.getRegion().maxZ))) {
            return false;
        }

        if (!serverLevel.isLoaded(new BlockPos(profile.getRegion().minX, profile.getRegion().minY, profile.getRegion().maxZ))) {
            return false;
        }

        if (!serverLevel.isLoaded(new BlockPos(profile.getRegion().maxX, profile.getRegion().minY, profile.getRegion().minZ))) {
            return false;
        }

        for (BlockPos pos : BlockPos.betweenClosed((int) profile.getRegion().minX, (int) profile.getRegion().minY, (int) profile.getRegion().minZ, (int) profile.getRegion().maxX, (int) profile.getRegion().maxY, (int) profile.getRegion().maxZ)) {

            BlockEntity blockentity = serverLevel.getBlockEntity(pos);
            Clearable.tryClear(blockentity);

            if (serverLevel.isWaterAt(pos)) {
                serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }

        serverLevel.getAllEntities().forEach((entity) -> {
            if (entity != null && profile.getRegion().contains(entity.position()) && !(entity instanceof Player)) {
                entity.kill();
            }
        });

        structureTemplate.placeInWorld(serverLevel, blockpos1, blockpos1, structureplacesettings, serverLevel.getRandom(), 2);

        return true;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        profile.loadFromNBT(tag);

        lastSaveTime = tag.getLong("LastSaveTime");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        profile.saveToNBT(tag);

        tag.putLong("LastSaveTime", lastSaveTime);
    }
}
