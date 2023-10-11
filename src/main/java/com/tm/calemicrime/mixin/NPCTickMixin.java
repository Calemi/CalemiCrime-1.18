package com.tm.calemicrime.mixin;

import flash.npcmod.entity.NpcEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(NpcEntity.class)
public abstract class NPCTickMixin extends LivingEntity {

    public NPCTickMixin(EntityType<? extends AmbientCreature> type, Level level) {
        super(type, level);
    }
    /**
     * @author Calemi
     * @reason Removes all ticking functionality for npcs.
     */
    @Overwrite
    public void tick() {
        this.baseTick();
    }
}
