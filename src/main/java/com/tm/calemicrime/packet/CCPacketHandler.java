package com.tm.calemicrime.packet;

import com.tm.calemicrime.main.CCReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CCPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CCReference.MOD_ID, CCReference.MOD_ID),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketRegionProtector.class, PacketRegionProtector::toBytes, PacketRegionProtector::new, PacketRegionProtector::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketRentAcceptor.class, PacketRentAcceptor::toBytes, PacketRentAcceptor::new, PacketRentAcceptor::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketMineGenerator.class, PacketMineGenerator::toBytes, PacketMineGenerator::new, PacketMineGenerator::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketRadiationProjector.class, PacketRadiationProjector::toBytes, PacketRadiationProjector::new, PacketRadiationProjector::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketCalculateRentAcceptorCount.class, PacketCalculateRentAcceptorCount::toBytes, PacketCalculateRentAcceptorCount::new, PacketCalculateRentAcceptorCount::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketSendClientCount.class, PacketSendClientCount::toBytes, PacketSendClientCount::new, PacketSendClientCount::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketCalculateRentAcceptorLimit.class, PacketCalculateRentAcceptorLimit::toBytes, PacketCalculateRentAcceptorLimit::new, PacketCalculateRentAcceptorLimit::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketSendClientLimit.class, PacketSendClientLimit::toBytes, PacketSendClientLimit::new, PacketSendClientLimit::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketSendClientLoot.class, PacketSendClientLoot::toBytes, PacketSendClientLoot::new, PacketSendClientLoot::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketGenerateLoot.class, PacketGenerateLoot::toBytes, PacketGenerateLoot::new, PacketGenerateLoot::handle);
        CCPacketHandler.INSTANCE.registerMessage(++id, PacketGiveLootReward.class, PacketGiveLootReward::toBytes, PacketGiveLootReward::new, PacketGiveLootReward::handle);
    }
}
