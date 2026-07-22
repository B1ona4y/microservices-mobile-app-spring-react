package com.notebookServices.notebook.notebook.pages;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.notebookServices.notebook.notebook.pages.dto.PageRequest;
import com.notebookServices.notebook.notebook.pages.dto.PageResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PageMapper {

    PageResponse toResponse(Page page);

    List<PageResponse> toResponseList(List<Page> pages);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Page toEntity(PageRequest request);
}
