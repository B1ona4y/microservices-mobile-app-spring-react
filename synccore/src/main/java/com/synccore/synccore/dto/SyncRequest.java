package com.synccore.synccore.dto;

import java.time.Instant;
import java.util.List;

import com.synccore.synccore.Syncable;

public record SyncRequest<T extends Syncable>(
    List<T> changes,
    Instant since
) {}
