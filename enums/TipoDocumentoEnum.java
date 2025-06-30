package com.banquito.originacion.enums;

public enum TipoDocumentoEnum {
    IDENTIFICACION("identificacion"),
    INGRESOS("ingresos"),
    DOMICILIO("domicilio"),
    CONTRATO("contrato"),
    PAGARE("pagare"),
    SOLICITUD("solicitud"),
    OTRO("otro");

    private final String valor;

    TipoDocumentoEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static TipoDocumentoEnum fromString(String valor) {
        for (TipoDocumentoEnum tipo : TipoDocumentoEnum.values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de documento no válido: " + valor);
    }
} 