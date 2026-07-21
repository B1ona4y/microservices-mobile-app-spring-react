package com.notebookServices.notebook.notebook.dto;

import java.util.UUID;

public record NotebookRequest(
    UUID id, String name, long version, boolean deleted) { }