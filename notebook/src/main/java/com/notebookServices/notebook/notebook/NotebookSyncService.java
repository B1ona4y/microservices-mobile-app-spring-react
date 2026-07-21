package com.notebookServices.notebook.notebook;

import java.util.List;

import org.springframework.stereotype.Service;

import com.synccore.synccore.SyncRepository;
import com.synccore.synccore.SyncService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class NotebookSyncService extends SyncService<Notebook> {

    private final NotebookRepository repo;
    private final Validator validator;

    public NotebookSyncService(NotebookRepository repo, Validator validator) {
        this.repo = repo;
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
}
