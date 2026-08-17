package com.synccore.synccore;

import java.time.Instant;
import java.util.UUID;

public interface Syncable {
    UUID getId();

    UUID getOwner();
    void setOwner(UUID owner);

    long getVersion();
    boolean isDeleted();

    Instant getUpdatedAt();
    void setUpdatedAt(Instant updatedAt);
}
