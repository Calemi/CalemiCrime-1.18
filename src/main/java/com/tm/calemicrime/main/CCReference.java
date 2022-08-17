package com.tm.calemicrime.main;

import net.minecraftforge.fml.loading.FMLPaths;

/**
 * A static reference class used to store common public values.
 * ex. the mod's name & version.
 */
public class CCReference {

    public static final String MOD_ID = "calemicrime";
    public static final String MOD_NAME = "Calemi's Organized Crime";
    public static final String CONFIG_DIR = FMLPaths.CONFIGDIR.get().toString() + "/" + MOD_ID;
}