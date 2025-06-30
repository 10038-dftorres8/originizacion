package com.banquito.originacion.service;

import com.banquito.originacion.controller.dto.*;
import com.banquito.originacion.controller.mapper.ClienteProspectoMapper;
import com.banquito.originacion.controller.mapper.DocumentoAdjuntoMapper;
import com.banquito.originacion.controller.mapper.HistorialEstadoMapper;
import com.banquito.originacion.controller.mapper.SolicitudCreditoMapper;
import com.banquito.originacion.enums.EstadoClientesEnum;
import com.banquito.originacion.enums.EstadoSolicitudEnum;
import com.banquito.originacion.exception.CreateEntityException;
import com.banquito.originacion.exception.ResourceNotFoundException;
import com.banquito.originacion.model.ClienteProspecto;
import com.banquito.originacion.model.DocumentoAdjunto;
import com.banquito.originacion.model.HistorialEstado;
import com.banquito.originacion.model.SolicitudCredito;
import com.banquito.originacion.repository.ClienteProspectoRepository;
import com.banquito.originacion.repository.DocumentoAdjuntoRepository;
import com.banquito.originacion.repository.HistorialEstadoRepository;
import com.banquito.originacion.repository.SolicitudCreditoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OriginacionCreditoService {

    private final ClienteProspectoRepository clienteProspectoRepository;
    private final SolicitudCreditoRepository solicitudCreditoRepository;
    private final DocumentoAdjuntoRepository documentoAdjuntoRepository;
    private final HistorialEstadoRepository historialEstadoRepository;
    private final ClienteProspectoMapper clienteProspectoMapper;
    private final SolicitudCreditoMapper solicitudCreditoMapper;
    private final DocumentoAdjuntoMapper documentoAdjuntoMapper;
    private final HistorialEstadoMapper historialEstadoMapper;

    public OriginacionCreditoService(ClienteProspectoRepository clienteProspectoRepository,
                                     SolicitudCreditoRepository solicitudCreditoRepository,
                                     DocumentoAdjuntoRepository documentoAdjuntoRepository,
                                     HistorialEstadoRepository historialEstadoRepository,
                                     ClienteProspectoMapper clienteProspectoMapper,
                                     SolicitudCreditoMapper solicitudCreditoMapper,
                                     DocumentoAdjuntoMapper documentoAdjuntoMapper,
                                     HistorialEstadoMapper historialEstadoMapper) {
        this.clienteProspectoRepository = clienteProspectoRepository;
        this.solicitudCreditoRepository = solicitudCreditoRepository;
        this.documentoAdjuntoRepository = documentoAdjuntoRepository;
        this.historialEstadoRepository = historialEstadoRepository;
        this.clienteProspectoMapper = clienteProspectoMapper;
        this.solicitudCreditoMapper = solicitudCreditoMapper;
        this.documentoAdjuntoMapper = documentoAdjuntoMapper;
        this.historialEstadoMapper = historialEstadoMapper;
    }

    @Transactional
    public SolicitudCreditoDTO iniciarSolicitudCredito(InicioSolicitudDTO solicitud) {
        log.info("Iniciando solicitud de crédito para cliente con cédula: {}", 
                solicitud.getClienteProspecto().getCedula());
        
        ClienteProspectoDTO clienteDTO = solicitud.getClienteProspecto();
        ClienteProspecto cliente;
        
        Optional<ClienteProspecto> clienteExistente = clienteProspectoRepository
                .findByCedula(clienteDTO.getCedula());
        
        if (clienteExistente.isPresent()) {
            log.info("Cliente ya existe, actualizando datos");
            cliente = clienteExistente.get();
            cliente.setNombres(clienteDTO.getNombres());
            cliente.setGenero(clienteDTO.getGenero());
            cliente.setFechaNacimiento(clienteDTO.getFechaNacimiento());
            cliente.setNivelEstudio(clienteDTO.getNivelEstudio());
            cliente.setEstadoCivil(clienteDTO.getEstadoCivil());
            cliente.setIngresos(clienteDTO.getIngresos());
            cliente.setEgresos(clienteDTO.getEgresos());
            cliente.setActividadEconomica(clienteDTO.getActividadEconomica());
            cliente.setCorreoTransaccional(clienteDTO.getCorreoTransaccional());
            cliente.setTelefonoTransaccional(clienteDTO.getTelefonoTransaccional());
            cliente.setTelefonoTipo(clienteDTO.getTelefonoTipo());
            cliente.setTelefonoNumero(clienteDTO.getTelefonoNumero());
            cliente.setDireccionTipo(clienteDTO.getDireccionTipo());
            cliente.setDireccionLinea1(clienteDTO.getDireccionLinea1());
            cliente.setDireccionLinea2(clienteDTO.getDireccionLinea2());
            cliente.setDireccionCodigoPostal(clienteDTO.getDireccionCodigoPostal());
            cliente.setDireccionGeoCodigo(clienteDTO.getDireccionGeoCodigo());
        } else {
            log.info("Creando nuevo cliente prospecto");
            cliente = clienteProspectoMapper.toEntity(clienteDTO);
            cliente.setEstado(EstadoClientesEnum.PROSPECTO.getValor());
        }
        
        cliente = clienteProspectoRepository.save(cliente);
        
        SolicitudCredito solicitudCredito = new SolicitudCredito();
        solicitudCredito.setIdClienteProspecto(cliente.getId());
        solicitudCredito.setIdVehiculo(solicitud.getIdVehiculo());
        solicitudCredito.setIdVendedor(solicitud.getIdVendedor());
        solicitudCredito.setIdProductoCredito(solicitud.getIdProductoCredito());
        solicitudCredito.setMontoSolicitado(solicitud.getMontoSolicitado());
        solicitudCredito.setPlazoMeses(solicitud.getPlazoMeses());
        solicitudCredito.setValorEntrada(solicitud.getValorEntrada());
        solicitudCredito.setFechaSolicitud(LocalDateTime.now());
        solicitudCredito.setEstado(EstadoSolicitudEnum.BORRADOR.getValor());
        
        String numeroSolicitud = "SOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        solicitudCredito.setNumeroSolicitud(numeroSolicitud);
        
        solicitudCredito.setTasaInteresAplicada(BigDecimal.valueOf(16.5));
        solicitudCredito.setCuotaMensualCalculada(calcularCuotaMensual(
                solicitudCredito.getMontoSolicitado(), 
                solicitudCredito.getTasaInteresAplicada(), 
                solicitudCredito.getPlazoMeses()));
        
        try {
            solicitudCredito = solicitudCreditoRepository.save(solicitudCredito);
        } catch (Exception e) {
            log.error("Error al crear solicitud de crédito", e);
            throw new CreateEntityException("SolicitudCredito", "Error al crear la solicitud: " + e.getMessage());
        }
        
        HistorialEstado historial = new HistorialEstado();
        historial.setIdSolicitud(solicitudCredito.getId());
        historial.setEstadoNuevo(EstadoSolicitudEnum.BORRADOR.getValor());
        historial.setFechaCambio(LocalDateTime.now());
        historial.setUsuarioModificacion(1L);
        historial.setMotivo("Creación inicial de solicitud");
        
        historialEstadoRepository.save(historial);
        
        SolicitudCreditoDTO respuesta = solicitudCreditoMapper.toDto(solicitudCredito);
        respuesta.setClienteProspecto(clienteProspectoMapper.toDto(cliente));
        
        return respuesta;
    }

    public List<EscenarioFinancieroDTO> simularOfertaCredito(SimulacionDTO parametros) {
        log.info("Simulando escenarios de crédito para monto: {}", parametros.getMontoSolicitado());
        
        List<EscenarioFinancieroDTO> escenarios = new ArrayList<>();
        
        if (parametros.getMontoSolicitado() == null || 
            parametros.getPlazoMeses() == null || 
            parametros.getValorEntrada() == null) {
            throw new IllegalArgumentException("Todos los parámetros son requeridos para la simulación");
        }
        
        BigDecimal tasaEstandar = parametros.getTasaInteres() != null ? 
                parametros.getTasaInteres() : BigDecimal.valueOf(16.5);
        
        EscenarioFinancieroDTO escenarioEstandar = generarEscenario(
                parametros.getMontoSolicitado(),
                parametros.getPlazoMeses(),
                parametros.getValorEntrada(),
                tasaEstandar,
                parametros.getIngresosMensuales());
        
        escenarios.add(escenarioEstandar);
        
        if (parametros.getPlazoMeses() < 60) {
            Integer plazoExtendido = Math.min(parametros.getPlazoMeses() + 12, 60);
            
            EscenarioFinancieroDTO escenarioPlazoExtendido = generarEscenario(
                    parametros.getMontoSolicitado(),
                    plazoExtendido,
                    parametros.getValorEntrada(),
                    tasaEstandar.add(BigDecimal.valueOf(0.5)),
                    parametros.getIngresosMensuales());
            
            escenarios.add(escenarioPlazoExtendido);
        }
        
        if (parametros.getValorEntrada().compareTo(parametros.getMontoSolicitado().multiply(BigDecimal.valueOf(0.4))) < 0) {
            BigDecimal entradaMayor = parametros.getMontoSolicitado().multiply(BigDecimal.valueOf(0.4));
            
            EscenarioFinancieroDTO escenarioEntradaMayor = generarEscenario(
                    parametros.getMontoSolicitado(),
                    parametros.getPlazoMeses(),
                    entradaMayor,
                    tasaEstandar.subtract(BigDecimal.valueOf(1.0)),
                    parametros.getIngresosMensuales());
            
            escenarios.add(escenarioEntradaMayor);
        }
        
        return escenarios;
    }

    @Transactional
    public List<DocumentoAdjuntoDTO> adjuntarDocumentacion(Long idSolicitud, List<DocumentoAdjuntoDTO> documentos) {
        log.info("Adjuntando {} documentos a la solicitud ID: {}", documentos.size(), idSolicitud);
        
        SolicitudCredito solicitud = solicitudCreditoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de crédito no encontrada con ID: " + idSolicitud));
        
        List<DocumentoAdjunto> documentosGuardados = new ArrayList<>();
        
        for (DocumentoAdjuntoDTO docDTO : documentos) {
            DocumentoAdjunto documento = documentoAdjuntoMapper.toEntity(docDTO);
            documento.setIdSolicitud(idSolicitud);
            documento.setFechaCarga(LocalDateTime.now());
            
            try {
                documentosGuardados.add(documentoAdjuntoRepository.save(documento));
            } catch (Exception e) {
                log.error("Error al guardar documento", e);
                throw new CreateEntityException("DocumentoAdjunto", "Error al guardar documento: " + e.getMessage());
            }
        }
        
        List<DocumentoAdjunto> todosDocumentos = documentoAdjuntoRepository.findByIdSolicitud(idSolicitud);
        
        if (todosDocumentos.size() >= 3 && 
                EstadoSolicitudEnum.BORRADOR.getValor().equals(solicitud.getEstado())) {
            
            solicitud.setEstado(EstadoSolicitudEnum.EN_REVISION.getValor());
            solicitudCreditoRepository.save(solicitud);
            
            HistorialEstado historial = new HistorialEstado();
            historial.setIdSolicitud(idSolicitud);
            historial.setEstadoAnterior(EstadoSolicitudEnum.BORRADOR.getValor());
            historial.setEstadoNuevo(EstadoSolicitudEnum.EN_REVISION.getValor());
            historial.setFechaCambio(LocalDateTime.now());
            historial.setUsuarioModificacion(1L);
            historial.setMotivo("Documentación completa");
            
            historialEstadoRepository.save(historial);
        }
        
        return documentosGuardados.stream()
                .map(documentoAdjuntoMapper::toDto)
                .collect(Collectors.toList());
    }

    public EstadoSolicitudDTO consultarEstadoSolicitud(String numeroSolicitud) {
        log.info("Consultando estado de solicitud número: {}", numeroSolicitud);
        
        SolicitudCredito solicitud = solicitudCreditoRepository.findByNumeroSolicitud(numeroSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con número: " + numeroSolicitud));
        
        List<HistorialEstado> historial = historialEstadoRepository
                .findByIdSolicitudOrderByFechaCambioDesc(solicitud.getId());
        
        ClienteProspecto cliente = clienteProspectoRepository.findById(solicitud.getIdClienteProspecto())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado para la solicitud"));
        
        EstadoSolicitudDTO estadoDTO = new EstadoSolicitudDTO();
        estadoDTO.setIdSolicitud(solicitud.getId());
        estadoDTO.setNumeroSolicitud(solicitud.getNumeroSolicitud());
        estadoDTO.setEstadoActual(solicitud.getEstado());
        
        if (!historial.isEmpty()) {
            HistorialEstado ultimoCambio = historial.get(0);
            estadoDTO.setFechaUltimoCambio(ultimoCambio.getFechaCambio());
            estadoDTO.setMotivoUltimoCambio(ultimoCambio.getMotivo());
        }
        
        estadoDTO.setHistorial(historialEstadoMapper.toDtoList(historial));
        estadoDTO.setClienteProspecto(clienteProspectoMapper.toDto(cliente));
        
        return estadoDTO;
    }

    public Page<SolicitudCreditoDTO> obtenerSolicitudesPendientes(FiltroSolicitudesDTO filtros, Pageable pageable) {
        log.info("Obteniendo solicitudes pendientes con filtros: {}", filtros);
        
        if (filtros.getEstado() != null && !filtros.getEstado().isEmpty()) {
            Page<SolicitudCredito> solicitudesPaginadas = solicitudCreditoRepository
                    .findByEstado(filtros.getEstado(), pageable);
            
            return solicitudesPaginadas.map(solicitud -> {
                SolicitudCreditoDTO dto = solicitudCreditoMapper.toDto(solicitud);
                clienteProspectoRepository.findById(solicitud.getIdClienteProspecto())
                        .ifPresent(cliente -> dto.setClienteProspecto(clienteProspectoMapper.toDto(cliente)));
                return dto;
            });
        }
        
        Page<SolicitudCredito> todasSolicitudes = solicitudCreditoRepository.findAll(pageable);
        
        return todasSolicitudes.map(solicitud -> {
            SolicitudCreditoDTO dto = solicitudCreditoMapper.toDto(solicitud);
            clienteProspectoRepository.findById(solicitud.getIdClienteProspecto())
                    .ifPresent(cliente -> dto.setClienteProspecto(clienteProspectoMapper.toDto(cliente)));
            return dto;
        });
    }

    private BigDecimal calcularCuotaMensual(BigDecimal monto, BigDecimal tasaAnual, Integer plazoMeses) {
        BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        
        BigDecimal numerador = tasaMensual.multiply(
                BigDecimal.ONE.add(tasaMensual).pow(plazoMeses));
        
        BigDecimal denominador = BigDecimal.ONE.add(tasaMensual).pow(plazoMeses)
                .subtract(BigDecimal.ONE);
        
        return monto.multiply(numerador.divide(denominador, 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private EscenarioFinancieroDTO generarEscenario(BigDecimal montoTotal, Integer plazo, 
                                                  BigDecimal entrada, BigDecimal tasa,
                                                  BigDecimal ingresosMensuales) {
        BigDecimal montoFinanciar = montoTotal.subtract(entrada);
        BigDecimal cuotaMensual = calcularCuotaMensual(montoFinanciar, tasa, plazo);
        BigDecimal montoTotalPagar = cuotaMensual.multiply(BigDecimal.valueOf(plazo));
        
        BigDecimal relacionCuotaIngreso = BigDecimal.ZERO;
        if (ingresosMensuales != null && ingresosMensuales.compareTo(BigDecimal.ZERO) > 0) {
            relacionCuotaIngreso = cuotaMensual.divide(ingresosMensuales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        List<EscenarioFinancieroDTO.CuotaDTO> cronograma = generarCronograma(montoFinanciar, tasa, plazo);
        
        return EscenarioFinancieroDTO.builder()
                .montoSolicitado(montoTotal)
                .valorEntrada(entrada)
                .plazoMeses(plazo)
                .tasaInteres(tasa)
                .cuotaMensual(cuotaMensual)
                .montoTotal(montoTotalPagar)
                .costosAdicionales(BigDecimal.valueOf(250))
                .relacionCuotaIngreso(relacionCuotaIngreso)
                .cronograma(cronograma)
                .build();
    }

    private List<EscenarioFinancieroDTO.CuotaDTO> generarCronograma(BigDecimal monto, BigDecimal tasaAnual, Integer plazo) {
        List<EscenarioFinancieroDTO.CuotaDTO> cronograma = new ArrayList<>();
        
        BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        BigDecimal cuotaFija = calcularCuotaMensual(monto, tasaAnual, plazo);
        BigDecimal saldoCapital = monto;
        
        for (int i = 1; i <= plazo; i++) {
            BigDecimal interesCuota = saldoCapital.multiply(tasaMensual).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capitalCuota = cuotaFija.subtract(interesCuota).setScale(2, RoundingMode.HALF_UP);
            saldoCapital = saldoCapital.subtract(capitalCuota).setScale(2, RoundingMode.HALF_UP);
            
            EscenarioFinancieroDTO.CuotaDTO cuota = EscenarioFinancieroDTO.CuotaDTO.builder()
                    .numeroCuota(i)
                    .capital(capitalCuota)
                    .interes(interesCuota)
                    .valorCuota(cuotaFija)
                    .saldoCapital(saldoCapital.max(BigDecimal.ZERO))
                    .build();
            
            cronograma.add(cuota);
            
            if (saldoCapital.compareTo(BigDecimal.valueOf(0.01)) <= 0) {
                break;
            }
        }
        
        return cronograma;
    }
} 