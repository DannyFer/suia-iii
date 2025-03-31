/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.observaciones;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.ObservacionesAgrupacion;

/**
 * @author christian
 */
public class ObservacionesAgrupacionBean implements Serializable {

    private static final long serialVersionUID = 8401384366315147473L;
    @Getter
    @Setter
    Map<String, List<ObservacionesAgrupacion>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesAgrupacion> listaObservaciones;
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
    private List<ObservacionesAgrupacion> listaObservacionesHistorico;

    public ObservacionesAgrupacionBean() {
        setListaObservaciones(new ArrayList<ObservacionesAgrupacion>());
        setMapaSecciones(new HashMap<String, List<ObservacionesAgrupacion>>());

    }
}
