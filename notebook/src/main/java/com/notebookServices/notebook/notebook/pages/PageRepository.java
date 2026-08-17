package com.notebookServices.notebook.notebook.pages;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.synccore.synccore.SyncRepository;

public interface PageRepository extends SyncRepository<Page> {
    Optional<Page> findByIdAndOwner(UUID id, UUID owner);
    List<Page> findByOwnerAndDeletedFalse(UUID owner);

    // Only the still-live pages: once they are flagged the cascade finds nothing
    // and stops writing, which is what keeps repeated syncs from churning.
    List<Page> findByNotebookIdInAndOwnerAndDeletedFalse(Collection<UUID> notebookIds, UUID owner);
}