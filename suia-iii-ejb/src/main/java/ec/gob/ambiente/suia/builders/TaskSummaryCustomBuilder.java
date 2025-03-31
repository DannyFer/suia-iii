/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.builders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.kie.api.task.model.TaskSummary;

import ec.fugu.ambiente.consultoring.projects.Task;
import ec.fugu.ambiente.consultoring.retasking.ProyectoLicenciaVo;
import ec.gob.ambiente.hyd.service.TaskSummaryDto;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 13/03/2015]
 *          </p>
 */
public class TaskSummaryCustomBuilder {

	private TaskSummaryCustom taskSummaryCustom;

	public TaskSummaryCustomBuilder() {
		taskSummaryCustom = new TaskSummaryCustom();
	}

	public TaskSummaryCustom build() {
		return taskSummaryCustom;
	}

	public TaskSummaryCustomBuilder fromHidrocarburos(TaskSummaryDto taskSummaryDto) {
		taskSummaryCustom.setSourceType(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_HYDROCARBONS);
		taskSummaryCustom.setTaskSummary(null);
		taskSummaryCustom.setProcessId(taskSummaryDto.getProcessId());
		taskSummaryCustom.setProcessInstanceId(taskSummaryDto.getProcessInstanceId());
		taskSummaryCustom.setTaskId(taskSummaryDto.getTaskId());
		taskSummaryCustom.setProcedure(taskSummaryDto.getStep());
		taskSummaryCustom.setProcessName(taskSummaryDto.getFlowName());
		taskSummaryCustom.setTaskName(taskSummaryDto.getName());
		taskSummaryCustom.setTaskNameHuman(taskSummaryDto.getName());
		taskSummaryCustom.setProcessNameHuman(taskSummaryDto.getFlowName());
		

		Date activationDate = new Date(taskSummaryDto.getActivationTime());
		String activationDateString = "";
		if (activationDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
			activationDateString = dateFormat.format(activationDate);
		}
		taskSummaryCustom.setActivationDate(activationDateString);

		return this;
	}

	public TaskSummaryCustomBuilder fromSuiaII(Task task) {
		taskSummaryCustom.setSourceType(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_SUIA);
		taskSummaryCustom.setTaskSummary(null);
		taskSummaryCustom.setProcessId(TaskSummaryCustom.LABEL_UNKNOW);
		taskSummaryCustom.setProcessInstanceId(-1);
		taskSummaryCustom.setTaskId(Long.parseLong(task.getDbid()));
		taskSummaryCustom.setProcedure(task.getProjectId());
		taskSummaryCustom.setProjectId(task.getProjectId());
		taskSummaryCustom.setProcessName(task.getNameFlow());
		taskSummaryCustom.setTaskName(task.getName());
		taskSummaryCustom.setTaskNameHuman(task.getNameShow());
		
		Date activationDate = task.getCreate().toGregorianCalendar().getTime();
		String activationDateString = "";
		if (activationDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
			activationDateString = dateFormat.format(activationDate);
		}
		taskSummaryCustom.setActivationDate(activationDateString);

		return this;
	}
	
	public TaskSummaryCustomBuilder fromSuiaII(ProyectoLicenciaVo proyectoLicenciaVo) {
		taskSummaryCustom.setSourceType(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_SUIA);
		taskSummaryCustom.setTaskSummary(null);
		taskSummaryCustom.setProcessId(TaskSummaryCustom.LABEL_UNKNOW);
		taskSummaryCustom.setProcessInstanceId(-1);
		taskSummaryCustom.setTaskId(-1);
		taskSummaryCustom.setProcedure(proyectoLicenciaVo.getId());
		taskSummaryCustom.setProjectId(proyectoLicenciaVo.getId());
		taskSummaryCustom.setProcessName(proyectoLicenciaVo.getFlujo());
		taskSummaryCustom.setProcessNameHuman(proyectoLicenciaVo.getFlujoMostrar());
		taskSummaryCustom.setTaskName(proyectoLicenciaVo.getActividadNombre());
		taskSummaryCustom.setTaskNameHuman(proyectoLicenciaVo.getActividadNombreMostrar());

		Date activationDate = proyectoLicenciaVo.getFechaRegistro().toGregorianCalendar().getTime();
		String activationDateString = "";
		if (activationDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
			activationDateString = dateFormat.format(activationDate);
		}
		taskSummaryCustom.setActivationDate(activationDateString);
		
		return this;
	}

	public TaskSummaryCustomBuilder fromSuiaIII(Map<String, Object> processVariables, String processName,
			TaskSummary taskSummary) {
		taskSummaryCustom.setProcessVariables(processVariables);
		taskSummaryCustom.setSourceType(TaskSummaryCustom.SOURCE_TYPE_INTERNAL);
		taskSummaryCustom.setTaskSummary(taskSummary);
		taskSummaryCustom.setProcessId(taskSummary.getProcessId());
		taskSummaryCustom.setProcessInstanceId(taskSummary.getProcessInstanceId());
		taskSummaryCustom.setTaskId(taskSummary.getId());
		taskSummaryCustom.setProcessName(processName);
		taskSummaryCustom.setProcessNameHuman(processName);
		taskSummaryCustom.setTaskName(taskSummary.getName());
		taskSummaryCustom.setTaskNameHuman(taskSummary.getName());

		Date activationDate = taskSummary.getActivationTime();
		String activationDateString = "";
		if (activationDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
			activationDateString = dateFormat.format(activationDate);
		}
		taskSummaryCustom.setActivationDate(activationDateString);
		return this;
	}
	
	public TaskSummaryCustomBuilder fromSuiaIII1(String processName,
			TaskSummary taskSummary,String tramite) {
		taskSummaryCustom.setProcessVariables1(tramite);
		taskSummaryCustom.setSourceType(TaskSummaryCustom.SOURCE_TYPE_INTERNAL);
		taskSummaryCustom.setTaskSummary(taskSummary);
		taskSummaryCustom.setProcessId(taskSummary.getProcessId());
		taskSummaryCustom.setProcessInstanceId(taskSummary.getProcessInstanceId());
		taskSummaryCustom.setTaskId(taskSummary.getId());
		taskSummaryCustom.setProcessName(processName);
		taskSummaryCustom.setProcessNameHuman(processName);
		taskSummaryCustom.setTaskName(taskSummary.getName());
		taskSummaryCustom.setTaskNameHuman(taskSummary.getName());

		Date activationDate = taskSummary.getActivationTime();
		String activationDateString = "";
		if (activationDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a");
			activationDateString = dateFormat.format(activationDate);
		}
		taskSummaryCustom.setActivationDate(activationDateString);
		return this;
	}
	
	public ProyectoLicenciaVo revertToProyectoLicenciaVo(TaskSummaryCustom taskSummaryCustom) {
		ProyectoLicenciaVo proyectoLicenciaVo = new ProyectoLicenciaVo();
		proyectoLicenciaVo.setActividadNombre(taskSummaryCustom.getTaskName());
		proyectoLicenciaVo.setActividadNombreMostrar(taskSummaryCustom.getTaskNameHuman());
		proyectoLicenciaVo.setFlujo(taskSummaryCustom.getProcessName());
		proyectoLicenciaVo.setFlujoMostrar(taskSummaryCustom.getProcessNameHuman());
		proyectoLicenciaVo.setId(taskSummaryCustom.getProjectId());
		return proyectoLicenciaVo;
	}
}
