package com.banquito.originacion.enums;

public enum TipoDireccionEnum {
    DOMICILIO("domicilio"),
    LABORAL("laboral");

    private final String valor;

    TipoDireccionEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static TipoDireccionEnum fromString(String valor) {
        for (TipoDireccionEnum tipo : TipoDireccionEnum.values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de dirección no válido: " + valor);
    }
} 