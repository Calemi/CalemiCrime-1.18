package com.tm.calemicrime.menu;

import com.tm.calemicore.util.menu.MenuBlockBase;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.init.InitMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;

public class MenuRentAcceptor extends MenuBlockBase {

    public MenuRentAcceptor(int containerID, Inventory playerInv, BlockEntityRentAcceptor rentAcceptor) {
        super(InitMenuTypes.RENT_ACCEPTOR.get(), containerID, rentAcceptor);

        addPlayerInventory(playerInv, 54);
    }

    public MenuRentAcceptor(int containerID, Inventory playerInv, BlockPos pos) {
        this(containerID, playerInv, (BlockEntityRentAcceptor) playerInv.player.level.getBlockEntity(pos));
    }

    @Override
    public int getContainerSize() {
        return 0;
    }
}
