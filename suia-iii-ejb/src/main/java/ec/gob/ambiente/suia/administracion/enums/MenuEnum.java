/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.enums;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public enum MenuEnum {

    MENU_SUIA("MENU_SUIA");

    private MenuEnum(String nemonico) {
        this.nemonico = nemonico;
    }

    @Getter
    @Setter
    private String nemonico;

}
