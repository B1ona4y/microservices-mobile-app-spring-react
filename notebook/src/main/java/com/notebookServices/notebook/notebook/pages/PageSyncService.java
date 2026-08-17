package com.notebookServices.notebook.notebook.pages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.notebookServices.notebook.notebook.Notebook;
import com.notebookServices.notebook.notebook.NotebookRepository;
import com.synccore.synccore.SyncRepository;
import com.synccore.synccore.SyncService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class PageSyncService extends SyncService<Page> {

    private final PageRepository repo;
    private final NotebookRepository notebooks;
    private final Validator validator;

    public PageSyncService(PageRepository repo, NotebookRepository notebooks, Validator validator) {
        this.repo = repo;
        this.notebooks = notebooks;
        this.validator = validator;
    }

    @Override
    protected SyncRepository<Page> repository() {
        return repo;
    }

    @Override
    protected List<String> validate(Page page) {
        return validator.validate(page).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }

    /**
     * Accepts a page whose notebook is already deleted, but stores it as deleted.
     *
     * Covers the client that went offline, created a page in a notebook someone
     * deleted meanwhile, and only now pushes it: without this the page would
     * arrive alive and outlive its notebook forever.
     */
    @Override
    protected void beforeApply(List<Page> incoming, String owner) {
        if (incoming.isEmpty()) {
            return;
        }

        Set<UUID> notebookIds = new HashSet<>();
        for (Page page : incoming) {
            if (page.getNotebookId() != null) {
                notebookIds.add(page.getNotebookId());
            }
        }
        if (notebookIds.isEmpty()) {
            return;
        }

        Set<UUID> deletedNotebookIds = new HashSet<>();
        for (Notebook notebook : notebooks.findByIdInAndOwnerAndDeletedTrue(notebookIds, owner)) {
            deletedNotebookIds.add(notebook.getId());
        }
        if (deletedNotebookIds.isEmpty()) {
            return;
        }

        for (Page page : incoming) {
            if (deletedNotebookIds.contains(page.getNotebookId())) {
                page.setDeleted(true);
            }
        }
    }
}
