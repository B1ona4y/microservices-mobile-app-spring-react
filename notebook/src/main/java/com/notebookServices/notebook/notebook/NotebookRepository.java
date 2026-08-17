package com.notebookServices.notebook.notebook;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.synccore.synccore.SyncRepository;

public interface NotebookRepository extends SyncRepository<Notebook> {
    Optional<Notebook> findByIdAndOwner(UUID id, String owner);

    List<Notebook> findByOwnerAndDeletedFalse(String owner);

    List<Notebook> findByIdInAndOwnerAndDeletedTrue(Collection<UUID> ids, String owner);
}