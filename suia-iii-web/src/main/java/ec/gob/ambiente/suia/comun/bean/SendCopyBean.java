package ec.gob.ambiente.suia.comun.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.EnvioCopiaDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.tareas.programadas.EnvioMailCC;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class SendCopyBean implements Serializable {

	private static final long serialVersionUID = 6957653818411770514L;

	private Map<String, List<String>> values;

	private Map<String, Usuario> users;

	@Getter
	@Setter
	private String value;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private EnvioMailCC envioMailCC;

	private boolean loaded;

	@PostConstruct
	private void init() {
		values = new HashMap<String, List<String>>();
		users = new HashMap<String, Usuario>();
	}

	public void sendFilesCopies(String className, Integer entityId, String discriminator, String[] absolutePathFiles)
			throws ServiceException {
		sendFilesCopies(className, entityId, discriminator, null, null, absolutePathFiles);
	}

	public void sendFilesCopies(String className, Integer entityId, String discriminator, String subject, String body,
			String[] absolutePathFiles) throws ServiceException {
		if (absolutePathFiles == null || absolutePathFiles.length == 0)
			throw new ServiceException("Attachment files are NULL.");

		guardarFilesCopies(className, entityId, discriminator);

		List<String> result = getEmailsDestinatarios(className, entityId, discriminator);
		if (result != null) {
			String[] to = new String[result.size()];
			for (int i = 0; i < result.size(); i++)
				to[i] = result.get(i);
			if (subject != null || body != null)
				envioMailCC.enviarConCopia(subject, body, (String[]) result.toArray(), absolutePathFiles);
			else
				envioMailCC.enviarConCopia(to, absolutePathFiles);
		}
	}

	public List<String> destinatarios(String className, Integer entityId, String discriminator) {
		if (!loaded) {
			EnvioCopiaDocumento envioCopiaDocumento = envioMailCC.getEnvioCopiaDocumento(className, entityId,
					discriminator);
			if (envioCopiaDocumento != null) {
				String to = envioCopiaDocumento.getDestinatarios();
				List<String> result = new ArrayList<String>();
				if (to != null && !to.isEmpty()) {
					String[] toArray = to.split(";");
					for (String string : toArray) {
						result.add(string);
					}
				}
				values.put(className + "_" + entityId + "_" + discriminator, result);
			}
			loaded = true;
		}
		if (values.containsKey(className + "_" + entityId + "_" + discriminator))
			return values.get(className + "_" + entityId + "_" + discriminator);
		return null;
	}

	private List<String> getEmailsDestinatarios(String className, Integer entityId, String discriminator) {
		if (values.containsKey(className + "_" + entityId + "_" + discriminator)) {
			List<String> destinatarios = values.get(className + "_" + entityId + "_" + discriminator);
			List<String> emails = new ArrayList<String>();
			for (String destinatario : destinatarios) {
				if (destinatario.contains("<"))
					emails.add(destinatario.substring(destinatario.lastIndexOf("<") + 1, destinatario.length() - 1));
				else
					emails.add(destinatario);
			}
			return emails;
		}
		return null;
	}
	
	public List<String> executeQuery(String query) {
		return usuarioFacade.getUserNamesOrEmails(query);
	}

	public void adicionar(String className, Integer entityId, String discriminator, boolean autoSave, boolean onlySystemEmail) {
		if (value == null || value.isEmpty()) {
			JsfUtil.addMessageError("El campo 'Destinatario' es requerido.");
			return;
		}
		
		String currentValue = value;

		if (!isEmailAddress(value)) {
			String result = usuarioFacade.getEmailFromUser(value);
			if (result == null) {
				JsfUtil.addMessageError("No se puede resolver una dirección de correo a partir de '" + value + "'.");
				return;
			}
			value += " <" + result + ">";
		} else {
			String result = usuarioFacade.getUserNameFromEmail(value);
			if (result != null)
				value = result + " <" + value + ">";
		}
		
		if(onlySystemEmail && !value.contains("<")) {
			JsfUtil.addMessageError("No se permite el envío a correos electrónicos no registrados en el sistema.");
			return;
		}

		if (!values.containsKey(className + "_" + entityId + "_" + discriminator))
			values.put(className + "_" + entityId + "_" + discriminator, new ArrayList<String>());
		if (!values.get(className + "_" + entityId + "_" + discriminator).contains(value))
			values.get(className + "_" + entityId + "_" + discriminator).add(value);
		else {
			JsfUtil.addMessageError("El destinatario ya se encuentra incluido.");
			value = currentValue;
			return;
		}

		if (autoSave)
			guardarFilesCopies(className, entityId, discriminator);

		clear();
		JsfUtil.addCallbackParam("destinatario");
	}

	public void guardarFilesCopies(String className, Integer entityId, String discriminator) {
		String[] to = null;
		if (values.containsKey(className + "_" + entityId + "_" + discriminator)) {
			List<String> current = values.get(className + "_" + entityId + "_" + discriminator);
			if (current != null && !current.isEmpty()) {
				to = new String[current.size()];
				for (int i = 0; i < current.size(); i++)
					to[i] = current.get(i);
			}
		}
		envioMailCC.saveEnvioCopiaDocumento(className, entityId, discriminator, to);
	}

	public void eliminar(String className, Integer entityId, String discriminator, String destinatario, boolean autoSave) {
		values.get(className + "_" + entityId + "_" + discriminator).remove(destinatario);
		if (autoSave)
			guardarFilesCopies(className, entityId, discriminator);
	}

	public void clear() {
		value = null;
	}

	public String showInfo(String item) {
		if (item.contains("<")) {
			Usuario usuario = null;
			if (users.containsKey(item))
				usuario = users.get(item);
			else {
				String name = item.substring(0, item.lastIndexOf("<") - 1);
				usuario = usuarioFacade.buscarUsuarioWithOutFilters(name);
				users.put(item, usuario);
			}
			if (usuario != null) {
				StringBuffer result = new StringBuffer();
				result.append("<table><tr><td class='bold alRight alTop'>CI:</td><td class='field_mrg'>")
						.append(usuario.getPin()).append("</td></tr>");
				result.append("<tr><td class='bold alRight alTop'>Nombre:</td><td class='field_mrg'>")
						.append(usuario.getPersona().getNombre()).append("</td></tr></table>");
				return result.toString();
			}
		}
		return "<b>Correo electr&oacute;nico externo</b>";
	}

	private boolean isEmailAddress(String candidate) {
		try {
			InternetAddress address = new InternetAddress(candidate);
			address.validate();
			return true;
		} catch (AddressException aex) {

		}
		return false;
	}
}
