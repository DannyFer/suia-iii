package ec.gob.ambiente.notificaciones.domain.list;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ec.gob.ambiente.notificaciones.domain.Notification;
import lombok.Getter;
import lombok.Setter; 

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationsList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@XmlElement
	private long totalNotifications;

	@Getter
	@Setter
	@XmlElement
	private List<Notification> notifications;

	public static class Builder {

		private NotificationsList notificationsList;

		public Builder() {
			notificationsList = new NotificationsList();
		}

		public Builder withNotificationList(List<Notification> notifications) {
			notificationsList.setNotifications(notifications);
			return this;
		}

		public Builder withTotalSize(long totalNotifications) {
			notificationsList.setTotalNotifications(totalNotifications);
			return this;
		}

		public NotificationsList build() {
			return this.notificationsList;
		}
	}
}
