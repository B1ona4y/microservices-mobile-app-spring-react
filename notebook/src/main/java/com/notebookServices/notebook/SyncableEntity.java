package com.notebookServices.notebook;

import java.time.Instant;
import java.util.UUID;

import com.synccore.synccore.Syncable;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class SyncableEntity implements Syncable {
    @Id private UUID id;
    @Column(nullable = false)
    private UUID owner;
    private long version;
    private boolean deleted;
    @Column(nullable = false)
    private Instant updatedAt;
}
