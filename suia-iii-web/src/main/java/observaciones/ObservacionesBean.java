/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observaciones;

import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author christian
 */
public class ObservacionesBean implements Serializable {

    private static final long serialVersionUID = 8401384366315147473L;
    @Getter
    @Setter
    Map<String, List<ObservacionesFormularios>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesFormularios> listaObservaciones;
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
    private List<ObservacionesFormularios> listaObservacionesHistorico;

    public ObservacionesBean() {
        setListaObservaciones(new ArrayList<ObservacionesFormularios>());
        setMapaSecciones(new HashMap<String, List<ObservacionesFormularios>>());

    }
}
