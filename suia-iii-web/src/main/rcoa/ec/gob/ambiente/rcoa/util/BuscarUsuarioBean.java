package ec.gob.ambiente.rcoa.util;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;

@ManagedBean
@ViewScoped
public class BuscarUsuarioBean {
	
	private static final Logger LOG = Logger.getLogger(BuscarUsuarioBean.class);
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ContactoFacade contactoFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	public Usuario buscarUsuario(String roleKey,Area area){
		try {
			return areaFacade.getUsuarioPorRolArea(roleKey,area);
		} catch (Exception e) {
			String mensaje="No se ha encontrado usuario con en rol "+Constantes.getRoleAreaName(roleKey)+" en el area "+area;
			LOG.error(mensaje +" "+roleKey);
			enviarNotificacion(mensaje);
		}
		
		return null;
	}
	
	private boolean enviarNotificacion(String mensaje) {
		try {
			//obtengo los usuarios con el rol de notificacion de errores para el envio de la notificacion
			List<Usuario> usuarios= usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES APLICATIVOS");
			List<String> correosLista = new ArrayList<String>();
			
			for (Usuario usuario : usuarios) {
				List<Contacto> contactos = contactoFacade.buscarPorPersona(usuario.getPersona());
				for (Contacto contacto : contactos) {
					if(contacto.getFormasContacto().getId().intValue()==FormasContacto.EMAIL && !correosLista.contains(contacto.getValor())) {
						correosLista.add(contacto.getValor());
						break;
					}
				}
			}
			if(!correosLista.isEmpty()) {
				Email.sendEmail(correosLista, null, "Administrador Sistema", mensaje, null, null, null);
				return true;
			}
			
		}catch (Exception e) {
			LOG.error("Error al notificar falta de usuario/rol "+e.getMessage());
		}
		return false;
	}
	
}
