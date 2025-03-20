/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ObservacionResolucionLicencia;
import lombok.Getter;
import lombok.Setter;


public class ObservacionesResolucionLicenciaBean implements Serializable {

	private static final long serialVersionUID = 1L;
    @Getter
    @Setter
    Map<String, List<ObservacionResolucionLicencia>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionResolucionLicencia> listaObservaciones;
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
    private List<ObservacionResolucionLicencia> listaObservacionesHistorico;

    public ObservacionesResolucionLicenciaBean() {
        setListaObservaciones(new ArrayList<ObservacionResolucionLicencia>());
        setMapaSecciones(new HashMap<String, List<ObservacionResolucionLicencia>>());

    }
}
