package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;

/**
 * Created by juangabriel on 14/07/15.
 */

public enum MultiplosArea {

    METRO("m2"), KILOMETRO("km2"),HECTAREA("ha");

    @Getter
    private String simbolo;

    private MultiplosArea(String simbolo){
        this.simbolo = simbolo;
    }

}
