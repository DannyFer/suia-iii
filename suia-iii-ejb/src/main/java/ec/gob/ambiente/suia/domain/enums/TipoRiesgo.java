package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by juangabriel on 25/06/15.
 */

public enum TipoRiesgo {
    ENDOGENO("Endógeno"), EXOGENO("Exógeno");

    private TipoRiesgo(String descripcion){
        this.descripcion = descripcion;
    }

    @Getter
    @Setter
    private String descripcion;






}
