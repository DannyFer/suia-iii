/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.kie.api.task.model.Task;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.carga.facade.CargaLaboralFacade;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.comun.classes.VariableForRole;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

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
public class ReasignarTareaMultipleComunBean extends ComunBean {

	private static final long serialVersionUID = 38127765056634918L;

	@Getter
	@Setter
	private long processId;

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
	private Map<String, String> variablesFormUsersNames;

	@Getter
	private Map<String, VariableForRole> variablesFormUsersNamesAndRoles;

	private Map<String, List<Usuario>> usersForRoles;

	@Getter
	private List<Usuario> users;

	@Getter
	private List<FillVariable> values;

	@Getter
	private FillVariable currentFillVariable;

	private Map<String, Object> paramsTaskToComplete;

	private boolean completeCurrentTask;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private CargaLaboralFacade cargaLaboralFacade;

	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	/**
	 * 
	 * <b> Navega a la pagina de asignar usuarios y asigna a las variables del proceso los usuarios seleccionados. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
	 *          </p>
	 * @param processId
	 *            id del proceso
	 * @param taskName
	 *            nombre de la tarea que se pretende asignar, es lo que se muestra en la pagina
	 * @param variablesForUsersNames
	 *            variables del proceso que reciben los usuarios seleccionados
	 * @param role
	 *            rol para consultar y mostrar el listado de candidatos
	 * @param subArea
	 *            subarea para filtrar los posibles cabdidatos
	 * @param nextURL
	 *            siguiente url al completar la asignacion, si es null va a la bandeja de trabajo
	 * @param completeOperation
	 *            logica a ejecutar al completar la operacion
	 */
	public void initFunctionOnNotStatartedTask(long processId, String taskName,
			Map<String, String> variablesForUsersNames, String role, String subArea, String nextURL,
			CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.processId = processId;
		this.taskName = taskName;
		this.variablesFormUsersNames = variablesForUsersNames;

		loadUsers(role, subArea);
	}

	/**
	 * 
	 * <b> Navega a la pagina de asignar usuarios y asigna a las variables del proceso los usuarios seleccionados.
	 * Tambien da por completada la tarea desde donde se ejecuta (se pasa por parametros) </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
	 *          </p>
	 * @param processId
	 *            id del proceso
	 * @param currentTaskToComplete
	 *            id de la tarea que se va a completar al asignar (debe ser desde donde se ejecuta)
	 * @param paramsTaskToComplete
	 *            parametros para pasarle a la tarea que se va a completar, puede ser null
	 * @param taskName
	 *            nombre de la tarea que se pretende asignar, es lo que se muestra en la pagina
	 * @param variablesForUsersNames
	 *            variables del proceso que reciben los usuarios seleccionados
	 * @param role
	 *            rol para consultar y mostrar el listado de candidatos
	 * @param subArea
	 *            subarea para filtrar los posibles cabdidatos
	 * @param nextURL
	 *            siguiente url al completar la asignacion, si es null va a la bandeja de trabajo
	 * @param completeOperation
	 *            logica a ejecutar al completar la operacion
	 */
	public void initFunctionOnNotStatartedTask(long processId, long currentTaskToComplete,
			Map<String, Object> paramsTaskToComplete, String taskName, Map<String, String> variablesForUsersNames,
			String role, String subArea, String nextURL, CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.processId = processId;
		this.currentTaskToComplete = currentTaskToComplete;
		this.taskName = taskName;
		this.variablesFormUsersNames = variablesForUsersNames;
		this.completeCurrentTask = true;
		this.paramsTaskToComplete = paramsTaskToComplete;

		loadUsers(role, subArea);
	}

	/**
	 * 
	 * <b> Navega a la pagina de asignar usuarios y asigna a las variables del proceso los usuarios seleccionados. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
	 *          </p>
	 * @param processId
	 *            id del proceso
	 * @param taskName
	 *            nombre de la tarea que se pretende asignar, es lo que se muestra en la pagina
	 * @param variablesFormUsersNamesAndRoles
	 *            variables del proceso que reciben los usuarios seleccionados, especificando rol, area para cada
	 *            mensajenotificacion
	 * @param role
	 *            rol para consultar y mostrar el listado de candidatos
	 * @param subArea
	 *            subarea para filtrar los posibles cabdidatos
	 * @param nextURL
	 *            siguiente url al completar la asignacion, si es null va a la bandeja de trabajo
	 * @param completeOperation
	 *            logica a ejecutar al completar la operacion
	 */
	public void initFunctionOnNotStatartedTask(long processId, String taskName,
			Map<String, VariableForRole> variablesFormUsersNamesAndRoles, String nextURL,
			CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.processId = processId;
		this.taskName = taskName;
		this.variablesFormUsersNamesAndRoles = variablesFormUsersNamesAndRoles;

		loadUsersForRoles();
	}

	/**
	 * 
	 * <b> Navega a la pagina de asignar usuarios y asigna a las variables del proceso los usuarios seleccionados.
	 * Tambien da por completada la tarea desde donde se ejecuta (se pasa por parametros) </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 08/01/2015]
	 *          </p>
	 * @param processId
	 *            id del proceso
	 * @param currentTaskToComplete
	 *            id de la tarea que se va a completar al asignar (debe ser desde donde se ejecuta)
	 * @param paramsTaskToComplete
	 *            parametros para pasarle a la tarea que se va a completar, puede ser null
	 * @param taskName
	 *            nombre de la tarea que se pretende asignar, es lo que se muestra en la pagina
	 * @param variablesFormUsersNamesAndRoles
	 *            variables del proceso que reciben los usuarios seleccionados, especificando rol, area para cada
	 *            mensajenotificacion
	 * @param role
	 *            rol para consultar y mostrar el listado de candidatos
	 * @param subArea
	 *            subarea para filtrar los posibles cabdidatos
	 * @param nextURL
	 *            siguiente url al completar la asignacion, si es null va a la bandeja de trabajo
	 * @param completeOperation
	 *            logica a ejecutar al completar la operacion
	 */
	public void initFunctionOnNotStatartedTask(long processId, long currentTaskToComplete,
			Map<String, Object> paramsTaskToComplete, String taskName,
			Map<String, VariableForRole> variablesFormUsersNamesAndRoles, String nextURL,
			CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.processId = processId;
		this.currentTaskToComplete = currentTaskToComplete;
		this.taskName = taskName;
		this.variablesFormUsersNamesAndRoles = variablesFormUsersNamesAndRoles;
		this.completeCurrentTask = true;
		this.paramsTaskToComplete = paramsTaskToComplete;

		loadUsersForRoles();
	}

	private void loadUsers(String role, String subArea) {
		users = cargaLaboralFacade.cargaLaboralPorUsuarioArea(role, subArea);
		// cargaLaboralFacade.cargaLaboralPorUsuario(role, subArea,
		// Constantes.getDeploymentId(),loginBean.getNombreUsuario(),loginBean.getPassword());
		initVariablesForUser();
	}

	private void loadUsersForRoles() {
		usersForRoles = new HashMap<String, List<Usuario>>();
		Iterator<String> iterator = variablesFormUsersNamesAndRoles.keySet().iterator();
		while (iterator.hasNext()) {
			String variable = (String) iterator.next();
			VariableForRole variableForRole = variablesFormUsersNamesAndRoles.get(variable);
			usersForRoles.put(variable, cargaLaboralFacade.cargaLaboralPorUsuario(variableForRole.getRole(),
					variableForRole.getArea(), Constantes.getDeploymentId(), loginBean.getNombreUsuario(),
					loginBean.getPassword()));
		}
		initVariablesForUserRoles();
	}

	private void initVariablesForUser() {
		values = new ArrayList<FillVariable>();
		Iterator<String> iterator = variablesFormUsersNames.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			values.add(new FillVariable(variablesFormUsersNames.get(key), key));
		}
	}

	private void initVariablesForUserRoles() {
		values = new ArrayList<FillVariable>();
		Iterator<String> iterator = variablesFormUsersNamesAndRoles.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			VariableForRole variableForRole = variablesFormUsersNamesAndRoles.get(key);
			values.add(new FillVariable(variableForRole.getVisibleName(), key));
		}
	}

	@Override
	public String getFunctionURL() {
		return "/comun/reasignarTareaMultiple.jsf";
	}

	@Override
	public void cleanData() {
		processId = 0;
		currentTaskToComplete = 0;
		taskName = null;
		task = null;
		users = null;
		completeCurrentTask = false;
		paramsTaskToComplete = null;
		values = null;
		currentFillVariable = null;
		variablesFormUsersNamesAndRoles = null;
		usersForRoles = null;
	}

	@Override
	public boolean executeBusinessLogic(Object object) {
		try {
			Map<String, Object> newUsers = new HashMap<String, Object>();
			for (FillVariable fv : values) {
				newUsers.put(fv.getVariable(), fv.getUser().getNombre());
			}

			Map<String, Usuario> users = new HashMap<String, Usuario>();
			for (FillVariable fv : values) {
				users.put(fv.getVariable(), fv.getUser());
			}

			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), processId, newUsers);
			if (completeCurrentTask) {
				procesoFacade.aprobarTarea(loginBean.getUsuario(), currentTaskToComplete, processId,
						paramsTaskToComplete);
			}
			executeOperation(users);
			JsfUtil.addMessageInfo("Se ha(n) asignado la(s) tarea(s) satisfactoriamente");
		} catch (JbpmException e) {
			e.printStackTrace();
		}

		return true;
	}

	public void selectVariable(FillVariable variable, boolean clear) {
		currentFillVariable = variable;
		if (clear) {
			if (currentFillVariable.getUser() != null) {
				currentFillVariable.getUser().setAvailable(true);
				currentFillVariable.getUser().setSelectable(false);
			}
			currentFillVariable.setUser(null);
		} else {
			if (variablesFormUsersNamesAndRoles != null)
				users = usersForRoles.get(variable.getVariable());
			for (FillVariable fv : values) {
				if (fv.getUser() != null)
					fv.getUser().setAvailable(false);
			}
			if (currentFillVariable.getUser() != null)
				currentFillVariable.getUser().setAvailable(true);
		}
	}

	public void updateSelectedUser(Usuario usuario) {
		for (Usuario u : users)
			u.setSelectable(false);
		usuario.setSelectable(true);
		currentFillVariable.setUser(usuario);
		for (FillVariable fv : values) {
			if (fv.getUser() != null)
				fv.getUser().setSelectable(true);
		}
	}

	public boolean isComplete() {
		int selected = 0;
		if (values == null)
			return false;
		for (FillVariable fv : values)
			selected += fv.getUser() != null ? 1 : 0;
		return selected == values.size();
	}

	public class FillVariable {

		@Getter
		@Setter
		private Usuario user;

		@Getter
		@Setter
		private String visibleName;

		@Getter
		@Setter
		private String variable;

		public FillVariable(String visibleName, String variable) {
			this.visibleName = visibleName;
			this.variable = variable;
		}
	}

}
