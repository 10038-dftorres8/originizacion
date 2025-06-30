package com.banquito.originacion.enums;

public enum EstadoCivilEnum {
    SOLTERO("soltero"),
    CASADO("casado"),
    DIVORCIADO("divorciado"),
    VIUDO("viudo"),
    UNION_LIBRE("union_libre");

    private final String valor;

    EstadoCivilEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoCivilEnum fromString(String valor) {
        for (EstadoCivilEnum estado : EstadoCivilEnum.values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado civil no válido: " + valor);
    }
} 