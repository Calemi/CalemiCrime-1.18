package com.tm.calemicrime.menu;

import com.tm.calemicore.util.menu.MenuBlockBase;
import com.tm.calemicore.util.menu.slot.SlotFilter;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.init.InitMenuTypes;
import com.tm.calemieconomy.init.InitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;

public class MenuRentAcceptor extends MenuBlockBase {

    public MenuRentAcceptor(int containerID, Inventory playerInv, BlockEntityRentAcceptor rentAcceptor) {
        super(InitMenuTypes.RENT_ACCEPTOR.get(), containerID, rentAcceptor);

        //Wallet Slot
        addSlot(new SlotFilter(rentAcceptor, 0, 26, 17, InitItems.WALLET.get()));

        addPlayerInventory(playerInv, 54);
    }

    public MenuRentAcceptor(int containerID, Inventory playerInv, BlockPos pos) {
        this(containerID, playerInv, (BlockEntityRentAcceptor) playerInv.player.level.getBlockEntity(pos));
    }

    @Override
    public int getContainerSize() {
        return 1;
    }
}
