package com.banquito.originacion.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudCreditoDTO {
    
    private Long id;
    private String numeroSolicitud;
    private Long idClienteProspecto;
    private Long idVehiculo;
    private Long idVendedor;
    private Long idProductoCredito;
    private BigDecimal montoSolicitado;
    private Integer plazoMeses;
    private BigDecimal valorEntrada;
    private BigDecimal tasaInteresAplicada;
    private BigDecimal cuotaMensualCalculada;
    private LocalDateTime fechaSolicitud;
    private String estado;
    
    // Datos adicionales para mostrar en la respuesta
    private ClienteProspectoDTO clienteProspecto;
} 