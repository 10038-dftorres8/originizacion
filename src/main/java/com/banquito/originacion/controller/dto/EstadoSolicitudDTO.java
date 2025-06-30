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
public class EstadoSolicitudDTO {
    
    private Long idSolicitud;
    private String numeroSolicitud;
    private String estadoActual;
    private LocalDateTime fechaUltimoCambio;
    private String motivoUltimoCambio;
    private List<HistorialEstadoDTO> historial;
    private ClienteProspectoDTO clienteProspecto;
} 