package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Oscar Campana
 */

public enum NivelIdentificacion {
    Cf("cf."), Aff("Aff."), SpNov("Sp.Nov.");

    private NivelIdentificacion(String descripcion){
        this.descripcion = descripcion;
    }

    @Getter
    @Setter
    private String descripcion;

}
