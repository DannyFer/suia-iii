package ec.gob.ambiente.suia.bandeja.controllers;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EnvioNotificacionesMailController implements Serializable {

	private static final long serialVersionUID = -5539428729621810720L;

	@EJB
    private EnvioNotificacionesMailFacade envioNotificacionesMailFacade;

	@PostConstruct
	public void init() {
	
	}

	public EnvioNotificacionesMail guardarEnvioNotificacion(String asunto, String mensaje, List<String> emailsDestino, List<String> emailsDestinoCopia, String codigoTramite, Usuario usuarioDestino, Usuario usuarioEnvia) {
		EnvioNotificacionesMail objMail = new EnvioNotificacionesMail();
		String mailDestino = JsfUtil.listToString(emailsDestino);
		String mailDestinoCopia = JsfUtil.listToString(emailsDestinoCopia);
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
		envioNotificacionesMailFacade.save(objMail);
		return objMail;
	}

	public EnvioNotificacionesMail guardarEnvioNotificacionExitoso(EnvioNotificacionesMail objMail) {
		objMail.setEnviado(true);
		objMail.setFechaEnvio(new Date());
		envioNotificacionesMailFacade.save(objMail);
		return objMail;
	}
}
