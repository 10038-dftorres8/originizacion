package com.banquito.originacion.controller.mapper;

import com.banquito.originacion.model.SolicitudCredito;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ClienteProspectoMapper.class})
public interface SolicitudCreditoMapper {
    
    @Mapping(target = "estado", source = "estado", qualifiedByName = "solicitudEstadoToString")
    @Mapping(target = "clienteProspecto", source = "clienteProspecto")
    SolicitudCreditoDTO toDto(SolicitudCredito entity);
    
    @Mapping(target = "estado", source = "estado", qualifiedByName = "solicitudStringToEstado")
    @Mapping(target = "clienteProspecto", ignore = true)
    SolicitudCredito toEntity(SolicitudCreditoDTO dto);
    
    List<SolicitudCreditoDTO> toDtoList(List<SolicitudCredito> entities);
    
    @Named("solicitudEstadoToString")
    default String solicitudEstadoToString(String estado) {
        return estado;
    }
    
    @Named("solicitudStringToEstado")
    default String solicitudStringToEstado(String estado) {
        return estado;
    }
} 