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
public class ReporteCompletitudDTO {
    
    private Long idSolicitud;
    private String numeroSolicitud;
    private LocalDateTime fechaValidacion;
    private boolean completo;
    private int totalDocumentosRequeridos;
    private int documentosPresentados;
    private List<DocumentoFaltanteDTO> documentosFaltantes;
    private String observaciones;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentoFaltanteDTO {
        private Long idDocumentoRequerido;
        private String nombreDocumento;
        private String descripcion;
        private boolean obligatorio;
    }
} 