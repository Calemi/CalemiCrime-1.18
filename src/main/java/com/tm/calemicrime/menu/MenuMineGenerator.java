package com.tm.calemicrime.menu;

import com.tm.calemicore.util.menu.MenuBlockBase;
import com.tm.calemicrime.blockentity.BlockEntityMineGenerator;
import com.tm.calemicrime.init.InitMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class MenuMineGenerator extends MenuBlockBase {

    public MenuMineGenerator(int containerID, Inventory playerInv, BlockEntityMineGenerator mineGenerator) {
        super(InitMenuTypes.MINE_GENERATOR.get(), containerID, mineGenerator);

        for(int rowY = 0; rowY < 3; ++rowY) {
            for(int rowX = 0; rowX < 9; ++rowX) {
                addSlot(new Slot(mineGenerator, rowX + rowY * 9, 8 + rowX * 18, rowY * 18 + 15));
            }
        }

        addPlayerInventory(playerInv, 73);
    }

    public MenuMineGenerator(int containerID, Inventory playerInv, BlockPos pos) {
        this(containerID, playerInv, (BlockEntityMineGenerator) playerInv.player.level.getBlockEntity(pos));
    }

    @Override
    public int getContainerSize() {
        return 27;
    }
}
