package com.tm.calemicrime.init;

import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.menu.MenuMineGenerator;
import com.tm.calemicrime.menu.MenuRentAcceptor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Handles setting up the Block Entities for the mod.
 */
public class InitMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CCReference.MOD_ID);

    public static final RegistryObject<MenuType<MenuRentAcceptor>> RENT_ACCEPTOR = MENU_TYPES.register("rent_acceptor", regBlockMenu(MenuRentAcceptor::new));
    public static final RegistryObject<MenuType<MenuMineGenerator>> MINE_GENERATOR = MENU_TYPES.register("mine_generator", regBlockMenu(MenuMineGenerator::new));

    static <M extends AbstractContainerMenu> Supplier<MenuType<M>> regBlockMenu(CEBlockMenuFactory<M> factory) {
        return () -> new MenuType<>(factory);
    }

    interface CEBlockMenuFactory<M extends AbstractContainerMenu> extends IContainerFactory<M> {

        @Override
        default M create(int windowId, Inventory inv, FriendlyByteBuf data) {
            return create(windowId, inv, data.readBlockPos());
        }

        M create(int id, Inventory inventory, BlockPos pos);
    }
}
