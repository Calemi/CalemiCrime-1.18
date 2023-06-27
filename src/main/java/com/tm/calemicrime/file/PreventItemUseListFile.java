package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemicrime.util.FileHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

public class PreventItemUseListFile {

    public static ArrayList<String> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("PreventItemUseList", getDefaults(), new TypeToken<ArrayList<String>>(){});
    }

    private static ArrayList<String> getDefaults() {

        ArrayList<String> entries = new ArrayList<>();
        entries.add("minecraft:chorus_fruit");
        entries.add("minecraft:ender_pearl");

        return entries;
    }

    public static boolean canUseItem(Player player, Item item) {

        if (!player.isCreative()) {

            for (String entry : list) {

                if (item.getRegistryName().toString().equalsIgnoreCase(entry)) {
                    return false;
                }
            }
        }

        return true;
    }
}