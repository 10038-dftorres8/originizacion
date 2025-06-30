package com.banquito.originacion.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlmacenamientoDocumentoDTO {
    
    private Long idSolicitud;
    private Long idDocumentoRequerido;
    private String nombreArchivo;
    private String tipoContenido;
    private byte[] contenido;
    private String descripcion;
} 