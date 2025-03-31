package ec.gob.ambiente.suia.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import ec.gob.ambiente.suia.bandeja.controllers.EnvioNotificacionesMailController;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;

public class Email {
	
	public static List<String> noDefinido = Arrays.asList("N/D");
	
	public static boolean sendEmail(List<String> emailsDestino, List<String> emailsDestinoCopia, String asunto, String mensaje, String codigoTramite, Usuario usuarioDestino, Usuario usuarioEnvio) {
		try {
			String servidorSMTP = "172.16.0.92";
			String puerto = "25";
			String usuario = "notificacionesmae.suia@ambiente.gob.ec";
			String password = "5u!AD!5e2014";
			Properties props = new Properties();
			props.setProperty("mail.smtp.host", servidorSMTP);
			props.setProperty("mail.smtp.port", puerto);
			props.setProperty("mail.smtp.user", usuario);
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.connectiontimeout", "5000");
			props.setProperty("mail.smtp.timeout", "5000");
			Session session = Session.getDefaultInstance(props);

			MimeMessage message = new MimeMessage(session);

			for (String email : emailsDestino) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			}
			if (emailsDestinoCopia != null) {
				for (String email : emailsDestinoCopia) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
				}
			}

			message.setSubject(asunto);
			message.setSentDate(new Date());
			message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));
			message.setText(asunto);
			message.setContent(mensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
			EnvioNotificacionesMail objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacion(asunto, mensaje
					, emailsDestino, emailsDestinoCopia, codigoTramite, usuarioDestino, usuarioEnvio);

			Transport tr = session.getTransport("smtp");
			tr.connect(usuario, password);
			message.saveChanges();
			tr.sendMessage(message, message.getAllRecipients());
			tr.close();
			// guardo estado envio exitoso
			objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacionExitoso(objMail);
			return true;
		} catch (MessagingException e) {
			System.out.println("Error al enviar Email (MessagingException). "+e.getCause()+" "+e.getMessage());
		} catch (Throwable e) {
			System.out.println("Error al enviar Email (Throwable). "+e.getCause()+" "+e.getMessage());
		}
		return false;
	}
	
	public static boolean sendEmailAdjuntos(List<String> emailsDestino, List<String> emailsDestinoCopia, String asunto, String mensaje, List<String>listaArchivos, String codigoTramite, Usuario usuarioDestino, Usuario usuarioEnvio) {
		try {
			String servidorSMTP = "172.16.0.92";
			String puerto = "25";
			String usuario = "notificacionesmae.suia@ambiente.gob.ec";
			String password = "5u!AD!5e2014";
			Properties props = new Properties();
			props.setProperty("mail.smtp.host", servidorSMTP);
			props.setProperty("mail.smtp.port", puerto);
			props.setProperty("mail.smtp.user", usuario);
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.connectiontimeout", "5000");
			props.setProperty("mail.smtp.timeout", "5000");
			Session session = Session.getDefaultInstance(props);
			
			MimeMessage message = new MimeMessage(session);
			for (String email : emailsDestino) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			}
			if (emailsDestinoCopia != null) {
				for (String email : emailsDestinoCopia) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
				}
			}

			message.setSubject(asunto);
			message.setSentDate(new Date());
			message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));
			message.setText(asunto);
			
			BodyPart texto = new MimeBodyPart();
			MimeMultipart multiParte = new MimeMultipart();
			texto.setContent(mensaje, "text/html; charset=utf-8");
			multiParte.addBodyPart(texto);
			if (listaArchivos.size() > 0) {
				for (String nombreArchivo : listaArchivos) {
					BodyPart adjunto = new MimeBodyPart();
					adjunto.setDataHandler(new DataHandler(new FileDataSource(System.getProperty("java.io.tmpdir")+"/"+nombreArchivo)));
					adjunto.setFileName(MimeUtility.encodeText(nombreArchivo, "UTF-8", null));
					multiParte.addBodyPart(adjunto);
					message.setContent(multiParte);
				}
			}

			/*  guardo la notificaccion enviada */
			EnvioNotificacionesMail objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacion(asunto, mensaje
					, emailsDestino, emailsDestinoCopia, codigoTramite, usuarioDestino, usuarioEnvio);
			Transport tr = session.getTransport("smtp");
			tr.connect(usuario, password);
			tr.sendMessage(message, message.getAllRecipients());
			tr.close();
			// guardo estado envio exitoso
			objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacionExitoso(objMail);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
    }
	
	public static boolean sendEmail(String emailDestino, String asunto, String mensaje, String codigoTramite) {
		Usuario usuarioDestino = null;
		
		if(emailDestino != null && !emailDestino.isEmpty()) {
			List<String> emailsDestinoList = Arrays.asList(emailDestino.split(";"));
			return sendEmail(emailsDestinoList, null, asunto, mensaje, codigoTramite, usuarioDestino, usuarioDestino);
		} else {
			EnvioNotificacionesMail objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacion(asunto, mensaje
					, noDefinido, null, codigoTramite, usuarioDestino, usuarioDestino);
			objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacionExitoso(objMail);
			return true;
		}
	}
	
	public static boolean sendEmail(Usuario usuarioDestino, String asunto, String mensaje, String codigoTramite, Usuario usuarioEnvio) {
		List<Contacto> contactos = usuarioDestino.getPersona().getContactos();
		String emailDestino = "";
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
				emailDestino=contacto.getValor();
				break;
			}
		}
		
		if(emailDestino != null && !emailDestino.isEmpty()) {
			List<String> emailsDestinoList = Arrays.asList(emailDestino.split(";"));
	
			return sendEmail(emailsDestinoList, null, asunto, mensaje, codigoTramite, usuarioDestino, usuarioEnvio);
		} else {
			EnvioNotificacionesMail objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacion(asunto, mensaje
					, noDefinido, null, codigoTramite, usuarioDestino, usuarioEnvio);
			objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacionExitoso(objMail);
			return true;
		}
	}
	
	public static boolean sendEmailAdjuntos(Usuario usuarioDestino, String asunto, String mensaje, List<String> listaArchivos, String codigoTramite, Usuario usuarioEnvio) {
		List<Contacto> contactos = usuarioDestino.getPersona().getContactos();
		String emailDestino = "";
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
				emailDestino=contacto.getValor();
				break;
			}
		}
		
		if(emailDestino != null && !emailDestino.isEmpty()) {
			List<String> emailsDestinoList = Arrays.asList(emailDestino.split(";"));
			
			return sendEmailAdjuntos(emailsDestinoList, null, asunto, mensaje, listaArchivos, codigoTramite, usuarioDestino, usuarioEnvio);
		} else {
			EnvioNotificacionesMail objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacion(asunto, mensaje
					, noDefinido, null, codigoTramite, usuarioDestino, usuarioEnvio);
			objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacionExitoso(objMail);
			return true;
		}
		
	}
	
	public static boolean sendEmail(Organizacion orga, String asunto, String mensaje, String codigoTramite, Usuario usuarioDestino, Usuario usuarioEnvio) {
		List<Contacto> contactos = orga.getContactos();
		String emailDestino = "";
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
				emailDestino=contacto.getValor();
				break;
			}
		}
		
		if(emailDestino != null && !emailDestino.isEmpty()) {
			List<String> emailsDestinoList = Arrays.asList(emailDestino.split(";"));
	
			return sendEmail(emailsDestinoList, null, asunto, mensaje, codigoTramite, usuarioDestino, usuarioEnvio);
		} else {
			EnvioNotificacionesMail objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacion(asunto, mensaje
					, noDefinido, null, codigoTramite, usuarioDestino, usuarioEnvio);
			objMail = JsfUtil.getBean(EnvioNotificacionesMailController.class).guardarEnvioNotificacionExitoso(objMail);
			return true;
		}
	}
}
