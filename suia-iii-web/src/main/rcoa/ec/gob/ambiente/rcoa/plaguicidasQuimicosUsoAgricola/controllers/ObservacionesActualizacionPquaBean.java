package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ObservacionesActualizacionPqua;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ObservacionesActualizacionPquaBean {
	
	@Getter
    @Setter
    Map<String, List<ObservacionesActualizacionPqua>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesActualizacionPqua> listaObservaciones;
    @Getter
    @Setter
    private String nombreClase = "";
    @Getter
    @Setter
    private Integer idClase;

    @Getter
    @Setter
    private String[] seccion;

    public ObservacionesActualizacionPquaBean() {
        setListaObservaciones(new ArrayList<ObservacionesActualizacionPqua>());
        setMapaSecciones(new HashMap<String, List<ObservacionesActualizacionPqua>>());
    }

}