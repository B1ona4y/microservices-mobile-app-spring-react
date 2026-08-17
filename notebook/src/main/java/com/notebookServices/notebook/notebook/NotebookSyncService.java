package com.notebookServices.notebook.notebook;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.notebookServices.notebook.notebook.pages.Page;
import com.notebookServices.notebook.notebook.pages.PageRepository;
import com.synccore.synccore.SyncRepository;
import com.synccore.synccore.SyncService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class NotebookSyncService extends SyncService<Notebook> {

    private final NotebookRepository repo;
    private final PageRepository pages;
    private final Validator validator;

    public NotebookSyncService(NotebookRepository repo, PageRepository pages, Validator validator) {
        this.repo = repo;
        this.pages = pages;
        this.validator = validator;
    }

    @Override
    protected SyncRepository<Notebook> repository() { return repo; }

    @Override
    protected List<String> validate(Notebook notebook) {
        return validator.validate(notebook).stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }

    /**
     * Soft-deletes the pages of every notebook that was just deleted.
     *
     * Runs on the rows that were actually written, so a notebook dropped on
     * version or ownership never takes its pages down with it.
     */
    @Override
    protected void afterApply(List<Notebook> saved, String owner) {
        List<UUID> deletedNotebookIds = saved.stream()
                .filter(Notebook::isDeleted)
                .map(Notebook::getId)
                .toList();

        if (deletedNotebookIds.isEmpty()) {
            return;
        }

        List<Page> orphans = pages.findByNotebookIdInAndOwnerAndDeletedFalse(deletedNotebookIds, owner);
        if (orphans.isEmpty()) {
            return;
        }

        Instant now = Instant.now();
        for (Page page : orphans) {
            page.setDeleted(true);
            // The version has to grow, or last-write-wins on the client drops
            // this update as stale and the page silently stays alive there.
            page.setVersion(page.getVersion() + 1);
            page.setUpdatedAt(now);
        }
        pages.saveAll(orphans);
    }
}
