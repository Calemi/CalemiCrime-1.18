package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tm.calemicrime.util.FileHelper;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class PlotBlockPlaceLimitFile {

    public static ArrayList<BlockPlaceLimitEntry> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("PlotBlockPlaceLimit", getDefaults(), new TypeToken<ArrayList<BlockPlaceLimitEntry>>() {
        });
    }

    private static ArrayList<BlockPlaceLimitEntry> getDefaults() {

        ArrayList<BlockPlaceLimitEntry> entries = new ArrayList<>();
        entries.add(new BlockPlaceLimitEntry("minecraft:stone", 3, 0));
        return entries;
    }

    public static BlockPlaceLimitEntry getEntry(Block block) {

        for (BlockPlaceLimitEntry entry : list) {

            if (entry.getBlock() == block) {
                return entry;
            }
        }

        return null;
    }

    public static final class BlockPlaceLimitEntry {

        private final String block;
        private final int limit;
        private final int blockCountToIncreaseLimit;

        public BlockPlaceLimitEntry(String block, int limit, int blockCountToIncreaseLimit) {
            this.block = block;
            this.limit = limit;
            this.blockCountToIncreaseLimit = blockCountToIncreaseLimit;
        }

        public Block getBlock() {

            StringReader reader = new StringReader(block);

            try {
                BlockStateParser blockstateparser = (new BlockStateParser(reader, false)).parse(true);
                return blockstateparser.getState().getBlock();
            }

            catch (CommandSyntaxException e) {
                return Blocks.AIR;
            }
        }

        public int getLimit(int plotSize) {

            if (blockCountToIncreaseLimit <= 0) {
                return limit;
            }

            return limit + Math.round((float)plotSize / blockCountToIncreaseLimit);
        }
    }
}