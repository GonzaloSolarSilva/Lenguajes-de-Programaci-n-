package com.saborcriollo.pedidos.model.enums;

import java.time.LocalTime;

/** Turno de trabajo del restaurante. */
public enum Turno {
    MANANA, TARDE, NOCHE;

    /** Determina el turno que corresponde a una hora del día. */
    public static Turno deHora(LocalTime hora) {
        int h = hora.getHour();
        if (h < 15) {
            return MANANA;
        }
        if (h < 20) {
            return TARDE;
        }
        return NOCHE;
    }
}
