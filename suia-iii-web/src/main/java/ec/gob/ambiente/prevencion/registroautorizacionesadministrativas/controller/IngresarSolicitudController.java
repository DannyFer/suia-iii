/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.bean.IngresarSolicitudBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.SolicitudAutorizacionesAdministrativas;
import ec.gob.ambiente.suia.domain.SolicitudAutorizacionesAdministrativasCoordenada;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 22/01/2015]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class IngresarSolicitudController {

	private static final Logger LOGGER = Logger
			.getLogger(IngresarSolicitudController.class);

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarSolicitudBean}")
	private IngresarSolicitudBean ingresarSolicitudBean;

	@EJB
	private CrudServiceBean crudServiceBean;

	public void sendAction() {

	}

	public String ingresarSolicitud() {

		// System.out.println("------------------------------------");
		// //
		// System.out.println(ingresarSolicitudBean.getCantonesActivos().length);
		// System.out.println(ingresarSolicitudBean.getCantonesActivos());
		// System.out.println(ingresarSolicitudBean.getLicencia().getFileName());
		// System.out.println("------------------------------------");
		// return "";

		try {

			SolicitudAutorizacionesAdministrativas solicitud = new SolicitudAutorizacionesAdministrativas();

			solicitud.setPromotor(ingresarSolicitudBean.getUsuarioActivo());
			solicitud.setRegistro(ingresarSolicitudBean.getRegistro());
			solicitud.setCategoria(ingresarSolicitudBean.getCategoria());

			solicitud.setNumeroResolucion(ingresarSolicitudBean
					.getNumeroResolucion());
			solicitud.setFechaEmision(ingresarSolicitudBean.getFechaEmision());

			//solicitud.setProyecto(ingresarSolicitudBean.getProyectoActivo());
			solicitud.setProceso(bandejaTareasBean.getTarea()
					.getProcessInstanceId());
			solicitud = crudServiceBean.saveOrUpdate(solicitud);
			//
			Documento documento = new Documento();
			documento.setDireccionAlfresco("url");
			// documento.setTipoDocumento(ingresarSolicitudBean.getLicencia()
			// .getContentType());
			// documento.setCodigoPublico();
			documento.setFechaGeneracion(new Date());
			documento.setDescripcion("Documento de solicitud.");
			documento.setExtesion(FilenameUtils
					.getExtension(ingresarSolicitudBean.getLicencia()
							.getFileName()));
			documento.setMime(ingresarSolicitudBean.getLicencia()
					.getContentType());
			documento.setNombre(ingresarSolicitudBean.getLicencia()
					.getFileName());
			// documento.setIdTareaBPM((int) bandejaTareasBean.getTarea()
			// .getProcessInstanceId()); Mapear en otra tabla

			crudServiceBean.saveOrUpdate(documento);

			// tipoDocumento direccionAlfresco codigoPublico fechaGeneracion
			// descripcion extesion mime nombre idTareaBPM

			List<SolicitudAutorizacionesAdministrativasCoordenada> cantones = new ArrayList<SolicitudAutorizacionesAdministrativasCoordenada>();
			for (UbicacionesGeografica ubic : ingresarSolicitudBean
					.getCantonesActivos()) {
				SolicitudAutorizacionesAdministrativasCoordenada solicitudCoordenada = new SolicitudAutorizacionesAdministrativasCoordenada();
				solicitudCoordenada.setUbicacionesGeografica(ubic);
				solicitudCoordenada
						.setSolicitudAutorizacionesAdministrativas(solicitud);
				crudServiceBean.saveOrUpdate(solicitudCoordenada);
			}
			solicitud.setCantones(cantones);
			crudServiceBean.saveOrUpdate(solicitud);

			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			params.put("u_coordinador", "msit");
			params.put("u_director", "msit");
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
					.getTarea().getProcessInstanceId(), params);

			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
					bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data,
					loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (JbpmException e) {
			LOGGER.error(
					"Error al iniciar la tarea (Ingresar solicitud de autorizaciones administrativas ambientales y licencia ambiental)",
					e);
			JsfUtil.addMessageError("Ocurrio un error al enviar la información, intente mas tarde.");
		}
		return "";

	}
}
