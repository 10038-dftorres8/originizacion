package com.banquito.originacion.enums;

public enum TipoTelefonoEnum {
    CELULAR("celular"),
    RESIDENCIAL("residencial"),
    LABORAL("laboral");

    private final String valor;

    TipoTelefonoEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static TipoTelefonoEnum fromString(String valor) {
        for (TipoTelefonoEnum tipo : TipoTelefonoEnum.values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de teléfono no válido: " + valor);
    }
} 