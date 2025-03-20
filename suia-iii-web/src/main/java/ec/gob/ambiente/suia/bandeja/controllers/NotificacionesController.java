package ec.gob.ambiente.suia.bandeja.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.mongodb.LazyNotificationDataModel;
import ec.gob.ambiente.notificaciones.domain.Notification;
import ec.gob.ambiente.suia.bandeja.beans.NotificacionesBean;
import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import ec.gob.ambiente.suia.notificaciones.facade.NotificacionesFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class NotificacionesController implements Serializable {

	private static final long serialVersionUID = -5539428729621810720L;

	private static final Logger LOG = Logger.getLogger(NotificacionesController.class);

	@Getter
	@Setter
	@ManagedProperty(value = "#{notificacionesBean}")
	private NotificacionesBean notificacionesBean;

	@EJB
	private NotificacionesFacade notificacionesFacade;

	@EJB
	private EnvioNotificacionesMailFacade envioNotificacionesMailFacade;

	@PostConstruct
	public void init() {
		notificacionesBean.setNotificationsList(new LazyNotificationDataModel(JsfUtil.getLoggedUser().getNombre(), true));
		notificacionesBean.setDeletedNotificationsList(new LazyNotificationDataModel(JsfUtil.getLoggedUser().getNombre(), false));

		notificacionesBean.setListaNotificacionesMail(new LazyEnvioNotificationesMailDataModel(JsfUtil.getLoggedUser().getNombre()));
		notificacionesBean.setListaNotificacionesMailRevisadas(new LazyEnvioNotificationesRevisadasDataModel(JsfUtil.getLoggedUser().getNombre()));
		try {
			notificacionesBean.setTotal(notificacionesFacade.getNotificacionesSize(JsfUtil.getLoggedUser().getNombre(), true));
			notificacionesBean.setTotalEliminadas(notificacionesFacade.getNotificacionesSize(JsfUtil.getLoggedUser().getNombre(), false));
			notificacionesBean.setTotal(envioNotificacionesMailFacade.contarRegistros(JsfUtil.getLoggedUser(), false));
			notificacionesBean.setTotalRevisadas(envioNotificacionesMailFacade.contarRegistros(JsfUtil.getLoggedUser(), true));
		} catch (Exception e) {
		}
	}

	public void seleccionarNotificacion(Notification notification, boolean removable) {
		notificacionesBean.setNotification(notification);
		notificacionesBean.setRemovable(removable);
		if (!notification.isReaded()) {
			notification.setReaded(true);
			try {
				notificacionesFacade.actualizarNotificacion(notification);
			} catch (Exception e) {
				LOG.error("Error al eliminar la notificacion", e);
			}
		}
	}

	public void eliminarNotificacion(Notification notification) {
		try {
			notificacionesFacade.eliminarNotificacion(notification);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			init();
		} catch (Exception e) {
			LOG.error("Error al eliminar la notificacion", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

	}

	public void seleccionarNotificacionMail(EnvioNotificacionesMail notification, boolean removable) {
		Notification objNotificacion = new Notification();
		objNotificacion.setSubject(notification.getAsunto());
		objNotificacion.setDate(notification.getFechaEnvio());
		objNotificacion.setProject(notification.getCodigoProyecto());
		objNotificacion.setText(notification.getContenido());
		notificacionesBean.setNotification(objNotificacion);
		notificacionesBean.setRemovable(removable);
		envioNotificacionesMailFacade.actualizarNotificacionesRevisada(notification.getId(), JsfUtil.getLoggedUser());
		// si no ha sido revisada cambio al estado revisado
		if(!notification.isNotificacionRevisada()){
			notificacionesBean.setTotal(envioNotificacionesMailFacade.contarRegistros(JsfUtil.getLoggedUser(), false));
			notificacionesBean.setTotalRevisadas(envioNotificacionesMailFacade.contarRegistros(JsfUtil.getLoggedUser(), true));
		}
	}
}
