package com.banquito.originacion.controller.mapper;

import com.banquito.originacion.model.DocumentoAdjunto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentoAdjuntoMapper {
    
    @Mapping(target = "contenido", ignore = true)
    DocumentoAdjuntoDTO toDto(DocumentoAdjunto entity);
    
    @Mapping(target = "solicitudCredito", ignore = true)
    DocumentoAdjunto toEntity(DocumentoAdjuntoDTO dto);
    
    List<DocumentoAdjuntoDTO> toDtoList(List<DocumentoAdjunto> entities);
} 