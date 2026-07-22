package com.notebookServices.notebook.notebook.dto;

import java.time.Instant;
import java.util.UUID;

public record NotebookResponse(
    UUID id, String name, long version, boolean deleted, Instant updatedAt) { }
