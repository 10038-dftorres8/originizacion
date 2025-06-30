package com.banquito.originacion.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EscenarioFinancieroDTO {
    
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal valorEntrada;
    private BigDecimal tasaInteres;
    private BigDecimal cuotaMensual;
    private BigDecimal montoTotal;
    private BigDecimal costosAdicionales;
    private BigDecimal relacionCuotaIngreso;
    private List<CuotaDTO> cronograma;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CuotaDTO {
        private Integer numeroCuota;
        private BigDecimal capital;
        private BigDecimal interes;
        private BigDecimal valorCuota;
        private BigDecimal saldoCapital;
    }
} 