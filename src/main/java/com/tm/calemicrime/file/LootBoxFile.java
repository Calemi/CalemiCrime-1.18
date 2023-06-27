package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tm.calemicrime.util.FileHelper;
import com.tm.calemicrime.util.RandomHelper;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootBoxFile {

    public static ArrayList<LootEntry> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("LootBoxLoot", getDefaults(), new TypeToken<ArrayList<LootEntry>>(){});
    }

    private static ArrayList<LootEntry> getDefaults() {

        ArrayList<LootEntry> entries = new ArrayList<>();
        entries.add(new LootEntry("generic", "dirt", "minecraft:dirt", 2, 0));
        entries.add(new LootEntry("generic", "cobblestone", "minecraft:cobblestone", 4, 1));
        entries.add(new LootEntry("generic", "oak_planks", "minecraft:oak_planks", 6, 2));
        entries.add(new LootEntry("generic", "diamond", "minecraft:diamond", 1, 3));
        entries.add(new LootEntry("generic", "nether_start", "minecraft:nether_star", 1, 4));
        return entries;
    }

    public static LootEntry getLootBoxReward(Player player, String pool) {

        List<String> poolGroups = new ArrayList<>();

        int firstRarity = 50;
        int secondRarity = 20;
        int thirdRarity = 15;
        int fourthRarity = 10;
        int fifthRarity = 5;

        int giftRarityIndex = RandomHelper.getWeightedRandom(firstRarity, secondRarity, thirdRarity, fourthRarity, fifthRarity);

        for (LootEntry entry : LootBoxFile.list) {

            if (entry.pool().equals(pool)) {

                if (entry.rarityIndex() == giftRarityIndex && !poolGroups.contains(entry.group())) {
                    poolGroups.add(entry.group());
                }
            }
        }

        if (poolGroups.isEmpty()) {
            return null;
        }

        String group = poolGroups.get(player.getRandom().nextInt(poolGroups.size()));

        List<LootEntry> poolEntries = new ArrayList<>();

        for (LootEntry entry : LootBoxFile.list) {

            if (entry.group().equals(group)) {
                poolEntries.add(entry);
            }
        }

        return poolEntries.get(player.getRandom().nextInt(poolEntries.size()));
    }

    public static final class LootEntry {

        private final String pool;
        private final String group;
        private final String itemArg;
        private final int count;
        private final int rarityIndex;

        public LootEntry(String pool, String group, String itemArg, int count, int rarityIndex) {
            this.pool = pool;
            this.group = group;
            this.itemArg = itemArg;
            this.count = count;
            this.rarityIndex = rarityIndex;
        }

        public ItemStack getStack() {

            ItemParser parser;

            try {
                parser = (new ItemParser(new StringReader(itemArg), false)).parse();
            } catch (CommandSyntaxException e) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = new ItemStack(parser.getItem());
            stack.setCount(count);
            stack.setTag(parser.getNbt());

            return stack;
        }

        public String pool() {
            return pool;
        }

        public String group() {
            return group;
        }

        public String itemArg() {
            return itemArg;
        }

        public int count() {
            return count;
        }

        public int rarityIndex() {
            return rarityIndex;
        }
    }
}