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
public class ReferenciaDocumentoDTO {
    
    private Long id;
    private Long idSolicitud;
    private String nombreArchivo;
    private String rutaStorage;
    private String hash;
    private LocalDateTime fechaCarga;
    private String estado;
} 