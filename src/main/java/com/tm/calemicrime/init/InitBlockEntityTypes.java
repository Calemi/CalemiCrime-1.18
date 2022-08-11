package com.tm.calemicrime.init;

import com.tm.calemicrime.blockentity.BlockEntityMineGenerator;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.main.CCReference;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Handles setting up the Block Entities for the mod.
 */
public class InitBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CCReference.MOD_ID);

    public static RegistryObject<BlockEntityType<BlockEntityRegionProtector>> REGION_PROTECTOR = BLOCK_ENTITY_TYPES.register(
            "region_protector", () -> BlockEntityType.Builder.of(BlockEntityRegionProtector::new, InitItems.REGION_PROJECTOR.get()).build(null));

    public static RegistryObject<BlockEntityType<BlockEntityRentAcceptor>> RENT_ACCEPTOR = BLOCK_ENTITY_TYPES.register(
            "rent_acceptor", () -> BlockEntityType.Builder.of(BlockEntityRentAcceptor::new, InitItems.RENT_ACCEPTOR.get()).build(null));

    public static RegistryObject<BlockEntityType<BlockEntityMineGenerator>> MINE_GENERATOR = BLOCK_ENTITY_TYPES.register(
            "mine_generator", () -> BlockEntityType.Builder.of(BlockEntityMineGenerator::new, InitItems.MINE_GENERATOR.get()).build(null));
}
