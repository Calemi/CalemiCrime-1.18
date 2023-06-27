package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.menu.MenuRentAcceptor;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemicrime.util.RegionTeamHelper;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.blockentity.ICurrencyNetworkUnit;
import com.tm.calemieconomy.util.helper.NetworkHelper;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockEntityRentAcceptor extends BlockEntityContainerBase implements ICurrencyNetworkUnit {

    public static final ArrayList<BlockEntityRentAcceptor> rentAcceptors = new ArrayList<>();

    public Location bankLocation;

    public final RegionRuleSet regionRuleSetOverride = new RegionRuleSet();

    public UUID residentTeamID;
    public String residentTeamName = "";
    public String rentType = "";

    public int maxRentHours = 1;
    public long lastRentRefreshTimeSeconds = 0;
    public long lastRentDepleteTimeSeconds = Integer.MAX_VALUE;
    public long systemTimeSeconds = 0;
    public long costToFillRentTime = 1;

    public boolean isPlotReset = true;
    public boolean autoPlotReset = false;
    public long plotResetTimeSeconds = 60;

    public String fileKey = "";

    public BlockEntityRentAcceptor(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.RENT_ACCEPTOR.get(), pos, state);
    }

    public RegionTeam getResidentTeam() {
        return RegionTeamHelper.getRegionTeam(residentTeamID);
    }

    public void setResidentTeam(RegionTeam team) {
        residentTeamID = team.getId();
        residentTeamName = team.getName();
        isPlotReset = false;
    }

    public void clearResidentTeam() {
        residentTeamName = "";
        residentTeamID = null;
    }

    public int getMaxRentSeconds() {
        return maxRentHours * 60 * 60;
    }

    public int getSecondsSinceRentRefresh() {

        long secondsSinceRefresh = systemTimeSeconds - lastRentRefreshTimeSeconds;
        secondsSinceRefresh = Mth.clamp(secondsSinceRefresh, 0, Integer.MAX_VALUE);

        return Math.toIntExact(secondsSinceRefresh);
    }

    public int getSecondsSinceRentDeplete() {

        long secondsSinceDeplete = systemTimeSeconds - lastRentDepleteTimeSeconds;
        secondsSinceDeplete = Mth.clamp(secondsSinceDeplete, 0, Integer.MAX_VALUE);

        return Math.toIntExact(secondsSinceDeplete);
    }

    public int getRemainingRentSeconds() {

        int remainingRentSeconds = getMaxRentSeconds() - getSecondsSinceRentRefresh();
        remainingRentSeconds = Mth.clamp(remainingRentSeconds, 0, remainingRentSeconds);

        return remainingRentSeconds;
    }

    public long getCostToRefillRentTime() {

        float valueF = ((float)getMaxRentSeconds() - (float)getRemainingRentSeconds()) * (float)costToFillRentTime / (float)getMaxRentSeconds();
        long value = (long)valueF;

        if (value == 0) {
            return 1;
        }

        return value;
    }

    public void addRentTime(int minutes) {
        lastRentRefreshTimeSeconds += minutes * 60L;
    }

    public void refillRentTime() {
        lastRentRefreshTimeSeconds = systemTimeSeconds;
    }

    public void emptyRentTime() {
        lastRentRefreshTimeSeconds = systemTimeSeconds - Integer.MAX_VALUE;
        lastRentDepleteTimeSeconds = systemTimeSeconds;
    }

    public int getTimeUntilPlotReset() {
        return (int)(plotResetTimeSeconds - getSecondsSinceRentDeplete());
    }

    public String getFormattedTime(int seconds) {

        long timeInSeconds = seconds % 60;
        long timeInMinutes = (seconds / 60) % 60;
        long timeInHours = (seconds / (60 * 60)) % 24;
        long timeInDays = seconds / (60 * 60 * 24);

        return timeInDays + "d | " + timeInHours + "h | " + timeInMinutes + "m | " + timeInSeconds + "s";
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRentAcceptor rentAcceptor) {

        if (!level.isClientSide() && level.getGameTime() % 20 == 0) {
            addRentAcceptorToList(rentAcceptor);
            cleanRegionProtectorList();
        }

        if (rentAcceptor.getRemainingRentSeconds() > 0) {

            rentAcceptor.lastRentDepleteTimeSeconds = Integer.MAX_VALUE;

            if (!level.isClientSide()) {

                TeamManager manager = TeamManager.INSTANCE;

                if (manager != null && rentAcceptor.getResidentTeam() == null) {
                    rentAcceptor.emptyRentTime();
                    rentAcceptor.markUpdated();
                    return;
                }
            }
        }

        else if (rentAcceptor.residentTeamID != null) {
            rentAcceptor.clearResidentTeam();
            rentAcceptor.markUpdated();
            rentAcceptor.lastRentDepleteTimeSeconds = rentAcceptor.systemTimeSeconds;
        }

        if (!level.isClientSide() && level.getGameTime() % 20 == 10) {

            rentAcceptor.systemTimeSeconds = System.nanoTime() / 1000000000;
            rentAcceptor.markUpdated();

            if (rentAcceptor.autoPlotReset && rentAcceptor.getResidentTeam() == null && !rentAcceptor.isPlotReset) {

                LogHelper.log(CCReference.MOD_NAME, "Deplete: " + rentAcceptor.getSecondsSinceRentDeplete());

                if (rentAcceptor.getSecondsSinceRentDeplete() >= rentAcceptor.plotResetTimeSeconds) {

                    rentAcceptor.isPlotReset = true;

                    for (BlockEntityRegionProtector regionProtector : BlockEntityRegionProtector.regionProtectors) {

                        BlockEntityRentAcceptor connectedRentAcceptor = regionProtector.rentAcceptor;

                        if (connectedRentAcceptor != null && connectedRentAcceptor.equals(rentAcceptor)) {
                            regionProtector.loadPlot((ServerLevel) level);
                        }
                    }
                }
            }
        }
    }

    private static void addRentAcceptorToList(BlockEntityRentAcceptor rentAcceptor) {

        if (!rentAcceptors.contains(rentAcceptor)) {
            rentAcceptors.add(rentAcceptor);
        }
    }

    public static void cleanRegionProtectorList() {

        List<BlockEntityRentAcceptor> toRemove = new ArrayList<>();

        for (BlockEntityRentAcceptor rentAcceptor : rentAcceptors) {

            if (rentAcceptor == null) {
                toRemove.add(rentAcceptor);
            }

            else if (rentAcceptor.isRemoved()) {
                toRemove.add(rentAcceptor);
            }
        }

        rentAcceptors.removeAll(toRemove);
    }

    /**
     * Network Methods
     */

    @Override
    public Direction[] getConnectedDirections() {
        return Direction.values();
    }

    @Override
    public BlockEntityBank getBank() {
        BlockEntityBank bank = NetworkHelper.getConnectedBank(getLocation(), bankLocation);
        if (bank == null) bankLocation = null;
        return bank;
    }

    @Override
    public Location getBankLocation() {
        return bankLocation;
    }

    @Override
    public void setBankLocation(Location location) {
        bankLocation = location;
    }

    /**
     * Container Methods
     */

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.rent_acceptor");
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory playerInv, Player player) {
        return new MenuRentAcceptor(containerID, playerInv, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        regionRuleSetOverride.loadFromNBT(tag);

        if (tag.hasUUID("ResidentTeam")) residentTeamID = tag.getUUID("ResidentTeam");
        residentTeamName = tag.getString("ResidentTeamName");
        rentType = tag.contains("RentType") ? tag.getString("RentType") : "";

        maxRentHours = tag.getInt("MaxRentTime");
        lastRentRefreshTimeSeconds = tag.getLong("LastRentRefreshTime");
        lastRentDepleteTimeSeconds = tag.getLong("LastRentDepleteTime");
        systemTimeSeconds = tag.getLong("SystemTime");
        costToFillRentTime = tag.getLong("CostToFillRentTime");

        isPlotReset = tag.getBoolean("IsPlotReset");
        autoPlotReset = tag.getBoolean("AutoPlotReset");
        plotResetTimeSeconds = tag.getLong("PlotResetTime");

        fileKey = tag.getString("FileKey");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        regionRuleSetOverride.saveToNBT(tag);
        if (residentTeamID != null) tag.putUUID("ResidentTeam", residentTeamID);
        tag.putString("ResidentTeamName", residentTeamName);
        tag.putString("RentType", rentType);

        tag.putInt("MaxRentTime", maxRentHours);
        tag.putLong("LastRentRefreshTime", lastRentRefreshTimeSeconds);
        tag.putLong("LastRentDepleteTime", lastRentDepleteTimeSeconds);
        tag.putLong("SystemTime", systemTimeSeconds);
        tag.putLong("CostToFillRentTime", costToFillRentTime);

        tag.putBoolean("IsPlotReset", isPlotReset);
        tag.putBoolean("AutoPlotReset", autoPlotReset);
        tag.putLong("PlotResetTime", plotResetTimeSeconds);

        tag.putString("FileKey", fileKey);
    }
}
