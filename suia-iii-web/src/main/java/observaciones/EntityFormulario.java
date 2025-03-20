/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observaciones;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * @author christian
 */
public class EntityFormulario implements Serializable {
    private static final long serialVersionUID = 2584683669324093077L;
    
    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private String apellido;
    @Getter
    @Setter
    private String cedula;
    @Getter
    @Setter
    private String combo;
    
    
}
