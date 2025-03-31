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
public class EntityPlaguicidasImprimirReporte  {

    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private String categoria;
    @Getter
    @Setter
    private String dosis;
    @Getter
    @Setter
    private String frecuencia;
    @Getter
    @Setter
    private String fecha;
    @Getter
    @Setter
    private String presentacion;
}
