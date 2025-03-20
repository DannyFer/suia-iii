/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.tareas.programadas;

import java.io.File;
import java.util.Date;

import javax.ejb.EJB;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ec.gob.ambiente.suia.administracion.facade.NotificacionesMailFacade;
import ec.gob.ambiente.suia.domain.NotificacionesMail;
import ec.gob.ambiente.suia.domain.NotificacionesMails;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 
 * @author christian
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "envioMail") })
public class EnvioMail implements MessageListener {

	@EJB
	private NotificacionesMailFacade notificacionesFacade;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnvioMail.class);

	@Resource(lookup = "java:jboss/mail/magma", type = javax.mail.Session.class)
	private Session javaMailSession;

	private void cargarMensaje(Message msg, final NotificacionesMails not) {
		try {
			msg.setSubject(not.getAsunto());
			String desde = javaMailSession.getProperty("mail.smtp.user");
			msg.setFrom(new InternetAddress(desde, "Ministerio del Ambiente y Agua"));
			msg.setRecipient(Message.RecipientType.BCC, new InternetAddress(not.getEmail()));

			Multipart multipart = new MimeMultipart();

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(not.getContenido(), not.getTipoMensaje());

			multipart.addBodyPart(messageBodyPart);

			if (not.getAdjuntos() != null && !not.getAdjuntos().isEmpty()) {
				String[] adjuntos = not.getAdjuntos().split(File.pathSeparator);
				for (String attachment : adjuntos) {
					try {
						MimeBodyPart attachPart = new MimeBodyPart();
						DataSource source = new FileDataSource(attachment);
						attachPart.setDataHandler(new DataHandler(source));
						attachPart.setFileName(new File(attachment).getName());
						multipart.addBodyPart(attachPart);
					} catch (Exception e) {
						LOG.error("Error adjuntando el archivo! Se ha podido enviar el mensaje sin el ajunto!", e);
					}
				}
			}
			
			msg.setContent(multipart);

			Transport.send(msg);
		} catch (Exception e) {
			not.setObservacion(e.getMessage());
			not.setFechaObservacion(new Date());
			LOG.error(e, e);
		}

		NotificacionesMail notificacion = new NotificacionesMail();
		notificacion.setAsunto(not.getAsunto());
		notificacion.setContenido(not.getContenido());
		notificacion.setEmail(not.getEmail());
		notificacion.setFechaObservacion(not.getFechaObservacion());
		notificacion.setObservacion(not.getObservacion());
		notificacion.setTipoMensaje(not.getTipoMensaje());
		notificacion.setEstado(notificacion.getFechaObservacion() == null);
		notificacion.setAdjuntos(not.getAdjuntos());

		try {
			notificacionesFacade.guardar(notificacion);
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	@Override
	public void onMessage(javax.jms.Message msg) {
		try {
			ObjectMessage ob = (ObjectMessage) msg;
			Message msg1 = new MimeMessage(javaMailSession);
			NotificacionesMails not = (NotificacionesMails) ob.getObject();
			cargarMensaje(msg1, not);
		} catch (RuntimeException e) {
			LOG.error(e, e);
		} catch (JMSException e) {
			LOG.error(e, e);
		}
	}
}
