package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Oscar Campana
 */

public enum Periodicidad {
    MENSUAL("Mensual"), TRIMESTRAL("Trimestral"), SEMESTRAL("Semestral"), ANUAL("Anual");

    private Periodicidad(String descripcion){
        this.descripcion = descripcion;
    }

    @Getter
    @Setter
    private String descripcion;

}
