/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.preliminar.observaciones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.ObservacionesPreliminar;

/**
 * @author christian
 */
public class ObservacionesPreliminarBean implements Serializable {

    private static final long serialVersionUID = 8401384366315147473L;
    @Getter
    @Setter
    Map<String, List<ObservacionesPreliminar>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesPreliminar> listaObservaciones;
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
    private List<ObservacionesPreliminar> listaObservacionesHistorico;

    public ObservacionesPreliminarBean() {
        setListaObservaciones(new ArrayList<ObservacionesPreliminar>());
        setMapaSecciones(new HashMap<String, List<ObservacionesPreliminar>>());

    }
}
