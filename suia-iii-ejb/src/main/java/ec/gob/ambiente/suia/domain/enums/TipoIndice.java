package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by martin.
 */

public enum TipoIndice {
    BAJA("Baja"), MEADIA("Media"), ALTA("Alta");

    private TipoIndice(String descripcion) {
        this.descripcion = descripcion;
    }

    @Getter
    @Setter
    private String descripcion;


}
