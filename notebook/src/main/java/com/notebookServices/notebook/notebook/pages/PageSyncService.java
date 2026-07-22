package com.notebookServices.notebook.notebook.pages;

import java.util.List;

import org.springframework.stereotype.Service;

import com.synccore.synccore.SyncRepository;
import com.synccore.synccore.SyncService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class PageSyncService extends SyncService<Page> {

    private final PageRepository repo;
    private final Validator validator;

    public PageSyncService(PageRepository repo, Validator validator) {
        this.repo = repo;
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
}
