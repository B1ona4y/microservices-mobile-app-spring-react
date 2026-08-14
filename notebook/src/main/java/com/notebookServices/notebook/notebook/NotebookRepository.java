package com.notebookServices.notebook.notebook;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.synccore.synccore.SyncRepository;

public interface NotebookRepository extends SyncRepository<Notebook> {
    Optional<Notebook> findByIdAndOwner(UUID id, UUID owner);

    List<Notebook> findByOwnerAndDeletedFalse(UUID owner);
}