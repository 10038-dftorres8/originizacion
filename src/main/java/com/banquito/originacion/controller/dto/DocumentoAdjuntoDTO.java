package com.banquito.originacion.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoAdjuntoDTO {
    
    private Long id;
    private Long idSolicitud;
    private Long idDocumentoRequerido;
    private String nombreArchivo;
    private String rutaStorage;
    private LocalDateTime fechaCarga;
    
    // Campo adicional para la carga de archivos
    private byte[] contenido;
} 