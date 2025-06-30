package com.banquito.originacion.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulacionDTO {
    
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal valorEntrada;
    private BigDecimal tasaInteres;
    private BigDecimal ingresosMensuales; // Para calcular capacidad de pago
} 