package com.banquito.originacion.enums;

public enum EstadoDocumentoEnum {
    CARGADO("cargado"),
    VALIDADO("validado"),
    RECHAZADO("rechazado"),
    ARCHIVADO("archivado"),
    FIRMADO("firmado"),
    GENERADO("generado");

    private final String valor;

    EstadoDocumentoEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoDocumentoEnum fromString(String valor) {
        for (EstadoDocumentoEnum estado : EstadoDocumentoEnum.values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de documento no válido: " + valor);
    }
} 