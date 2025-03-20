package ec.gob.ambiente.suia.notificaciones.facade;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.kie.api.task.model.Task;

import ec.gob.ambiente.notificaciones.domain.Notification;
import ec.gob.ambiente.notificaciones.domain.list.NotificationsList;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

@Stateless
public class NotificacionesFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	JbpmSuiaCustomServicesFacade jbpmSuiaCustomServicesFacade;

	public List<Notification> getNotificacionesDelUsuarioRest(String nombreUsuario, int first, int pageSize,
			Boolean estado) throws ServiceException {
		return getNotificacionesDelUsuarioRest(nombreUsuario, first, pageSize, estado, "");
	}

	public List<Notification> getNotificacionesDelUsuarioRest(String nombreUsuario, int first, int pageSize,
			Boolean estado, String proyecto) throws ServiceException {

		if (nombreUsuario == null) {
			throw new ServiceException("El nombre de usuario no puede ser NULL");
		}
		try {
			if (proyecto == null) {
				proyecto = "";
			}
			String url = Constantes.getNotificationService() + "?actorId=" + nombreUsuario + "&status="
					+ estado.toString() + "&startIndex=" + String.valueOf(first) + "&size=" + String.valueOf(pageSize)
					+ "&project=" + proyecto;

			ClientRequest request = new ClientRequest(url);
			ClientResponse<NotificationsList> response = null;
			try {
				response = request.get(NotificationsList.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (response.getStatus() != 200) {
				throw new RuntimeException("Error " + response.getStatus());
			}
			return response.getEntity().getNotifications();
		} catch (Exception e) {
			throw new ServiceException("No se pudo listar las notificaciones", e);
		}

	}

	public long getNotificacionesSize(String nombreUsuario, Boolean estado) throws ServiceException {
		return getNotificacionesSize(nombreUsuario, estado, "");
	}

	public long getNotificacionesSize(String nombreUsuario, Boolean estado, String proyecto) throws ServiceException {

		try {
			String url = Constantes.getNotificationService() + "?actorId=" + nombreUsuario + "&status="
					+ estado.toString() + "&project=" + proyecto;

			ClientRequest request = new ClientRequest(url);
			ClientResponse<NotificationsList> response = null;
			try {
				response = request.get(NotificationsList.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (response.getStatus() != 200) {
				throw new RuntimeException("Error " + response.getStatus());
			}
			return response.getEntity().getTotalNotifications();
		} catch (Exception e) {
			throw new ServiceException("No se pudo listar las notificaciones", e);
		}

	}

	public void adicionarNotificacion(String project, String subject, String text, String[] actorsIds,
			String processId, long processInstanceId, Task task) throws Exception {
		for (String actorId : actorsIds) {
			Notification notification = new Notification();
			notification.setDate(new Date());
			notification.setDeploymentId(Constantes.getDeploymentId());
			notification.setSubject(subject);
			notification.setText(text);
			notification.setProcessId(processId);
			notification.setProcessInstanceId(processInstanceId);
			notification.setActorId(actorId);
			notification.setProject(project);
			if (task != null) {
				notification.setTaskId(task.getId());
				notification.setTaskName(task.getNames().get(0).getText());
			}
			actualizarNotificacion(notification);
		}
	}

	public void adicionarNotificacion(Notification notification) throws Exception {
		actualizarNotificacion(notification);

	}

	public void actualizarNotificacion(Notification notification) throws Exception {
		String url = Constantes.getNotificationService();
		ClientRequest request = new ClientRequest(url);
		ClientResponse<String> response = null;
		JAXBContext jaxbContext = JAXBContext.newInstance(Notification.class);
		StringWriter writer = new StringWriter();
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.marshal(notification, writer);
		request.body("application/xml", writer.getBuffer().toString());
		response = request.put(String.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Error " + response.getStatus());
		}

	}

	public void eliminarNotificacion(Notification notification) throws Exception {
		notification.setStatus(false);
		actualizarNotificacion(notification);
	}

	public void actualizarNotificacionesTarea(Long taskId) throws Exception {
		String url = Constantes.getNotificationService();
		url+="-batch/?taskId="+taskId.toString();
		ClientRequest request = new ClientRequest(url);
		ClientResponse<String> response = null;
		response = request.put(String.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Error " + response.getStatus());
		}
	}
}
