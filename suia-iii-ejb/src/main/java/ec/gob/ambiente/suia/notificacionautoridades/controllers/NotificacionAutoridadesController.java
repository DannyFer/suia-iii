package ec.gob.ambiente.suia.notificacionautoridades.controllers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;


public class NotificacionAutoridadesController implements Serializable{
	
	 private static final long serialVersionUID = -8083094528006538436L;

	 private EnvioNotificacionesMailFacade envioNotificacionesMailFacade = (EnvioNotificacionesMailFacade) BeanLocator.getInstance(EnvioNotificacionesMailFacade.class);
	 private UsuarioFacade usuarioFacade = (UsuarioFacade) BeanLocator.getInstance(UsuarioFacade.class);

	 public void sendEmailAutoridades(String tipo,String tipo1,String destino, String asunto, String mensaje, String codigo, String nombre, String categoria,String ubicacion, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
			  try {
			   MimeMessage message = new MimeMessage(session);
			   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
			   message.setSubject(asunto);
			   message.setSentDate(new Date());		
			   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
			   message.setText(mensaje);			  
			   message.setContent(cargarAutoridadesMail(tipo,tipo1,codigo,nombre,categoria,ubicacion), "text/html; charset=utf-8");
				/*  guardo la notificaccion enviada */
			   List<String> emailsDestinoCopia = new ArrayList<>();;
			   List<String> emailsDestino = new ArrayList<>();
			   emailsDestino.add(destino);
			   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, mensaje, emailsDestino, emailsDestinoCopia, codigo, usuarioDestino, usuarioEnvio);

			   Transport tr = session.getTransport("smtp");
			   tr.connect(usuario, password);
			   message.saveChanges();   
			   tr.sendMessage(message, message.getAllRecipients());
			   tr.close();
				// guardo estado envio exitoso
			   guardarEnvioNotificacionExitoso(objMail);
			  } catch (MessagingException e) {
			   e.printStackTrace();
			  }
	    }	    
	 

	public String cargarAutoridadesMail(final String tipo,final String tipo1,final String codigo,final String nombres,
			final String categoria, final String ubicacion) {
		SimpleDateFormat fecha = new SimpleDateFormat(
                "dd 'de' MMMM 'de' yyyy",new Locale("es"));
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span><br></br>"
				+ "Emisión de "
				+ tipo
				+ " Ambiental"
				+ "</center>"
				+ "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>"
				+ "<tr>"
				+ "<td class=\"der\"> Se comunica que con fecha "
				+ fecha.format(new Date())
				+ " se emitió "+tipo1+" Ambiental del proyecto que consta de los siguientes datos: </td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Nombre: "
				+ nombres
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Codigo: "
				+ codigo
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Actividad: "
				+ categoria
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Tipo de permiso ambiental: "
				+ tipo.toUpperCase()
				+ " AMBIENTAL<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Ubicacion: provincia/s "
				+ ubicacion
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - @2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	public void sendEmailCuentaCoordenadas(String destino, String asunto, String mensaje,  String nombre,String codigo, String proponente, String categoria, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText(mensaje);			  
		   message.setContent(enviarMailCuentaCoordenadas(nombre,codigo, proponente,categoria), "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();;
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(destino);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, mensaje, emailsDestino, emailsDestinoCopia, codigo, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }	 

	public String enviarMailCuentaCoordenadas(final String nombres, final String codigo,final String proponente,final String categoria) {
		SimpleDateFormat fecha = new SimpleDateFormat(
                "dd 'de' MMMM 'de' yyyy",new Locale("es"));
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N DE INGRESO DE COORDENADAS </span><br></br>"
				+ ""
				+ "</center>"
				+ "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>"
				+ "<tr>"
				+ "<td> Estimadas Autoridades Ambientales:"
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\"> Con la finalidad de brindar un mejor seguimiento y control a los proyectos que ingresan a  través de la plataforma del Sistema Único de Información Ambiental SUIA, ponemos en su conocimiento que con fecha "
				+ fecha.format(new Date())
				+ ", se ha realizado la actualización de coordenadas por más de 5 ocasiones en el registro del siguiente trámite. </td>"
				+ "<br/>"
				+ "<br/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Nombre: "
				+ nombres
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Codigo: "
				+ codigo
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Proponente: "
				+ proponente
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>Actividad: "
				+ categoria
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - @2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	public void sendEmailSeguimientoRGD(String destino, String asunto, String mensaje,  String nombre,String codigoProyecto, List<TaskSummary> nombreTarea, String nombreProponente, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText(mensaje);
		   String textomensaje = enviarMailSeguimientoProponenteRGD(nombre,codigoProyecto, nombreTarea,nombreProponente);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(destino);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigoProyecto, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }
	
	public void sendEmailNotificacion90Dias(String destino, String asunto, String mensaje,String nombre,String codigoProyecto, String nombreProyecto, String nombreProponente,Integer numDias, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText(mensaje);
		   String textomensaje = enviarMail90DiasDesactivacion(nombre,codigoProyecto, nombreProyecto,nombreProponente,numDias);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(destino);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigoProyecto, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }
	
	public void sendEmailNotificacionPago(String destino, String asunto, String mensaje,String nombreProponente,String codigoProyecto, List<String>listPathArchivo, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText(mensaje);			  
		   
		   BodyPart texto = new MimeBodyPart();
		   MimeMultipart multiParte = new MimeMultipart();
		   String textomensaje = enviarMailPago(nombreProponente);
		   texto.setContent(textomensaje, "text/html; charset=utf-8");
		   multiParte.addBodyPart(texto);
		   if(listPathArchivo.size()>0){
			 int cont=1;
			 for (String pathArchivo : listPathArchivo) {
			   BodyPart adjunto = new MimeBodyPart();
			   adjunto.setDataHandler(new DataHandler(new FileDataSource(pathArchivo)));
			   adjunto.setFileName("comprobantePago"+cont+".pdf");
			   multiParte.addBodyPart(adjunto);
			   message.setContent(multiParte);
			   cont++;
			   }
		   }

			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(destino);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigoProyecto, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password); 
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail); 
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }
	
	public void sendEmailNotificacionAntesArchivar(String destino, String asunto, String mensaje,String nombre,String codigoProyecto, String nombreProyecto, String nombreProponente, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText(mensaje);
		   String textomensaje = enviarMailAntesDeArchivar(nombre,codigoProyecto, nombreProyecto,nombreProponente);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(destino);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigoProyecto, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);    
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }
	
	public void sendEmailSeguimientoLicencia(String destino, String asunto, String mensaje,  String nombre,String codigoProyecto, List<TaskSummary> nombreTarea, String nombreProponente, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText(mensaje);
		   String textomensaje = enviarMailSeguimientoLicencia(nombre,codigoProyecto, nombreTarea,nombreProponente);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(destino);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigoProyecto, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }

	public void sendEmailNotificacionesError(String destino, String asunto, String mensaje,  String nombre,String codigo, String proponente, String categoria, String enteResponsable, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText(mensaje);
		   String textomensaje = enviarMailNotificacionesError(mensaje, nombre,codigo, proponente,categoria,enteResponsable);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(destino);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigo, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }

	public String enviarMailNotificacionesError(final String mensaje, final String nombres, final String codigo,final String proponente,final String categoria,final String enteResponsable) {
		SimpleDateFormat fecha = new SimpleDateFormat(
                "dd 'de' MMMM 'de' yyyy",new Locale("es"));
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N DE ERROR REGISTRO DE PROYECTO </span><br></br>"
				+ ""
				+ "</center>"
				+ "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>"
				+ "<tr>"
				+ "<td> Estimados:"
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\"> Con la finalidad de brindar un mejor seguimiento y control a los proyectos que ingresan a  través de la plataforma del Sistema Único de Información Ambiental SUIA, ponemos en su conocimiento que con fecha "
				+ fecha.format(new Date())
				+ ", no se pudo realizar el registro del siguiente trámite."
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td><strong>Nombre:</strong> "
				+ nombres
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td><strong>Codigo:</strong> "
				+ codigo
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td><strong>Proponente:</strong> "
				+ proponente
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td><strong>Ente responsable:</strong> "
				+ enteResponsable
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td><strong>Actividad:</strong> "
				+ categoria
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td><strong>Error:</strong> "
				+ mensaje
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - @2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}

	public String enviarMailSeguimientoProponenteRGD(final String nombres, final String codigoProyecto,final List<TaskSummary> nombreTarea, final String nombreProponente) {

		String tareas = "<ol>";
		String tipoProceso ="";
		for (TaskSummary taskSummary : nombreTarea) {
			String taskName="";
			
			if(taskSummary.getProcessId().compareTo(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL)==0 
					&& taskSummary.getName().toUpperCase().equals(Constantes.TASK_NAME_DESCARGA_GUIAS_ESIA.toUpperCase())) {
				taskName=Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA;
			}
			
			if(taskSummary.getProcessId().compareTo(Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2)==0 
					&& taskSummary.getName().toUpperCase().equals(Constantes.TASK_NAME_DESCARGA_GUIAS_ESIA.toUpperCase())) {
				taskName=Constantes.NEW_TASK_NAME_DESCARGA_GUIAS_ESIA;
			}
			
			if(taskSummary.getName().toString().equals("Realizar pago")){
				taskName="Realizar pago (Operador)";
			}
			if(taskSummary.getName().toString().equals("Ingresar datos del registro")){
				taskName="Ingresar datos del registro (Operador)";
			}
			if(taskSummary.getName().toString().equals("Recibir tramite y asignar tecnico responsable")||taskSummary.getName().toString().equals("Recibir y analizar informaciÃÂÃÂÃÂÃÂ³n")){
				taskName="Recibir tramite y asignar tecnico responsable";
			}
			if(taskSummary.getName().toString().equals("Recibir y analizar informaciÃÂÃÂÃÂÃÂ³n")||taskSummary.getName().toString().equals("Recibir y analizar información") || taskSummary.getName().toString().equals("Recibir y analizar informaciÃ³n") ){
				taskName="Recibir y analizar información";
			}
			if(taskSummary.getName().toString().equals("Emitir informe tÃÂÃÂÃÂÃÂ©cnico y oficio")||taskSummary.getName().toString().equals("Emitir informe técnico y oficio") || taskSummary.getName().toString().equals("Emitir informe tÃ©cnico y oficio") ){
				taskName="Emitir informe técnico y oficio";
			}
			if(taskSummary.getName().toString().equals("Revisar pronunciamiento e informe tÃÂÃÂÃÂÃÂ©cnico")||taskSummary.getName().toString().equals("Revisar pronunciamiento e informe técnico") || taskSummary.getName().toString().equals("Revisar pronunciamiento e informe tÃ©cnico")){
				taskName="Revisar pronunciamiento e informe técnico";	
			}
			if(taskSummary.getName().toString().equals("Revisar pronunciamiento, informe tÃÂÃÂÃÂÃÂ©cnico y borrador de registro")||taskSummary.getName().toString().equals("Revisar pronunciamiento, informe técnico y borrador de registro")){
				taskName="Revisar pronunciamiento, informe técnico y borrador de registro";
			}
			if(taskSummary.getName().toString().equals("Firmar pronunciamiento favorable con registro de generador")){
				taskName="Firmar pronunciamiento favorable con registro de generador";
			}
			if(taskSummary.getName().toString().equals("Recibir pronunciamiento y registro de generador")){
				taskName="Recibir pronunciamiento y registro de generador";
			}
			if(taskSummary.getName().toString().equals("Firmar pronunciamiento electrÃÂÃÂÃÂÃÂ³nicamente")||taskSummary.getName().toString().equals("Firmar pronunciamiento electrónicamente")){
				taskName="Firmar pronunciamiento electrónicamente";
			}
			if(taskSummary.getName().toString().equals("Remitir respuestas aclaratorias")){
				taskName="Remitir respuestas aclaratorias (Operador)";
			}
			String tipoUsuario="";
			Usuario usuario = usuarioFacade.buscarUsuario(taskSummary.getActualOwner().getId());
			try {
				if(usuario != null && usuario.getId() != null){
					List<RolUsuario> listaRol = usuarioFacade.listarPorIdUsuario(usuario.getId());
					if(listaRol != null && listaRol.size() > 0){
						for (RolUsuario rolUsuario : listaRol) {
							if(rolUsuario.getRol().getNombre().toLowerCase().equals("sujeto de control")){
								tipoUsuario = "(Operador)";
							}
						}
						if(tipoUsuario.isEmpty())
							tipoUsuario = "(Autoridad Ambiental Competente)";
					}
				}
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			if(taskSummary.getStatus().toString().equals("Reserved")||taskSummary.getStatus().toString().equals("InProgress")){
				tareas +=  "Actualmente tiene pendiente la siguiente tarea: <br/><li> <b>"+ (taskName.isEmpty()?taskSummary.getName() :taskName)+" "+tipoUsuario +"</b></li>";
			}else {
				tareas +=  "<li> "+(taskName.isEmpty()?taskSummary.getName() :taskName) +" "+tipoUsuario+"</li>";
			}
			switch (taskSummary.getProcessId()) {
			case "Certificado-viabilidad-ambiental":
				tipoProceso ="Certificado Viabilidad Ambiental";
				break;
			case "mae-procesos.CertificadoAmbiental":
			case "rcoa.CertificadoAmbiental":
				tipoProceso ="Certificado Ambiental";
				break;
			case "mae-procesos.GeneradorDesechos":
			case "rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales":
				tipoProceso ="Generador Desechos Peligrosos y/o Especiales";
				break;
			case "mae-procesos.Recaudaciones":
				tipoProceso ="Recaudaciones";
				break;
			case "mae-procesos.registro-ambiental":
			case "mae-procesos.RegistroAmbiental":
			case "rcoa.RegistroAmbiental":
				tipoProceso ="Registro Ambiental";
				break;
			case "mae-procesos.RegistroEmisionesTransferenciaContaminantesEcuador":
				tipoProceso ="Registro Emisiones Transferencia Contaminantes del Ecuador";
				break;
			case "mae-procesos.RequisitosPrevios":
				tipoProceso ="Requisitos Previos";
				break;
			case "rcoa.AprobacionFinalEstudio":
				tipoProceso ="Aprobacion Final Estudio";
				break;
			case "rcoa.DeclaracionSustanciasQuimicas":
				tipoProceso ="Declaracion Sustancias Quimicas";
				break;
			case "rcoa.DigitalizacionProyectos":
			case "suia-iii.DigitalizacionProyectos":
				tipoProceso ="Digitalizacion Proyectos";
				break;
			case "rcoa.EstudioImpactoAmbiental":
			case "rcoa.EstudioImpactoAmbiental_v2":
				tipoProceso ="Estudio Impacto Ambiental";
				break;
			case "rcoa.InventarioForestal":
				tipoProceso ="Inventario Forestal";
				break;
			case "rcoa.ProcesoParticipacionCiudadana":
			case "rcoa.ProcesoParticipacionCiudadanaBypass":
				tipoProceso ="Proceso Participacion Ciudadana";
				break;
			case "rcoa.RegistroPreliminar":
			case "rcoa.RegistroPreliminarV2":
			case "rcoa.RegistroPreliminarV3":
				tipoProceso ="Registro Preliminar";
				break;
			case "rcoa.RegistroSustanciasQuimicas":
				tipoProceso ="Registro Sustancias Quimicas";
				break;
			case "rcoa.RegistroSustanciasQuimicasImportacion":
				tipoProceso ="Registro Sustancias Quimicas Importacion";
				break;
			case "rcoa.ResolucionLicenciaAmbiental":
				tipoProceso ="Resolucion Licencia Ambiental";
				break;
			case "rcoa.ViabilidadAmbiental":
			case "rcoa.ViabilidadAmbiental2":
				tipoProceso ="Viabilidad Ambiental";
				break;
			case "Suia.AprobracionRequisitosTecnicosGesTrans2":
				tipoProceso ="Aprobracion Requisitos Tecnicos";
				break;
			case "SUIA.LicenciaAmbiental":
				tipoProceso ="Licencia Ambiental";
				break;
			case "suia.participacion-social":
				tipoProceso ="Participacion Social";
				break;
			case "mae-procesos.Eliminarproyecto":
				tipoProceso ="";
				break;
			default:
				break;
			}
		}
		tareas+= "	</ol>";
		
		
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACIÓN DEL AVANCE DEL PROYECTO, OBRA O ACTIVIDAD </span><br></br>"
				+ ""
				+ "</center>"
				+ "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>"
				+ "<tr>"
				+ "<td> Estimado/a Operador: "+nombreProponente
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Con la finalidad de dar continuidad a su proceso y cumplir con los tiempos establecidos en la Normativa Ambiental Vigente para la obtención de "+tipoProceso+", con código: "+codigoProyecto+" en el Sistema de Regularización y Control Ambiental, se notifica lo siguiente:"
				+ "<br/><br/> Su trámite a cursado las siguientes etapas:<br/>"
				+tareas+"</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td>"
				+ "<br/>Finalmente, se solicita atender las tareas pendientes que se encuentran en su bandeja para poderlos culminar con éxito."
				+ "<td/>"
				+ "<tr/>"
				
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - @2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	public String enviarMailSeguimientoLicencia(final String nombres, final String codigoProyecto,final List<TaskSummary> nombreTarea, final String nombreProponente) {
		String tareas = "<ol>";
			
		for (TaskSummary taskSummary : nombreTarea) {			
			if(taskSummary.getStatus().toString().equals("Reserved")||taskSummary.getStatus().toString().equals("InProgress")){
			tareas +=  "Su trámite actualmente se encuentra en la tarea <br/><li> <b>"+taskSummary.getName().toString()+"</b></li><br/>";
			}else {
				tareas +=  "<li> "+taskSummary.getName().toString()+"</li><br/>";
			}
		}
		tareas+= "	</ol>";
		
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">AVANCES DEL PROYECTO </span><br></br>"
				+ "</center>"
				+ "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>"
				+ "<tr>"
				+ "<td> Estimado/a Proponente: "+nombreProponente
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Con la finalidad de brindar un mejor seguimiento y control a los trámites que ingresan a través de la plataforma del Sistema Único de Información Ambiental SUIA, ponemos en su conocimiento que el proceso: <b>"+codigoProyecto+"</b>,"
				+tareas+"</td>"
				+ "<br/>"
				+ "<br/>"
				+ "<tr/>"
				
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - @2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	
	 public void sendEmailProponentesTutoriales(String proponente,String destino, String asunto, String mensaje, String nombre, String codigo, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	

			  String usuario = "notificacionesmae.suia@ambiente.gob.ec";
			  String password = "5u!AD!5e2014";		
			  Properties props = new Properties();
	            props.setProperty("mail.smtp.host", "172.16.0.92");
	            props.setProperty("mail.smtp.port", "25");
	            props.setProperty("mail.smtp.user", usuario);
	            props.setProperty("mail.smtp.auth", "true");
	            props.setProperty("mail.smtp.connectiontimeout", "5000");
	            props.setProperty("mail.smtp.timeout", "5000");
	            Session session = Session.getDefaultInstance(props);			  
			  try {
			   MimeMessage message = new MimeMessage(session);
			   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
			   message.setSubject(asunto);
			   message.setSentDate(new Date());		
			   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
			   message.setText(mensaje);
			   String textomensaje = cargarTutorialMail(proponente,nombre, codigo);
			   message.setContent(textomensaje, "text/html; charset=utf-8");
				/*  guardo la notificaccion enviada */
			   List<String> emailsDestinoCopia = new ArrayList<>();
			   List<String> emailsDestino = new ArrayList<>();
			   emailsDestino.add(destino);
			   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigo, usuarioDestino, usuarioEnvio);
			   Transport tr = session.getTransport("smtp");
			   tr.connect(usuario, password);
			   message.saveChanges();   
			   tr.sendMessage(message, message.getAllRecipients());
			   tr.close();
				// guardo estado envio exitoso
			   guardarEnvioNotificacionExitoso(objMail);
			  } catch (MessagingException e) {
			   e.printStackTrace();
			  }
	    }	   
	 
	 public String cargarTutorialMail(final String  proponente ,final String nombres,final String  codigo) {

			String disenio = "<div class=\"centro\"	>	"
					+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
					+ "<center>"
					+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span><br></br>"
					+ ""
					+ "</center>"
					+ "<br /><br />"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
					+ "<tbody>"
					+ "<tr>"
					+ "<td> Estimado/a: "+ proponente
					+ "<br/>"
					+ "<br/>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td class=\"der\"> Su proyecto con nombre: "+nombres+" con codigo : "+codigo+" se encuentra registrado en la plataforma del Sistema Único de Información Ambiental SUIA. </td>"
					+ "<br/>"
					+ "<br/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td>Se pesenta los videos tutoriales para el mejor uso del sistema. "
					+ "<br/>"
					+ "<br/>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td>"
					+ "<td/>"
					+ "<tr/>"					
					+ "<tr>"
					+ "<td> <ul> <li><a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/EstadoProyecto.mp4\" target=\"_blank\"> Estado del proyecto </a></li></ul>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td> <ul> <li><a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/RegistroPago.mp4\" target=\"_blank\"> Pago de tasa </a></li></ul>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td><ul> <li><a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/TomarCoordenadas.mp4\" target=\"_blank\"> Toma de coordenadas </a> </li></ul>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td><ul> <li><a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/CreacionProyecto.mp4\" target=\"_blank\"> Tutorial registro del proyecto </a></li></ul> "
					+ "<td/>"
					+ "<tr/>"
//					+ "<tr>"
//					+ "<td> <ul> <li><a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/ReseteoClave.mp4\" target=\"_blank\"> Tutorial reseto de claves </a></li></ul>"
//					+ "<td/>"
//					+ "<tr/>"					
					+ "<tr>"
					+ "<td colspan=\"3\">"
					+ "<br/>"
					+ "<br/>"
					+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
					+ "Saludos Cordiales"
					+ "<br/>"
					+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
					+ "<br/>"
					+ "<br/>"
					+ "www.ambiente.gob.ec "
					+ "<br/>"
					+ "Quito - Ecuador"
					+ "<br/>"
					+ "</div>"
					+ "</td>"
					+ "</tr>"
					+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
			return disenio;
		}
	 
	 public void sendEmailCambioClaves(String destino, String asunto, String mensaje, String nombre, String codigo, Usuario usuarioDestino, Usuario usuarioEnvio) {
		 	String urlClave = usuarioDestino.getUsuarioCodigoCapcha();
			  String usuario = "notificacionesmae.suia@ambiente.gob.ec";
			  String password = "5u!AD!5e2014";		
			  Properties props = new Properties();
	            props.setProperty("mail.smtp.host", "172.16.0.92");
	            props.setProperty("mail.smtp.port", "25");
	            props.setProperty("mail.smtp.user", usuario);
	            props.setProperty("mail.smtp.auth", "true");
	            props.setProperty("mail.smtp.connectiontimeout", "5000");
	            props.setProperty("mail.smtp.timeout", "5000");
	            Session session = Session.getDefaultInstance(props);			  
			  try {
			   MimeMessage message = new MimeMessage(session);
			   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
			   message.setSubject(asunto);
			   message.setSentDate(new Date());		
			   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
			   message.setText(mensaje);
			   String textomensaje = enviarMailReseteroClaves(nombre,codigo,urlClave);
			   message.setContent(textomensaje, "text/html; charset=utf-8");
				/*  guardo la notificaccion enviada */
			   List<String> emailsDestinoCopia = new ArrayList<>();
			   List<String> emailsDestino = new ArrayList<>();
			   emailsDestino.add(destino);
			   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, null, usuarioDestino, usuarioEnvio);
			   Transport tr = session.getTransport("smtp");
			   tr.connect(usuario, password);
			   message.saveChanges();   
			   tr.sendMessage(message, message.getAllRecipients());
			   tr.close();
				// guardo estado envio exitoso
			   guardarEnvioNotificacionExitoso(objMail);
			  } catch (MessagingException e) {
			   e.printStackTrace();
			  }
	    }	   
	 
	 public String enviarMailReseteroClaves(final String nombres, final String codigo, final String urlClave) {
		 
			String disenio = "<div class=\"centro\"	>	"
					+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
					+ "<center>"
					+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">SISTEMA UNICO DE INFORMACIÓN AMBIENTAL</span><br></br>"
					+ ""
					+ "</center>"
					+ "<br /><br />"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
					+ "<tbody>"
					+ "<tr>"
					+ "<td> Estimado(a): "+nombres
					+ "<br/>"
					+ "<br/>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td class=\"der\"> El Sistema Único de Información Ambiental les da la bienvenida. Su cuenta ha sido registrada con el usuario:"
					+ codigo+ " </td>"
					+ "<br/>"
					+ "<br/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td>Para poder acceder al sistema deberá definir su contraseña ingresando a: "
					+ urlClave
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td>Si en lapso de 15 minutos no ha realizado el cambio de contraseña tendrá que volver a realizar todos los pasos anteriores para cambio de contraseña "
					+ "<td/>"
					+ "<tr/>"
					
					+ "<tr>"
					+ "<td colspan=\"3\">"
					+ "<br/>"
					+ "<br/>"
					+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
					+ "Saludos Cordiales"
					+ "<br/>"
					+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
					+ "<br/>"
					+ "Teléfono (593 2) 3987600 ext 3002"
					+ "<br/>"
					+ "www.ambiente.gob.ec "
					+ "<br/>"
					+ "Quito - Ecuador"
					+ "<br/>"
					+ "SUIA - @2017 "
					+ "</div>"
					+ "</td>"
					+ "</tr>"
					+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
			return disenio;
		}
	 public String enviarMail90DiasDesactivacion(final String nombres, final String codigoProyecto,final String nombreProyecto, final String nombreProponente, final Integer numDias) {
			
		 	String diasDesactivacion="";
		 	switch (numDias) {
			case 90:
				diasDesactivacion="noventa (90)";
				break;
			case 30:
				diasDesactivacion="treinta (30)";
				break;
			default:
				diasDesactivacion="("+numDias+")";
				break;
			}
		 	
			String disenio = "<div class=\"centro\"	>	"
					+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
					+ "<center>"
					+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACIÓN DE ARCHIVO DE PROYECTO</span><br></br>"
					+ ""
					+ "</center>"
					+ "<br /><br />"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
					+ "<tbody>"
					+ "<tr>"
					+ "<td> Estimado/a: "+nombreProponente
					+ "<br/>"
					+ "<br/>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td class=\"der\" style=\"text-align:justify;\"> Su proyecto con nombre: "+nombreProyecto+" con código: "+codigoProyecto+" se encuentra registrado en la plataforma del Sistema Único de Información Ambiental."
					+ "</td>"
					+ "</tr>"
					+ "<tr>"
					+ "<td class=\"der\" style=\"text-align:justify; font-size: 10px; \"> <br/><br/>En cumplimiento a la quinta disposición transitoria del Acuerdo Ministerial No. 061, publicado en Registro Oficial No. 316 de 04 de mayo de 2015, informamos que su proyecto, obra o actividad ha cumplido "+diasDesactivacion+" días hábiles sin ningún impulso de su parte, por tal razón se notifica el archivo del mismo."
					+ "</td>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td colspan=\"3\">"
					+ "<br/>"
					+ "<br/>"
					+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
					+ "Saludos Cordiales"
					+ "<br/>"
					+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
					+ "<br/>"
					+ "www.ambiente.gob.ec "
					+ "<br/>"
					+ "Quito - Ecuador"
					+ "<br/>"
					+ "SUIA - @2018 "
					+ "</div>"
					+ "</td>"
					+ "</tr>"
					+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
			return disenio;
		}
	 
	 public String enviarMailPago(final String nombreProponente) {
			
		 	
			String disenio = "<div class=\"centro\"	>	"
					+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
					+ "<center>"
					+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACIÓN PARA PAGO</span><br></br>"
					+ ""
					+ "</center>"
					+ "<br /><br />"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
					+ "<tbody>"
					+ "<tr>"
					+ "<td> Estimado/a: "+nombreProponente
					+ "<br/>"
					+ "<br/>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td class=\"der\" style=\"text-align:justify;\"> Por favor realizar el pago que se detalla en los archivos adjuntos."
					+ "</td>"
					+ "</tr>"
					+ "<tr>"
					+ "<td colspan=\"3\">"
					+ "<br/>"
					+ "<br/>"
					+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
					+ "Saludos Cordiales"
					+ "<br/>"
					+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
					+ "<br/>"
					+ "www.ambiente.gob.ec "
					+ "<br/>"
					+ "Quito - Ecuador"
					+ "<br/>"
					+ "SUIA - @2018 "
					+ "</div>"
					+ "</td>"
					+ "</tr>"
					+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
			return disenio;
		}
	 
	 public String enviarMailAntesDeArchivar(final String nombres, final String codigoProyecto,final String nombreProyecto, final String nombreProponente) {
		 	
			String disenio = "<div class=\"centro\"	>	"
					+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
					+ "<center>"
					+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACIÓN DE ARCHIVO DE PROYECTO</span><br></br>"
					+ ""
					+ "</center>"
					+ "<br /><br />"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
					+ "<tbody>"
					+ "<tr>"
					+ "<td> Estimado/a: "+nombreProponente
					+ "<br/>"
					+ "<br/>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td class=\"der\" style=\"text-align:justify;\"> Su proyecto con nombre: "+nombreProyecto+" con código: "+codigoProyecto+" se encuentra registrado en la plataforma del Sistema Único de Información Ambiental."
					+ "</td>"
					+ "</tr>"
					+ "<tr>"
					+ "<td class=\"der\" style=\"text-align:justify; font-size: 10px; \"> <br/><br/> Si el usuario o proponente no da una respuesta al trámite a través del SUIA, la administración podrá declarar la caducidad del proceso de licenciamiento de conformidad con lo indicado en el Estatuto del Régimen Jurídico y Administrativo de la Función Ejecutiva – ERJAFE, cuerpo legal que en el numeral 1 del artículo 159 establece que: \"En los procedimientos iniciados a solicitud del interesado, cuando se produzca su paralización por causa imputable al mismo, la administración le advertirá que, transcurridos dos meses, se producirá la caducidad del mismo. Consumido este plazo sin que el particular requerido realice las actividades necesarias para reanudar la tramitación, la administración acordará el archivo de las actuaciones, notificándoselo al interesado (...)\"<br/> Con este antecedente, queremos poner en su conocimiento que, tiene un tiempo restante de hasta 15 días para no ser archivado automáticamente por el SUIA."
					+ "</td>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td colspan=\"3\">"
					+ "<br/>"
					+ "<br/>"
					+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
					+ "Saludos Cordiales"
					+ "<br/>"
					+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
					+ "<br/>"
					+ "www.ambiente.gob.ec "
					+ "<br/>"
					+ "Quito - Ecuador"
					+ "<br/>"
					+ "SUIA - @2018 "
					+ "</div>"
					+ "</td>"
					+ "</tr>"
					+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
			return disenio;
		}
	 
	public void sendEmailDocumentosTdrsMineria(String destino, String asunto, String destinatario, String mensaje) {
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
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
			message.setSubject(asunto);
			message.setSentDate(new Date());
			message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));
			message.setText("");
			String textomensaje = mailDocumentosTdrsMineria(destinatario, mensaje);
			message.setContent(textomensaje, "text/html; charset=utf-8");
			Transport tr = session.getTransport("smtp");
			tr.connect(usuario, password);
			message.saveChanges();
			tr.sendMessage(message, message.getAllRecipients());
			tr.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public String mailDocumentosTdrsMineria(final String destinatario, final String mensaje) {
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span>"
				+ "</center>" + "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>" 
				+ "<tr>" 
				+ "<td> Estimado/a: " + destinatario
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> " + mensaje 
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - @2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	
	/**
	 * Cristina Flores Enviar mail	 * 
	 */
	
	public void sendEmailFacilitadores(String asunto,String facilitador, String nombreProceso, String ubicacion, 
			String email, String representanteLegal, String correoProponente, String telefono, String codigoProyecto, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText("");
		   String textomensaje = mailFacilitador(facilitador, nombreProceso, ubicacion, representanteLegal, correoProponente, telefono);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(email);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigoProyecto, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }	   
	
	public String mailFacilitador(String facilitador, String nombreProceso, String ubicacion, String representanteLegal, 
    		String correoProponente, String telefono) {
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span>"
				+ "</center>" + "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>" 
				+ "<tr>" 
				+ "<td> Estimado/a " + facilitador + ":"
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Por medio del presente me permito informar a Usted que ha sido asignado (a) como facilitador (a) para el proceso " 
				+ nombreProceso + " ubicado en  " + ubicacion
				+ "<br/>"
				+ "<br/>"
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Los datos del proponente son:"
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\">Nombre del Representante Legal del Proyecto: "+ representanteLegal
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\">e-mail: " + correoProponente
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\">Teléfono: "+ telefono
				+ "<br/>"
				+ "<br/>"
				+ "</td>"
				+ "<tr/>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Usted tiene tres días hábiles para aceptar o rechazar el proceso; en caso de rechazo deberá adjuntar un "
				+ "documento firmado, con los respectivos medios de verificación, donde se especifiquen las causas de la no "
				+ "aceptación, así también en caso de no se acepte en el periodo establecido será considerado como "
				+ "rechazo no justificado conforme se establece en el Art. 24 del Acuerdo Ministerial 103."
				+ "<br/>"
				+ "<br/>"
				+ "</td>"
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - 2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	public void sendEmailFacilitadoresSinDatos(String asunto,String facilitador, String email, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText("");
		   String textomensaje = mailFacilitadorSinDatos(facilitador);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(email);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, null, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }	   
	
	public String mailFacilitadorSinDatos(String facilitador) {
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span>"
				+ "</center>" + "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>" 
				+ "<tr>" 
				+ "<td> Estimado/a " + facilitador + ":"
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Por medio del presente me permito informar a Usted que ha sido asignado (a) como facilitador (a) de un proceso."
				+ "<br/>"
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Revise su Bandeja de Tareas."
				+ "<br/>"
				+ "<br/>"
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - 2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	//mail de facilitadores
	public void sendEmailFacilitadoresFinalProceso(String asunto,String facilitador, String email, String url, String tramite, String nombreProceso, String ubicacion, String nombreDocumento, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText("");	
		   
		   BodyPart messageBodyPart = new MimeBodyPart();
		   messageBodyPart.setText("");
		   String textomensaje = mailFacilitadorFinProceso(facilitador, tramite, nombreProceso, ubicacion);
		   messageBodyPart.setContent(textomensaje, "text/html; charset=utf-8");
		   Multipart multipart = new MimeMultipart();
		   multipart.addBodyPart(messageBodyPart);
		   
		   messageBodyPart = new MimeBodyPart();
		   String filename = url;
		   DataSource source = new FileDataSource(filename);
		   messageBodyPart.setDataHandler(new DataHandler(source));
		   messageBodyPart.setFileName(nombreDocumento);
		   multipart.addBodyPart(messageBodyPart);
		   		   
		   message.setContent(multipart);
		   message.saveChanges();   

			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(email);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, tramite, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		 
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
    }	 
	
	public String mailFacilitadorFinProceso(String facilitador, String tramite, String nombreProceso, String ubicacion) {
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span>"
				+ "</center>" + "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>" 
				+ "<tr>" 
				+ "<td> Estimado/a " + facilitador + ":"
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Por medio del presente me permito informar a Usted que ha culminado la evaluación social del proceso " 
				+ nombreProceso + " " + tramite + " ubicado en  " + ubicacion
				+ "<br/>"
				+ "<br/>"
				+ "</td>"
				+ "<tr/>"				
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - 2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	//mail notificacion de rechazo pps
	public void sendEmailRechazoFacilitadores(String asunto,String facilitador, String email, String tramite, String nombreUsuario, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));
		   message.setText("");
		   String textomensaje = mailRechazoFacilitadores(facilitador, tramite, nombreUsuario);
		   message.setContent(textomensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(email);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, tramite, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }	   
	
	public String mailRechazoFacilitadores(String facilitador, String tramite, String nombreUsuario) {
		String disenio = "<div class=\"centro\"	>	"
				+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
				+ "<center>"
				+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span>"
				+ "</center>" + "<br /><br />"
				+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
				+ "<tbody>" 
				+ "<tr>" 
				+ "<td> Estimado/a " + nombreUsuario + ":"
				+ "<br/>"
				+ "<br/>"
				+ "<td/>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td class=\"der\" style=\"text-align:justify;\"> Por medio del presente me permito informar a Usted que el facilitador " + facilitador
				+ " ha rechazado el proceso de participación social " + tramite + "."
				+ "<br/>"
				+ "</td>"
				+ "<tr/>"
				+ "<tr>"
				+ "<td colspan=\"3\">"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
				+ "Saludos Cordiales"
				+ "<br/>"
				+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
				+ "<br/>"
				+ "Teléfono (593 2) 3987600 ext 3002"
				+ "<br/>"
				+ "www.ambiente.gob.ec "
				+ "<br/>"
				+ "Quito - Ecuador"
				+ "<br/>"
				+ "SUIA - 2018 "
				+ "</div>"
				+ "</td>"
				+ "</tr>"
				+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
		return disenio;
	}
	
	public void sendEmailInformacionProponente(String email, String nombreUsuario, String mensaje, String asunto, String tramite, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
		  try {
		   MimeMessage message = new MimeMessage(session);
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		   message.setSubject(asunto);
		   message.setSentDate(new Date());		
		   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
		   message.setText("");			  
		   message.setContent(mensaje, "text/html; charset=utf-8");
			/*  guardo la notificaccion enviada */
		   List<String> emailsDestinoCopia = new ArrayList<>();
		   List<String> emailsDestino = new ArrayList<>();
		   emailsDestino.add(email);
		   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, mensaje, emailsDestino, emailsDestinoCopia, tramite, usuarioDestino, usuarioEnvio);
		   Transport tr = session.getTransport("smtp");
		   tr.connect(usuario, password);
		   message.saveChanges();   
		   tr.sendMessage(message, message.getAllRecipients());
		   tr.close();
			// guardo estado envio exitoso
		   guardarEnvioNotificacionExitoso(objMail);
		  } catch (MessagingException e) {
		   e.printStackTrace();
		  }
    }
	
	//mail notificacion de eliminacion de proyecto a facilitadores
		public void sendEmailBajaProyectoFacilitadores(String asunto, String email, String tramite, String nombreFacilitador) {   		    	
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
			  try {
			   MimeMessage message = new MimeMessage(session);
			   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			   message.setSubject(asunto);
			   message.setSentDate(new Date());		
			   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
			   message.setText("");
			   String textomensaje = mailBajaProyectoFacilitadores(tramite, nombreFacilitador);
			   message.setContent(textomensaje, "text/html; charset=utf-8");
				/*  guardo la notificaccion enviada */
			   List<String> emailsDestinoCopia = new ArrayList<>();
			   List<String> emailsDestino = new ArrayList<>();
			   emailsDestino.add(email);
			   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, tramite, new Usuario(), new Usuario());
			   Transport tr = session.getTransport("smtp");
			   tr.connect(usuario, password);
			   message.saveChanges();   
			   tr.sendMessage(message, message.getAllRecipients());
			   tr.close();
				// guardo estado envio exitoso
			   guardarEnvioNotificacionExitoso(objMail);
			  } catch (MessagingException e) {
			   e.printStackTrace();
			  }
	    }	   
		
		public String mailBajaProyectoFacilitadores(String tramite, String nombreFacilitador) {
			String disenio = "<div class=\"centro\"	>	"
					+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
					+ "<center>"
					+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">NOTIFICACI&Oacute;N</span>"
					+ "</center>" + "<br /><br />"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
					+ "<tbody>" 
					+ "<tr>" 
					+ "<td> Estimado/a " + nombreFacilitador + ":"
					+ "<br/>"
					+ "<br/>"
					+ "<td/>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td class=\"der\" style=\"text-align:justify;\"> "
					+ "Por medio del presente me permito informar a Usted que el proceso "+tramite+" "
					+ "ha sido archivado y/o dado de baja, motivo por el cual Usted ya no deber&aacute; continuar con la ejecuci&oacute;n del proceso de participaci&oacute;n social"
					+ "<br/>"
					+ "</td>"
					+ "<tr/>"
					+ "<tr>"
					+ "<td colspan=\"3\">"
					+ "<br/>"
					+ "<br/>"
					+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
					+ "Saludos Cordiales"
					+ "<br/>"
					+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
					+ "<br/>"
					+ "Teléfono (593 2) 3987600 ext 3002"
					+ "<br/>"
					+ "www.ambiente.gob.ec "
					+ "<br/>"
					+ "Quito - Ecuador"
					+ "<br/>"
					+ "SUIA - 2018 "
					+ "</div>"
					+ "</td>"
					+ "</tr>"
					+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
			return disenio;
		}
		
		public void sendEmailArchivoAdjunto(String asunto,String email, String url, String nombreDocumento, String mensaje, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
			  try {
			   MimeMessage message = new MimeMessage(session);
			   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			   message.setSubject(asunto);
			   message.setSentDate(new Date());		
			   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
			   message.setText("");	
			   
			   BodyPart messageBodyPart = new MimeBodyPart();
			   messageBodyPart.setText("");
			   messageBodyPart.setContent(mensaje, "text/html; charset=utf-8");
			   Multipart multipart = new MimeMultipart();
			   multipart.addBodyPart(messageBodyPart);
			   
			   messageBodyPart = new MimeBodyPart();
			   String filename = url;
			   DataSource source = new FileDataSource(filename);
			   messageBodyPart.setDataHandler(new DataHandler(source));
			   messageBodyPart.setFileName(nombreDocumento);
			   multipart.addBodyPart(messageBodyPart);
			   		   
			   message.setContent(multipart);
			   message.saveChanges();   

				/*  guardo la notificaccion enviada */
			   List<String> emailsDestinoCopia = new ArrayList<>();
			   List<String> emailsDestino = new ArrayList<>();
			   emailsDestino.add(email);
			   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, mensaje, emailsDestino, emailsDestinoCopia, null, usuarioDestino, usuarioEnvio);
			   Transport tr = session.getTransport("smtp");
			   tr.connect(usuario, password);
			 
			   tr.sendMessage(message, message.getAllRecipients());
			   tr.close();
				// guardo estado envio exitoso
			   guardarEnvioNotificacionExitoso(objMail);
			  } catch (Exception e) {
			   e.printStackTrace();
			  }
	    }	
		
		public void sendEmailInformacion(String email, String mensaje, String asunto, String tramite, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
			  try {
			   MimeMessage message = new MimeMessage(session);
			   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			   message.setSubject(asunto);
			   message.setSentDate(new Date());		
			   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
			   message.setText("");
			   message.setContent(mensaje, "text/html; charset=utf-8");
				/*  guardo la notificaccion enviada */
			   List<String> emailsDestinoCopia = new ArrayList<>();
			   List<String> emailsDestino = new ArrayList<>();
			   emailsDestino.add(email);
			   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, mensaje, emailsDestino, emailsDestinoCopia, tramite, usuarioDestino, usuarioEnvio);
			   Transport tr = session.getTransport("smtp");
			   tr.connect(usuario, password);
			   message.saveChanges();   
			   tr.sendMessage(message, message.getAllRecipients());
			   tr.close();
				// guardo estado envio exitoso
			   guardarEnvioNotificacionExitoso(objMail);
			  } catch (MessagingException e) {
			   e.printStackTrace();
			  }
	    }
		
		/**
		 * Maeil para notificar al tecnico de mineria en scout drilling
		 * @param destino
		 * @param asunto
		 * @param mensaje
		 * @param codigo
		 * @param nombre
		 */
		 public void sendEmailAutoridadMineria(String tipo, String destino, String asunto, String mensaje, String codigo, String nombre, String proponente, Usuario usuarioDestino, Usuario usuarioEnvio) {   		    	
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
				  try {
				   MimeMessage message = new MimeMessage(session);
				   message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
				   message.setSubject(asunto);
				   message.setSentDate(new Date());		
				   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
				   message.setText(mensaje);
				   String textomensaje = cargarAutoridadMineria(tipo, codigo,nombre, proponente);
				   message.setContent(textomensaje, "text/html; charset=utf-8");
					/*  guardo la notificaccion enviada */
				   List<String> emailsDestinoCopia = new ArrayList<>();
				   List<String> emailsDestino = new ArrayList<>();
				   emailsDestino.add(destino);
				   EnvioNotificacionesMail objMail = guardarEnvioNotificacion(asunto, textomensaje, emailsDestino, emailsDestinoCopia, codigo, usuarioDestino, usuarioEnvio);
				   Transport tr = session.getTransport("smtp");
				   tr.connect(usuario, password);
				   message.saveChanges();   
				   tr.sendMessage(message, message.getAllRecipients());
				   tr.close();
					// guardo estado envio exitoso
				   guardarEnvioNotificacionExitoso(objMail);
				  } catch (MessagingException e) {
				   e.printStackTrace();
				  }
		    }

			public String cargarAutoridadMineria(final String tipo, final String codigo,final String nombre, final String proponente) {

				String disenio = "<div class=\"centro\"	>	"
						+ "<img src=\"http://desa-saf.ambiente.gob.ec/sib_patente/assets/img/logo_mae.png\"/> "
						+ "<center>"
						+ "<span style=\"font-weight: bold; font-size: 16px; text-decoration; underline; \">SISTEMA UNICO DE INFORMACIÓN AMBIENTAL</span><br></br>"
						+ tipo
						+ "</center>"
						+ "<br /><br />"
						+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"
						+ "<tbody>"
						+ "<tr>"
						+ "<td class=\"der\"> Estimado: Se informa que el trámite "+codigo+",  "+nombre+", "+proponente+" ha finalizado el registro del proyecto, favor dirigirse a Proyectos – Sondeo de pruebas o reconocimiento para que verifique si el valor del 100% al PMA corresponde al valor de la póliza o garantía bancaria así como a la declaración juramentada. Para la finalización del proceso "
						+ "  </td>"
						+ "<tr/>"
						+ "<tr>"
						+ "<td colspan=\"3\">"
						+ "<br/>"
						+ "<br/>"
						+ "<div style=\"padding: 5px; margin: 0 auto; font-size: 10px; align=\"left\">"
						+ "Saludos Cordiales"
						+ "<br/>"
						+ "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA"
						+ "<br/>"
						+ "Teléfono (593 2) 3987600 ext 3002"
						+ "<br/>"
						+ "www.ambiente.gob.ec "
						+ "<br/>"
						+ "Quito - Ecuador"
						+ "<br/>"
						+ "SUIA - @2018 "
						+ "</div>"
						+ "</td>"
						+ "</tr>"
						+ "</tbody>" + "</table>" + "</p>" + "<br />" + "</div>";
				return disenio;
			}
	
			public void sendEmailDocumentoAdjunto(String asunto, String mensaje, String email, List<String> urls) {   		    	
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
				  try {
				   MimeMessage message = new MimeMessage(session);
				   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
				   message.setSubject(asunto);
				   message.setSentDate(new Date());		
				   message.setFrom(new InternetAddress("notificacionesmae.suia@ambiente.gob.ec"));			   
				   message.setText("");	
				   
				   BodyPart messageBodyPart = new MimeBodyPart();
				   messageBodyPart.setText("");
				   messageBodyPart.setContent(mensaje, "text/html; charset=utf-8");
				   Multipart multipart = new MimeMultipart();
				   multipart.addBodyPart(messageBodyPart);
				   
				   for(String url : urls){
					   
					   List<String> paramArray = Arrays.asList(url.split(";"));
					   
					   messageBodyPart = new MimeBodyPart();
					   String filename = paramArray.get(0);
					   DataSource source = new FileDataSource(filename);
					   messageBodyPart.setDataHandler(new DataHandler(source));
					   messageBodyPart.setFileName(paramArray.get(1));
					   multipart.addBodyPart(messageBodyPart);					   
				   }				   				   				   		 
				   message.setContent(multipart);
				   message.saveChanges();   
				   
				   Transport tr = session.getTransport("smtp");
				   tr.connect(usuario, password);
				 
				   tr.sendMessage(message, message.getAllRecipients());
				   tr.close();		    
				  } catch (Exception e) {
				   e.printStackTrace();
				  }
		    }
	
			public EnvioNotificacionesMail guardarEnvioNotificacion(String asunto, String mensaje, List<String> emailsDestino, List<String> emailsDestinoCopia, String codigoTramite, Usuario usuarioDestino, Usuario usuarioEnvia) {
				EnvioNotificacionesMail objMail = new EnvioNotificacionesMail();
				try{
					String mailDestino = listToString(emailsDestino);
					String mailDestinoCopia = listToString(emailsDestinoCopia);
					objMail.setAsunto(asunto);
					objMail.setContenido(mensaje);
					objMail.setEmail(mailDestino);
					objMail.setEmailCopia(mailDestinoCopia);
					objMail.setCodigoProyecto(codigoTramite);
					objMail.setEnviado(false);
					objMail.setEstado(true);
					if(usuarioDestino != null && usuarioDestino.getNombre() != null){
						objMail.setNombreUsuarioDestino(usuarioDestino.getNombre());
						objMail.setUsuarioDestinoId(usuarioDestino.getId());
					}
					if(usuarioEnvia != null && usuarioEnvia.getId() != null){
						objMail.setUsuarioEnvioId(usuarioEnvia.getId());
					}
					// para guardar si fue automatico a que tipo de usuario envio SITEAA= automatico
					if(usuarioEnvia != null && usuarioEnvia.getNombrePersona() != null && !usuarioEnvia.getNombrePersona().isEmpty()){
						objMail.setAsunto(usuarioEnvia.getNombrePersona() + " - " + objMail.getAsunto());
					}
					envioNotificacionesMailFacade.save(objMail);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return objMail;
			}

			public EnvioNotificacionesMail guardarEnvioNotificacionExitoso(EnvioNotificacionesMail objMail) {				
				objMail.setEnviado(true);
				objMail.setFechaEnvio(new Date());
				envioNotificacionesMailFacade.save(objMail);
				return objMail;
			}
			/**
			 * listToString Retorna un String con los valores separados por ";"
			 * @param lista
			 * @return
			 */
			public static String listToString(List<String> lista)
			{
				if(lista!=null&&!lista.isEmpty())
				{
					String ret=lista.toString();		
					//ret=ret.replace(", ", ";");
					ret=ret.replace("[", "");
					ret=ret.replace("]", "");
					return ret;
				}
				return null;
			}
}
