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
public class DocumentosGeneradosDTO {
    
    private Long idSolicitud;
    private String numeroSolicitud;
    private LocalDateTime fechaGeneracion;
    private List<ReferenciaDocumentoDTO> documentos;
    private String estado;
} 