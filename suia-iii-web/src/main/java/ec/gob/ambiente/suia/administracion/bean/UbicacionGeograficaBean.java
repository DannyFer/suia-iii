/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ishmael
 */
public class UbicacionGeograficaBean implements Serializable {
    
    
    private static final long serialVersionUID = 3275869834786406236L;

    @Getter
    @Setter
    private List<UbicacionesGeografica> listaProvincia;

    @Getter
    @Setter
    private List<UbicacionesGeografica> listaCanton;

    @Getter
    @Setter
    private List<UbicacionesGeografica> listaParroquia;

    @Getter
    @Setter
    private String idProvincia;

    @Getter
    @Setter
    private String idCanton;

    @Getter
    @Setter
    private String idParroquia;
    
}
