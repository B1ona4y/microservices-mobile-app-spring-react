package com.synccore.synccore;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SyncRepository<T extends Syncable> extends JpaRepository<T, UUID> {
    List<T> findByOwnerAndUpdatedAtAfter(String owner, Instant since);
}
