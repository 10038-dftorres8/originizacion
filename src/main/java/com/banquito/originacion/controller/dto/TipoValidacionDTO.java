package com.banquito.originacion.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TipoValidacionDTO {
    
    private String tipoValidacion;
    private boolean validarCalidad;
    private boolean validarLegibilidad;
    private boolean validarFirmas;
} 