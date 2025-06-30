package com.banquito.originacion.enums;

public enum EstadoClientesEnum {
    PROSPECTO("prospecto"),
    ACTIVO("activo"),
    INACTIVO("inactivo");

    private final String valor;

    EstadoClientesEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoClientesEnum fromString(String valor) {
        for (EstadoClientesEnum estado : EstadoClientesEnum.values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de cliente no válido: " + valor);
    }
} 