package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.util.RegionRuleSet;
import dev.ftb.mods.ftbteams.data.Team;
import dev.ftb.mods.ftbteams.data.TeamManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityRentAcceptor extends BlockEntityBase {

    private Team residentTeam;

    private int maxRentTicks = 20 * 60 * 60;
    private int rentTicksRemaining = 0;
    private int costPerHour = 1;

    private final RegionRuleSet regionRuleSetOverride = new RegionRuleSet();

    public BlockEntityRentAcceptor(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.RENT_ACCEPTOR.get(), pos, state);
    }

    public Team getResidentTeam() {
        return residentTeam;
    }

    public void setResidentTeam(Team value) {
        residentTeam = value;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityRentAcceptor rentAcceptor) {
        LogHelper.log(CCReference.MOD_NAME, rentAcceptor.getResidentTeam().getName().getString());
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
