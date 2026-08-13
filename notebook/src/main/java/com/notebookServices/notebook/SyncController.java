package com.notebookServices.notebook;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.notebookServices.notebook.dto.CombinedSyncRequest;
import com.notebookServices.notebook.dto.CombinedSyncResponse;
import com.notebookServices.notebook.notebook.NotebookSyncService;
import com.notebookServices.notebook.notebook.pages.PageSyncService;

@RestController
public class SyncController {
    private final NotebookSyncService notebooks;
    private final PageSyncService pages;

    public SyncController(NotebookSyncService notebooks, PageSyncService pages) {
        this.notebooks = notebooks;
        this.pages = pages;
    }

    @PostMapping("/sync")
    public CombinedSyncResponse sync(@RequestBody CombinedSyncRequest req,
                                    @AuthenticationPrincipal Jwt jwt) {
        UUID owner = UUID.fromString(jwt.getSubject());
        return new CombinedSyncResponse(
            notebooks.sync(req.notebooks(), owner),
            pages.sync(req.pages(), owner)
        );
    }
}