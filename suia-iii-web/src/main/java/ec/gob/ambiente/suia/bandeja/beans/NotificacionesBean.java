package ec.gob.ambiente.suia.bandeja.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.LazyDataModel;

import ec.gob.ambiente.notificaciones.domain.Notification;
import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;

@ManagedBean
@SessionScoped
public class NotificacionesBean implements Serializable {

	private static final long serialVersionUID = 2768524390649809086L;

	@Getter
	@Setter
	private LazyDataModel<Notification> notificationsList;

	@Getter
	@Setter
	private LazyDataModel<Notification> deletedNotificationsList;

	@Getter
	@Setter
	private LazyDataModel<EnvioNotificacionesMail> listaNotificacionesMail;

	@Getter
	@Setter
	private LazyDataModel<EnvioNotificacionesMail> listaNotificacionesMailRevisadas;

	@Getter
	@Setter
	private Notification notification;

	@Getter
	@Setter
	private Notification notificationEliminadas;

	@Getter
	@Setter
	private long total;

	@Getter
	@Setter
	private long totalRevisadas;

	@Getter
	@Setter
	private long totalEliminadas;

	@Getter
	@Setter
	private boolean removable;
}
