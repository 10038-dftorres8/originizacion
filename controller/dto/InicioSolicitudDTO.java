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
public class InicioSolicitudDTO {
    
    // Datos del cliente prospecto
    private ClienteProspectoDTO clienteProspecto;
    
    // Datos básicos de la solicitud
    private Long idVehiculo;
    private Long idVendedor;
    private Long idProductoCredito;
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal valorEntrada;
} 