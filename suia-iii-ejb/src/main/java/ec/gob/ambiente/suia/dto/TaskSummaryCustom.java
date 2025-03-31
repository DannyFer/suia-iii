/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 12/03/2015]
 *          </p>
 */
public class TaskSummaryCustom implements Serializable {

	private static final long serialVersionUID = 8501278765482887582L;

	public static final String SOURCE_TYPE_EXTERNAL_SUIA = "source_type_external_suia";
	public static final String SOURCE_TYPE_EXTERNAL_HYDROCARBONS = "source_type_external_hydrocarbons";
	public static final String SOURCE_TYPE_INTERNAL = "source_type_internal";

	public static final String LABEL_UNKNOW = "(Desconocido)";

	@Getter
	@Setter
	private TaskSummary taskSummary;

	@Getter
	@Setter
	private String processId;

	@Getter
	@Setter
	private long processInstanceId;

	@Getter
	@Setter
	private long taskId;

	@Getter
	@Setter
	private String sourceType;

	@Getter
	@Setter
	private String procedure;

	@Getter
	@Setter
	private String procedureResolverClass;

	@Getter
	@Setter
	private String processName;

	@Getter
	@Setter
	private String processNameHuman;

	@Getter
	@Setter
	private String taskName;

	@Getter
	@Setter
	private String taskNameHuman;

	@Getter
	@Setter
	private String activationDate;

	@Getter
	@Setter
	private String projectId;

	@Getter
	@Setter
	private String deploymentId;

	@Getter
	@Setter
	private String versionEar;

	public boolean isInternal() {
		return getSourceType().equals(SOURCE_TYPE_INTERNAL);
	}

	private void initVariables() {
		Object tramiteValue = getVariable(Constantes.VARIABLE_PROCESO_TRAMITE);
		if (tramiteValue != null) {
			this.procedure = tramiteValue.toString();
			this.procedure = (!this.procedure.trim().isEmpty()) ? this.procedure.trim():TaskSummaryCustom.LABEL_UNKNOW;
			if(procedure.startsWith("MAE-RSQ-")
					|| procedure.startsWith("MAAE-RSQ-")
					|| procedure.startsWith("MAATE-RSQ-")) {
				procedure = getVariable("codigoProyecto").toString();
			}
		} else
			this.procedure = TaskSummaryCustom.LABEL_UNKNOW;

		Object idProyectoValue = getVariable(Constantes.ID_PROYECTO);
		if(idProyectoValue==null)
			idProyectoValue = getVariable(Constantes.U_ID_PROYECTO);
			
		this.projectId = idProyectoValue == null ? null : idProyectoValue.toString();

		Object procedureResolverClassValue = getVariable(Constantes.VARIABLE_PROCESO_TRAMITE_RESOLVER);
		this.procedureResolverClass = procedureResolverClassValue == null ? null : procedureResolverClassValue
				.toString();
	}
	@Getter
	private Map<String, Object> processVariables;

	public void setProcessVariables(Map<String, Object> processVariables) {
		this.processVariables = processVariables;
		initVariables();
	}
	
	/**
	 * Walter 
	 * acelerar bandeja 
	 * @param tramite
	 */
	public void setProcessVariables1(String tramite) {		
		initVariables1(tramite);
	}
	private void initVariables1(String tramite) {		
		if (tramite != null || tramite != "") {
			this.procedure = tramite.toString();
			this.procedure = (!this.procedure.trim().isEmpty()) ? this.procedure.trim()
					: TaskSummaryCustom.LABEL_UNKNOW;
		} else
			this.procedure = TaskSummaryCustom.LABEL_UNKNOW;
	}
	//////////////////////

	public Object getVariable(String key) {
		processVariables = processVariables == null ? processVariables = new HashMap<String, Object>()
				: processVariables;
		if (processVariables.containsKey(key))
			return processVariables.get(key);
		return null;
	}

	public void updateVariable(String key, Object value) {
		if (processVariables == null)
			getVariable("null");
		processVariables.put(key, value);
	}
}
