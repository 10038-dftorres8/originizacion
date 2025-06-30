package com.banquito.originacion.controller.mapper;

import com.banquito.originacion.controller.dto.*;
import com.banquito.originacion.enums.EstadoDocumentoEnum;
import com.banquito.originacion.model.DocumentoAdjunto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GestionDocumentalMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "solicitudCredito", ignore = true)
    @Mapping(target = "fechaCarga", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "nombreArchivo", source = "nombreArchivo")
    @Mapping(target = "rutaStorage", ignore = true)
    DocumentoAdjunto toDocumentoAdjunto(AlmacenamientoDocumentoDTO dto);
    
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "estado", expression = "java(com.banquito.originacion.enums.EstadoDocumentoEnum.CARGADO.getValor())")
    ReferenciaDocumentoDTO toReferenciaDocumento(DocumentoAdjunto documentoAdjunto);
    
    @Mapping(target = "contenido", ignore = true)
    @Mapping(target = "tipoContenido", ignore = true)
    DocumentoDTO toDocumentoDTO(DocumentoAdjunto documentoAdjunto);
    
    @Mapping(target = "contenido", source = "contenido")
    @Mapping(target = "tipoContenido", ignore = true)
    DocumentoDTO toDocumentoDTOWithContent(DocumentoAdjunto documentoAdjunto, byte[] contenido);
    
    List<ReferenciaDocumentoDTO> toReferenciaDocumentoList(List<DocumentoAdjunto> documentos);
} 