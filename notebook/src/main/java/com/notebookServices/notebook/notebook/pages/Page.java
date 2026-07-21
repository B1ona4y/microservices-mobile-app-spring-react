package com.notebookServices.notebook.notebook.pages;

import java.util.UUID;

import com.notebookServices.notebook.SyncableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Page extends SyncableEntity{
    @Column(nullable = false)
    private UUID notebookId;
    private String content;
}
