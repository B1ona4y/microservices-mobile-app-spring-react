package com.notebookServices.notebook.notebook.pages;

import java.util.UUID;

import com.notebookServices.notebook.SyncableEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Page extends SyncableEntity{
    @Id
    private UUID id;
    private UUID notebookId;
    private String content;
}
