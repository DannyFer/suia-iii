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
 * @author frank torres
 */
public class EntityHerramientaMineria {

    @Getter
    @Setter
    private String tipoObtencion;
    @Getter
    @Setter
    private String proceso;
    @Getter
    @Setter
    private String materiales;
    @Getter
    @Setter
    private String cantidad;
}
