/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.ActividadesImpactoProyecto;

/**
 *
 * @author bburbano
 */
public class EntityActividad {

    @Getter
    @Setter
    private String actividad;
    @Getter
    @Setter
    private List<ActividadesImpactoProyecto> subactividades;
}
