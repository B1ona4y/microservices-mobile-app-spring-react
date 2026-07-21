package com.notebookServices.notebook.notebook.pages;

import java.util.List;

import org.mapstruct.Mapping;
import org.springframework.data.domain.PageRequest;

import com.notebookServices.notebook.notebook.pages.dto.PageResponse;


public interface PageMapper {
    PageResponse toResponse(Page page);

    List<PageResponse> tResponseList(List<Page> pages);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Page toEntity(PageRequest request);
}
