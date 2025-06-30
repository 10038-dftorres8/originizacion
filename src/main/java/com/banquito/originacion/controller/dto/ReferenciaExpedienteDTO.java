package com.banquito.originacion.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferenciaExpedienteDTO {
    
    private Long idSolicitud;
    private String numeroSolicitud;
    private String referenciaExpediente;
    private LocalDateTime fechaArchivado;
    private String ubicacionArchivo;
    private int totalDocumentos;
    private List<ReferenciaDocumentoDTO> indiceDocumentos;
    private String estado;
} 