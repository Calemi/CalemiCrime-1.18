package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemicrime.util.FileHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;

public class PreventBlockPlaceListFile {

    public static ArrayList<String> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("PreventBlockPlaceList", getDefaults(), new TypeToken<ArrayList<String>>(){});
    }

    private static ArrayList<String> getDefaults() {

        ArrayList<String> entries = new ArrayList<>();
        entries.add("minecraft:bedrock");

        return entries;
    }

    public static boolean canPlaceBlock(Player player, Block block) {

        if (!player.isCreative()) {

            for (String entry : list) {

                if (block.getRegistryName().toString().equalsIgnoreCase(entry)) {
                    return false;
                }
            }
        }

        return true;
    }
}