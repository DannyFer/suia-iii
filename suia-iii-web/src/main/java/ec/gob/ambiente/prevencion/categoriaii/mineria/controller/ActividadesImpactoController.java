/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.ActividadesImpactoProyecto;
//import ec.gob.ambiente.suia.domain.CatalogoGeneralFisico;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.dto.EntityActividad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ActividadesImpactoFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * @author bburbano
 */
@ManagedBean
@ViewScoped
public class ActividadesImpactoController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7332827533098349739L;
    
    @EJB
    private ActividadesImpactoFacade actividadesImpactoFacade;
    
	private boolean guardar;

    @Getter
    @Setter
	private Integer exploracionPetroleraId;

    @Getter
    @Setter
    private List<EntityActividad> listaActividades;

    @Getter
    @Setter
    private Map<String, List<ActividadesImpactoProyecto>> listaActividades1;

    @Getter
    @Setter
    private List<ActividadesImpactoProyecto> listaActividadesImpactoPerndientes;
    
    @Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
    
    @Getter
	@Setter
	private PerforacionExplorativa perforacionExplorativa= new PerforacionExplorativa();
    
    @EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;

    @PostConstruct
    public void inicio() throws ServiceException {
    	if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null) {
			perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectosBean.getProyectoRcoa().getId());
		} else {
			perforacionExplorativa=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto());
		}
    	exploracionPetroleraId = perforacionExplorativa.getId();
    	listaActividades = actividadesImpactoFacade.obtenerActividdaesProyecto(exploracionPetroleraId);
      }


	public void guardar() throws Exception{
		guardar = true;
		//String mensaje= "";
		List<String> listaMensajes = new ArrayList<String>();
		listaActividadesImpactoPerndientes = new ArrayList<ActividadesImpactoProyecto>();
		try{
			Integer x=0;
			for (EntityActividad objListaImpacto: listaActividades){
				for (ActividadesImpactoProyecto objImpacto: objListaImpacto.getSubactividades()){
						if((!"".equals(objImpacto.getHerramientas())  && !"".equals(objImpacto.getImpacto()) && !"".equals(objImpacto.getDescripcionImpacto()))
							&&(	
								objImpacto.isAgua() 
								|| objImpacto.isAire() 
								|| objImpacto.isSocial() 
								|| objImpacto.isSuelo()
								|| objImpacto.isBiotico()
								|| objImpacto.isPaisaje()
								)){
							listaActividadesImpactoPerndientes.add(objImpacto);
						}else{
							x=1;
							listaMensajes.add("indicar al menos un componenete para la sub actividad "+objImpacto.getActividad().getNombre());

						}

				}
			}
			if(x==1)
			{
				for(int i=0; i < listaMensajes.size() ; i++){
					JsfUtil.addMessageError(listaMensajes.get(i));
				}
				return;
			}
			
			actividadesImpactoFacade.guardarList(listaActividadesImpactoPerndientes);
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	        
		}catch(Exception ex){
            JsfUtil.addMessageError("Error al guardar la información.");
			throw new Exception("No se puede guardar las actividades o impactos.", ex);
		}
	}

    
}
