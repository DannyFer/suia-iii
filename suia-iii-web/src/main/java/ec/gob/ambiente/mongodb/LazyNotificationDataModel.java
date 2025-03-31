package ec.gob.ambiente.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.notificaciones.domain.Notification;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.notificaciones.facade.NotificacionesFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public class LazyNotificationDataModel extends LazyDataModel<Notification> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3479713500589851595L;

	private NotificacionesFacade notificacionesFacade = BeanLocator
			.getInstance(NotificacionesFacade.class);

	private String nombreUsuario;
	private List<Notification> notificaciones;
	private Boolean estado;

	public LazyNotificationDataModel(String nombreUsuario, Boolean estado) {
		this.nombreUsuario = nombreUsuario;
		try {
			this.estado = estado;
			notificaciones = notificacionesFacade
					.getNotificacionesDelUsuarioRest(nombreUsuario, 0, 100,
							estado);
		} catch (ServiceException e) {
			notificaciones = new ArrayList<Notification>();
		}
	}

	@Override
	public Object getRowKey(Notification notificacion) {
		return notificacion.getId();
	}

	@Override
	public List<Notification> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		List<Notification> data = new ArrayList<Notification>();
		List<Notification> notifications = new ArrayList<Notification>();

		try {
			if (filters != null) {
				String project = "";
				if (filters.containsKey("project")) {
					project = (String) filters.get("project");
				}

				notifications = notificacionesFacade
						.getNotificacionesDelUsuarioRest(nombreUsuario, first
								/ pageSize, pageSize, this.estado, project);
				this.setRowCount(Integer.parseInt(Long
						.toString(notificacionesFacade.getNotificacionesSize(
								nombreUsuario, this.estado, project))));
			} else {
				notifications = notificacionesFacade
						.getNotificacionesDelUsuarioRest(nombreUsuario, first
								/ pageSize, pageSize, this.estado);
				this.setRowCount(Integer.parseInt(Long
						.toString(notificacionesFacade.getNotificacionesSize(
								nombreUsuario, this.estado))));
			}
			//
			// // sort
			// if (sortField != null) {
			// // Collections.sort(data, new LazySorter(sortField, sortOrder));
			// }}

			return notifications;

		} catch (ServiceException e1) {
		}
		// filter
		return data;
	}

	@Override
	public Notification getRowData(String rowKey) {
		for (Notification not : notificaciones) {
			if (not.getId().equals(rowKey))
				return not;
		}

		return null;
	}

}
