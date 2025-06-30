package com.banquito.originacion.controller.mapper;

import com.banquito.originacion.model.HistorialEstado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistorialEstadoMapper {
    
    @Mapping(target = "estadoAnterior", source = "estadoAnterior")
    @Mapping(target = "estadoNuevo", source = "estadoNuevo")
    HistorialEstadoDTO toDto(HistorialEstado entity);
    
    @Mapping(target = "solicitudCredito", ignore = true)
    HistorialEstado toEntity(HistorialEstadoDTO dto);
    
    List<HistorialEstadoDTO> toDtoList(List<HistorialEstado> entities);
} 