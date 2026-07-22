package com.notebookServices.notebook.dto;

import com.notebookServices.notebook.notebook.Notebook;
import com.notebookServices.notebook.notebook.pages.Page;
import com.synccore.synccore.dto.SyncRequest;


public record CombinedSyncRequest(
    SyncRequest<Notebook> notebooks,
    SyncRequest<Page> pages
) {}
