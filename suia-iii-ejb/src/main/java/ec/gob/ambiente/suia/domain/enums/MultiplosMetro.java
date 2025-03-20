package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;

/**
 * Created by juangabriel on 14/07/15.
 */

public enum MultiplosMetro {

    METRO("m"), KILOMETRO("km");

    @Getter
    private String simbolo;

    private MultiplosMetro(String simbolo){
        this.simbolo = simbolo;
    }

}
