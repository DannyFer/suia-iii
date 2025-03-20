/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.proceso.facade;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.ProcessBeanFacade;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EstadoProceso;

@Stateless
public class ProcesoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB(lookup = Constantes.JBPM_EJB_PROCESS_BEAN)
	private ProcessBeanFacade processBeanFacade;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;
	
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();

	public synchronized long iniciarProceso(Usuario usuario, String nombreProceso, String tramite, Map<String, Object> parametros)
			throws JbpmException {
		if (parametros == null)
			parametros = new HashMap<String, Object>();
		parametros.put(Constantes.VARIABLE_PROCESO_TRAMITE, tramite);
		if(parametros != null) {
			String text="";
			for (Map.Entry<String, Object> entry : parametros.entrySet()) {
				if(entry.getValue() instanceof String) {
					text = (String) entry.getValue();
					//Eliminamos las tildes y los caracteres especiales.
					if (text.contains("<") && text.contains(">"))//Asumimos que es un texto en formato HTML
						text = reemplazarCaracteresEspecialesHTML(text);
//					else
//						text =reemplazarCaracteresEspeciales(text);

					parametros.put(entry.getKey(), new String((text).getBytes(Charset.forName("UTF-8"))));
				}
			}
		}
		Long processId = processBeanFacade.startProcess(nombreProceso, parametros, Constantes.getDeploymentId(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());

		envioSeguimientoRGD(usuario, processId);
		return processId;
	}

	public synchronized void aprobarTarea(Usuario usuario, Long taskId, Long processId, Map<String, Object> data)
			throws JbpmException {
		if(data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				if(entry.getValue() instanceof String) {
					data.put(entry.getKey(), new String(((String) entry.getValue()).getBytes(Charset.forName("UTF-8"))));
				}
			}
		}
		taskBeanFacade.approveTask(usuario.getNombre(), taskId, processId, data, usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(),
				Constantes.getNotificationService());
		try
		{
			envioSeguimientoRGD(usuario, processId);	
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void buscarAprobarActualTareaProceso(Usuario usuario, Long processInstanceId, Map<String, Object> data)
			throws JbpmException {
		Long taskId = getCurrenTask(usuario, processInstanceId).getId();
		if(data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				if(entry.getValue() instanceof String) {
					data.put(entry.getKey(), new String(((String) entry.getValue()).getBytes(Charset.forName("UTF-8"))));
				}
			}
		}
		taskBeanFacade.approveTask(usuario.getNombre(), taskId, processInstanceId, data, usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(),
				Constantes.getNotificationService());
	}

	public Map<String, Object> recuperarVariablesTarea(Usuario usuario, Long taskId) throws JbpmException {
		Map<String, Object> variables = taskBeanFacade.getTaskVariables(taskId, Constantes.getDeploymentId(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());
		return variables;
	}

	public synchronized void modificarVariablesProceso(Usuario usuario, long processInstanceID, Map<String, Object> params)
			throws JbpmException {

		String text="";
		ProcessInstanceLog proces = getProcessInstanceLog(usuario, processInstanceID);
		if(params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {

				if(entry.getValue() instanceof String) {

					text = (String) entry.getValue();
					//Eliminamos las tildes y los caracteres especiales.
					//NOTA: Lo ideal sería utilizar siempre el método de codificación HTML y luego en el bpm
					// tener tareas de scripts que reemplacen los caracteres correctos antes de utilizar los valos de las variables.
					if (text.contains("<") && text.contains(">"))//Asumimos que es un texto en formato HTML
						text = reemplazarCaracteresEspecialesHTML(text);
//					else
//						text =reemplazarCaracteresEspeciales(text);

					params.put(entry.getKey(), new String((text).getBytes(Charset.forName("UTF-8"))));
				}
			}
		}
		processBeanFacade.setProcessVariables(processInstanceID, params, proces.getExternalId(), usuario.getNombre(),
				usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}
	
	public synchronized void modificarVariablesProcesoH(Usuario usuario, long processInstanceID, Map<String, Object> params)
			throws JbpmException {

		String text="";
		ProcessInstanceLog proces = getProcessInstanceLogH(usuario, processInstanceID);
		if(params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {

				if(entry.getValue() instanceof String) {

					text = (String) entry.getValue();
					//Eliminamos las tildes y los caracteres especiales.
					//NOTA: Lo ideal sería utilizar siempre el método de codificación HTML y luego en el bpm
					// tener tareas de scripts que reemplacen los caracteres correctos antes de utilizar los valos de las variables.
					if (text.contains("<") && text.contains(">"))//Asumimos que es un texto en formato HTML
						text = reemplazarCaracteresEspecialesHTML(text);
//					else
//						text =reemplazarCaracteresEspeciales(text);

					params.put(entry.getKey(), new String((text).getBytes(Charset.forName("UTF-8"))));
				}
			}
		}
		processBeanFacade.setProcessVariables(processInstanceID, params, proces.getExternalId(), usuario.getNombre(),
				usuario.getPassword(), Constantes.getUrlBusinessCentralHidro(), Constantes.getRemoteApiTimeout());
	}

	private static final String ORIGINAL
			= "ÁáÉéÍíÓóÚúÑñÜü";
//	private static final String REPLACEMENT
//			= "AaEeIiOoUuNnUu";
	private static final String REPLACEMENT_HTML
			= "&Aacute;--&aacute;--&Eacute;--&eacute;--" +
			"&Iacute;--&iacute;--&Oacute;--&oacute;--&Uacute;--" +
			"&uacute;--&Ntilde;--&ntilde;--U--u";

	/***
	 * @Autor Denis Linares
	 * Método que reemplaza las tíldes y caracteres especiales de una cadena te texto plano por otros.
	 * Esto se utiliza para evitar errores de codificación en el BPM.
	 * @param value
	 * @return
	 */
//	private String reemplazarCaracteresEspeciales(String value) {
//
//		if (value != null && !value.isEmpty()) {
//			char[] array = value.toCharArray();
//			for (int index = 0; index < array.length; index++) {
//				int pos = ORIGINAL.indexOf(array[index]);
//				if (pos > -1) {
//					array[index] = REPLACEMENT.charAt(pos);
//				}
//			}
//			return new String(array);
//		}
//
//		return value;
//	}

	/***
	 * @Autor Denis Linares
	 * Método que reemplaza las tíldes y caracteres especiales de una cadena de texto en formato HTML por otros.
	 * Esto se utiliza para evitar errores de codificación en el BPM.
	 * @param value
	 * @return
	 */
	private String reemplazarCaracteresEspecialesHTML(String value){

		if (value != null && !value.isEmpty()) {
			String[] replacementArr = REPLACEMENT_HTML.split("--");
			char[] array = value.toCharArray();

			ArrayList<String> foundList = new ArrayList<String>();
			String character;
			for (int index = 0; index < array.length; index++) {
				int pos = ORIGINAL.indexOf(array[index]);

				character = new Character(array[index]).toString();
				if (pos > -1 && !foundList.contains(character)) {
					value = value.replace(character, replacementArr[pos]);
					foundList.add(character);
				}
			}
		}
		return value;
	}

	public Map<String, Object> recuperarVariablesProceso(Usuario usuario, long processInstanceID) throws JbpmException {
		return processBeanFacade.getProcessVariables(processInstanceID, Constantes.getDeploymentId(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());
	}

	public long recuperarIdTareaActual(Usuario usuario, Long processInstanceId) throws JbpmException {
		return taskBeanFacade.actualTaskId(usuario.getNombre(), processInstanceId, Constantes.getDeploymentId(),
				usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}

	public ProcessInstance getProcessInstance(Usuario usuario, long processInstanceID) throws JbpmException {
		return processBeanFacade.getProcessInstance(processInstanceID, Constantes.getDeploymentId(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());
	}

	public List<TaskSummary> getAllTaskAssigned(Usuario usuario) throws JbpmException {
		return taskBeanFacade.retrieveTaskList(usuario.getNombre(), usuario.getNombre(), Constantes.getDeploymentId(),
				usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());

	}

	public ProcessInstanceLog getProcessInstanceLog(Usuario usuario, long processInstanceID) throws JbpmException {
		return processBeanFacade.getProcessInstanceLog(processInstanceID, Constantes.getDeploymentId(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());
	}
	
	public ProcessInstanceLog getProcessInstanceLogH(Usuario usuario, long processInstanceID) throws JbpmException {
		return processBeanFacade.getProcessInstanceLog(processInstanceID, Constantes.getDeploymentIdHidro(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentralHidro(),
				Constantes.getRemoteApiTimeout());
	}

	public synchronized void reasignarTarea(Usuario usuario, long taskId, String currentActorId, String targetUserId,String deploymentId)
			throws JbpmException {
		taskBeanFacade.reassignTask(taskId, currentActorId, targetUserId, deploymentId,
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());
	}
	
	public synchronized void reasignarTareaH(Usuario usuario, long taskId, String currentActorId, String targetUserId,String deploymentId)
			throws JbpmException {
		taskBeanFacade.reassignTask(taskId, currentActorId, targetUserId, deploymentId,
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentralHidro(),
				Constantes.getRemoteApiTimeout());
	}

	public List<TaskSummary> getTaskCompletedReserved(Usuario usuario, Long processInstanceID) throws JbpmException {
		return processBeanFacade.getTaskSumaryByProcessId(processInstanceID, Constantes.getDeploymentId(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());
	}

	public List<TaskSummary> getTaskReservedInProgress(Usuario usuario, Long processInstanceID) throws JbpmException {
		return processBeanFacade.getTaskSumaryByProcessIdWithoutCompletedStatus(processInstanceID,
				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}

	public TaskSummary getCurrenTask(Usuario usuario, long processInstanceId) throws JbpmException {
		return taskBeanFacade.actualTaskSummary(usuario.getNombre(), processInstanceId, Constantes.getDeploymentId(),
				usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}

	public synchronized void abortProcess(Usuario usuario, long processInstanceId) throws JbpmException {
		processBeanFacade.abortProcess(processInstanceId, Constantes.getDeploymentId(), usuario.getNombre(),
				usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}
	
	public List<TaskSummary> obtenerTareaReserved(long processInstanceId,Usuario usuario) throws JbpmException {
		return taskBeanFacade.retrieveReservedTasks(processInstanceId, "en-UK", Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}
	
	public synchronized void abortProcessDelete(long taskId,Usuario usuario, Usuario usuarioTarea) throws JbpmException {
		taskBeanFacade.changeTaskToReleaseStatus(taskId, usuarioTarea.getNombre(), Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout()); 
	}

	public Map<String, Object> getProcesosProyecto(Usuario usuario, String nombreVariableProceso,
			String valorVariableProceso) throws JbpmException {
		List<VariableInstanceLog> listaVariables = processBeanFacade.getVariableValueInstanceLog(
				Constantes.getDeploymentId(), usuario.getNombre(),
				usuario.getPassword(),// "f78b00ced8a7337426e2c91b76682713"
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), nombreVariableProceso,
				valorVariableProceso);
		Map<String, Object> valoresRecuperados = new HashMap<String, Object>();
		for (int i = 0; i < listaVariables.size(); i++) {
			try {
				ProcessInstanceLog process = getProcessInstanceLog(usuario, listaVariables.get(i)
						.getProcessInstanceId());
				try {
					List<TaskSummary> listaTareas = obtenerEstados(usuario, process.getProcessInstanceId());
					for (int j = 0; j < listaTareas.size(); j++) {
						// t=tarea
						valoresRecuperados.put("t_" + process.getProcessInstanceId() + "_" + j, listaTareas.get(j));
					}
				} catch (JbpmException e) {
					e.printStackTrace();
				}
				// ep=estado proceso
				valoresRecuperados.put("ep_" + process.getProcessInstanceId() + "_" + i,
						EstadoProceso.getNombreEstado(process.getStatus()));
			} catch (JbpmException e) {
				e.printStackTrace();
			}
		}
		return valoresRecuperados;
	}

	private List<TaskSummary> obtenerEstados(Usuario usuario, Long processId) throws JbpmException {
		return processBeanFacade.getTaskSumaryByProcessIdWithoutCompletedStatus(processId,
				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}

	public List<VariableInstanceLog> getCurrentValuesVariableInstanceLogs(Usuario usuario, String nameVariableProcess)
			throws JbpmException {

		Map<Long, VariableInstanceLog> variables = new HashMap<Long, VariableInstanceLog>();

		List<VariableInstanceLog> listVariable = processBeanFacade.getVariableNameInstanceLog(
				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), nameVariableProcess, false);

		for (VariableInstanceLog variableInstanceLog : listVariable) {
			if (variables.containsKey(variableInstanceLog.getProcessInstanceId())) {
				if (variables.get(variableInstanceLog.getProcessInstanceId()).getId() < variableInstanceLog.getId())
					variables.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
			} else
				variables.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
		}

		List<VariableInstanceLog> result = new ArrayList<VariableInstanceLog>();
		result.addAll(variables.values());

		return result;
	}
	
	public List<VariableInstanceLog> getVariableInstanceLogs(Usuario usuario, String nameVariableProcess,String valueVariableProcess)
			throws JbpmException {

		Map<Long, VariableInstanceLog> variables = new HashMap<Long, VariableInstanceLog>();

		List<VariableInstanceLog> listVariable = processBeanFacade.getVariableValueInstanceLog(
				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), nameVariableProcess,
				valueVariableProcess);

		for (VariableInstanceLog variableInstanceLog : listVariable) {
			if (variables.containsKey(variableInstanceLog.getProcessInstanceId())) {
				if (variables.get(variableInstanceLog.getProcessInstanceId()).getId() < variableInstanceLog.getId())
					variables.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
			} else
				variables.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
		}

		List<VariableInstanceLog> result = new ArrayList<VariableInstanceLog>();
		result.addAll(variables.values());

		return result;
	}

	public List<ProcessInstanceLog> getProcessInstancesLogsVariableValue(Usuario usuario, String nameVariableProcess,
			String valueVariableProcess) throws JbpmException {
		List<ProcessInstanceLog> flowsByProject = new ArrayList<ProcessInstanceLog>();
		
		List<Long> listVariable = getProcessIdVariablesValue(nameVariableProcess, valueVariableProcess);

		for (int i = 0; i < listVariable.size(); i++) {
			try
			{
				ProcessInstanceLog process = getProcessInstanceLog(usuario, listVariable.get(i));
				flowsByProject.add(process);
			}
			catch(Exception e){
				System.out.println("error al recuperar el ProcessInstanceLog::"+listVariable.get(i));
			}
		}
		
		return flowsByProject;
	}

	public List<ProcessInstanceLog> getProcessInstancesLogsVariableValueUpdated(Usuario usuario,
			String nameVariableProcess, String valueVariableProcess) throws JbpmException {
		List<ProcessInstanceLog> flowsByProject = new ArrayList<ProcessInstanceLog>();

//		Map<Long, VariableInstanceLog> variables = new HashMap<Long, VariableInstanceLog>();
//
//		Map<Long, VariableInstanceLog> variablesValues = new HashMap<Long, VariableInstanceLog>();
//
//		List<VariableInstanceLog> listVariable = processBeanFacade.getVariableNameInstanceLog(
//				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
//				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), nameVariableProcess, false);
//
//		for (VariableInstanceLog variableInstanceLog : listVariable) {
//			if (variables.containsKey(variableInstanceLog.getProcessInstanceId())) {
//				if (variables.get(variableInstanceLog.getProcessInstanceId()).getId() < variableInstanceLog.getId())
//					variables.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
//			} else
//				variables.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
//		}
//
//		List<VariableInstanceLog> listVariableValues = processBeanFacade.getVariableValueInstanceLog(
//				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
//				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), nameVariableProcess,
//				valueVariableProcess);
//
//		for (VariableInstanceLog variableInstanceLog : listVariableValues) {
//			if (variablesValues.containsKey(variableInstanceLog.getProcessInstanceId())) {
//				if (variablesValues.get(variableInstanceLog.getProcessInstanceId()).getId() < variableInstanceLog
//						.getId())
//					variablesValues.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
//			} else
//				variablesValues.put(variableInstanceLog.getProcessInstanceId(), variableInstanceLog);
//		}
//
//		List<VariableInstanceLog> finalVariables = new ArrayList<VariableInstanceLog>();
//
//		for (Long pocessInstanceId : variablesValues.keySet()) {
//			if (variables.containsKey(pocessInstanceId))
//				if (variables.get(pocessInstanceId).getId() >= variablesValues.get(pocessInstanceId).getId()
//						&& variables.get(pocessInstanceId).getValue().equals(valueVariableProcess))
//					finalVariables.add(variables.get(pocessInstanceId));
//		}
//
//		for (int i = 0; i < finalVariables.size(); i++) {
//			ProcessInstanceLog process = getProcessInstanceLog(usuario, finalVariables.get(i).getProcessInstanceId());
//			if(process!=null)
//			flowsByProject.add(process);
//		}
		
		List<VariableInstanceLog> listVariableValues = processBeanFacade.getVariableValueInstanceLog(
				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), nameVariableProcess,
				valueVariableProcess);
		
		for (VariableInstanceLog log : listVariableValues) {				
			try
			{
				ProcessInstanceLog process = getProcessInstanceLog(usuario, log.getProcessInstanceId());
				flowsByProject.add(process);
			}
			catch(Exception e){
				System.out.println("error al recuperar el ProcessInstanceLog::"+log.getProcessInstanceId());
			}
		}
		return flowsByProject;
	}
	
	public Map<Long, String> getProcessInstancesIdsVariableValue(Usuario usuario, String nameVariableProcess,
			boolean procesosActivos) throws JbpmException {
		List<VariableInstanceLog> variables = processBeanFacade.getVariableNameInstanceLog(
				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), nameVariableProcess,
				procesosActivos);
		Map<Long, String> processIdsVariableValue = new HashMap<Long, String>();
		for (VariableInstanceLog variable : variables)
			processIdsVariableValue.put(variable.getProcessInstanceId(), variable.getValue());
		return processIdsVariableValue;
	}

	public List<TaskSummary> getTaskBySelectFlow(Usuario usuario, Long processId) throws JbpmException {
		List<TaskSummary> listaTareas = getTaskCompletedReserved(usuario, processId);
		return listaTareas;
	}

	public List<ProcessInstanceLog> getActiveProcessInstances(Usuario usuario, String processId) throws JbpmException {
		return processBeanFacade.getActiveProcessInstances(processId, Constantes.getDeploymentId(),
				usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout());
	}

	public List<ProcessInstanceLog> getProcessInstances(Usuario usuario, String processId) throws JbpmException {
		return processBeanFacade.getProcessInstances(processId, Constantes.getDeploymentId(), usuario.getNombre(),
				usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}

	public List<ProcessInstanceLog> getActiveProcessInstancesLogsVariableValue(Usuario usuario, String processId,
			String variableName, Object variableValue) throws JbpmException {

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(variableName, variableValue);

		return  new ArrayList<ProcessInstanceLog>(); //getActiveProcessInstancesLogsVariableValue(usuario, processId, variables);
	}

	public List<ProcessInstanceLog> getActiveProcessInstancesLogsVariableValue(Usuario usuario, String processId,
			Map<String, Object> variablesToFind) throws JbpmException {
		List<ProcessInstanceLog> count = new ArrayList<ProcessInstanceLog>();
		List<ProcessInstanceLog> result = processBeanFacade.getActiveProcessInstances(processId,
				Constantes.getDeploymentId(), usuario.getNombre(), usuario.getPassword(),
				Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());

		for (ProcessInstanceLog processInstanceLog : result) {
			Map<String, Object> variables = recuperarVariablesProceso(usuario,
					processInstanceLog.getProcessInstanceId());

			boolean found = true;
			for (String keyToFind : variablesToFind.keySet()) {
				if (variables.containsKey(keyToFind)
						&& variables.get(keyToFind) != null
						&& (variables.get(keyToFind).toString().equals(variablesToFind.get(keyToFind).toString()) || variables
								.get(keyToFind).equals(variablesToFind.get(keyToFind))))
					continue;
				else {
					found = false;
					break;
				}
			}

			if (found)
				count.add(processInstanceLog);
		}

		return count;
	}

	public Task getTaskById(Usuario usuario, long taskId) throws JbpmException {
		return taskBeanFacade.getTaskById(taskId, Constantes.getDeploymentId(), usuario.getNombre(),
				usuario.getPassword(), Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout());
	}

	public synchronized void asociarProcesoUsuario(Usuario usuario, long processInstanceId) throws JbpmException {
		asociarProcesoUsuarioNombre(usuario, usuario.getNombre(), processInstanceId);
	}

	public synchronized void asociarProcesoUsuarioNombre(Usuario usuario, String nombreUsuario, long processInstanceId)
			throws JbpmException {
		Map<String, Object> user = new HashMap<String, Object>();
		user.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, nombreUsuario);
		modificarVariablesProceso(usuario, processInstanceId, user);
	}
	
	public boolean envioSeguimientoRGD(Usuario usuario, Long processId) {		
		try {
			ProcessInstanceLog proceso = getProcessInstanceLog(usuario, processId) ;
			if(proceso == null)
				return false;
			if(!proceso.getStatus().equals(1)){
				return false;
			}
			List<TaskSummary> listaTareas= getTaskCompletedReserved(usuario, processId);
			List<Long> slist = new ArrayList<Long>();
			for (TaskSummary taskSummary : listaTareas) {
				slist.add(taskSummary.getId());
			}
			Collections.sort(slist);
		    
		    List<TaskSummary> listaTareasOrdenada = new ArrayList<TaskSummary>();
		    for (int i = 0; i < slist.size(); i++) {
		    	for (TaskSummary taskSummary : listaTareas) {
		    		if(slist.get(i).equals(taskSummary.getId())){
		    			listaTareasOrdenada.add(taskSummary);
		    			break;
		    		}
		    	}
			}

			Map<String, Object> variables = new HashMap<String, Object>();
			variables = recuperarVariablesProceso(usuario, processId);
			String docOperador = String.valueOf(variables.get("sujetoControl"));
			if(docOperador.equals("null"))
				docOperador = String.valueOf(variables.get("u_Promotor"));
			if(docOperador.equals("null"))
				docOperador = String.valueOf(variables.get("proponente"));
			if(docOperador.equals("null"))
				docOperador = String.valueOf(variables.get("u_Proponente"));
			if(docOperador.equals("null"))
				docOperador = String.valueOf(variables.get("operador"));
			if(docOperador.equals("null"))
				docOperador = String.valueOf(variables.get("u_operador"));
			if(docOperador.equals("null"))
				docOperador = String.valueOf(variables.get("usuario_operador"));
			List<Contacto> contactos = contactoFacade
					.buscarUsuarioNativeQuery(docOperador);
			String email= null;
			String nombreProponente=null;
			
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					email=contacto.getValor();
					nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();
					
					if(nombreProponente!=null)
					break;
				}
			}
			Usuario usuariooperador = usuarioFacade.buscarUsuario(docOperador);
			//busco el nombre del operador
			if(usuariooperador.getNombre().length() == 10){
				nombreProponente=usuariooperador.getPersona().getNombre();
			}else{
				Organizacion organizacion = organizacionFacade.buscarPorRuc(usuariooperador.getNombre());
				if(organizacion != null){
					nombreProponente = organizacion.getNombre();
				}else{
					nombreProponente = usuariooperador.getPersona().getNombre();
				}
			}
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailSeguimientoRGD(email, "Seguimiento del proyecto", "Este correo fue enviado usando JavaMail", usuario.getPersona().getNombre(), String.valueOf(variables.get("tramite")), listaTareasOrdenada,nombreProponente, usuariooperador, usuario);
			Thread.sleep(2000);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean envioSeguimientoLicenciaAmbiental(Usuario usuario, Long processId) {		
		try {
			
			List<TaskSummary> listaTareas= getTaskCompletedReserved(usuario, processId);

			Map<String, Object> variables = new HashMap<String, Object>();
			variables = recuperarVariablesProceso(usuario, processId);			
			List<Contacto> contactos = contactoFacade
					.buscarUsuarioNativeQuery(String.valueOf(variables.get("u_Promotor")));
			if(contactos.size()==0){
				contactos = contactoFacade
						.buscarUsuarioNativeQuery(String.valueOf(variables.get("proponente")));
			}
			if(contactos.size()==0){
				contactos = contactoFacade
						.buscarUsuarioNativeQuery(String.valueOf(variables.get("u_Proponente")));
			}
			String email= null;
			String nombreProponente=null;

			Usuario usuarioCorreo = new Usuario();
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					email=contacto.getValor();
					nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();
					usuarioCorreo = usuarioFacade.buscarUsuario(contacto.getPersona().getPin());
					if(nombreProponente!=null)
					break;
				}
			}
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailSeguimientoLicencia(email, "Seguimiento del proyecto", "Este correo fue enviado usando JavaMail", nombreProponente, String.valueOf(variables.get("tramite")), listaTareas,nombreProponente, usuarioCorreo, usuario);
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getProcessIdVariablesValue(String nameVariableProcess, String valueVariableProcess) {
		List<Long> listaProcesos = new ArrayList<>();
		String sql = "select * "
				+ "from dblink('" + dblinkBpmsSuiaiii + "',' "
				+ "select distinct processinstanceid  "
				+ "from "
				+ "variableinstancelog v "
				+ "where "
				+ "v.variableid = ''" + nameVariableProcess + "'' and value = ''" + valueVariableProcess + "'' "
				+ "') as ( processinstanceid varchar)";
		
		Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);
		List<Object>  resultList = q.getResultList();		
		if (resultList.size() > 0) {
			for (Object a : resultList) {
				Long idProceso = Long.parseLong(a.toString());
				listaProcesos.add(idProceso);
			}
		}

		return listaProcesos;
	}
	
	//paginadores------------------------------------------
	
	public Integer getCountProcessInstance(Usuario usuario, String processId, List<Integer> states) throws JbpmException {
		return processBeanFacade.getCountProcessInstance(processId, usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),states);
	}
	
	public List<ProcessInstanceLog> getProcessInstancesPages(Usuario usuario, String processId,Integer page,Integer pageSize,List<Integer> states) throws JbpmException {
		return processBeanFacade.getProcessInstancesPages(processId, usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(), page, pageSize,states);
	}
	
	public Integer getCountProcessInstanceFilter(Usuario usuario, String processId,String filtro,List<Integer> states) throws JbpmException {
		return processBeanFacade.getCountProcessInstanceFilter(processId,Constantes.VARIABLE_PROCESO_TRAMITE,filtro,usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(),states);
	}
	
	public List<ProcessInstanceLog> getProcessInstancesFilterPages(Usuario usuario, String processId,String filtro,Integer page,Integer pageSize,List<Integer> states) throws JbpmException {
		return processBeanFacade.getProcessInstancesFilterPages(processId,Constantes.VARIABLE_PROCESO_TRAMITE,filtro,usuario.getNombre(), usuario.getPassword(), Constantes.getUrlBusinessCentral(), page, pageSize,states);
	}
	
	public String getStatusPagoRcoa(String codigoTramite) {
		String sql = "SELECT * FROM dblink('" + Constantes.getDblinkBpmsSuiaiii() + "', "
	               + "'SELECT b.status FROM variableinstancelog v "
	               + "INNER JOIN bamtasksummary b ON v.processinstanceid = b.processinstanceid "
	               + "WHERE v.value = ''" + codigoTramite + "'' "
	               + "AND b.taskname LIKE ''%Realizar%'' "
	               + "AND v.processid = ''" + Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS + "''') "
	               + "AS t(status TEXT);";

	    Query query = null;
	    try {
	        query = crudServiceBean.getEntityManager().createNativeQuery(sql);
	        return (String) query.getSingleResult();
	    } catch (NoResultException e) {
	        return null; 
	    } catch (Exception e) {
	        throw new RuntimeException("Error al obtener el estado del pago", e);
	    } finally {
	        if (query != null) {
	        }
	    }
	}
}