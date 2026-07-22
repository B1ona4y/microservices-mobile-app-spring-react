package com.notebookServices.notebook.notebook.pages.dto;

import java.time.Instant;
import java.util.UUID;

public record PageResponse(
    UUID id, UUID notebookId, String content, long version, boolean deleted, Instant updatedAt) { }
