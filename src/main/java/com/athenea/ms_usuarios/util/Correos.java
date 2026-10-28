package com.athenea.ms_usuarios.util;

import java.util.Locale;

public final class Correos {

    private Correos() {
    }

    public static String normalizar(String correo) {
        if (correo == null || correo.isBlank()) {
            return null;
        }
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
