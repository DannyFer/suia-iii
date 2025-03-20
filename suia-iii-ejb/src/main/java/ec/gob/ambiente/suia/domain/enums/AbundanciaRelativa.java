package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * Oscar Campana
 */

public enum AbundanciaRelativa {
    Abundante("Abundante"), Comun("Común"), PocoComun("Poco común"), Raro("Raro");

    private AbundanciaRelativa(String descripcion){
        this.descripcion = descripcion;
    }

    @Getter
    @Setter
    private String descripcion;

}
