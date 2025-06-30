package com.banquito.originacion.service;

import com.banquito.originacion.controller.dto.*;
import com.banquito.originacion.controller.mapper.GestionDocumentalMapper;
import com.banquito.originacion.controller.mapper.SolicitudCreditoMapper;
import com.banquito.originacion.enums.EstadoDocumentoEnum;
import com.banquito.originacion.enums.EstadoSolicitudEnum;
import com.banquito.originacion.enums.TipoDocumentoEnum;
import com.banquito.originacion.exception.CreateEntityException;
import com.banquito.originacion.exception.ResourceNotFoundException;
import com.banquito.originacion.model.DocumentoAdjunto;
import com.banquito.originacion.model.SolicitudCredito;
import com.banquito.originacion.repository.DocumentoAdjuntoRepository;
import com.banquito.originacion.repository.SolicitudCreditoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GestionDocumentalService {

    private final DocumentoAdjuntoRepository documentoAdjuntoRepository;
    private final SolicitudCreditoRepository solicitudCreditoRepository;
    private final GestionDocumentalMapper gestionDocumentalMapper;
    
    private static final String BASE_STORAGE_PATH = "C:/documentos_banquito/";

    public GestionDocumentalService(DocumentoAdjuntoRepository documentoAdjuntoRepository,
                                   SolicitudCreditoRepository solicitudCreditoRepository,
                                   GestionDocumentalMapper gestionDocumentalMapper) {
        this.documentoAdjuntoRepository = documentoAdjuntoRepository;
        this.solicitudCreditoRepository = solicitudCreditoRepository;
        this.gestionDocumentalMapper = gestionDocumentalMapper;
        
        crearDirectorioBase();
    }
    
    private void crearDirectorioBase() {
        File directorio = new File(BASE_STORAGE_PATH);
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                log.info("Directorio base para documentos creado: {}", BASE_STORAGE_PATH);
            } else {
                log.error("No se pudo crear el directorio base para documentos: {}", BASE_STORAGE_PATH);
            }
        }
    }

    @Transactional
    public ReferenciaDocumentoDTO almacenarDocumento(AlmacenamientoDocumentoDTO documento) {
        log.info("Almacenando documento para solicitud ID: {}", documento.getIdSolicitud());
        
        SolicitudCredito solicitud = solicitudCreditoRepository.findById(documento.getIdSolicitud())
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + documento.getIdSolicitud()));
        
        validarDocumento(documento);
        
        DocumentoAdjunto documentoAdjunto = gestionDocumentalMapper.toDocumentoAdjunto(documento);
        
        String rutaStorage = generarRutaAlmacenamiento(documento.getIdSolicitud(), documento.getNombreArchivo());
        documentoAdjunto.setRutaStorage(rutaStorage);
        
        try {
            almacenarContenido(documento.getContenido(), rutaStorage);
            
            String hash = generarHash(documento.getContenido());
            
            documentoAdjunto = documentoAdjuntoRepository.save(documentoAdjunto);
            
            ReferenciaDocumentoDTO referencia = gestionDocumentalMapper.toReferenciaDocumento(documentoAdjunto);
            referencia.setHash(hash);
            
            return referencia;
        } catch (Exception e) {
            log.error("Error al almacenar documento", e);
            throw new CreateEntityException("DocumentoAdjunto", "Error al almacenar documento: " + e.getMessage());
        }
    }

    @Transactional
    public DocumentosGeneradosDTO generarDocumentosContractuales(Long idSolicitud) {
        log.info("Generando documentos contractuales para solicitud ID: {}", idSolicitud);
        
        SolicitudCredito solicitud = solicitudCreditoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + idSolicitud));
        
        if (!EstadoSolicitudEnum.APROBADA.getValor().equals(solicitud.getEstado())) {
            throw new IllegalStateException("No se pueden generar documentos contractuales para una solicitud que no está aprobada");
        }
        
        try {
            List<ReferenciaDocumentoDTO> documentosGenerados = new ArrayList<>();
            
            ReferenciaDocumentoDTO contratoRef = generarContrato(solicitud);
            documentosGenerados.add(contratoRef);
            
            ReferenciaDocumentoDTO pagareRef = generarPagare(solicitud);
            documentosGenerados.add(pagareRef);
            
            return DocumentosGeneradosDTO.builder()
                    .idSolicitud(idSolicitud)
                    .numeroSolicitud(solicitud.getNumeroSolicitud())
                    .fechaGeneracion(LocalDateTime.now())
                    .documentos(documentosGenerados)
                    .estado(EstadoDocumentoEnum.GENERADO.getValor())
                    .build();
            
        } catch (Exception e) {
            log.error("Error al generar documentos contractuales", e);
            throw new RuntimeException("Error al generar documentos contractuales: " + e.getMessage());
        }
    }

    public ReporteCompletitudDTO validarCompletitudDocumental(Long idSolicitud, TipoValidacionDTO tipo) {
        log.info("Validando completitud documental para solicitud ID: {}", idSolicitud);
        
        SolicitudCredito solicitud = solicitudCreditoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + idSolicitud));
        
        List<DocumentoAdjunto> documentosAdjuntos = documentoAdjuntoRepository.findByIdSolicitud(idSolicitud);
        
        int totalRequeridos = 3;
        
        List<ReporteCompletitudDTO.DocumentoFaltanteDTO> faltantes = new ArrayList<>();
        
        if (documentosAdjuntos.size() < totalRequeridos) {
            for (int i = documentosAdjuntos.size(); i < totalRequeridos; i++) {
                faltantes.add(ReporteCompletitudDTO.DocumentoFaltanteDTO.builder()
                        .idDocumentoRequerido((long) (i + 1))
                        .nombreDocumento("Documento requerido " + (i + 1))
                        .descripcion("Descripción del documento requerido " + (i + 1))
                        .obligatorio(true)
                        .build());
            }
        }
        
        return ReporteCompletitudDTO.builder()
                .idSolicitud(idSolicitud)
                .numeroSolicitud(solicitud.getNumeroSolicitud())
                .fechaValidacion(LocalDateTime.now())
                .completo(faltantes.isEmpty())
                .totalDocumentosRequeridos(totalRequeridos)
                .documentosPresentados(documentosAdjuntos.size())
                .documentosFaltantes(faltantes)
                .observaciones(faltantes.isEmpty() ? "Documentación completa" : "Faltan documentos obligatorios")
                .build();
    }

    public DocumentoDTO recuperarDocumento(String referenciaDocumento) {
        log.info("Recuperando documento con referencia: {}", referenciaDocumento);
        
        Long idDocumento = extraerIdDeReferencia(referenciaDocumento);
        
        DocumentoAdjunto documento = documentoAdjuntoRepository.findById(idDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + idDocumento));
        
        try {
            byte[] contenido = recuperarContenido(documento.getRutaStorage());
            
            return gestionDocumentalMapper.toDocumentoDTOWithContent(documento, contenido);
            
        } catch (Exception e) {
            log.error("Error al recuperar documento", e);
            throw new RuntimeException("Error al recuperar documento: " + e.getMessage());
        }
    }

    @Transactional
    public ReferenciaExpedienteDTO archivarExpediente(Long idSolicitud) {
        log.info("Archivando expediente para solicitud ID: {}", idSolicitud);
        
        SolicitudCredito solicitud = solicitudCreditoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + idSolicitud));
        
        List<DocumentoAdjunto> documentos = documentoAdjuntoRepository.findByIdSolicitud(idSolicitud);
        
        if (documentos.isEmpty()) {
            throw new IllegalStateException("No se puede archivar un expediente sin documentos");
        }
        
        try {
            String refExpediente = "EXP-" + solicitud.getNumeroSolicitud() + "-" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            String ubicacion = BASE_STORAGE_PATH + "ARCHIVO/" + refExpediente;
            
            File directorioExpediente = new File(ubicacion);
            if (!directorioExpediente.exists()) {
                directorioExpediente.mkdirs();
            }
            
            List<ReferenciaDocumentoDTO> referencias = gestionDocumentalMapper.toReferenciaDocumentoList(documentos);
            
            return ReferenciaExpedienteDTO.builder()
                    .idSolicitud(idSolicitud)
                    .numeroSolicitud(solicitud.getNumeroSolicitud())
                    .referenciaExpediente(refExpediente)
                    .fechaArchivado(LocalDateTime.now())
                    .ubicacionArchivo(ubicacion)
                    .totalDocumentos(documentos.size())
                    .indiceDocumentos(referencias)
                    .estado(EstadoDocumentoEnum.ARCHIVADO.getValor())
                    .build();
            
        } catch (Exception e) {
            log.error("Error al archivar expediente", e);
            throw new RuntimeException("Error al archivar expediente: " + e.getMessage());
        }
    }
    
    private void validarDocumento(AlmacenamientoDocumentoDTO documento) {
        if (documento.getContenido() == null || documento.getContenido().length == 0) {
            throw new IllegalArgumentException("El contenido del documento no puede estar vacío");
        }
        
        if (documento.getContenido().length > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("El tamaño del documento excede el límite permitido (10MB)");
        }
        
        if (documento.getNombreArchivo() == null || documento.getNombreArchivo().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo es requerido");
        }
    }
    
    private String generarRutaAlmacenamiento(Long idSolicitud, String nombreArchivo) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String rutaRelativa = "solicitud_" + idSolicitud + "/" + uniqueId + "_" + nombreArchivo;
        
        File directorio = new File(BASE_STORAGE_PATH + "solicitud_" + idSolicitud);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        
        return BASE_STORAGE_PATH + rutaRelativa;
    }
    
    private void almacenarContenido(byte[] contenido, String rutaStorage) throws IOException {
        File archivo = new File(rutaStorage);
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(contenido);
            log.info("Documento almacenado en: {}", rutaStorage);
        }
    }
    
    private byte[] recuperarContenido(String rutaStorage) throws IOException {
        Path path = Paths.get(rutaStorage);
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        } else {
            log.warn("El archivo no existe en la ruta: {}", rutaStorage);
            return new byte[0];
        }
    }
    
    private String generarHash(byte[] contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contenido);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Error al generar hash", e);
            return "";
        }
    }
    
    private Long extraerIdDeReferencia(String referencia) {
        try {
            return Long.parseLong(referencia);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Referencia de documento inválida: " + referencia);
        }
    }
    
    private ReferenciaDocumentoDTO generarContrato(SolicitudCredito solicitud) {
        DocumentoAdjunto contrato = new DocumentoAdjunto();
        contrato.setIdSolicitud(solicitud.getId());
        contrato.setIdDocumentoRequerido(1L);
        contrato.setNombreArchivo("Contrato_" + solicitud.getNumeroSolicitud() + ".pdf");
        
        String rutaContrato = BASE_STORAGE_PATH + "solicitud_" + solicitud.getId() + "/contratos/";
        File directorioContratos = new File(rutaContrato);
        if (!directorioContratos.exists()) {
            directorioContratos.mkdirs();
        }
        
        contrato.setRutaStorage(rutaContrato + "Contrato_" + solicitud.getNumeroSolicitud() + ".pdf");
        contrato.setFechaCarga(LocalDateTime.now());
        
        try {
            String contenidoSimulado = "Contrato de crédito para la solicitud " + solicitud.getNumeroSolicitud();
            almacenarContenido(contenidoSimulado.getBytes(), contrato.getRutaStorage());
        } catch (IOException e) {
            log.error("Error al generar archivo de contrato", e);
        }
        
        contrato = documentoAdjuntoRepository.save(contrato);
        
        ReferenciaDocumentoDTO referencia = gestionDocumentalMapper.toReferenciaDocumento(contrato);
        referencia.setEstado(EstadoDocumentoEnum.GENERADO.getValor());
        
        return referencia;
    }
    
    private ReferenciaDocumentoDTO generarPagare(SolicitudCredito solicitud) {
        DocumentoAdjunto pagare = new DocumentoAdjunto();
        pagare.setIdSolicitud(solicitud.getId());
        pagare.setIdDocumentoRequerido(2L);
        pagare.setNombreArchivo("Pagare_" + solicitud.getNumeroSolicitud() + ".pdf");
        
        String rutaPagare = BASE_STORAGE_PATH + "solicitud_" + solicitud.getId() + "/pagares/";
        File directorioPagares = new File(rutaPagare);
        if (!directorioPagares.exists()) {
            directorioPagares.mkdirs();
        }
        
        pagare.setRutaStorage(rutaPagare + "Pagare_" + solicitud.getNumeroSolicitud() + ".pdf");
        pagare.setFechaCarga(LocalDateTime.now());
        
        try {
            String contenidoSimulado = "Pagaré para la solicitud " + solicitud.getNumeroSolicitud();
            almacenarContenido(contenidoSimulado.getBytes(), pagare.getRutaStorage());
        } catch (IOException e) {
            log.error("Error al generar archivo de pagaré", e);
        }
        
        pagare = documentoAdjuntoRepository.save(pagare);
        
        ReferenciaDocumentoDTO referencia = gestionDocumentalMapper.toReferenciaDocumento(pagare);
        referencia.setEstado(EstadoDocumentoEnum.GENERADO.getValor());
        
        return referencia;
    }
} 