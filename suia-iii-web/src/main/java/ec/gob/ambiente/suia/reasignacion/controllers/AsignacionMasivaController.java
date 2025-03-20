/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.reasignacion.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.fugu.ambiente.consultoring.retasking.ProyectoLicenciaVo;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.builders.TaskSummaryCustomBuilder;
import ec.gob.ambiente.suia.carga.facade.CargaLaboralFacade;
import ec.gob.ambiente.suia.comun.classes.Selectable;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.dto.UserWorkload;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reasignacion.bean.AsignacionMasivaBean;
import ec.gob.ambiente.suia.reasignacion.classes.LazyUserWorkloadDataModel;
import ec.gob.ambiente.suia.reasignacionproyecto.facade.ReasignacionProyectosFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 14/01/2015]
 *          </p>
 */
@ManagedBean
public class AsignacionMasivaController implements Serializable {

	private static final long serialVersionUID = -352637128711377441L;

	private static final Logger LOGGER = Logger.getLogger(AsignacionMasivaController.class);

	@ManagedProperty(value = "#{asignacionMasivaBean}")
	@Setter
	private AsignacionMasivaBean asignacionMasivaBean;

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private CargaLaboralFacade cargaLaboralFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private BandejaFacade bandejaFacade;

	@EJB
	private IntegracionFacade integracionFacade;

	@EJB
	private ConexionBpms conexionBpms;
	
	@EJB
	private RolFacade rolFacade;
	@EJB
	private ReasignacionProyectosFacade reasignacionProyectosFacade;
	@EJB
    private ProyectoLicenciaCoaFacade proyectoFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEiaFacade;
	@EJB
	private ObservacionesEsIAFacade observacionesEsIAFacade;
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	@EJB
	private AreaFacade areaFacade;
	
	@Getter
	@Setter
	private boolean adminAreas = false;
	
	public void buscarUsuario() throws ServiceException {
		List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
		Integer adminInst=0;
		Integer reasignarInst=0;
		for (Rol rol : rolesusuario) {
			if (rol.getDescripcion().contains("admin")){
				adminInst=1;
				break;
			}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
				adminInst=2;
				break;
			}else if (rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
				adminInst=3;
				break;
			}else{
				adminInst=0;
			}
			
		}
		if (adminInst==2){	
			Usuario usuariobusqueda= usuarioFacade.buscarUsuario(asignacionMasivaBean.getNombre());
			for (AreaUsuario areaUserBusqueda : usuariobusqueda.getListaAreaUsuario()) {
				Area areaBusqueda = areaUserBusqueda.getArea();
				for (AreaUsuario areaUserLogin : loginBean.getUsuario().getListaAreaUsuario()) {
					Area areaLogin = areaUserLogin.getArea();
					if (areaBusqueda.getAreaName().contains(areaLogin.getAreaName())){
						reasignarInst=1;
						break;
					}
				}
				
				if(reasignarInst == 1)
					break;
			}
		}
		if (adminInst==1 || (adminInst==2 && reasignarInst==1)){
			asignacionMasivaBean.setUsuario(null);
			asignacionMasivaBean.setUserNameFilter(null);
			Usuario usuario = usuarioFacade.buscarUsuario(asignacionMasivaBean.getNombre());
			if (usuario != null) {
				if (usuario.getListaAreaUsuario() == null || usuario.getListaAreaUsuario().size() == 0) {
					JsfUtil.addMessageError("No se puede recuperar el usuario, no está asignado a ningún área.");
					return;
				}
				asignacionMasivaBean.setUsuario(usuario);
				loadInfo();
			} else
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
		}else if(adminInst == 3){
			
		}else{
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOSDESCONCENTRADO);
		}
	}
	
	public void buscarUsuarioCompleto(EntityUsuario usuarioCompleto) throws ServiceException {
		List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
		Integer adminInst=0;
		Integer reasignarInst=0;
		for (Rol rol : rolesusuario) {
			if (rol.getDescripcion().contains("admin")){
				adminInst=1;
				break;
			}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
				adminInst=2;
				break;
			}else if (rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
				adminInst=3;
				break;
			}else{
				adminInst=0;
			}			
		}
		
		if(adminInst == 3){
			
			asignacionMasivaBean.setUsuario(null);
			asignacionMasivaBean.setUserNameFilter(null);
			Usuario usuario = usuarioFacade.buscarUsuario(usuarioCompleto.getNombre());
			if (usuario != null) {
				if (usuario.getListaAreaUsuario() == null || usuario.getListaAreaUsuario().size() == 0) {
					JsfUtil.addMessageError("No se puede recuperar el usuario, no está asignado a ningún área.");
					return;
				}
				asignacionMasivaBean.setUsuario(usuario);
			}			
		}		
		
		if (adminInst==2){	
			Usuario usuariobusqueda= usuarioFacade.buscarUsuario(usuarioCompleto.getNombre());
			usuariobusqueda= usuarioFacade.buscarUsuario(usuarioCompleto.getNombre());
			
			Area areaUsuarioInstitucional = loginBean.getUsuario().getListaAreaUsuario().get(0).getArea();
    		if(areaUsuarioInstitucional.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
    			//se utiliza el area padre y todas las áreas hijas correspondientes
    			List<Area> areasReasignacion = new ArrayList<>();
    			areasReasignacion.add(areaUsuarioInstitucional.getArea());
    			
    			List<Area> areasHijas = areaFacade.listarAreasHijos(areaUsuarioInstitucional.getArea());
    			areasReasignacion.addAll(areasHijas);
    			
    			for (AreaUsuario areaUserBusqueda : usuariobusqueda.getListaAreaUsuario()) {
    				Area areaBusqueda = areaUserBusqueda.getArea();
    				for (Area areaReasignar : areasReasignacion) {
    					if (areaBusqueda.getAreaName().contains(areaReasignar.getAreaName())){
    						reasignarInst=1;
    						break;
    					}
    				}
    				
    				if(reasignarInst == 1)
    					break;
    			}
    		} else {
    			for (AreaUsuario areaUserBusqueda : usuariobusqueda.getListaAreaUsuario()) {
    				Area areaBusqueda = areaUserBusqueda.getArea();
    				for (AreaUsuario areaUserLogin : loginBean.getUsuario().getListaAreaUsuario()) {
    					Area areaLogin = areaUserLogin.getArea();
    					if (areaBusqueda.getAreaName().contains(areaLogin.getAreaName())){
    						reasignarInst=1;
    						break;
    					}
    				}
    				
    				if(reasignarInst == 1)
    					break;
    			}
    		}
		}
		if (adminInst==1 || (adminInst==2 && reasignarInst==1)){
			asignacionMasivaBean.setUsuario(null);
			asignacionMasivaBean.setUserNameFilter(null);
			Usuario usuario = usuarioFacade.buscarUsuario(usuarioCompleto.getNombre());
			if (usuario != null) {
				if (usuario.getListaAreaUsuario() == null || usuario.getListaAreaUsuario().size() == 0) {
					JsfUtil.addMessageError("No se puede recuperar el usuario, no está asignado a ningún área.");
					return;
				}
				asignacionMasivaBean.setUsuario(usuario);
				loadInfo();
			} else{
				
				Usuario usuarioDes = usuarioFacade.buscarUsuarioPorIdDesactivado(Integer.valueOf(usuarioCompleto.getId()));				
				
				if(usuarioDes != null){
					List<RolUsuario> lista = usuarioFacade.listarPorIdUsuario(usuarioDes.getId());	
					usuarioDes.setRolUsuarios(new ArrayList<RolUsuario>());
					usuarioDes.getRolUsuarios().addAll(lista);
					
					asignacionMasivaBean.setUsuario(usuarioDes);
					loadInfo();					
				}else{
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
				}								
			}
		}else if(adminInst == 3){
			loadInfoAP();
		}else{
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOSDESCONCENTRADO);
		}
	}

	private void loadInfo() {
		try {
			asignacionMasivaBean.setMotivoReasignacion(asignacionMasivaBean.getMotivoReasignacion());
			asignacionMasivaBean.getReasignacionProyectos().setMotivoReasignacion(asignacionMasivaBean.getMotivoReasignacion());
			asignacionMasivaBean.setTasks(bandejaFacade.getTasksByUserForRetasking(asignacionMasivaBean.getUsuario()
					.getNombre(), JsfUtil.getLoggedUser()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JsfUtil.addMessageError("No se pudieron cargar las tareas para este usuario.");
			return;
		}

		try {
			if (asignacionMasivaBean.isLoadUsers()) {
				if (asignacionMasivaBean.getUserNameFilter() != null
						&& !asignacionMasivaBean.getUserNameFilter().isEmpty()) {
					List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
					Integer adminInst=0;
					for (Rol rol : rolesusuario) {
						if (rol.getDescripcion().contains("admin")){
							adminInst=1;
							break;
						}
						
					}
					Usuario usuario = usuarioFacade.buscarUsuario(asignacionMasivaBean.getUserNameFilter());
					if (adminInst==1){
						if (usuario != null) {
	                        usuario.setSeleccionado(true);
							asignacionMasivaBean.setLazyUserWorkloadDataModel(new LazyUserWorkloadDataModel(new UserWorkload(usuario)));
						} else
							JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
					}else{
						if (usuario != null) {
							Boolean asignado = false;
							for (AreaUsuario areaUserBusqueda : usuario.getListaAreaUsuario()) {
								Area areaBusqueda = areaUserBusqueda.getArea();
								for (AreaUsuario areaUserLogin : asignacionMasivaBean.getUsuario().getListaAreaUsuario()) {
									Area areaLogin = areaUserLogin.getArea();
									if (areaBusqueda.getAreaName().contains(areaLogin.getAreaName())){
										usuario.setSeleccionado(true);
										asignacionMasivaBean.setLazyUserWorkloadDataModel(new LazyUserWorkloadDataModel(new UserWorkload(usuario)));
										asignado = true;
									}
								}
							}
							
							if (!asignado) {
								JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
							}									                        
						} else{
							JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
						}
					}					
				} else{
					if(asignacionMasivaBean.getUsuario().getRolUsuarios() != null && !asignacionMasivaBean.getUsuario().getRolUsuarios().isEmpty() &&
							asignacionMasivaBean.getUsuario().getListaAreaUsuario() != null && !asignacionMasivaBean.getUsuario().getListaAreaUsuario().isEmpty()){
						asignacionMasivaBean.setLazyUserWorkloadDataModel(new LazyUserWorkloadDataModel(
								asignacionMasivaBean.getUsuario()));
					}					
				}					
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JsfUtil.addMessageError("No se pudieron cargar los usuarios candidatos para reasignar tareas.");
		}
	}	
	
	private void loadInfoAP() {
		try {
			adminAreas = true;
			asignacionMasivaBean.setMotivoReasignacion(asignacionMasivaBean.getMotivoReasignacion());
			asignacionMasivaBean.getReasignacionProyectos().setMotivoReasignacion(asignacionMasivaBean.getMotivoReasignacion());
			asignacionMasivaBean.setTasks(bandejaFacade.getTasksByUserForRetasking(asignacionMasivaBean.getUsuario()
					.getNombre(), JsfUtil.getLoggedUser()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JsfUtil.addMessageError("No se pudieron cargar las tareas para este usuario.");
			return;
		}

		try {
			
			if (asignacionMasivaBean.isLoadUsers()) {
				
				List<String> listaRoles = new ArrayList<>();
				listaRoles.add("ADMINISTRADOR ÁREA PROTEGIDA");
				listaRoles.add("RESPONSABLE DE ÁREAS PROTEGIDAS");				
				
				if (asignacionMasivaBean.getUserNameFilter() != null
						&& !asignacionMasivaBean.getUserNameFilter().isEmpty()) {
					
					Usuario usuario = usuarioFacade.buscarUsuario(asignacionMasivaBean.getUserNameFilter());
					
					if (usuario != null) {
						usuario.setSeleccionado(true);
						asignacionMasivaBean.setLazyUserWorkloadDataModel(new LazyUserWorkloadDataModel(new UserWorkload(usuario)));
								                        
					} else{
						JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);
					}
								
				} else{					
					asignacionMasivaBean.setLazyUserWorkloadDataModel(new LazyUserWorkloadDataModel(
							asignacionMasivaBean.getUsuario(), listaRoles));					
				}					
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JsfUtil.addMessageError("No se pudieron cargar los usuarios candidatos para reasignar tareas.");
		}
	}
	

	public void cancelar() {
		asignacionMasivaBean.init();
	}

	public void selectUser(UserWorkload usuario) {
		asignacionMasivaBean.setSelectedUser(usuario);
	}

	public void asignar() throws ServiceException {		
		if (!asignacionMasivaBean.getMotivoReasignacion().isEmpty()){				
			asignacionMasivaBean.getReasignacionProyectos().setMotivoReasignacion(asignacionMasivaBean.getMotivoReasignacion());
			List<Selectable<TaskSummaryCustom>> assigned = new ArrayList<Selectable<TaskSummaryCustom>>();
			List<TaskSummaryCustom> externalTasks = new ArrayList<TaskSummaryCustom>();
			boolean error = false;
			for (Selectable<TaskSummaryCustom> selectable : asignacionMasivaBean.getTasksToAssign()) {
				if (selectable.isSelected()) {
					assigned.add(selectable);
					TaskSummaryCustom taskSummaryCustom = selectable.getValue();				
					if (taskSummaryCustom.getSourceType() == "source_type_internal") {
						try {						
							
							procesoFacade.reasignarTarea(loginBean.getUsuario(), 
									taskSummaryCustom.getTaskSummary().getId(),
									taskSummaryCustom.getTaskSummary().getActualOwner().getId(), 
									asignacionMasivaBean.getSelectedUser().getUserName(),
									conexionBpms.deploymentId(taskSummaryCustom.getTaskSummary().getId(), "S"));
							
							conexionBpms.reasigment(loginBean.getUsuario(),taskSummaryCustom.getTaskSummary().getId(), asignacionMasivaBean.getSelectedUser().getUserName(), taskSummaryCustom.getTaskSummary().getActualOwner().getId(), taskSummaryCustom.getTaskSummary().getProcessInstanceId());
							
							if(taskSummaryCustom.getProcessId().equals(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL) || 
									taskSummaryCustom.getProcessId().equals(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2)){
								
								modificacionObservacionesEstudio(taskSummaryCustom.getProcessInstanceId(), asignacionMasivaBean.getSelectedUser().getUserName(), asignacionMasivaBean.getUsuario().getNombre());
							}														
							
						} catch (JbpmException e) {
							System.out.println("Error en la tarea suia-iii:::"+taskSummaryCustom.getTaskSummary().getId());
						}
					}
					if(taskSummaryCustom.getSourceType()=="source_type_external_suia")
					{
						externalTasks.add(taskSummaryCustom);
					}
					Integer idTask = Long.valueOf(selectable.getValue().getTaskId()).intValue();
					Integer idUsuarioAsignado = Integer.parseInt(asignacionMasivaBean.getSelectedUser().getId());
					
					asignacionMasivaBean.getReasignacionProyectos().setCodigoReacctivacion(selectable.getValue().getProcedure());
					asignacionMasivaBean.getReasignacionProyectos().setTareaReasignada(selectable.getValue().getTaskName());
					asignacionMasivaBean.getReasignacionProyectos().setUsuario(asignacionMasivaBean.getUsuario());
					asignacionMasivaBean.getReasignacionProyectos().setIdTarea(idTask);
					asignacionMasivaBean.getReasignacionProyectos().setIdUsuarioAsignado(idUsuarioAsignado);
					if (!(asignacionMasivaBean.getReasignacionProyectos().getId()==null)){
						asignacionMasivaBean.getReasignacionProyectos().setId(null);
					}				
					reasignacionProyectosFacade.modificarReasignacionProyectos(asignacionMasivaBean.getReasignacionProyectos());
					if(taskSummaryCustom.getSourceType().equals("source_type_external_hydrocarbons"))
					{
						try {
							procesoFacade.reasignarTareaH(loginBean.getUsuario(), 
									taskSummaryCustom.getTaskId(),
									asignacionMasivaBean.getUsuario().getNombre(), 
									asignacionMasivaBean.getSelectedUser().getUserName(),
									conexionBpms.deploymentId(taskSummaryCustom.getTaskId(), "H"));
							
							conexionBpms.reasigmenthyd(loginBean.getUsuario(), taskSummaryCustom.getTaskId(), asignacionMasivaBean.getSelectedUser().getUserName(), taskSummaryCustom.getProcessInstanceId(),taskSummaryCustom.getProcedure());
							
						} catch (JbpmException e) {
							System.out.println("Error en la tarea hidrocarburos:::"+taskSummaryCustom.getTaskId());
						}	
					}
				}
			}
			
			if (!externalTasks.isEmpty()) {
				List<ProyectoLicenciaVo> proyectosReasignar = new ArrayList<ProyectoLicenciaVo>();
				for (TaskSummaryCustom taskSummaryCustom : externalTasks) {
					proyectosReasignar.add(new TaskSummaryCustomBuilder().revertToProyectoLicenciaVo(taskSummaryCustom));
				}
				try {
					integracionFacade.executeRetasking(proyectosReasignar, asignacionMasivaBean.getUsuario().getNombre(),
							asignacionMasivaBean.getSelectedUser().getUserName());
				} catch (Exception e) {
					LOGGER.error(e);
					JsfUtil.addMessageError("Error al reasignar las tareas.");
					error = true;
				}
			}
	
	
			if (!error)			
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			asignacionMasivaBean.setMotivoReasignacion(null);
			loadInfo();
		}else{
			JsfUtil.addMessageError("El motivo reasignación es requerido");
			loadInfo();
		}
	}

	public void filtrarUsuarios() {
		asignacionMasivaBean.setUserNameFilter(asignacionMasivaBean.getUserNameFilter().trim());
		if (asignacionMasivaBean.getUserNameFilter().isEmpty()) {
			JsfUtil.addMessageErrorForComponent("userName", JsfUtil.getMessageFromBundle(null,
					"javax.faces.component.UIInput.REQUIRED", "Filtrar usuario"));
			return;
		}
		
		if(esAdminAreas()){
			loadInfoAP();
		}else{
			loadInfo();
		}	
		
	}

	public void recargarUsuarios() {
		asignacionMasivaBean.setUserNameFilter(null);
		loadInfo();
	}
	
	private boolean esAdminAreas(){
		try {
			
			List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
			Integer adminInst=0;
			for (Rol rol : rolesusuario) {
				if (rol.getDescripcion().contains("admin")){
					adminInst=1;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
					adminInst=2;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
					adminInst=3;
					break;
				}else{
					adminInst=0;
				}			
			}
			
			if(adminInst == 3)
				return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void modificacionObservacionesEstudio(Long id, String actualUsuario, String anteriorUsuario){
		try {
			
			ProyectoLicenciaCoa proyecto = new ProyectoLicenciaCoa();
			Map<String, Object> variables =procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), id);
			
			String tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			int numeroRevision = 1;
			String revision = (String) variables.get("numeroRevision");
			if(revision != null) 
				numeroRevision = Integer.parseInt(revision);
			
			proyecto = proyectoFacade.buscarProyecto(tramite);
			InformacionProyectoEia informacion = informacionProyectoEiaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);								
			
			List<ObservacionesEsIA> observacionesPendientes = observacionesEsIAFacade.listarPorIdClaseNoCorregidasTodas(informacion.getId());
			
			for(ObservacionesEsIA observacion : observacionesPendientes){
				
				String[] cadena = observacion.getNombreClase().split("_");
				String nombreClase = "";
				if(cadena.length > 2){
					if(!cadena[2].equals("6")){
						nombreClase = cadena[0]+"_"+cadena[2];
						observacion.setNombreClase(nombreClase);
						observacionesEsIAFacade.guardar(observacion);
					}else{
						
						if(cadena[1].toString().equals(anteriorUsuario)){
							nombreClase = cadena[0]+"_"+actualUsuario+"_"+cadena[2];
							observacion.setNombreClase(nombreClase);
							observacionesEsIAFacade.guardar(observacion);
							
							InformeTecnicoEsIA informeTecnico = new InformeTecnicoEsIA();
							informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInformeNroRevision(informacion, Integer.valueOf(cadena[2].toString()), numeroRevision);
							if(informeTecnico != null && informeTecnico.getId() != null){
								informeTecnico.setUsuarioCreacion(actualUsuario);
								informeTecnicoEsIAFacade.guardar(informeTecnico);
							}
							
						}
					}
				}								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
