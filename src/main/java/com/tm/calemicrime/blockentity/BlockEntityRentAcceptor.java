package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.main.CCReference;
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

    private int maxRentTicks = 20 * 60 * 60;
    private int rentTicksRemaining = 0;
    private int costPerHour = 1;

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

    public int getMaxRentTicks() {
        return maxRentTicks;
    }

    public int getRentTicksRemaining() {
        return rentTicksRemaining;
    }

    public void setRentTicksRemaining(int value) {
        rentTicksRemaining = value;
    }

    public int getCostToRefillRentTime() {
        return ((maxRentTicks - rentTicksRemaining) / 72000) * costPerHour;
    }

    public void refillRentTime() {
        setRentTicksRemaining(getMaxRentTicks());
    }

    public String getFormattedTimeLeft() {

        int timeInSeconds = rentTicksRemaining / (20) % 60;
        int timeInMinutes = rentTicksRemaining / (20 * 60) % 60;
        int timeInHours = rentTicksRemaining / (20 * 60 * 60) % 24;
        int timeInDays = rentTicksRemaining / (20 * 60 * 60 * 24);

        return timeInDays + "d:" + timeInHours + "h:" + timeInMinutes + "m:" + timeInSeconds + "s";
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRentAcceptor rentAcceptor) {

        if (rentAcceptor.getRentTicksRemaining() > 0) {
            rentAcceptor.setRentTicksRemaining(rentAcceptor.getRentTicksRemaining() - 1);
        }

        //LogHelper.log(CCReference.MOD_NAME, rentAcceptor.getResidentTeam().getName().getString());
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

        residentTeam = TeamManager.INSTANCE.getTeamByID(tag.getUUID("ResidentTeam"));

        maxRentTicks = tag.getInt("MaxRentTicks");
        rentTicksRemaining = tag.getInt("RentTicksRemaining");
        costPerHour = tag.getInt("CostPerHour");

        regionRuleSetOverride.loadFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putUUID("ResidentTeam", residentTeam.getId());

        tag.putInt("MaxRentTicks", maxRentTicks);
        tag.putInt("RentTicksRemaining", rentTicksRemaining);
        tag.putInt("CostPerHour", costPerHour);

        regionRuleSetOverride.saveToNBT(tag);
    }

}
