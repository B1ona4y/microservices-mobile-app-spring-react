package com.notebookServices.notebook.dto;

import com.notebookServices.notebook.notebook.Notebook;
import com.notebookServices.notebook.notebook.pages.Page;
import com.synccore.synccore.dto.SyncResponse;


public record CombinedSyncResponse(
    SyncResponse<Notebook> notebooks,
    SyncResponse<Page> pages
) {}
