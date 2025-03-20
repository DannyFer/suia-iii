/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.viabilidadtecnica.bean;

import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author christian
 */
@ManagedBean
@ViewScoped
public class ObservacionesViabilidadTecnicaBean implements Serializable {

    private static final long serialVersionUID = 8401384366315147473L;
    @Getter
    @Setter
    Map<String, List<ObservacionesFormularios>> mapaSecciones;
    @Getter
    @Setter
    private List<ObservacionesFormularios> listaObservaciones;
    
    @Getter
    @Setter
    private ObservacionesFormularios observacionesformulario;
    
    
    @EJB
    private ObservacionesFacade obsevacionesFacade;
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
    private String tema;
    @Getter
    @Setter
    private String observacion;
    @Getter
    @Setter
    private String nombreTecnico;
    @Getter
    @Setter
    private Boolean requiereInspeccion;
    
    @PostConstruct
  	public void init() {
  		System.out
  				.println("||||||||||||||||||||||||||||observacionesBean");
  		observacionesformulario=new ObservacionesFormularios();
  		//listaObservaciones=new ArrayList<ObservacionesFormularios>();
  		
  		try {
			listaObservaciones = obsevacionesFacade.listarPorIdClaseNombreClase(1, "observaciones");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	}
    
    public ObservacionesViabilidadTecnicaBean() {
        setListaObservaciones(new ArrayList<ObservacionesFormularios>());
        setMapaSecciones(new HashMap<String, List<ObservacionesFormularios>>());
    }
    
	public void cargarObservaciones(ObservacionesFormularios obj)
	{
		
		this.observacionesformulario=obj;
		
	}
   
    
    
    
}