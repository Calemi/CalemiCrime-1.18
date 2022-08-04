package com.tm.calemicrime.util;

import net.minecraft.nbt.CompoundTag;

public class RegionRuleSet {

    private final RuleOverrideType blockBreaking = RuleOverrideType.OFF;
    private final RuleOverrideType blockPlacing = RuleOverrideType.OFF;
    private final RuleOverrideType blockUsing = RuleOverrideType.OFF;

    private final RuleOverrideType entityHurting = RuleOverrideType.OFF;
    private final RuleOverrideType entityInteracting = RuleOverrideType.OFF;

    public RuleOverrideType[] ruleSets = {
            blockBreaking,
            blockPlacing,
            blockUsing,
            entityHurting,
            entityInteracting
    };

    public void loadFromNBT(CompoundTag tag) {
        CompoundTag ruleSetTag = tag.getCompound("RegionRuleSet");

        for (int i = 0; i < ruleSets.length; i++) {
            ruleSets[i] = RuleOverrideType.fromIndex(ruleSetTag.getByte("Rule" + i));
        }
    }

    public void saveToNBT(CompoundTag tag) {
        CompoundTag ruleSetTag = new CompoundTag();

        for (int i = 0; i < ruleSets.length; i++) {
            ruleSetTag.putByte("Rule" + i, ruleSets[i].getIndex());
        }

        tag.put("RegionRuleSet", ruleSetTag);
    }

    public enum RuleOverrideType {

        OFF ((byte)0),
        ALLOW ((byte)1),
        PREVENT ((byte)2);

        private final byte index;

        RuleOverrideType(byte index) {
            this.index = index;
        }

        public byte getIndex() {
            return index;
        }

        public static RuleOverrideType fromIndex(byte index) {

            return switch (index) {
                case 1 -> ALLOW;
                case 2 -> PREVENT;
                default -> OFF;
            };
        }
    }
}
