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
public enum RolEnum {

    SUJETO_CONTROL_PROPONENTE("sujeto de control");

    @Getter
    @Setter
    private String nemonico;

    private RolEnum(String nemonico) {
        this.nemonico = nemonico;
    }
}
