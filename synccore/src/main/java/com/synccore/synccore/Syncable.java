package com.synccore.synccore;

import java.time.Instant;
import java.util.UUID;

public interface Syncable {
    UUID getId();

    String getOwner();
    void setOwner(String owner);

    long getVersion();
    boolean isDeleted();

    Instant getUpdatedAt();
    void setUpdatedAt(Instant updatedAt);
}
