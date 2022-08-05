package com.tm.calemicrime.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class BlockSheetOfMeth extends Block {
    public BlockSheetOfMeth() {
        super(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(0.5F, 0.2F));
    }
}
