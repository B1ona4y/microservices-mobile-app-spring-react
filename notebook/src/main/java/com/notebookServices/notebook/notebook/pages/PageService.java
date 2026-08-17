package com.notebookServices.notebook.notebook.pages;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PageService {
    
    private final PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Optional<Page> findByIdAndOwner(UUID id, UUID owner) {
        return pageRepository.findByIdAndOwner(id, owner);
    }

    public List<Page> findByOwnerAndDeletedFalse(UUID owner) {
        return pageRepository.findByOwnerAndDeletedFalse(owner);
    }
}
