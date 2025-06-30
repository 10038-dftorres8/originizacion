package com.banquito.originacion.controller;

import com.banquito.originacion.controller.dto.*;
import com.banquito.originacion.exception.ResourceNotFoundException;
import com.banquito.originacion.service.OriginacionCreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/originacion")
@Tag(name = "Originación de Crédito", description = "Operaciones relacionadas con el proceso de originación de créditos")
public class OriginacionCreditoController {

    private final OriginacionCreditoService originacionCreditoService;

    public OriginacionCreditoController(OriginacionCreditoService originacionCreditoService) {
        this.originacionCreditoService = originacionCreditoService;
    }

    @PostMapping("/solicitudes")
    @Operation(summary = "Iniciar solicitud de crédito", description = "Inicia el proceso de solicitud de crédito registrando al cliente prospecto y datos básicos del crédito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud iniciada correctamente",
                    content = @Content(schema = @Schema(implementation = SolicitudCreditoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
            @ApiResponse(responseCode = "500", description = "Error al iniciar la solicitud")
    })
    public ResponseEntity<SolicitudCreditoDTO> iniciarSolicitudCredito(@Valid @RequestBody InicioSolicitudDTO solicitud) {
        log.info("Iniciando solicitud de crédito para cliente con cédula: {}", 
                solicitud.getClienteProspecto().getCedula());
        return ResponseEntity.ok(originacionCreditoService.iniciarSolicitudCredito(solicitud));
    }

    @PostMapping("/simulaciones")
    @Operation(summary = "Simular oferta de crédito", description = "Genera escenarios de financiamiento basados en los parámetros proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Simulación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros de simulación inválidos"),
            @ApiResponse(responseCode = "500", description = "Error al realizar la simulación")
    })
    public ResponseEntity<List<EscenarioFinancieroDTO>> simularOfertaCredito(@Valid @RequestBody SimulacionDTO parametros) {
        log.info("Simulando escenarios de crédito para monto: {}", parametros.getMontoSolicitado());
        return ResponseEntity.ok(originacionCreditoService.simularOfertaCredito(parametros));
    }

    @PostMapping("/solicitudes/{idSolicitud}/documentos")
    @Operation(summary = "Adjuntar documentación", description = "Adjunta documentos a una solicitud de crédito existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documentos adjuntados correctamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de documentos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error al adjuntar documentos")
    })
    public ResponseEntity<List<DocumentoAdjuntoDTO>> adjuntarDocumentacion(
            @PathVariable Long idSolicitud,
            @Valid @RequestBody List<DocumentoAdjuntoDTO> documentos) {
        log.info("Adjuntando {} documentos a la solicitud ID: {}", documentos.size(), idSolicitud);
        return ResponseEntity.ok(originacionCreditoService.adjuntarDocumentacion(idSolicitud, documentos));
    }

    @GetMapping("/solicitudes/estado/{numeroSolicitud}")
    @Operation(summary = "Consultar estado de solicitud", description = "Obtiene el estado actual e historial de una solicitud de crédito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente",
                    content = @Content(schema = @Schema(implementation = EstadoSolicitudDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error al consultar el estado")
    })
    public ResponseEntity<EstadoSolicitudDTO> consultarEstadoSolicitud(@PathVariable String numeroSolicitud) {
        log.info("Consultando estado de solicitud número: {}", numeroSolicitud);
        return ResponseEntity.ok(originacionCreditoService.consultarEstadoSolicitud(numeroSolicitud));
    }

    @GetMapping("/solicitudes/pendientes")
    @Operation(summary = "Obtener solicitudes pendientes", description = "Obtiene un listado paginado de solicitudes pendientes con posibilidad de filtrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error al consultar las solicitudes")
    })
    public ResponseEntity<Page<SolicitudCreditoDTO>> obtenerSolicitudesPendientes(
            @Valid FiltroSolicitudesDTO filtros,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("Obteniendo solicitudes pendientes con filtros: {}", filtros);
        return ResponseEntity.ok(originacionCreditoService.obtenerSolicitudesPendientes(filtros, pageable));
    }
} 