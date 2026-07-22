package com.synccore.synccore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;

import com.synccore.synccore.dto.RejectedRecord;
import com.synccore.synccore.dto.SyncRequest;
import com.synccore.synccore.dto.SyncResponse;

import jakarta.transaction.Transactional;

public abstract class SyncService<T extends Syncable> {

    protected abstract SyncRepository<T> repository();

    protected List<String> validate(T entity) {
        return List.of();
    }

    protected int pullLimit() {
        return 500;
    }

    @Transactional
    public SyncResponse<T> sync(SyncRequest<T> request, String owner) {
        List<T> accepted = new ArrayList<>();
        List<RejectedRecord> rejected = new ArrayList<>();

        for (T incoming : request.changes()) {
            List<String> errors = validate(incoming);
            if (!errors.isEmpty()) {
                rejected.add(new RejectedRecord(incoming.getId(), errors));
                continue;
            }
            accepted.add(incoming);
        }
        applyChange(accepted, owner);

        int limit = pullLimit();
        List<T> serverChanges = repository().findByOwnerAndUpdatedAtAfterOrderByUpdatedAtAsc(
                owner, request.since(), PageRequest.of(0, limit));

        boolean hasMore = serverChanges.size() == limit;
        Instant newSince = hasMore
                ? serverChanges.get(serverChanges.size() - 1).getUpdatedAt()
                : Instant.now();

        return new SyncResponse<>(serverChanges, newSince, hasMore, rejected);
    }

    private void applyChange(List<T> incoming, String owner) {
        if (incoming.isEmpty()) {
            return;
        }

        List<UUID> ids = incoming.stream().map(Syncable::getId).toList();

        Map<UUID, T> existing = new HashMap<>();
        for (T found : repository().findAllById(ids)) {
            existing.put(found.getId(), found);
        }

        Instant now = Instant.now();
        List<T> toSave = new ArrayList<>();

        for (T candidate : incoming) {
            T current = existing.get(candidate.getId());

            if (current != null) {
                if (!owner.equals(current.getOwner())) {
                    continue;
                }
                if (candidate.getVersion() <= current.getVersion()) {
                    continue;
                }
            }

            candidate.setOwner(owner);
            candidate.setUpdatedAt(now);
            toSave.add(candidate);
        }

        repository().saveAll(toSave);
    }
}
