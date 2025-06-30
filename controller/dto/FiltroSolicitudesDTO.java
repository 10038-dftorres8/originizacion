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
public class FiltroSolicitudesDTO {
    
    private String estado;
    private Long idClienteProspecto;
    private Long idVendedor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
} 