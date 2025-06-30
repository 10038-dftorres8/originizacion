package com.banquito.originacion.controller;

import com.banquito.originacion.controller.dto.*;
import com.banquito.originacion.exception.ResourceNotFoundException;
import com.banquito.originacion.service.GestionDocumentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/gestion-documental")
@Tag(name = "Gestión Documental", description = "Operaciones relacionadas con la gestión de documentos")
public class GestionDocumentalController {

    private final GestionDocumentalService gestionDocumentalService;

    public GestionDocumentalController(GestionDocumentalService gestionDocumentalService) {
        this.gestionDocumentalService = gestionDocumentalService;
    }

    @PostMapping(value = "/documentos", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Almacenar un documento", description = "Almacena un documento en el sistema y lo asocia a una solicitud de crédito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento almacenado correctamente",
                    content = @Content(schema = @Schema(implementation = ReferenciaDocumentoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de documento inválidos"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error al almacenar el documento")
    })
    public ResponseEntity<ReferenciaDocumentoDTO> almacenarDocumento(@Valid @RequestBody AlmacenamientoDocumentoDTO documento) {
        log.info("Almacenando documento para solicitud ID: {}", documento.getIdSolicitud());
        return ResponseEntity.ok(gestionDocumentalService.almacenarDocumento(documento));
    }

    @PostMapping("/documentos/contractuales/{idSolicitud}")
    @Operation(summary = "Generar documentos contractuales", description = "Genera los documentos contractuales para una solicitud de crédito aprobada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documentos generados correctamente",
                    content = @Content(schema = @Schema(implementation = DocumentosGeneradosDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud no está en estado aprobado"),
            @ApiResponse(responseCode = "500", description = "Error al generar los documentos")
    })
    public ResponseEntity<DocumentosGeneradosDTO> generarDocumentosContractuales(@PathVariable Long idSolicitud) {
        log.info("Generando documentos contractuales para solicitud ID: {}", idSolicitud);
        return ResponseEntity.ok(gestionDocumentalService.generarDocumentosContractuales(idSolicitud));
    }

    @PostMapping("/validacion-completitud/{idSolicitud}")
    @Operation(summary = "Validar completitud documental", description = "Valida si una solicitud cuenta con todos los documentos requeridos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validación realizada correctamente",
                    content = @Content(schema = @Schema(implementation = ReporteCompletitudDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error al realizar la validación")
    })
    public ResponseEntity<ReporteCompletitudDTO> validarCompletitudDocumental(
            @PathVariable Long idSolicitud, 
            @Valid @RequestBody TipoValidacionDTO tipo) {
        log.info("Validando completitud documental para solicitud ID: {}", idSolicitud);
        return ResponseEntity.ok(gestionDocumentalService.validarCompletitudDocumental(idSolicitud, tipo));
    }

    @GetMapping("/documentos/{referenciaDocumento}")
    @Operation(summary = "Recuperar documento", description = "Recupera un documento a partir de su referencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento recuperado correctamente",
                    content = @Content(schema = @Schema(implementation = DocumentoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al recuperar el documento")
    })
    public ResponseEntity<DocumentoDTO> recuperarDocumento(@PathVariable String referenciaDocumento) {
        log.info("Recuperando documento con referencia: {}", referenciaDocumento);
        return ResponseEntity.ok(gestionDocumentalService.recuperarDocumento(referenciaDocumento));
    }

    @PostMapping("/expedientes/{idSolicitud}")
    @Operation(summary = "Archivar expediente", description = "Archiva todos los documentos de una solicitud en un expediente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expediente archivado correctamente",
                    content = @Content(schema = @Schema(implementation = ReferenciaExpedienteDTO.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "400", description = "La solicitud no tiene documentos"),
            @ApiResponse(responseCode = "500", description = "Error al archivar el expediente")
    })
    public ResponseEntity<ReferenciaExpedienteDTO> archivarExpediente(@PathVariable Long idSolicitud) {
        log.info("Archivando expediente para solicitud ID: {}", idSolicitud);
        return ResponseEntity.ok(gestionDocumentalService.archivarExpediente(idSolicitud));
    }
} 