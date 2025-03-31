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
 * @author ishmael
 */
public class EntityAdjunto {

    @Getter
    @Setter
    private byte[] archivo;
    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private String extension;
    @Getter
    @Setter
    private String mimeType;
    @Getter
    @Setter
    private boolean editar;
    @Getter
    @Setter
    private int indice;

    public EntityAdjunto() {
    }

    public EntityAdjunto(String nombre) {
        this.nombre = nombre;
    }
}
