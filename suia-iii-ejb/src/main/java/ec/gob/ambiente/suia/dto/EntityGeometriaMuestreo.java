/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class EntityGeometriaMuestreo implements Serializable {

    private static final long serialVersionUID = 7680906416661920924L;

    @Getter
    @Setter
    private Double[] coordena;
}
