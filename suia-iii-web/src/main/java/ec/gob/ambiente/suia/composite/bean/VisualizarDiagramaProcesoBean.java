/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.dto.NodeInstanceLog;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;
import ec.gob.ambiente.suia.webservicesclientes.facade.RestfulClientFacade;

@ManagedBean(name = "visualizarDiagrama")
@ViewScoped
public class VisualizarDiagramaProcesoBean implements Serializable {

	private static final long serialVersionUID = -8956271287113629686L;

	private static final Logger LOG = Logger
			.getLogger(VisualizarDiagramaProcesoBean.class);
	public static final String SPLIT = "\n";

	@Getter
	@Setter
	private String diagrama;
	@Getter
	@Setter
	private String nombreProceso;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	// EJBS
	@EJB
	ProcesoFacade procesoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	RestfulClientFacade restfulClientFacade;

	@PostConstruct
	public void init() {
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			Long processId = (Long) request.getSession(false).getAttribute(
					Constantes.VARIABLE_SESION_PROCESS_ID);

			if (processId != null) {

				// Obtener nombre del proceso
				ProcessInstanceLog process = procesoFacade
						.getProcessInstanceLog(loginBean.getUsuario(), processId);
				String processname = process.getProcessName();
				setNombreProceso(processname);

				List<TaskSummary> listaTareas = procesoFacade
						.getTaskCompletedReserved(loginBean.getUsuario(), processId);

				ServletContext servletContext = (ServletContext) FacesContext
						.getCurrentInstance().getExternalContext().getContext();

				diagrama = Constantes.PATH_SVG_DIAGRAMA_PROCESO
						+ processname.replace(" ", "") + ".svg";
				diagrama = reemplazarCaracteresRaros(diagrama);

				InputStream svgAsStream = servletContext
						.getResourceAsStream(diagrama);

				String tasksCompleted = "";
				String tasksReserved = "";
				for (TaskSummary task : listaTareas) {
					if (task.getStatus().equals(Status.Completed)) {
						if (tasksCompleted.equals("")) {
							tasksCompleted += task.getName();
						} else {
							tasksCompleted += SPLIT + task.getName();
						}
					} else if (task.getStatus().equals(Status.Reserved)) {
						if (tasksReserved.equals("")) {
							tasksReserved += task.getName();
						} else {
							tasksReserved += SPLIT + task.getName();
						}
					}
				}

				List<NodeInstanceLog> listaNodos = BeanLocator.getInstance(
						JbpmSuiaCustomServicesFacade.class).getNodeInstanceLogs(processId);

				int nodoFinal = 0;
				for (NodeInstanceLog nodeInstanceLog : listaNodos) {
					if(nodeInstanceLog.getNodeName() != null && tasksReserved.contains(nodeInstanceLog.getNodeName())){
						if(Integer.parseInt(nodeInstanceLog.getNodeInstanceId()) > nodoFinal){
							nodoFinal = Integer.parseInt(nodeInstanceLog.getNodeInstanceId());
						}
					}
				}
				
				for (NodeInstanceLog nodeInstanceLog : listaNodos) {
					if(Integer.parseInt(nodeInstanceLog.getNodeInstanceId()) < nodoFinal){
						if (tasksCompleted.equals("") && nodeInstanceLog.getNodeName() != null && !tasksCompleted.contains(nodeInstanceLog.getNodeName())) {
							tasksCompleted += nodeInstanceLog.getNodeName();
						} else if (!tasksCompleted.equals("") && nodeInstanceLog.getNodeName() != null && !tasksCompleted.contains(nodeInstanceLog.getNodeName())){
							tasksCompleted += SPLIT + nodeInstanceLog.getNodeName();
						}
					}
				}
				
				Map<String, Object> parametros = new HashMap<String, Object>();

				List<String> nombreParametros = new ArrayList<String>();
				List<Object> objetoParametros = new ArrayList<Object>();

				nombreParametros.add("svgFile");
				nombreParametros.add("tasksReserved");
				nombreParametros.add("tasksCompleted");

				objetoParametros.add(svgAsStream);
				objetoParametros.add(tasksReserved);
				objetoParametros.add(tasksCompleted);

				parametros.put(Constantes.REST_MAP_PARAMETER_NAME,
						nombreParametros);
				parametros.put(Constantes.REST_MAP_PARAMETER_OBJECT,
						objetoParametros);

				File file = (File) restfulClientFacade.callServicePostFile(
						parametros, Constantes.getProcessImageUrl());
				setDiagrama(file.getPath());

			}
		} catch (IOException e) {
			LOG.error(
					"Error: No se puede visualizar el diagrama de procesos: ",
					e);
			JsfUtil.addMessageError("Error: No se puede visualizar el diagrama de procesos");
		} catch (JbpmException e) {
			LOG.error("Error: No se puede acceder al proceso ", e);
			JsfUtil.addMessageError("Error: No se puede acceder al proceso");
		} catch (Exception e) {
			LOG.error("Error: No se puede visualizar el diagrama de procesos ",
					e);
			JsfUtil.addMessageError("Error: No se puede visualizar el diagrama de procesos");
		}

	}

	public void init(Long processId) {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		request.getSession(false).setAttribute(
				Constantes.VARIABLE_SESION_PROCESS_ID, processId);

		Map<String, Object> opciones = new HashMap<String, Object>();
		opciones.put("modal", true);
		opciones.put("draggable", false);
		opciones.put("resizable", true);
		opciones.put("contentWidth", 800);
		opciones.put("contentHeight", 500);

		RequestContext.getCurrentInstance().openDialog(
				"/comun/visualizarDiagramaProceso", opciones, null);

	}
	
	/**
	 * 
	 * <b> Método que retorna una cadena sin acentos. </b>
	 * <p>
	 * [Author: vero, Date: 18/08/2015]
	 * </p>
	 * 
	 * @param input
	 * @return
	 */
	public String reemplazarCaracteresRaros(String input) {
		// Cadena de caracteres original a sustituir.
		String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
		// Cadena de caracteres ASCII que reemplazarán los originales.
		String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
		String output = input;
		for (int i = 0; i < original.length(); i++) {
			// Reemplazamos los caracteres especiales.
			output = output.replace(original.charAt(i), ascii.charAt(i));
		}
		return output;
	}

}
