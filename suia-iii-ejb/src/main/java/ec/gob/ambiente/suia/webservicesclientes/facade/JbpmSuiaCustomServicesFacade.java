/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.webservicesclientes.facade;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ec.gob.ambiente.suia.dto.DefinicionProceso;
import ec.gob.ambiente.suia.dto.EventoTarea;
import ec.gob.ambiente.suia.dto.NodeInstanceLog;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorJsonUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 10/03/2015]
 *          </p>
 */
@Stateless
public class JbpmSuiaCustomServicesFacade {

	public List<DefinicionProceso> listaDefinicionesProcesos() throws Exception {		
		return this.listaDefinicionesProcesos(null, null, null);
	}
	
	public List<DefinicionProceso> listaDefinicionesProcesos(String paqueteImplementacion, String nombreImplementacion,
			String versionImplementacion) throws Exception {
		String urlServicio = Constantes.getUrlSuiaDefinicionesProcesos();
		if (paqueteImplementacion == null || nombreImplementacion == null || versionImplementacion == null) {
			String[] datosImplementacion = Constantes.getDeploymentId().split(":");
			urlServicio += datosImplementacion[0] + "/" + datosImplementacion[1] + "/" + datosImplementacion[2];
		} else
			urlServicio += paqueteImplementacion + "/" + nombreImplementacion + "/" + versionImplementacion;

		List<DefinicionProceso> procesosLista = new ArrayList<DefinicionProceso>();
		JSONArray jsonArray = consumirServicio(urlServicio);

		for (int i = 0; i < jsonArray.size(); i++) {
			DefinicionProceso proceso = new DefinicionProceso();
			procesosLista
					.add(ConvertidorJsonUtil.convertirJsonDefinicionProceso((JSONObject) jsonArray.get(i), proceso));
		}
		return procesosLista;
	}

	public List<EventoTarea> listaEventosTarea(long idTarea) throws Exception {
		String urlServicio = Constantes.getUrlSuiaEventosTarea() + idTarea;
		List<EventoTarea> eventosTarea = new ArrayList<EventoTarea>();
		JSONArray jsonArray = consumirServicio(urlServicio);
		for (int i = 0; i < jsonArray.size(); i++) {
			EventoTarea eventoTarea = new EventoTarea();
			eventosTarea.add(ConvertidorJsonUtil.convertirJsonEventoTarea((JSONObject) jsonArray.get(i), eventoTarea));
		}
		return eventosTarea;
	}

	public JSONArray consumirServicio(String urlServicio) throws Exception {
		ClientRequest request = new ClientRequest(urlServicio);
		request.accept("application/json");
		ClientResponse<String> response = request.get(String.class);
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity()
				.getBytes())));
		JSONParser parser = new JSONParser();
		return (JSONArray) parser.parse(br);
	}

	public JSONArray consumirServicio(String urlServicio, String codificacion) throws Exception {
		ClientRequest request = new ClientRequest(urlServicio);
		request.getHeaders().add("Content-Type", "application/xml");
		request.header("Content-Type", "application/xml");
		request.accept("application/json");

		ClientResponse<String> response = request.get(String.class);
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity()
				.getBytes())));
		JSONParser parser = new JSONParser();
		return (JSONArray) parser.parse(br);
	}

	public Date getFechaFinTarea(long idTarea) throws Exception {
		List<EventoTarea> listaEventosTarea = listaEventosTarea(idTarea);
		for (int i = 0; i < listaEventosTarea.size(); i++) {
			if (listaEventosTarea.get(i).getType().equals(Constantes.ESTADO_EVENTO_TAREA_COMPLETADA))
				return listaEventosTarea.get(i).getLogTime();
		}
		return null;

	}

	/**
	 * 
	 * <b> Metodo que consume el servicio para obtener el resumen de tareas. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 04/05/2015]
	 * </p>
	 * 
	 * @param processInstanceId
	 *            : id del proceso
	 * @return List<ResumeTarea> : Lista de resumenes de tarea
	 * @throws Exception
	 *             : excepcion
	 */
	public List<ResumeTarea> listaResumenTareas(long processInstanceId) throws Exception {
		String urlServicio = Constantes.getUrlSuiaResumenTarea() + processInstanceId;
		List<ResumeTarea> resumenTareas = new ArrayList<ResumeTarea>();
		JSONArray jsonArray = consumirServicio(urlServicio);
		for (int i = 0; i < jsonArray.size(); i++) {
			ResumeTarea resumenTarea = new ResumeTarea();
			resumenTareas
					.add(ConvertidorJsonUtil.convertirJsonResumenTarea((JSONObject) jsonArray.get(i), resumenTarea));
		}
		return resumenTareas;
	}

	/**
	 * 
	 * <b> . </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 04/05/2015]
	 * </p>
	 * 
	 * @param processInstanceId
	 *            : id del proceso
	 * @return List<ResumeTarea> : Lista de resumenes de tarea
	 * @throws Exception
	 *             : excepcion
	 */
	public List<ResumeTarea> getResumenTareas(long processInstanceId) throws Exception {
		return listaResumenTareas(processInstanceId);

	}

	public List<NodeInstanceLog> getNodeInstanceLogs(long processInstanceId) throws Exception {
		String urlServicio = Constantes.getUrlNodeInstanceLog() + processInstanceId;
		List<NodeInstanceLog> listaNodos = new ArrayList<NodeInstanceLog>();
		JSONArray jsonArray = consumirServicio(urlServicio);
		for (int i = 0; i < jsonArray.size(); i++) {
			listaNodos.add(ConvertidorJsonUtil.convertirJsonNodeInstanceLog((JSONObject) jsonArray.get(i),
					new NodeInstanceLog()));
		}
		return listaNodos;
	}

}
