package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Oscar Campana
 */

public enum TipoConformidad {
    CONFORMIDAD("Conformidad"), NO_CON_MEN("No conformidad menor"), NO_CON_MAY("No conformidad mayor");

    private TipoConformidad(String descripcion){
        this.descripcion = descripcion;
    }

    @Getter
    @Setter
    private String descripcion;

}
