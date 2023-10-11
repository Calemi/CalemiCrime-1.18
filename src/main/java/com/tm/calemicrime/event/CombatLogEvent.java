package com.tm.calemicrime.event;

import com.tm.calemicore.util.Location;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.main.CalemiCrime;
import com.tm.calemicrime.util.RegionHelper;
import com.tm.calemicrime.util.RegionProfile;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class CombatLogEvent {

    /*public static List<DeathCooldown> deathCooldowns = new ArrayList<>();

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {

        if (!(event.getEntityLiving() instanceof Player player)) {
            return;
        }

        DeathCooldown foundCooldown = null;

        for (DeathCooldown deathCooldown : deathCooldowns) {

            if (deathCooldown.playerID == player.getUUID()) {
                foundCooldown = deathCooldown;
                break;
            }
        }

        if (foundCooldown != null) {
            foundCooldown.cooldown = 0;
        }
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event) {

        if (event.getEntity().getLevel().isClientSide()) {
            return;
        }

        LogHelper.log(CCReference.MOD_NAME, "WHO GOT DAMAGE " + event.getEntityLiving());
        LogHelper.log(CCReference.MOD_NAME, "WHO DAMAGED YOU " + event.getSource().getDirectEntity());
        LogHelper.log(CCReference.MOD_NAME, "WHO DAMAGED YOU2 " + event.getSource().getEntity());

        if (!(event.getEntityLiving() instanceof Player player)) {
            return;
        }

        if (!(event.getSource().getDirectEntity() instanceof Player) && !(event.getSource().getEntity() instanceof Player)) {
            return;
        }

        DeathCooldown foundCooldown = null;

        for (DeathCooldown deathCooldown : deathCooldowns) {

            if (deathCooldown.playerID == player.getUUID()) {
                foundCooldown = deathCooldown;
                break;
            }
        }

        if (foundCooldown != null) {
            foundCooldown.cooldown = CCConfig.server.combatLogTimeTicks.get();
        }

        else {
            deathCooldowns.add(new DeathCooldown(player.getUUID(), CCConfig.server.combatLogTimeTicks.get()));
        }
    }

    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        if (event.player.getLevel().isClientSide()) {
            return;
        }

        for (DeathCooldown deathCooldown : deathCooldowns) {

            if (deathCooldown.playerID == event.player.getUUID()) {

                deathCooldown.cooldown -= 1;

                if (deathCooldown.cooldown > 0) {

                    DecimalFormat df = new DecimalFormat("0.0");

                    ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(new TextComponent(ChatFormatting.RED + "[In Combat for " + df.format((float)deathCooldown.cooldown / 20) + "s] Disconnecting will kill you!"));
                    ((ServerPlayer)event.player).connection.send(packet);
                }

                break;
            }
        }
    }*/

    @SubscribeEvent
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {

        if (event.getEntity().getLevel().isClientSide()) {
            return;
        }

        Location location = new Location(event.getPlayer());
        RegionProfile profile = RegionHelper.getPrioritizedRegionProfile(location, 5);

        if (profile != null && profile.getRuleSet().ruleSets[5] != RegionRuleSet.RuleOverrideType.PREVENT) {
            event.getPlayer().hurt(CalemiCrime.COMBAT_LOG, Float.MAX_VALUE);
        }

        /*for (DeathCooldown deathCooldown : deathCooldowns) {

            if (deathCooldown.playerID == event.getPlayer().getUUID()) {

                if (deathCooldown.cooldown > 0) {
                    event.getPlayer().hurt(CalemiCrime.COMBAT_LOG, Float.MAX_VALUE);
                }

                break;
            }
        }*/
    }

    static class DeathCooldown {

        private final UUID playerID;
        private int cooldown;

        public DeathCooldown(UUID playerID, int cooldown) {
            this.playerID = playerID;
            this.cooldown = cooldown;
        }
    }
}
