package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemicrime.team.RegionTeam;
import com.tm.calemicrime.util.FileHelper;

import java.util.ArrayList;
import java.util.List;

public class UnstuckLocationsFile {

    public static ArrayList<UnstuckLocation> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("UnstuckLocations", new ArrayList<>(), new TypeToken<ArrayList<UnstuckLocation>>(){});
    }

    public static void save() {
        FileHelper.saveToFile("UnstuckLocations", list);
    }

    public static final class UnstuckLocation {

        private final String worldName;
        private final int x;
        private final int y;
        private final int z;

        public UnstuckLocation(String worldName, int x, int y, int z) {
            this.worldName = worldName;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String getWorldName() {
            return worldName;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }
}