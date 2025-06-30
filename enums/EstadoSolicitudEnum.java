package com.banquito.originacion.enums;

public enum EstadoSolicitudEnum {
    BORRADOR("borrador"),
    EN_REVISION("en_revision"),
    APROBADA("aprobada"),
    RECHAZADA("rechazada"),
    CANCELADA("cancelada");

    private final String valor;

    EstadoSolicitudEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoSolicitudEnum fromString(String valor) {
        for (EstadoSolicitudEnum estado : EstadoSolicitudEnum.values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de solicitud no válido: " + valor);
    }
} 