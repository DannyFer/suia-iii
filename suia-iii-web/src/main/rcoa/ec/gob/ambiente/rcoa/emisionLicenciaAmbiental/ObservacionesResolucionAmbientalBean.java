package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ObservacionesResolucionAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ObservacionesInventarioForestal;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ObservacionesResolucionAmbientalBean {
	
	@Getter
    @Setter
    Map<String, List<ObservacionesResolucionAmbiental>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesResolucionAmbiental> listaObservaciones;
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

    public ObservacionesResolucionAmbientalBean() {
        setListaObservaciones(new ArrayList<ObservacionesResolucionAmbiental>());
        setMapaSecciones(new HashMap<String, List<ObservacionesResolucionAmbiental>>());
    }

}