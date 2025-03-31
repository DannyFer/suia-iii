/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public enum TipoMensajeMailEnum {

    TEXT_HTML("text/html");

    private TipoMensajeMailEnum(String nemonico) {
        this.nemonico = nemonico;
    }

    @Getter
    @Setter
    private String nemonico;
}
