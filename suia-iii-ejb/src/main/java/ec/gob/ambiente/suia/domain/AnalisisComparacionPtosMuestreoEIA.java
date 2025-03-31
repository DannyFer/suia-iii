package ec.gob.ambiente.suia.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;

/**
 * Created by M-SIT on 02/11/15.
 */
public class AnalisisComparacionPtosMuestreoEIA {

    @Getter
    @Setter
    @Transient
    private String puntoMuestreoA;

    @Getter
    @Setter
    @Transient
    private String puntoMuestreoB;

    @Getter
    @Setter
    private Double jaccard;

    @Getter
    @Setter
    private Double sorensen;

    @Getter
    @Setter
    private boolean agregado;

}
