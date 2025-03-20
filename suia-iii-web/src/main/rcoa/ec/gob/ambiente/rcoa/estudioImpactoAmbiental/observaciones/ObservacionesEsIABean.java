/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.observaciones;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;

/**
 * @author christian
 */
public class ObservacionesEsIABean implements Serializable {

    private static final long serialVersionUID = 8401384366315147473L;
    @Getter
    @Setter
    Map<String, List<ObservacionesEsIA>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesEsIA> listaObservaciones;
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
    private List<ObservacionesEsIA> listaObservacionesHistorico;

    public ObservacionesEsIABean() {
        setListaObservaciones(new ArrayList<ObservacionesEsIA>());
        setMapaSecciones(new HashMap<String, List<ObservacionesEsIA>>());

    }
}
