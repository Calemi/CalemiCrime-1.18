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
    }
}
