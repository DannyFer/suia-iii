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
 * @author Frank Torres
 */
public class EntityVehiculosDesechoImprimirReporte {

    @Getter
    @Setter
    private String desecho;
    @Getter
    @Setter
    private String capacidad;
    @Getter
    @Setter
    private String tipo;
    @Getter
    @Setter
    private String tipoEmbalaje;
}
