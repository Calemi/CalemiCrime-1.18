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
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class BlockEntityRentAcceptor extends BlockEntityContainerBase implements ICurrencyNetworkUnit {

    private static final ArrayList<BlockEntityRentAcceptor> rentAcceptors = new ArrayList<>();

    private Location bankLocation;

    private UUID residentTeamID;

    private int maxRentTime = 20 * 60 * 60;
    private int remainingRentTime = 0;
    private int costToFillRentTime = 1;
    private String rentType = "";

    private final RegionRuleSet regionRuleSetOverride = new RegionRuleSet();

    public BlockEntityRentAcceptor(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.RENT_ACCEPTOR.get(), pos, state);
    }

    public static ArrayList<BlockEntityRentAcceptor> getRentAcceptors() {
        return rentAcceptors;
    }

    public RegionRuleSet getRegionRuleSetOverride() {
        return regionRuleSetOverride;
    }

    public UUID getResidentTeamID() {
        return residentTeamID;
    }

    public Team getResidentTeamServer(TeamManager manager) {
        return manager.getTeamByID(residentTeamID);
    }

    public ClientTeam getResidentTeamClient(ClientTeamManager manager) {
        return manager.getTeam(residentTeamID);
    }

    public void setResidentTeam(Team value) {
        residentTeamID = value.getId();
    }

    public void clearResidentTeam() {
        residentTeamID = null;
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

    public void emptyRentTime() {
        setRemainingRentTime(0);
    }

    public String getRentAcceptorType() {
        return rentType;
    }

    public void setRentAcceptorType(String value) {
        rentType = value;
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

            if (!level.isClientSide()) {

                TeamManager manager = TeamManager.INSTANCE;

                if (manager != null && rentAcceptor.getResidentTeamServer(manager) == null) {
                    rentAcceptor.emptyRentTime();
                    return;
                }

                if (level.getGameTime() % 20 == 0) {
                    addRentAcceptorToList(rentAcceptor);
                    cleanRegionProtectorList();
                }
            }

            if (rentAcceptor.getRemainingRentTime() > rentAcceptor.getMaxRentTime()) {
                rentAcceptor.setRemainingRentTime(rentAcceptor.getMaxRentTime());
            }

            rentAcceptor.setRemainingRentTime(rentAcceptor.getRemainingRentTime() - 1);
        }

        else if (rentAcceptor.getResidentTeamID() != null) {
            rentAcceptor.clearResidentTeam();
        }
    }

    private static void addRentAcceptorToList(BlockEntityRentAcceptor rentAcceptor) {

        if (!rentAcceptors.contains(rentAcceptor)) {
            rentAcceptors.add(rentAcceptor);
        }
    }

    public static void cleanRegionProtectorList() {
        rentAcceptors.removeIf(BlockEntity::isRemoved);
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

        if (tag.hasUUID("ResidentTeam")) residentTeamID = tag.getUUID("ResidentTeam");

        maxRentTime = tag.getInt("MaxRentTime");
        remainingRentTime = tag.getInt("RemainingRentTime");
        costToFillRentTime = tag.getInt("CostToFillRentTime");
        rentType = tag.contains("RentType") ? tag.getString("RentType") : "";

        regionRuleSetOverride.loadFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (residentTeamID != null) tag.putUUID("ResidentTeam", residentTeamID);

        tag.putInt("MaxRentTime", maxRentTime);
        tag.putInt("RemainingRentTime", remainingRentTime);
        tag.putInt("CostToFillRentTime", costToFillRentTime);
        tag.putString("RentType", rentType);

        regionRuleSetOverride.saveToNBT(tag);
    }

}
