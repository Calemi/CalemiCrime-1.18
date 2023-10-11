package com.tm.calemicrime.accessor;

import java.util.UUID;

public interface VehicleAccessor {

    UUID getOwnerID();
    void setOwnerID(UUID value);
}
