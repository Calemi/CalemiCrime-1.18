package com.tm.calemicrime.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mrcrayfish.guns.init.ModEffects;
import com.tm.calemicore.util.Location;
import com.tm.calemicrime.file.UnstuckLocationsFile;
import com.tm.calemicrime.util.RegionHelper;
import com.tm.calemicrime.util.RegionProfile;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class UnstuckCommands {

    public static ArgumentBuilder<CommandSourceStack, ?> unstuck() {

        return teleport().then(add());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> teleport() {

        return Commands.literal("unstuck").executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            RegionProfile profile = RegionHelper.getPrioritizedRegionProfile(new Location(player), 0);

            if (profile == null || profile.getType() == RegionProfile.Type.NONE) {
                CrimeCommandsBase.sendError(player, "You can only use that in plots!");
                return Command.SINGLE_SUCCESS;
            }

            UnstuckLocationsFile.UnstuckLocation selectedLocation = null;
            double selectedDistance = Double.MAX_VALUE;

            for (UnstuckLocationsFile.UnstuckLocation location : UnstuckLocationsFile.list) {

                if (!location.getWorldName().equalsIgnoreCase(player.getLevel().dimension().location().toString())) {
                    continue;
                }

                double distance = player.distanceToSqr(location.getX(), location.getY(), location.getZ());

                if (distance < selectedDistance) {
                    selectedLocation = location;
                    selectedDistance = distance;
                }
            }

            if (selectedLocation == null) {
                CrimeCommandsBase.sendError(player, "Could not find valid location to teleport to!");
                return Command.SINGLE_SUCCESS;
            }

            int effectSeconds = (int)Mth.sqrt((float)selectedDistance);

            player.teleportTo(selectedLocation.getX() + 0.5F, selectedLocation.getY(), selectedLocation.getZ() + 0.5F);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * effectSeconds, 32000));
            player.addEffect(new MobEffectInstance(ModEffects.BLINDED.get(), 20 * effectSeconds));

            CrimeCommandsBase.sendSuccess(player, "You're unstuck! You will be paralyzed for " + effectSeconds + " second(s)");

            return Command.SINGLE_SUCCESS;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> add() {

        return Commands.literal("add").requires((player) -> player.hasPermission(3)).executes(ctx -> {

            Player player = ctx.getSource().getPlayerOrException();

            UnstuckLocationsFile.list.add(new UnstuckLocationsFile.UnstuckLocation(player.getLevel().dimension().location().toString(), player.getBlockX(), player.getBlockY(), player.getBlockZ()));
            UnstuckLocationsFile.save();

            CrimeCommandsBase.sendSuccess(player, "Added unstuck location!");

            return Command.SINGLE_SUCCESS;
        });
    }
}
