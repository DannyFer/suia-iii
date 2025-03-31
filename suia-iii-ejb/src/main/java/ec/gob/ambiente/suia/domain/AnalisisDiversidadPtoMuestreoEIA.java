package ec.gob.ambiente.suia.domain;

import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by M-SIT on 02/11/15.
 */

public class AnalisisDiversidadPtoMuestreoEIA {

    @Getter
    @Setter
    @Transient
    private PuntosMuestreoEIA puntosMuestreoEIA;

    @Getter
    @Setter
    private Double shannon;

    @Getter
    @Setter
    private Double simpson;

    @Getter
    @Setter
    private Double riqueza;

    @Getter
    @Setter
    private Double abundancia;

}
