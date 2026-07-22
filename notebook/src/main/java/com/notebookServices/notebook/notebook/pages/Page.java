package com.notebookServices.notebook.notebook.pages;

import java.util.UUID;

import com.notebookServices.notebook.SyncableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pages", indexes = {
    @Index(name = "idx_pages_owner_updated", columnList = "owner, updated_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Page extends SyncableEntity {

    @Column(nullable = false)
    private UUID notebookId;

    @Size(max = 1500, message = "content must be at most 1500 characters")
    private String content;
}
