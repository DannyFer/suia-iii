package ec.gob.ambiente.suia.tareas.programadas;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EnvioCopiaDocumento;
import ec.gob.ambiente.suia.domain.NotificacionesMails;
import ec.gob.ambiente.suia.domain.enums.TipoMensajeMailEnum;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class EnvioMailCC {

	@EJB
	private EnvioMailCliente envioMailCliente;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	/**
	 * Envia adjuntos por correo a los destinatarios especificados. Los
	 * destinatarios pueden ser direcciones de correo válidas o nombres de
	 * usuario del sistema. El sistema carga asunto y cuerpo del mensaje de
	 * forma predeterminada.
	 * 
	 * @param destinatarios
	 * @param adjuntos
	 * @throws ServiceException
	 */
	public void enviarConCopia(String[] destinatarios, String[] adjuntos) throws ServiceException {
		String asunto = "Documentación adjunta";
		String contenido = "Adjunto documentaci&oacute;n de su inter&eacute;s.";

		enviarConCopia(asunto, contenido, destinatarios, adjuntos);
	}

	/**
	 * Envia adjuntos por correo a los destinatarios especificados. Los
	 * destinatarios pueden ser direcciones de correo válidas o nombres de
	 * usuario del sistema. El sistema coloca el contendio en la plantilla de
	 * correos establecida.
	 * 
	 * @param asunto
	 * @param contenido
	 * @param destinatarios
	 * @param adjuntos
	 * @throws ServiceException
	 */
	public void enviarConCopia(String asunto, String contenido, String[] destinatarios, String[] adjuntos)
			throws ServiceException {

		for (String destinatario : destinatarios) {
			StringBuilder mensaje = new StringBuilder();
			mensaje.append("<p>Estimado usuario").append("</p>").append("<br/>");
			mensaje.append("<p>").append(contenido).append("</p>");
			mensaje.append("<br/><p>Saludos,").append("</p>").append("<br/>");
			mensaje.append("<p>Ministerio del Ambiente y Agua").append("</p>").append("<br/>");
			mensaje.append("<img src=\"").append(Constantes.getHttpSuiaImagenFooterMail())
					.append("\" height=\"110\" width=\"750\" alt=\"Smiley face\">");

			String attach = "";
			for (int i = 0; i < adjuntos.length; i++) {
				attach += adjuntos[i];
				if (i + 1 < adjuntos.length)
					attach += File.pathSeparator;
			}

			try {
				InternetAddress address = new InternetAddress(destinatario);
				address.validate();
			} catch (AddressException aex) {
				String usuario = destinatario;
				destinatario = usuarioFacade.getEmailFromUser(usuario);
				if (destinatario == null)
					destinatario = usuario;
			}

			NotificacionesMails notificacionesMail = new NotificacionesMails();
			notificacionesMail.setAsunto(asunto);
			notificacionesMail.setContenido(mensaje.toString());
			notificacionesMail.setEmail(destinatario);
			notificacionesMail.setTipoMensaje(TipoMensajeMailEnum.TEXT_HTML.getNemonico());
			notificacionesMail.setAdjuntos(attach);

			envioMailCliente.enviarMensaje(notificacionesMail);
		}
	}

	public EnvioCopiaDocumento getEnvioCopiaDocumento(String className, Integer entityId, String discriminator) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("className", className);
		parameters.put("entityId", entityId);
		parameters.put("discriminator", discriminator);
		return crudServiceBean.findByNamedQuerySingleResult(EnvioCopiaDocumento.GET_BY_CC_PARAMS, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public List<EnvioCopiaDocumento> getEnviosCopiasDocumentos(String className, Integer entityId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("className", className);
		parameters.put("entityId", entityId);
		return (List<EnvioCopiaDocumento>) crudServiceBean.findByNamedQuery(EnvioCopiaDocumento.GET_BY_CC_PARAMS, parameters);
	}

	public void saveEnvioCopiaDocumento(String className, Integer entityId, String discriminator, String[] to) {

		EnvioCopiaDocumento envioCopiaDocumento = getEnvioCopiaDocumento(className, entityId, discriminator);

		if (envioCopiaDocumento == null) {
			envioCopiaDocumento = new EnvioCopiaDocumento();
			envioCopiaDocumento.setNombreClase(className);
			envioCopiaDocumento.setIdEntidad(entityId);
			envioCopiaDocumento.setDiscriminador(discriminator);
		}

		String toString = "";
		if (to != null) {
			for (String string : to) {
				toString += string + ";";
			}
			toString = toString.substring(0, toString.length() - 1);
		}
		envioCopiaDocumento.setDestinatarios(toString);

		crudServiceBean.saveOrUpdate(envioCopiaDocumento);
	}
}
