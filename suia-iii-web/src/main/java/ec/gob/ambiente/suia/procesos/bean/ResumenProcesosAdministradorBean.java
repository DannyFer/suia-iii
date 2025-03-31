/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.procesos.bean;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.Visibility;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.controllers.LazyDataListaProcesos;
import ec.gob.ambiente.suia.domain.ProcesoSuspendido;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.DefinicionProceso;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.ResumenInstanciaProceso;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 31/08/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ResumenProcesosAdministradorBean extends ResumenProcesosBean {

	private static final long serialVersionUID = 3178215636598281174L;

	private static final Logger LOG = Logger.getLogger(ResumenProcesosAdministradorBean.class);

	@EJB
	private JbpmSuiaCustomServicesFacade jbpmSuiaCustomServicesFacade;

	@EJB
	private UsuarioServiceBean usuarioServiceBean;
	
	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;

	private List<DefinicionProceso> definiciones;

	@Getter
	@Setter
	private ResumenInstanciaProceso resumenInstanciaProceso;

	@Getter
	@Setter
	private String proceso, nombteTarea, usuarioResponsable;

	@Getter
	private Map<String, Object> variables;
	
	@Getter
	private List<ProcesoSuspendido> procesosSuspendidoList;
	
	@Getter
	@Setter
    private LazyDataModel <ResumenInstanciaProceso> listarProcesoPaginador;

	@PostConstruct
	public void init() {
		try {

			process = new HashMap<String, List<ResumenInstanciaProceso>>();

			definiciones = jbpmSuiaCustomServicesFacade.listaDefinicionesProcesos();

			if (definiciones != null) {
				for (DefinicionProceso definicionProceso : definiciones) {
					process.put(definicionProceso.getName(), new ArrayList<ResumenInstanciaProceso>());
				}
			}
			
			procesosSuspendidoList=procesoSuspendidoFacade.getProcesosSuspendidos();

		} catch (Exception exception) {
			JsfUtil.addMessageError("Error al cargar el resumen de procesos.");
			LOG.error("No se puede obtener el resumen de procesos", exception);
		}
	}

	@Override
	public void onToggleProcess(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN))
			return;

		Panel panel = (Panel) event.getComponent();
		String processName = panel.getHeader();		
		if (process.get(processName).isEmpty()) {
			String processId = getProcessId(processName);
			if (processId != null) {
				try {
					
//					List<ProcessInstanceLog> processInstances = procesoFacade
//							.getProcessInstances(JsfUtil.getLoggedUser(), processId);
//
//					Map<Long, String> variablesTramite = procesoFacade.getProcessInstancesIdsVariableValue(
//							JsfUtil.getLoggedUser(), Constantes.VARIABLE_PROCESO_TRAMITE, false);
//
//					for (ProcessInstanceLog log : processInstances) {
//						if (!process.containsKey(log.getProcessName()))
//							process.put(log.getProcessName(), new ArrayList<ResumenInstanciaProceso>());
//
//						String tramite = "(Desconocido)";
//						if (variablesTramite.containsKey(log.getProcessInstanceId()))
//							tramite = variablesTramite.get(log.getProcessInstanceId());
//
//						ResumenInstanciaProceso proceso = new ResumenInstanciaProceso(log, tramite);
////						if(proceso.getEstado()!=4){
//							process.get(log.getProcessName()).add(proceso);
////						}
//
//					}
					List<Integer> states = new ArrayList<Integer>();
					states.add(0);
					states.add(1);
					states.add(2);
					states.add(3);
					states.add(4);
					listarProcesoPaginador= new LazyDataListaProcesos(processId,states);

				} catch (Exception e) {

				}
			}
		}
	}

	public void selectProcess(ResumenInstanciaProceso resumenInstanciaProceso, String proceso) {
		this.resumenInstanciaProceso = resumenInstanciaProceso;
		this.proceso = proceso;
		this.variables = new HashMap<>();
		try {
			this.variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
					this.resumenInstanciaProceso.getProcessInstanceId());
		} catch (JbpmException e) {
			JsfUtil.addMessageError("Error recuperando las variables del proceso id: "
					+ this.resumenInstanciaProceso.getProcessInstanceId() + ".");
			LOG.error(e);
		}
	}

	public void reiniciar(boolean abortar) {
		ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
		if (variables != null) {
			for (String variable : variables.keySet()) {
				String result = null;
				if (variables.get(variable) != null) {
					result = new String(variables.get(variable).toString().getBytes(Charset.forName("UTF-8")));
					variables.put(variable, result);
					if (result.toLowerCase().equals("false") || result.toLowerCase().equals("true")) {
						variables.put(variable, Boolean.parseBoolean(result));
						continue;
					}
					if(result.toLowerCase().startsWith("0") || result.isEmpty()){
						variables.put(variable, result);
						continue;
					}
					if (result.toLowerCase().matches("\\d+")) {
						Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, result);
						ContribuyenteCompleto contribuyente = consultaRucCedula.obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, result);
						if((cedula != null && cedula.getCedula() == null) || (contribuyente != null && contribuyente.getNumeroRuc() == null)) {
							variables.put(variable, result);
							continue;
						} else {
							variables.put(variable, Integer.parseInt(result));
							continue;
						}
					}
					if (result.toLowerCase().matches("^([+-]?\\d*\\.?\\d*)$")) {
						variables.put(variable, Float.parseFloat(result));
						continue;
					}
				}
			}
		}
		try {
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), resumenInstanciaProceso.getProcessId(),
					resumenInstanciaProceso.getTramite(), variables);
			if (abortar)
				procesoFacade.abortProcess(JsfUtil.getLoggedUser(), resumenInstanciaProceso.getProcessInstanceId());
			RequestContext.getCurrentInstance()
					.execute(abortar ? "PF('processRestartAbortDialog').show()" : "PF('processRestartDialog').show()");
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
			LOG.error(e);
		}
	}

	/**
	 *
	 * Confirma si se quiere avanza una tarea en la instancia actual
	 * suplantando la identidad del usuario que debe hacerla.
	 *
	 */

	public void avanzarTareaConfirm() {
		try {
			verTareas(resumenInstanciaProceso);
			Tarea tarea = getTareas().isEmpty() ? null : getTareas().get(0);
			if(tarea != null) {
				nombteTarea = tarea.getNombre();
				usuarioResponsable = tarea.getResponsable();
				RequestContext.getCurrentInstance().execute("PF('nexttaskDialog').show();");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	/**
	 *
	 * Avanza una tarea en la instancia actual ( hay que agregar que sea posible esto siempre y cuando
	 * haya una instancia del mismo proceso más antigua y con un trámite igual que esté en estado 4).
	 *
	 */

	public void avanzarTarea() {
		try {
			List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class)
					.getResumenTareas(resumenInstanciaProceso.getProcessInstanceId());
			ResumeTarea resumeTarea = resumeTareas.isEmpty() ? null : resumeTareas.get(0);
			verTareas(resumenInstanciaProceso);
			Tarea tarea = getTareas().isEmpty() ? null : getTareas().get(0);
			if(resumeTarea != null) {
				Usuario usuario = ((UsuarioServiceBean) BeanLocator.getInstance(UsuarioServiceBean.class))
						.buscarUsuarioWithOutFilters(resumeTarea.getUserId());
				procesoFacade.aprobarTarea(usuario, tarea.getId(), resumenInstanciaProceso.getProcessInstanceId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			}
		} catch (JbpmException e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}

	private String getProcessId(String name) {
		for (DefinicionProceso definicionProceso : definiciones) {
			if (definicionProceso.getName().equals(name))
				return definicionProceso.getId();
		}
		return null;
	}
	
	public boolean proyectoSuspendido(String codigoProyecto) {
		ProcesoSuspendido ps =procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(codigoProyecto);
		if(ps != null && ps.getSuspendido())
			return true;		
		return false;			
	}
	
	public void reactivarProceso(Integer id,String motivo) {
		try {
			
			ProcesoSuspendido procesoSuspendido=procesoSuspendidoFacade.getProcesoSuspendidoPorId(id);
			if(procesoSuspendido==null)
			{
				JsfUtil.addMessageError("Proceso Suspendido No Encontrado");
				return;
			}			
			
			ProyectoLicenciamientoAmbiental proyecto = procesoSuspendidoFacade.getProyectoPorCodigo(procesoSuspendido.getCodigo());
			
			if(proyecto!=null && proyecto.getEstado())
			{
				JsfUtil.addMessageError("El Proyecto está activo");
				return;
			}
			
			boolean tareasModificadas=false;
			String tipoProyecto=procesoSuspendido.getTipoProyecto()!=null?procesoSuspendido.getTipoProyecto():"";
			switch (tipoProyecto) {
			case "ReguralizacionAmbiental":
				if(proyecto==null)
				{
					JsfUtil.addMessageError("Proyecto No Encontrado");
					return;
				}
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas(proyecto.getCodigo().toString(),JsfUtil.getLoggedUser(),false);
				break;
			case "Hidrocarburos":
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;			
			case "4Categorias":
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;
			default:
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas(proyecto.getCodigo().toString(),JsfUtil.getLoggedUser(),false);
				if(!tareasModificadas)
					tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;
			}			
			
			if(tareasModificadas)
			{
				procesoSuspendido.setSuspendido(false);	
				procesoSuspendido.setDescripcion(motivo!=null?motivo:"REACTIVACION");
				procesoSuspendidoFacade.guardar(procesoSuspendido,JsfUtil.getLoggedUser().getNombre());
				if(proyecto!=null)
				{
					procesoSuspendidoFacade.setEstadoProyecto(proyecto.getId(),true);					
				}
				JsfUtil.addMessageInfo("Se ha reactivado el proyecto.");
				procesosSuspendidoList=procesoSuspendidoFacade.getProcesosSuspendidos();
			}else{
				JsfUtil.addMessageError("No hay tareas suspendidas.");
			}		
			
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void reactivarProceso(Integer id,String motivo,Integer numDias) {
		try {
			
			ProcesoSuspendido procesoSuspendido=procesoSuspendidoFacade.getProcesoSuspendidoPorId(id);
			if(procesoSuspendido==null)
			{
				JsfUtil.addMessageError("Proceso Suspendido No Encontrado");
				return;
			}			
			
			ProyectoLicenciamientoAmbiental proyecto = procesoSuspendidoFacade.getProyectoPorCodigo(procesoSuspendido.getCodigo());
			
			if(proyecto!=null && proyecto.getEstado())
			{
				JsfUtil.addMessageError("El Proyecto está activo");
				return;
			}
			
			boolean tareasModificadas=false;
			String tipoProyecto=procesoSuspendido.getTipoProyecto()!=null?procesoSuspendido.getTipoProyecto():"";
			switch (tipoProyecto) {
			case "ReguralizacionAmbiental":
				if(proyecto==null)
				{
					JsfUtil.addMessageError("Proyecto No Encontrado");
					return;
				}
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas(proyecto.getCodigo().toString(),JsfUtil.getLoggedUser(),false);
				break;
			case "RegistroGeneradorDesechosNoAsociado":
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareasRGD(procesoSuspendido.getCodigo(),JsfUtil.getLoggedUser(),false,false,null,null);
				break;		
			case "Hidrocarburos":
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;			
			case "4Categorias":
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;			
			default:
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas(proyecto.getCodigo().toString(),JsfUtil.getLoggedUser(),false);
				if(!tareasModificadas)
					tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;
			}			
			
			if(tareasModificadas)
			{
				procesoSuspendido.setSuspendido(false);	
				procesoSuspendido.setDescripcion(motivo!=null?motivo:"REACTIVACION");
				procesoSuspendido.setDiasReactivados(numDias);
				if(numDias!=null)
				{
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_YEAR, numDias);
					procesoSuspendido.setFechaActivacion(calendar.getTime());
				}else
				{
					procesoSuspendido.setFechaActivacion(null);
				}
				
				procesoSuspendidoFacade.guardar(procesoSuspendido,JsfUtil.getLoggedUser().getNombre());
				if(proyecto!=null)
				{
					procesoSuspendidoFacade.setEstadoProyecto(proyecto.getId(),true);					
				}
				JsfUtil.addMessageInfo("Se ha reactivado el proyecto.");
				procesosSuspendidoList=procesoSuspendidoFacade.getProcesosSuspendidos();
			}else{
				JsfUtil.addMessageError("No hay tareas suspendidas.");
			}		
			
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void reactivarProceso(String codigo) {
		try {
			
			ProcesoSuspendido procesoSuspendido=procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(codigo);
			if(procesoSuspendido==null)
			{
				JsfUtil.addMessageError("Proceso Suspendido No Encontrado");
				return;
			}			
			
			ProyectoLicenciamientoAmbiental proyecto = procesoSuspendidoFacade.getProyectoPorCodigo(procesoSuspendido.getCodigo());
			
			if(proyecto!=null && proyecto.getEstado())
			{
				JsfUtil.addMessageError("El Proyecto está activo");
				return;
			}
			
			boolean tareasModificadas=false;
			String tipoProyecto=procesoSuspendido.getTipoProyecto()!=null?procesoSuspendido.getTipoProyecto():"";
			switch (tipoProyecto) {
			case "ReguralizacionAmbiental":
				if(proyecto==null)
				{
					JsfUtil.addMessageError("Proyecto No Encontrado");
					return;
				}
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas(proyecto.getCodigo().toString(),JsfUtil.getLoggedUser(),false);
				break;
			case "Hidrocarburos":
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;			
			case "4Categorias":
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;
			default:
				tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas(proyecto.getCodigo().toString(),JsfUtil.getLoggedUser(),false);
				if(!tareasModificadas)
					tareasModificadas=procesoSuspendidoFacade.modificarPropietarioTareas4Categorias(proyecto.getCodigo().toString(),"REACTIVACION",false);
				break;
			}			
			
			if(tareasModificadas)
			{
				procesoSuspendido.setSuspendido(false);	
				procesoSuspendido.setDescripcion("REACTIVACION");
				procesoSuspendidoFacade.guardar(procesoSuspendido,JsfUtil.getLoggedUser().getNombre());
				if(proyecto!=null)
				{
					procesoSuspendidoFacade.setEstadoProyecto(proyecto.getId(),true);					
				}
				JsfUtil.addMessageInfo("Se ha reactivado el proyecto.");
				procesosSuspendidoList=procesoSuspendidoFacade.getProcesosSuspendidos();
			}else{
				JsfUtil.addMessageError("No hay tareas suspendidas.");
			}		
			
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}
