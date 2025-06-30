package com.banquito.originacion.enums;

public enum GeneroEnum {
    MASCULINO("masculino"),
    FEMENINO("femenino"),
    OTRO("otro");

    private final String valor;

    GeneroEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static GeneroEnum fromString(String valor) {
        for (GeneroEnum genero : GeneroEnum.values()) {
            if (genero.valor.equalsIgnoreCase(valor)) {
                return genero;
            }
        }
        throw new IllegalArgumentException("Género no válido: " + valor);
    }
} 