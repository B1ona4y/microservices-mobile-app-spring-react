package com.notebookServices.notebook.notebook.pages;

import java.util.UUID;

import com.notebookServices.notebook.SyncableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;

@Entity
public class Page extends SyncableEntity{
    @Column(nullable = false)
    private UUID notebookId;
    @Size(max = 1500, message = "name must be at most 1500 characters")
    private String content;
}
