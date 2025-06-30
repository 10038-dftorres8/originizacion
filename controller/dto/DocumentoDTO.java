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
public class DocumentoDTO {
    
    private Long id;
    private Long idSolicitud;
    private Long idDocumentoRequerido;
    private String nombreArchivo;
    private String tipoContenido;
    private byte[] contenido;
    private String rutaStorage;
    private LocalDateTime fechaCarga;
    private String hash;
} 