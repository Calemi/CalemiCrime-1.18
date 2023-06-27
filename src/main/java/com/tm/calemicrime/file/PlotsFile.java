package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemicrime.util.FileHelper;

import java.util.HashMap;
import java.util.Map;

public class PlotsFile {

    public static Map<String, PlotEntry> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("Plots", getDefaults(), new TypeToken<Map<String, PlotEntry>>(){});
    }

    private static Map<String, PlotEntry> getDefaults() {

        Map<String, PlotEntry> entries = new HashMap<>();
        entries.put("bilbo_motel_room", new PlotEntry("Residential", 8, 100, true, 10));
        entries.put("bilbo_motel_room_max", new PlotEntry("Residential", 24, 1000, true, 10));
        return entries;
    }

    public static final class PlotEntry {

        private final String rentType;
        private final int rentTimeHours;
        private final long rentCost;
        private final boolean autoReset;
        private final long resetTimeSeconds;

        public PlotEntry(String rentType, int rentTimeHours, long rentCost, boolean autoReset, long resetTimeSeconds) {
            this.rentType = rentType;
            this.rentTimeHours = rentTimeHours;
            this.rentCost = rentCost;
            this.autoReset = autoReset;
            this.resetTimeSeconds = resetTimeSeconds;
        }

        public String getRentType() {
            return rentType;
        }

        public int getRentTimeHours() {
            return rentTimeHours;
        }

        public long getRentCost() {
            return rentCost;
        }

        public boolean isAutoReset() {
            return autoReset;
        }

        public long getResetTimeSeconds() {
            return resetTimeSeconds;
        }
    }
}