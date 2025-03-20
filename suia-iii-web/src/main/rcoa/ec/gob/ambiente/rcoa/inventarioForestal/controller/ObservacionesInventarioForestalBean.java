/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.gob.ambiente.rcoa.inventarioForestal.model.ObservacionesInventarioForestal;
import lombok.Getter;
import lombok.Setter;


public class ObservacionesInventarioForestalBean implements Serializable {

	private static final long serialVersionUID = 1L;
    @Getter
    @Setter
    Map<String, List<ObservacionesInventarioForestal>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesInventarioForestal> listaObservaciones;
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
    private List<ObservacionesInventarioForestal> listaObservacionesHistorico;

    public ObservacionesInventarioForestalBean() {
        setListaObservaciones(new ArrayList<ObservacionesInventarioForestal>());
        setMapaSecciones(new HashMap<String, List<ObservacionesInventarioForestal>>());

    }
}
