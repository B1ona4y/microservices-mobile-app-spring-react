package com.notebookServices.notebook.notebook.pages;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.synccore.synccore.SyncRepository;

public interface PageRepository extends SyncRepository<Page> {
    Optional<Page> findByIdAndOwner(UUID id, UUID owner);
    List<Page> findByOwnerAndDeletedFalse(UUID owner);
}