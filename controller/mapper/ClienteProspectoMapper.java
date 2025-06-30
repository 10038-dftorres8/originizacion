package com.banquito.originacion.controller.mapper;

import com.banquito.originacion.controller.dto.ClienteProspectoDTO;
import com.banquito.originacion.enums.EstadoClientesEnum;
import com.banquito.originacion.enums.EstadoCivilEnum;
import com.banquito.originacion.enums.GeneroEnum;
import com.banquito.originacion.enums.TipoDireccionEnum;
import com.banquito.originacion.enums.TipoTelefonoEnum;
import com.banquito.originacion.model.ClienteProspecto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteProspectoMapper {
    
    @Mapping(target = "estado", source = "estado", qualifiedByName = "estadoToString")
    @Mapping(target = "genero", source = "genero", qualifiedByName = "generoToString")
    @Mapping(target = "estadoCivil", source = "estadoCivil", qualifiedByName = "estadoCivilToString")
    @Mapping(target = "telefonoTipo", source = "telefonoTipo", qualifiedByName = "tipoTelefonoToString")
    @Mapping(target = "direccionTipo", source = "direccionTipo", qualifiedByName = "tipoDireccionToString")
    ClienteProspectoDTO toDto(ClienteProspecto entity);
    
    @Mapping(target = "estado", source = "estado", qualifiedByName = "stringToEstado")
    @Mapping(target = "genero", source = "genero", qualifiedByName = "stringToGenero")
    @Mapping(target = "estadoCivil", source = "estadoCivil", qualifiedByName = "stringToEstadoCivil")
    @Mapping(target = "telefonoTipo", source = "telefonoTipo", qualifiedByName = "stringToTipoTelefono")
    @Mapping(target = "direccionTipo", source = "direccionTipo", qualifiedByName = "stringToTipoDireccion")
    ClienteProspecto toEntity(ClienteProspectoDTO dto);
    
    List<ClienteProspectoDTO> toDtoList(List<ClienteProspecto> entities);
    
    @Named("estadoToString")
    default String estadoToString(String estado) {
        return estado;
    }
    
    @Named("stringToEstado")
    default String stringToEstado(String estado) {
        return estado;
    }
    
    @Named("generoToString")
    default String generoToString(String genero) {
        return genero;
    }
    
    @Named("stringToGenero")
    default String stringToGenero(String genero) {
        return genero;
    }
    
    @Named("estadoCivilToString")
    default String estadoCivilToString(String estadoCivil) {
        return estadoCivil;
    }
    
    @Named("stringToEstadoCivil")
    default String stringToEstadoCivil(String estadoCivil) {
        return estadoCivil;
    }
    
    @Named("tipoTelefonoToString")
    default String tipoTelefonoToString(String tipoTelefono) {
        return tipoTelefono;
    }
    
    @Named("stringToTipoTelefono")
    default String stringToTipoTelefono(String tipoTelefono) {
        return tipoTelefono;
    }
    
    @Named("tipoDireccionToString")
    default String tipoDireccionToString(String tipoDireccion) {
        return tipoDireccion;
    }
    
    @Named("stringToTipoDireccion")
    default String stringToTipoDireccion(String tipoDireccion) {
        return tipoDireccion;
    }
} 