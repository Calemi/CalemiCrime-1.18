package com.tm.calemicrime.mixin;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicrime.main.CCReference;
import flash.npcmod.entity.NpcEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NpcEntity.class)
public abstract class NPCMoveMixin extends AmbientCreature {

    public NPCMoveMixin(EntityType<? extends AmbientCreature> type, Level level) {
        super(type, level);
    }

    /**
     * @author Calemi
     */
    @Override
    public void setDeltaMovement(Vec3 pMotion) {}
}
