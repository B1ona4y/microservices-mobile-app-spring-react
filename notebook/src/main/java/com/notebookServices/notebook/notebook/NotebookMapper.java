package com.notebookServices.notebook.notebook;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.notebookServices.notebook.notebook.dto.NotebookRequest;
import com.notebookServices.notebook.notebook.dto.NotebookResponse;

@Mapper(componentModel = "spring")
public interface NotebookMapper {
    NotebookResponse toResponse(Notebook notebook);

    List<NotebookResponse> tResponseList(List<Notebook> notebooks);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Notebook toEntity(NotebookRequest request);
}
