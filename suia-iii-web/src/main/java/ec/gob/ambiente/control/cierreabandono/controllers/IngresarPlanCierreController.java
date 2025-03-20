/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.cierreabandono.controllers;

import java.io.Serializable;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.control.cierreabandono.bean.PlanCierreBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.cierreabandono.facade.CierreAbandonoFacade;
import ec.gob.ambiente.suia.domain.PlanCierre;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
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
public class IngresarPlanCierreController implements Serializable {

	private static final long serialVersionUID = 3366156341761047494L;

	private static final Logger LOGGER = Logger.getLogger(IngresarPlanCierreController.class);

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@ManagedProperty(value = "#{planCierreBean}")
	@Setter
	private PlanCierreBean planCierreBean;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private CierreAbandonoFacade cierreAbandonoFacade;

	/**
	 * 
	 * <b> Es llamado en el menu, inicia el proceso de Plan de Cierre. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 10/01/2015]
	 *          </p>
	 */
	public void initIngresarPlanCierreAction() {
		/*identificarProyectoComunBean.initFunction("/control/cierreabandono/insertarPlanCierre.jsf",
				new CompleteOperation() {

					@Override
					public Object endOperation(final Object object) {
						planCierreBean.setProyecto((ProyectoLicenciamientoAmbiental) object);

						final Map<String, Object> params = new ConcurrentHashMap<String, Object>();
						params.put(PlanCierreAbandonoVariables.idProyecto.name(), planCierreBean.getProyecto().getId());
						params.put(PlanCierreAbandonoVariables.tecnicoControlProvincial.name(),
								Constantes.USUARIO_PRUEBA_BC);
						params.put(PlanCierreAbandonoVariables.coordinadorControlProvincial.name(),
								Constantes.USUARIO_PRUEBA_BC);
						params.put(PlanCierreAbandonoVariables.directorControlProvincial.name(),
								Constantes.USUARIO_PRUEBA_BC);
						params.put(PlanCierreAbandonoVariables.subsecretaria.name(), Constantes.USUARIO_PRUEBA_BC);
						params.put(PlanCierreAbandonoVariables.sujetoControl.name(), Constantes.USUARIO_PRUEBA_BC);
						params.put(PlanCierreAbandonoVariables.viceministra.name(), Constantes.USUARIO_PRUEBA_BC);

						try {
							final long processId = procesoFacade.iniciarProceso(Constantes.NOMBRE_PROCESO_PLAN_CIERRE,
									planCierreBean.getProyecto().getCodigo(), Constantes.getDeploymentId(), params,
									Constantes.getUrlBusinessCentral(), loginBean.getNombreUsuario(),
									loginBean.getPassword());
							//bandejaTareasBean.setTarea(procesoFacade.getCurrenTask(loginBean.getNombreUsuario(),
								//	loginBean.getPassword(), processId));
							bandejaTareasBean.setProcessId(processId);
						} catch (JbpmException e) {
							LOGGER.error("Error al iniciar el proceso " + Constantes.NOMBRE_PROCESO_PLAN_CIERRE);
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
						}
						return null;
					}
				});*/
	}

	/**
	 * 
	 * <b> Salva el Plan de Cierre y continua el proceso. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 10/01/2015]
	 *          </p>
	 * @return la regla de navegacion correspondiente
	 */
	public String ingresarPlanAction() {
		final Iterator<String> iterator = planCierreBean.getFiles().keySet().iterator();
		String points = "";
		while (iterator.hasNext()) {
			final String key = iterator.next();
			if (planCierreBean.getFiles().get(key).isEmpty()) {
				points += planCierreBean.getLabelsToStrings().get(key) + ";;";
			}
		}
		if (!points.isEmpty()) {
			JsfUtil.addMessageError("Adjunte la información de todos los puntos del Plan de Cierre:<br/>"
					+ JsfUtil.getStringAsHtmlUL(points, true));
			// return null;
		}
		try {
			final PlanCierre planCierre = planCierreBean.getPlanCierre();
			cierreAbandonoFacade.salvarPlanCierre(loginBean.getUsuario(), bandejaTareasBean.getProcessId(), planCierre, planCierreBean.getDocumentosFromUpload());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			return JsfUtil.NAVIGATION_TO_BANDEJA;
		} catch (JbpmException e) {
			LOGGER.error(e.getMessage());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
		return null;
	}

	/**
	 * 
	 * <b> Proceso al subir un archivo </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 10/01/2015]
	 *          </p>
	 * @param event
	 *            evento de subir un file
	 */
	public void handleFileUpload(final FileUploadEvent event) {
		planCierreBean.getFiles().get(planCierreBean.getFileKey()).add(event.getFile());
	}

	/**
	 * 
	 * <b> Elimina un fichero de la lista. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 10/01/2015]
	 *          </p>
	 * @param file
	 *            fichero a eliminar
	 */
	public void eliminarArchivo(final UploadedFile file) {
		planCierreBean.getFiles().get(planCierreBean.getFileKey()).remove(file);
	}
}
