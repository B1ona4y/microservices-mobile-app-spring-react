package com.notebookServices.notebook.notebook;

import com.notebookServices.notebook.SyncableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notebooks", indexes = {
    @Index(name = "idx_notebooks_owner_updated", columnList = "owner, updated_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Notebook extends SyncableEntity {
    @NotBlank(message = "name must not be blank")
    @Size(max = 256, message = "name must be at most 256 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N} _\\-.,!?()]+$", message = "name contains invalid characters")
    @Column(nullable = false)
    private String name;
}
