package com.notebookServices.notebook.notebook.pages.dto;

import java.util.UUID;

public record PageRequest(
    UUID id, UUID notebookId, String content, long version, boolean deleted) { }
