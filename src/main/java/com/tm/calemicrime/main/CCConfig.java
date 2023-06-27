package com.tm.calemicrime.main;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CCConfig {

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final CategoryServer server = new CategoryServer(SERVER_BUILDER);
    public static final CategoryDrugs drugs = new CategoryDrugs(SERVER_BUILDER);

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    public static class CategoryServer {

        public final ForgeConfigSpec.ConfigValue<Integer> maximumPlotSaveVolume;

        public CategoryServer (ForgeConfigSpec.Builder builder) {

            builder.push("Server");

            maximumPlotSaveVolume = builder
                    .comment("Maximum Plot Save Volume")
                    .defineInRange("maximumPlotSaveVolume", 1000000, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class CategoryDrugs {

        public final ForgeConfigSpec.ConfigValue<Integer> timeToConsumeDrug;
        public final ForgeConfigSpec.ConfigValue<Integer> drugWithdrawEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> taniunSoakedSeedsCrackTime;
        public final ForgeConfigSpec.ConfigValue<Integer> taniunEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Boolean> cannabisPlantRequireBonemeal;
        public final ForgeConfigSpec.ConfigValue<Integer> cannabisPlantGrowTime;
        public final ForgeConfigSpec.ConfigValue<Integer> kushEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> lsdEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> mushroomGrowTime;
        public final ForgeConfigSpec.ConfigValue<Integer> mushroomEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> methEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> bikerMethEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> ketamineEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Boolean> cocaPlantRequireBonemeal;
        public final ForgeConfigSpec.ConfigValue<Integer> cocaPlantGrowTime;
        public final ForgeConfigSpec.ConfigValue<Integer> cocaineEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> heroinEffectDuration;

        public final ForgeConfigSpec.ConfigValue<Integer> blackTarHeroinEffectDuration;

        public CategoryDrugs (ForgeConfigSpec.Builder builder) {

            builder.push("Drugs");

            timeToConsumeDrug = builder
                    .comment("Time to Consume Drug")
                    .defineInRange("timeToConsumeDrug", 2, 0, Integer.MAX_VALUE);

            drugWithdrawEffectDuration = builder
                    .comment("Drug Withdraw Effect Duration")
                    .defineInRange("drugWithdrawEffectDuration", 30, 0, Integer.MAX_VALUE);

            builder.push("Taniun");

            taniunSoakedSeedsCrackTime = builder
                    .comment("Taniun Soaked Seeds Crack Time")
                    .defineInRange("driedSeedsConsumeLiquidChance", 5, 0, Integer.MAX_VALUE);
            taniunEffectDuration = builder
                    .comment("Taniun Effect Duration")
                    .defineInRange("taniunEffectDuration", 60, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Kush");

            cannabisPlantRequireBonemeal = builder
                    .comment("Cannabis Plant Require Bonemeal")
                    .define("cannabisPlantRequireBonemeal", false);
            cannabisPlantGrowTime = builder
                    .comment("Cannabis Plant Grow Time")
                    .defineInRange("cannabisPlantGrowTime", 15, 1, Integer.MAX_VALUE);
            kushEffectDuration = builder
                    .comment("Kush Effect Duration")
                    .defineInRange("kushEffectDuration", 60, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("LSD");

            lsdEffectDuration = builder
                    .comment("LSD Effect Duration")
                    .defineInRange("lsdEffectDuration", 60, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Mushroom");

            mushroomGrowTime = builder
                    .comment("Mushroom Grow Time")
                    .defineInRange("mushroomGrowTime", 150, 0, Integer.MAX_VALUE);
            mushroomEffectDuration = builder
                    .comment("Mushroom Effect Duration")
                    .defineInRange("mushroomEffectDuration", 90, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Meth");

            methEffectDuration = builder
                    .comment("Meth Effect Duration")
                    .defineInRange("methEffectDuration", 120, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Biker Meth");

            bikerMethEffectDuration = builder
                    .comment("Biker Meth Effect Duration")
                    .defineInRange("bikerMethEffectDuration", 120, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Ketamine");

            ketamineEffectDuration = builder
                    .comment("Ketamine Effect Duration")
                    .defineInRange("ketamineEffectDuration", 120, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Cocaine");

            cocaPlantRequireBonemeal = builder
                    .comment("Coca Plant Require Bonemeal")
                    .define("cocaPlantRequireBonemeal", false);
            cocaPlantGrowTime = builder
                    .comment("Coca Plant Grow Time")
                    .defineInRange("cocaPlantGrowTime", 50, 1, Integer.MAX_VALUE);
            cocaineEffectDuration = builder
                    .comment("Cocaine Effect Duration")
                    .defineInRange("cocaineEffectDuration", 180, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Heroin");

            heroinEffectDuration = builder
                    .comment("Heroin Effect Duration")
                    .defineInRange("heroinEffectDuration", 200, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.push("Black Tar Heroin");

            blackTarHeroinEffectDuration = builder
                    .comment("Black Tar Heroin Effect Duration")
                    .defineInRange("blackTarHeroinEffectDuration", 300, 0, Integer.MAX_VALUE);

            builder.pop();


            builder.pop();
        }
    }
}