package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by martin.
 */

public enum TipoCategoriaCuerpoHidrico {
    LOTICO("Lotico"), LENTICO("Lentico");

    private TipoCategoriaCuerpoHidrico(String descripcion) {
        this.descripcion = descripcion;
    }

    @Getter
    @Setter
    private String descripcion;


}
