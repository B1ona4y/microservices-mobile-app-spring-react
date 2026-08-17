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

    /**
     * Runs on validated incoming rows before they are merged, and may mutate them.
     * Lets a subclass enforce rules this generic engine knows nothing about —
     * for example rejecting or re-flagging a row based on some other entity's state.
     */
    protected void beforeApply(List<T> incoming, String owner) {
        // no-op by default
    }

    /**
     * Runs on the rows that were actually written — not the ones merely offered,
     * since some are dropped on version or ownership. Lets a subclass cascade a
     * change to entities the engine does not know exist.
     */
    protected void afterApply(List<T> saved, String owner) {
        // no-op by default
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
        beforeApply(accepted, owner);
        afterApply(applyChange(accepted, owner), owner);

        int limit = pullLimit();
        List<T> serverChanges = repository().findByOwnerAndUpdatedAtAfterOrderByUpdatedAtAsc(
                owner, request.since(), PageRequest.of(0, limit));

        boolean hasMore = serverChanges.size() == limit;
        Instant newSince = hasMore
                ? serverChanges.get(serverChanges.size() - 1).getUpdatedAt()
                : Instant.now();

        return new SyncResponse<>(serverChanges, newSince, hasMore, rejected);
    }

    private List<T> applyChange(List<T> incoming, String owner) {
        if (incoming.isEmpty()) {
            return List.of();
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
        return toSave;
    }
}
