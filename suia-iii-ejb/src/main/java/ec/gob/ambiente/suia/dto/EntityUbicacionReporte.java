/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class EntityUbicacionReporte {

    @Getter
    @Setter
    private String provincia;
    @Getter
    @Setter
    private String canton;
    @Getter
    @Setter
    private String parroquia;
}
