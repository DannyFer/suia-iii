package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ObservacionesFormulariosRSQ;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObservacionesRSQBean implements Serializable {

    private static final long serialVersionUID = 8401384366315147473L;
    @Getter
    @Setter
    Map<String, List<ObservacionesFormulariosRSQ>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesFormulariosRSQ> listaObservaciones;
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
    private List<ObservacionesFormulariosRSQ> listaObservacionesHistorico;

    public ObservacionesRSQBean() {
        setListaObservaciones(new ArrayList<ObservacionesFormulariosRSQ>());
        setMapaSecciones(new HashMap<String, List<ObservacionesFormulariosRSQ>>());

    }
}
