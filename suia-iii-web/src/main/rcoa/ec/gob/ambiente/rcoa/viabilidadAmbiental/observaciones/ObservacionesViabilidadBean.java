/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.viabilidadAmbiental.observaciones;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;

/**
 * @author christian
 */
public class ObservacionesViabilidadBean implements Serializable {

    private static final long serialVersionUID = 8401384366315147473L;
    @Getter
    @Setter
    Map<String, List<ObservacionesViabilidad>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesViabilidad> listaObservaciones;
    @Getter
    @Setter
    private String nombreClase = "";
    @Getter
    @Setter
    private Integer idClase;

    @Getter
    @Setter
    private String[] seccion;
    
    @Getter
    @Setter
    private List<ObservacionesViabilidad> listaObservacionesHistorico;

    public ObservacionesViabilidadBean() {
        setListaObservaciones(new ArrayList<ObservacionesViabilidad>());
        setMapaSecciones(new HashMap<String, List<ObservacionesViabilidad>>());

    }
}
