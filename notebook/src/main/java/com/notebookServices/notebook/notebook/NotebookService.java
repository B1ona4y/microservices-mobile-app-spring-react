package com.notebookServices.notebook.notebook;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class NotebookService {

    private final NotebookRepository notebookRepository;

    public NotebookService(NotebookRepository notebookRepository) {
        this.notebookRepository = notebookRepository;
    }

    public Optional<Notebook> findByIdAndOwner(UUID id, UUID owner) {
        return notebookRepository.findByIdAndOwner(id, owner);
    }

    public List<Notebook> findByOwnerAndDeletedFalse(UUID owner) {
        return notebookRepository.findByOwnerAndDeletedFalse(owner);
    }
}
