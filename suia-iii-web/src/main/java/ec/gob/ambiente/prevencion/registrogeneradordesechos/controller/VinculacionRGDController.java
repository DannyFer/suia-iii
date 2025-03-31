package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.RealizarPagoRegistroGeneradorBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.comun.bean.PagoServiciosBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RegistroGeneradorDesechosAsociado;
import ec.gob.ambiente.suia.dto.EntityFichaCompleta;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tramiteresolver.RegistroGeneradorTramiteResolver;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class VinculacionRGDController implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(RealizarPagoRegistroGeneradorBean.class);
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	private String codigoRGD, codigoProyecto;
	
	@Getter
	@Setter
	private Boolean crearNuevoProceso, habilitarVinculacion;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrosos registro;
	
	private Map<String, Object> variables, variablesProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	private String numeroReferenciaPago;
	
	@PostConstruct
	private void init() {
		try{
			codigoRGD = "";
			codigoProyecto = "";	
			habilitarVinculacion = true;
			numeroReferenciaPago = "";
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void validarExistenciaRGDListener(){
		try {
			
			variables = new HashMap<String, Object>();
			variables = registroGeneradorDesechosFacade.buscarPago(codigoRGD);
				
			if(variables != null){
				numeroReferenciaPago = variables.get("referenciaPago").toString();
			}else{
				JsfUtil.addMessageWarning("No existe un pago del RGDP o existe una vinculación previa con otro proyecto.");
				numeroReferenciaPago = "";
			}	
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void verificarProyecto(){
		try{
			if(codigoProyecto == null || codigoProyecto.isEmpty()){
				JsfUtil.addMessageInfo("Ingrese el código del proyecto");
				return;
			}	
			
			if(numeroReferenciaPago.isEmpty() || numeroReferenciaPago.equals("")){
				JsfUtil.addMessageInfo("No existe un pago de registro generador de desechos para vincular.");
				return;
			}
			
			if(codigoRGD == null || codigoRGD.isEmpty()){
				JsfUtil.addMessageInfo("Ingrese el código del registro generador de desechos");
				return;
			}
			
			variables = new HashMap<String, Object>();
			variables = registroGeneradorDesechosFacade.validacionRGD(codigoRGD);
				
			if(variables != null){
			}else{
				JsfUtil.addMessageWarning("No existe el proyecto de Registro Generador de Desechos.");				
				return;
			}			
			
			if(registroGeneradorDesechosFacade.verificarRGDAsociado(codigoRGD)){
				JsfUtil.addMessageWarning("El registro generador de desechos tiene una vinculación con un proyecto.");	
				return;
			}
			
			
			crearNuevoProceso = false;			
			
			List<EntityFichaCompleta> finalizados = fichaAmbientalPmaFacade.getFinalizados(codigoProyecto);
			if(finalizados != null && !finalizados.isEmpty()){
				
				if (variables != null && !variables.isEmpty()) {

					String cedulaProponenteRGD = variables.get("sujetoControl").toString();

					if (cedulaProponenteRGD.equals(finalizados.get(0).getCedulaProponente())) {
						
						proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(codigoProyecto);					
						
						//Validamos que no tenga un generador de desechos en curso asociado.
						//aqui se valida si esta ingresado en la tabla hazardous_wastes_generators
						//si es que existe entonces el nuevo rgd se asocia 
//						boolean existeGeneradorEnCurso = proyectoLicenciaAmbientalFacade.tienenRgdEnCurso(proyecto.getId());
//						if(existeGeneradorEnCurso){
//							generadorEnCurso = true;
//						}else{
//							generadorEnCurso = false;
//						}
//						
//						if(!generadorEnCurso){
							variablesProyecto = new HashMap<String, Object>();
							variablesProyecto = registroGeneradorDesechosFacade.validacionRGD(codigoProyecto);
								
							if(variablesProyecto != null){
								crearNuevoProceso = false;
							}else{
								crearNuevoProceso = true;								
							}	
//						}
						
							habilitarVinculacion = false;
					}else{
						JsfUtil.addMessageInfo("Los proyectos no son del mismo usuario.");
					}
				}else{
					JsfUtil.addMessageError("No existe variable de usuario en el proyecto.");
				}			
			}else{
				JsfUtil.addMessageInfo("El codigo " + codigoProyecto + " no esta finalizado");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void validarProyectos(){
		try {			
			if(codigoRGD == null || codigoRGD.isEmpty() || codigoProyecto == null || codigoProyecto.isEmpty()){
				JsfUtil.addMessageInfo("Ingrese los codigos de los proyectos");
				return;
			}	
			//ver si se queda esta validacion o solo con el botón sirve
			verificarProyecto();
			
			if(crearNuevoProceso){
				iniciarProceso();
			}			
									
			if(actualizarProyecto(codigoRGD, codigoProyecto)){
				JsfUtil.addMessageInfo("Se vinculó los proyectos.");
				codigoProyecto = "";
				codigoRGD = "";
				numeroReferenciaPago = "";
				habilitarVinculacion = true;
				
			}else{
				JsfUtil.addMessageError("No se realizó la operación de vinculación revise los proyectos ");
				codigoProyecto = "";
				codigoRGD = "";
				numeroReferenciaPago = "";
				habilitarVinculacion = true;
			}					
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error en el sistema.");
			codigoProyecto = "";
			codigoRGD = "";
			numeroReferenciaPago = "";
			habilitarVinculacion = true;
		}		
	}
	
	private boolean actualizarProyecto(String codigoRGD, String codigoProyecto){
		try {			
			return registroGeneradorDesechosFacade.actualizarRGD(codigoRGD, codigoProyecto);			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	//iniciar tramite
	public void iniciarProceso() {
		boolean error = false;
		String solicitud = "";
		String workspace="";
		Map<String, Float> paramsValorAPagar = new ConcurrentHashMap<String, Float>();
   	    paramsValorAPagar.put("valorAPagar", (float) 180);
		try {
			Object[] result = registroGeneradorDesechosFacade.iniciarEmisionProcesoRegistroGenerador(proyecto.getUsuario(), RegistroGeneradorTramiteResolver.class, proyecto);

			if (proyecto != null){
				proyecto.setRgdEncurso(true);
				proyectoLicenciamientoAmbientalFacade.actualizarProyecto(proyecto);
			}

			JsfUtil.getBean(BandejaTareasBean.class).setProcessId((Long)result[0]);
			JsfUtil.getBean(BandejaTareasBean.class).setTarea(
					new TaskSummaryCustomBuilder().fromSuiaIII((Map<String, Object>)result[1],
							"Registro de generador de desechos especiales y peligrosos",
							procesoFacade.getCurrenTask(proyecto.getUsuario(), (Long)result[0])).build());
			proyectosBean.setProyecto(proyecto);
			solicitud = ((Map<String, Object>)result[1]).get(GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD).toString();

		} catch (JbpmException e) {
			error = true;
			LOGGER.debug("Error iniciando procesos Registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
		}
		if (error)
			return;

	}

}
