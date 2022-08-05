package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.menu.MenuRentAcceptor;
import com.tm.calemicrime.util.RegionRuleSet;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.blockentity.ICurrencyNetworkUnit;
import com.tm.calemieconomy.util.helper.NetworkHelper;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityRentAcceptor extends BlockEntityContainerBase implements ICurrencyNetworkUnit {

    private Location bankLocation;

    private Team residentTeam;

    private int maxRentTime = 20 * 60 * 60;
    private int remainingRentTime = 0;
    private int costToFillRentTime = 1;

    private final RegionRuleSet regionRuleSetOverride = new RegionRuleSet();

    public BlockEntityRentAcceptor(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.RENT_ACCEPTOR.get(), pos, state);
    }

    public RegionRuleSet getRegionRuleSetOverride() {
        return regionRuleSetOverride;
    }

    public Team getResidentTeam() {
        return residentTeam;
    }

    public void setResidentTeam(Team value) {
        residentTeam = value;
    }

    public int getMaxRentTime() {
        return maxRentTime;
    }

    public void setMaxRentTime(int value) {
        maxRentTime = value;
    }

    public int getRemainingRentTime() {
        return remainingRentTime;
    }

    public void setRemainingRentTime(int value) {
        remainingRentTime = value;
    }

    public int getCostToFillRentTime() {
        return costToFillRentTime;
    }

    public void setCostToFillRentTime(int value) {
        costToFillRentTime = value;
    }

    public int getCostToRefillRentTime() {

        int value = MathHelper.scaleInt(getMaxRentTime() - getRemainingRentTime(), getMaxRentTime(), getCostToFillRentTime());

        if (value == 0) {
            return 1;
        }

        return value;
    }

    public void refillRentTime() {
        setRemainingRentTime(getMaxRentTime());
    }

    public String getFormattedTime(int ticks) {

        int timeInSeconds = ticks / (20) % 60;
        int timeInMinutes = ticks / (20 * 60) % 60;
        int timeInHours = ticks / (20 * 60 * 60) % 24;
        int timeInDays = ticks / (20 * 60 * 60 * 24);

        return timeInDays + "d | " + timeInHours + "h | " + timeInMinutes + "m | " + timeInSeconds + "s";
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRentAcceptor rentAcceptor) {

        if (rentAcceptor.getRemainingRentTime() > 0) {

            if (rentAcceptor.getRemainingRentTime() > rentAcceptor.getMaxRentTime()) {
                rentAcceptor.setRemainingRentTime(rentAcceptor.getMaxRentTime());
            }

            rentAcceptor.setRemainingRentTime(rentAcceptor.getRemainingRentTime() - 1);
        }

        else if (rentAcceptor.getResidentTeam() != null) {
            rentAcceptor.setResidentTeam(null);
        }
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
        return 1;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory playerInv, Player player) {
        return new MenuRentAcceptor(containerID, playerInv, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.hasUUID("ResidentTeam")) residentTeam = TeamManager.INSTANCE.getTeamByID(tag.getUUID("ResidentTeam"));

        maxRentTime = tag.getInt("MaxRentTime");
        remainingRentTime = tag.getInt("RemainingRentTime");
        costToFillRentTime = tag.getInt("CostToFillRentTime");

        regionRuleSetOverride.loadFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (residentTeam != null) tag.putUUID("ResidentTeam", residentTeam.getId());

        tag.putInt("MaxRentTime", maxRentTime);
        tag.putInt("RemainingRentTime", remainingRentTime);
        tag.putInt("CostToFillRentTime", costToFillRentTime);

        regionRuleSetOverride.saveToNBT(tag);
    }

}
