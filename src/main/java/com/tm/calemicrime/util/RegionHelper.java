package com.tm.calemicrime.util;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.team.RegionTeam;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class RegionHelper {

    public static final HashMap<BlockPos, RegionProfile> allProfiles = new HashMap<>();

    public static boolean handleEventCancellation(Event event, LevelAccessor level, Player player, Location location, int ruleSetIndex) {

        RegionProfile profile = getPrioritizedRegionProfile(location, ruleSetIndex);

        if (player == null || !player.isCreative()) {

            //LogHelper.log(CCReference.MOD_NAME, "EVENT CALLED");

            if (profile != null) {

                //LogHelper.log(CCReference.MOD_NAME, "FOUND REGION PROTECTOR");

                BlockEntityRentAcceptor rentAcceptor = profile.getRentAcceptor();

                if (player != null && rentAcceptor != null) {

                    RegionTeam team = rentAcceptor.getResidentTeam();

                    if (team != null) {

                        //Fix ally
                        if (team.isMember(player) || (team.isAlly(player) && ruleSetIndex == 2)) {

                            if (rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF) {

                                //LogHelper.log(CCReference.MOD_NAME, "TEAM FOUND! CHECKING NEW RULES");
                                return rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT;
                            }
                        }
                    }
                }

                return profile.getRuleSet().ruleSets[ruleSetIndex] == RegionRuleSet.RuleOverrideType.PREVENT;
            }
        }

        return false;
    }

    public static RegionProfile getPrioritizedRegionProfile(Location location, int ruleSetIndex) {

        ArrayList<RegionProfile> regionProfilesAffectingLocation = new ArrayList<>();

        for (RegionProfile profile : allProfiles.values()) {

            if (profile.getRegion().contains(location.getVector()) || profile.isGlobal()) {
                regionProfilesAffectingLocation.add(profile);
            }
        }

        if (!regionProfilesAffectingLocation.isEmpty()) {

            RegionProfile profile = null;

            for (RegionProfile profileAffectingLocation : regionProfilesAffectingLocation) {

                if (profileAffectingLocation.getRuleSet().ruleSets[ruleSetIndex] != RegionRuleSet.RuleOverrideType.OFF) {

                    if (profile == null || profileAffectingLocation.getPriority() > profile.getPriority()) {
                        profile = profileAffectingLocation;
                    }
                }
            }

            return profile;
        }

        return null;
    }
}
