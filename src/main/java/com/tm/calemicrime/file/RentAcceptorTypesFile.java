package com.tm.calemicrime.file;

import com.google.gson.reflect.TypeToken;
import com.tm.calemicrime.util.FileHelper;

import java.util.HashMap;
import java.util.Map;

public class RentAcceptorTypesFile {

    public static Map<String, Integer> list;

    public static void init() {
        list = FileHelper.readFileOrCreate("RentAcceptorTypes", getDefaults(), new TypeToken<Map<String, Integer>>(){});
    }

    private static Map<String, Integer> getDefaults() {

        Map<String, Integer> map = new HashMap<>();
        map.put("Residential", 3);
        map.put("Commercial", 5);

        return map;
    }

    public static int getLimit(String type) {
        return list.getOrDefault(type, 100);
    }
}