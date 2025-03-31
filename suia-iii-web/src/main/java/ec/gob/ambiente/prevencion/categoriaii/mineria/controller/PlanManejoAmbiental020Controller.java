package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental020;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalProyecto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.PlanManejoAmbiental020Facade;

@ManagedBean
@ViewScoped
public class PlanManejoAmbiental020Controller implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7691650895545383147L;
	@EJB
	private PlanManejoAmbiental020Facade planManejoAmbiental020Facade;
	
	@Getter
	@Setter 
	private List<PlanManejoAmbiental020> plan020;
	
	@Getter
	@Setter
	private PlanManejoAmbientalProyecto planManejoProyecto;
	

	@Getter
	@Setter
	private Map<String, List<PlanManejoAmbientalProyecto>> parameters = new HashMap<String, List<PlanManejoAmbientalProyecto>>();


	@Getter
	@Setter
	private Integer perforacionExplorativa;
	
    @Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
    
    @EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
    
    @PostConstruct
	protected void init() throws ServiceException {
    	if(proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getId() != null)
    		perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto()).getId();
    	else if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null)
    		perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectosBean.getProyectoRcoa().getId()).getId();
    	
		 planManejoProyecto = new PlanManejoAmbientalProyecto();
		 parameters = planManejoAmbiental020Facade.obtenerActividdaesProyecto(perforacionExplorativa);
	 }
	 
	 public void adicionar(String planNombre) {
		 try{
		 planManejoProyecto = new PlanManejoAmbientalProyecto();
		 planManejoProyecto.setPerforacionExplorativa(perforacionExplorativa);
		 planManejoProyecto.setDescripcion("Otros");
		 PlanManejoAmbiental020 objPlanPadre002 = planManejoAmbiental020Facade.buscarPMAPadres020PorNombre(planNombre.trim());

		if(objPlanPadre002 != null){
			 planManejoProyecto.setPadreId(objPlanPadre002.getId());
		 }
		 }catch(Exception e){
		 }

	}
	 
	 public void editar(PlanManejoAmbientalProyecto planActividad) {
		 planManejoProyecto = new PlanManejoAmbientalProyecto();
		 planManejoProyecto = planActividad;
	 }

	 public void eliminar(PlanManejoAmbientalProyecto planActividad) {
         planActividad.setEstado(false);
         planManejoAmbiental020Facade.guardar(planActividad);
         parameters = planManejoAmbiental020Facade.obtenerActividdaesProyecto(perforacionExplorativa);
    }
	 
	 public void guardarPlanOtros() {
		 try{
			 planManejoAmbiental020Facade.guardar(planManejoProyecto);
		     JsfUtil.addMessageInfo("Registro guardado correctamente.");
		     parameters = planManejoAmbiental020Facade.obtenerActividdaesProyecto(perforacionExplorativa);
		     RequestContext.getCurrentInstance().execute("PF('dlgActividad').hide();");
//		     guardar();
		 }catch(Exception e){
		     JsfUtil.addMessageError("Error al guardar.");
		 }
	 }
	 
	 public void guardar() {
		 List<PlanManejoAmbientalProyecto>  listaGuardardatos =  new ArrayList<PlanManejoAmbientalProyecto>();
		 if (perforacionExplorativa == null){
			 try{
				 perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto()).getId();
				 for (Map.Entry<String, List<PlanManejoAmbientalProyecto>> entry : parameters.entrySet()) {
						List<PlanManejoAmbientalProyecto>  datos = entry.getValue();
						for(PlanManejoAmbientalProyecto datosGuardar :  datos){
									datosGuardar.setPerforacionExplorativa(perforacionExplorativa);
						}
					}
			 }catch(ServiceException e){
				 
			 }
		 }
		 for (Map.Entry<String, List<PlanManejoAmbientalProyecto>> entry : parameters.entrySet()) {
				List<PlanManejoAmbientalProyecto>  datos = entry.getValue();
				for(PlanManejoAmbientalProyecto datosGuardar :  datos){
//					if(datosGuardar.getId() != null ){
//						listaGuardardatos.add(datosGuardar);
//					}else 
					if(datosGuardar.getCalificacion() != null)
					{
						if(datosGuardar.getCalificacion()>0){
							listaGuardardatos.add(datosGuardar);
						}
					}
				}
			}
		 if(listaGuardardatos.size() > 0 ){
			 planManejoAmbiental020Facade.guardarLista(listaGuardardatos); 
		 }
		 JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
	 }
	
}
