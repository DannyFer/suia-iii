/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;

import ec.gob.ambiente.notificaciones.domain.Notification;
import ec.gob.ambiente.suia.dto.DefinicionProceso;
import ec.gob.ambiente.suia.dto.EventoTarea;
import ec.gob.ambiente.suia.dto.NodeInstanceLog;
import ec.gob.ambiente.suia.dto.ResumeTarea;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 10/03/2015]
 *          </p>
 */
public final class ConvertidorJsonUtil {

	public static DefinicionProceso convertirJsonDefinicionProceso(JSONObject jsonObject, DefinicionProceso proceso) {
		proceso.setId((String) jsonObject.get("id"));
		proceso.setName((String) jsonObject.get("name"));
		proceso.setPackageName((String) jsonObject.get("packageName"));
		proceso.setVersion((String) jsonObject.get("version"));
		return proceso;
	}

	public static EventoTarea convertirJsonEventoTarea(JSONObject jsonObject, EventoTarea eventoTarea) {
		eventoTarea.setId((Long) jsonObject.get("id"));
		eventoTarea.setLogTime(new Date((Long) jsonObject.get("logTime")));
		eventoTarea.setTaskId((Long) jsonObject.get("taskId"));
		eventoTarea.setType((String) jsonObject.get("type"));
		eventoTarea.setUserId((String) jsonObject.get("userId"));
		return eventoTarea;
	}

	public static Notification convertirJsonNotification(JSONObject jsonObject) {
		Notification notificacion = new Notification();
		notificacion.setActorId((String) jsonObject.get("actorId"));
		notificacion.setTaskId((Long) jsonObject.get("taskId"));
		notificacion.setTaskName((String) jsonObject.get("taskName"));
		notificacion.setDeploymentId((String) jsonObject.get("deploymentId"));
		notificacion.setProcessId((String) jsonObject.get("processId"));

		notificacion.setProcessInstanceId((Long) jsonObject.get("processInstanceId"));
		notificacion.setSubject((String) jsonObject.get("subject"));
		notificacion.setText((String) jsonObject.get("text"));

		DateFormat format = new SimpleDateFormat("y-M-d h:m:s");
		Date date = new Date();
		try {
			date = format.parse((String) jsonObject.get("date"));
		} catch (ParseException e) {
		}

		notificacion.setDate(date);
		notificacion.setReaded((Boolean) jsonObject.get("readed"));
		return notificacion;
		/**
		 * "id": "55147c1c27364205677559ad", "actorId": "msit", "taskId": 12, "taskName": "eia", "deploymentId": "123",
		 * "processId": "56", "processInstanceId": 78, "subject": "Estudio de Impacto Ambiental", "text": "EIA", "date":
		 * "2015-03-30 18:00:00", "readed": false, "status": true
		 */
	}

	/**
	 * 
	 * <b> Metodo para el mapeo de la consulta de resumen de tarea. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 04/05/2015]
	 * </p>
	 * 
	 * @param jsonObject
	 *            : objeto json
	 * @param resumeTarea
	 *            : resumen de tarea
	 * @return ResumeTarea : resumen de tareas
	 */
	public static ResumeTarea convertirJsonResumenTarea(JSONObject jsonObject, ResumeTarea resumeTarea) {
		resumeTarea.setPk((String) jsonObject.get("pk"));
		resumeTarea.setTaskName((String) jsonObject.get("taskName"));
		resumeTarea.setStatus((String) jsonObject.get("status"));
		resumeTarea.setUserId((String) jsonObject.get("userId"));
		resumeTarea.setCreatedDate((String) jsonObject.get("createdDate"));
		resumeTarea.setEndDate((String) jsonObject.get("endDate"));
		return resumeTarea;
	}

	public static NodeInstanceLog convertirJsonNodeInstanceLog(JSONObject jsonObject, NodeInstanceLog nodeInstanceLog) {
		nodeInstanceLog.setNodeInstanceId((String) jsonObject.get("nodeInstanceId"));
		nodeInstanceLog.setNodeName((String) jsonObject.get("nodeName"));
		nodeInstanceLog.setNodeType((String) jsonObject.get("nodeType"));
		return nodeInstanceLog;
	}

	public static String[] convertirJsonTemplate(JSONObject jsonObject) {
		String[] arr = new String[2];
		arr[0] = (String) jsonObject.get("name");
		arr[1] = (String) jsonObject.get("body");
		return arr;
	}
}
