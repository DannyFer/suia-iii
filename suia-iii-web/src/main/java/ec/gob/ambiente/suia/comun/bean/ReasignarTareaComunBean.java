/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.kie.api.task.model.Task;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.carga.facade.CargaLaboralFacade;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@ManagedBean
@SessionScoped
public class ReasignarTareaComunBean extends ComunBean {

	private static final long serialVersionUID = 38127765056634918L;

	@Getter
	@Setter
	private long processId;

	@Getter
	@Setter
	private String deploymentId;

	@Getter
	@Setter
	private long currentTaskToComplete;

	@Getter
	@Setter
	private String taskName;

	@Getter
	@Setter
	private Task task;

	@Getter
	@Setter
	private String variableFormUserName;

	@Getter
	private List<Usuario> users;

	private Map<String, Object> paramsTaskToComplete;

	@Getter
	private boolean onStartedTask;

	private boolean completeCurrentTask;

	private boolean processNotStarted;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private CargaLaboralFacade cargaLaboralFacade;

	@EJB
	private AsignarTareaFacade asignarTareaFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
    private ConexionBpms conexionBpms;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

	/**
	 * <b> Navega a la pagina de asignar usuarios y asigna a la mensajenotificacion del proceso el usuario
	 * seleccionado. </b>
	 * 
	 * @param processId
	 *            id del proceso
	 * @param taskName
	 *            nombre de la tarea que se pretende asignar, es lo que se muestra en la pagina
	 * @param variableForUserName
	 *            mensajenotificacion del proceso que recibe el usuario seleccionado
	 * @param role
	 *            rol para consultar y mostrar el listado de candidatos
	 * @param subArea
	 *            subarea para filtrar los posibles cabdidatos
	 * @param nextURL
	 *            siguiente url al completar la asignacion, si es null va a la bandeja de trabajo
	 * @param completeOperation
	 *            logica a ejecutar al completar la operacion
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
	 *          </p>
	 */
	public void initFunctionOnNotStatartedTask(long processId, String taskName, String variableForUserName, String role,
			String subArea, String nextURL, CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.processId = processId;
		this.taskName = taskName;
		this.variableFormUserName = variableForUserName;
		this.onStartedTask = false;
		this.deploymentId = Constantes.getDeploymentId();
		this.completeCurrentTask = true;
		loadUsers(role, subArea);
	}

	public void initFunctionOnNotStatartedTask(String deploymentId, long processId, String taskName,
			String variableForUserName, String role, String subArea, String nextURL,
			CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.processId = processId;
		this.taskName = taskName;
		this.variableFormUserName = variableForUserName;
		this.onStartedTask = false;
		this.deploymentId = deploymentId;
		this.completeCurrentTask = true;
		 try {
				Map<String,Object>variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
				Integer codigoSector=variables.containsKey("codigoSector")?Integer.valueOf((String)variables.get("codigoSector")):0;

				List<Usuario> tecnicosSociales=new ArrayList<Usuario>();
				List<Usuario> tecnicosCartografos=new ArrayList<Usuario>();
				List<Usuario> tecnicosElectricos=new ArrayList<Usuario>();
				List<Usuario> tecnicosMineros=new ArrayList<Usuario>();
				List<Usuario> tecnicosOtrosSectores=new ArrayList<Usuario>();
				users=new ArrayList<Usuario>();
				if(codigoSector==2){
					if (role.equals("TÉCNICO SOCIAL") || role.equals("TÉCNICO SOCIAL MAE")) {
						String roleSocial="TÉCNICO SOCIAL MAE MINERÍA";

						tecnicosSociales = cargaLaboralFacade.cargaLaboralPorUsuarioArea(roleSocial, subArea);
					}else 
					if(role.equals("TÉCNICO CARTÓGRAFO") || role.equals("TÉCNICO CARTÓGRAFO MAE") || role.equals("TÉCNICO CARTOGRAFO") ){
						String roleCartografo="TÉCNICO CARTÓGRAFO MAE MINERÍA";

						tecnicosCartografos = cargaLaboralFacade.cargaLaboralPorUsuarioArea(roleCartografo, subArea);
					}else 
					if(role.equals("TÉCNICO ELÉCTRICO") || role.equals("TÉCNICO ELÉCTRICO MAE") || role.equals("TÉCNICO ELECTRICO")){
						String rolElectrico="TÉCNICO ELÉCTRICO MAE MINERÍA";

						tecnicosElectricos = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolElectrico, subArea);
					}else
					if(role.equals("TÉCNICO MINERÍA") || role.equals("TÉCNICO MINERÍA MAE") || role.equals("TÉCNICO MINERIA")){
						String rolMinero="TÉCNICO MINERÍA MAE";

						tecnicosMineros = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolMinero, subArea);
					}else
					if(role.equals("TÉCNICO OTROS SECTORES") ||role.equals("TÉCNICO OTROS SECTORES MAE")){
						String rolOtrosSectores="TÉCNICO OTROS SECTORES MAE";

						tecnicosMineros = cargaLaboralFacade.cargaLaboralPorUsuarioArea(rolOtrosSectores, subArea);
					}else{
						loadUsers(role, subArea);
					}
					if(tecnicosSociales.size()>0){
						for (Usuario usuario : tecnicosSociales) {
							users.add(usuario);
						}
					}
					if(tecnicosCartografos.size()>0){
						for (Usuario usuario : tecnicosCartografos) {
							users.add(usuario);
						} 


					}
					if(tecnicosElectricos.size()>0){
						for (Usuario usuario : tecnicosElectricos) {
							users.add(usuario);
						}
					}
					if(tecnicosMineros.size()>0){
						for (Usuario usuario : tecnicosMineros) {
							users.add(usuario);
						}
					}
					if(tecnicosOtrosSectores.size()>0){
						for (Usuario usuario : tecnicosOtrosSectores) {
							users.add(usuario);
						}
					}

					if (onStartedTask && task != null && task.getTaskData().getActualOwner() != null)
						for (Usuario u : users) {
							if (u.getNombre().equals(task.getTaskData().getActualOwner().getId())) {
								u.setSelectable(true);
								break;
							}
						}
				}else{
					loadUsers(role, subArea);
				}
			 } catch (JbpmException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			}
			
	}

	public void initFunctionOnNotStatartedProcess(String taskName, String role, String subArea, String nextURL,
			CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.taskName = taskName;
		this.previousURL = nextURL;
		this.processNotStarted = true;
		loadUsers(role, subArea);
	}

	/**
	 * <b> Navega a la pagina de asignar usuarios y asigna a la mensajenotificacion del proceso el usuario seleccionado.
	 * Tambien da por completada la tarea desde donde se ejecuta (se pasa por parametros) </b>
	 * 
	 * @param processId
	 *            id del proceso
	 * @param currentTaskToComplete
	 *            id de la tarea que se va a completar al asignar (debe ser desde donde se ejecuta)
	 * @param paramsTaskToComplete
	 *            parametros para pasarle a la tarea que se va a completar, puede ser null
	 * @param taskName
	 *            nombre de la tarea que se pretende asignar, es lo que se muestra en la pagina
	 * @param variableForUserName
	 *            mensajenotificacion del proceso que recibe el usuario seleccionado
	 * @param role
	 *            rol para consultar y mostrar el listado de candidatos
	 * @param subArea
	 *            subarea para filtrar los posibles cabdidatos
	 * @param nextURL
	 *            siguiente url al completar la asignacion, si es null va a la bandeja de trabajo
	 * @param completeOperation
	 *            logica a ejecutar al completar la operacion
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
	 *          </p>
	 */
	public void initFunctionOnNotStatartedTask(long processId, long currentTaskToComplete,
			Map<String, Object> paramsTaskToComplete, String taskName, String variableForUserName, String role,
			String subArea, String nextURL, CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.processId = processId;
		this.currentTaskToComplete = currentTaskToComplete;
		this.taskName = taskName;
		this.variableFormUserName = variableForUserName;
		this.onStartedTask = false;
		this.completeCurrentTask = true;
		this.paramsTaskToComplete = paramsTaskToComplete;

		loadUsers(role, subArea);
	}

	public void initFunctionOnNotStatartedTask(long processId, String taskName, String variableForUserName, String role,
			String subArea, String nextURL, boolean needNavigateToFunction, CompleteOperation completeOperation) {
		super.initFunction(nextURL, needNavigateToFunction, completeOperation);
		this.processId = processId;
		this.taskName = taskName;
		this.variableFormUserName = variableForUserName;
		this.onStartedTask = false;
		this.completeCurrentTask = true;

		loadUsers(role, subArea);
	}

	/**
	 * <b> Navega a la pagina de asignar usuarios y reasigna la tarea que se pasa como parametro </b>
	 * 
	 * @param task
	 *            instancia de la tarea que se pretende reasignar
	 * @param role
	 *            rol para consultar y mostrar el listado de candidatos
	 * @param subArea
	 *            subarea para filtrar los posibles cabdidatos
	 * @param nextURL
	 *            siguiente url al completar la asignacion, si es null va a la bandeja de trabajo
	 * @param completeOperation
	 *            logica a ejecutar al completar la operacion
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
	 *          </p>
	 */
	public void initFunctionOnStatartedTask(Task task, String role, String subArea, String nextURL,
			CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.task = task;
		this.onStartedTask = true;

		loadUsers(role, subArea);
	}

	public void initFunctionOnNotStatartedTaskNoNavigation(String deploymentId, long processId,
			long currentTaskToComplete, Map<String, Object> paramsTaskToComplete, String taskName,
			String variableForUserName, String role, String subArea, String nextURL,
			CompleteOperation completeOperation) {
		super.initFunction(nextURL, false, completeOperation);
		this.processId = processId;
		this.currentTaskToComplete = currentTaskToComplete;
		this.taskName = taskName;
		this.variableFormUserName = variableForUserName;
		this.onStartedTask = false;
		this.completeCurrentTask = true;
		this.paramsTaskToComplete = paramsTaskToComplete;
		this.deploymentId = deploymentId;
		loadUsers(role, subArea);
	}

	private void loadUsers(String role, String subArea) {
		users = cargaLaboralFacade.cargaLaboralPorUsuarioArea(role, subArea);
		if (onStartedTask && task != null && task.getTaskData().getActualOwner() != null)
			for (Usuario u : users) {
				if (u.getNombre().equals(task.getTaskData().getActualOwner().getId())) {
					u.setSelectable(true);
					break;
				}
			}
	}

	@Override
	public String getFunctionURL() {
		return "/comun/reasignarTarea.jsf";
	}

	@Override
	public void cleanData() {
		processId = 0;
		currentTaskToComplete = 0;
		taskName = null;
		task = null;
		users = null;
		onStartedTask = false;
		completeCurrentTask = false;
		processNotStarted = false;
		paramsTaskToComplete = null;
		previousURL = null;
	}

	@Override
	public boolean executeBusinessLogic(Object object) {
		String userName = null;
		for (Usuario u : users) {
			if (u.isSelectable()) {
				userName = u.getNombre();
				break;
			}
		}
		if (userName == null) {
			JsfUtil.addMessageError("Debe seleccionar un usuario para completar esta operación");
			return false;
		}
		if (processNotStarted) {
			executeOperation(usuarioFacade.buscarUsuario(userName));
			return true;
		}

		if (onStartedTask) {
			try {
				procesoFacade.reasignarTarea(loginBean.getUsuario(), task.getId(),
						task.getTaskData().getActualOwner().getId(), userName,conexionBpms.deploymentId(task.getId(), "S"));
				executeOperation(procesoFacade.getTaskById(loginBean.getUsuario(), task.getId()));
				JsfUtil.addMessageInfo("Se ha reasignado la tarea satisfactoriamente al usuario '" + userName + "'");
			} catch (JbpmException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Map<String, Object> newUser = new HashMap<String, Object>();
				newUser.put(variableFormUserName, userName);
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), newUser);
				if (completeCurrentTask) {
					if (currentTaskToComplete == 0)
						currentTaskToComplete = JsfUtil.getBean(BandejaTareasBean.class).getTarea().getTaskId();
					if (processId == 0)
						processId = JsfUtil.getBean(BandejaTareasBean.class).getProcessId();
						procesoFacade.aprobarTarea(loginBean.getUsuario(), currentTaskToComplete, processId,
							paramsTaskToComplete);
						if(JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessId().contains("GeneradorDesechos")){
						procesoFacade.envioSeguimientoRGD(loginBean.getUsuario(), JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId());
						}else {
							procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), JsfUtil.getBean(BandejaTareasBean.class).getTarea().getProcessInstanceId());
						}
				}
				executeOperation(userName);
				JsfUtil.addMessageInfo("Se ha asignado la tarea satisfactoriamente al usuario '" + userName + "'");
			} catch (JbpmException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void updateSelectedUser(Usuario usuario) {
		for (Usuario u : users)
			u.setSelectable(false);
		usuario.setSelectable(true);
	}

}