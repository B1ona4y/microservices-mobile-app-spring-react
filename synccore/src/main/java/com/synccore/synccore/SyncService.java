package com.synccore.synccore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.synccore.synccore.dto.RejectedRecord;
import com.synccore.synccore.dto.SyncRequest;
import com.synccore.synccore.dto.SyncResponse;

import jakarta.transaction.Transactional;

public abstract class SyncService<T extends Syncable> {

    protected abstract SyncRepository<T> repository();

    protected List<String> validate(T entity) {
        return List.of();
    }

    @Transactional
    public SyncResponse<T> sync(SyncRequest<T> request, String owner) {
        List<RejectedRecord> rejected = new ArrayList<>();

        for (T incoming : request.changes()) {
            List<String> errors = validate(incoming);
            if (!errors.isEmpty()) {
                rejected.add(new RejectedRecord(incoming.getId(), errors));
                continue;
            }
            applyChange(incoming, owner);
        }

        Instant now = Instant.now();
        List<T> serverChanges = repository().findByOwnerAndUpdatedAtAfter(owner, request.since());

        return new SyncResponse<>(serverChanges, now, rejected);
    }

    private void applyChange(T incoming, String owner) {
        incoming.setOwner(owner);

        Optional<T> existing = repository().findById(incoming.getId());

        if (existing.isEmpty()) {
            incoming.setUpdatedAt(Instant.now());
            repository().save(incoming);
            return;
        }

        T current = existing.get();

        if (!current.getOwner().equals(owner)) {
            return;
        }

        if (incoming.getVersion() > current.getVersion()) {
            incoming.setUpdatedAt(Instant.now());
            repository().save(incoming);
        }
    }
}
