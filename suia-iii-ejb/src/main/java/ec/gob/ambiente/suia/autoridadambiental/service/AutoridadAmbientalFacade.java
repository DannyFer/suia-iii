package ec.gob.ambiente.suia.autoridadambiental.service;

import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AutoridadAmbientalFacade {

	private static final String DIRECTOR_FORESTAL = "DIRECTOR FORESTAL";
	private static final String DIRECTOR_BIODIVERSIDAD = "DIRECTOR BIODIVERSIDAD";
	private static final String SUBSECRETARIA_DE_PATRIMONIO_NATURAL = "SUBSECRETARIO DE PATRIMONIO NATURAL";
	private static final String DIRECTOR_DE_PREVENCION_DE_LA_CONTAMINACION_AMBIENTAL = "AUTORIDAD AMBIENTAL2 MAE";
	private static final String SUBSECRETARIO_DE_CALIDAD_AMBIENTAL = "SUBSECRETARIO DE CALIDAD AMBIENTAL";
	public static final String GAD_MUNICIPAL = "GAD MUNICIPAL";
	public static final String DIRECTOR_PROVINCIAL = "DIRECTOR DIRECCIÓN PROVINCIAl";
	/**
	 * Nombre: SUIA
	 * Descripción: Parámetro don el rol de la autoridad
	 * @param area
	 * @return
	 * @throws ServiceException
	 * Fecha:16/08/2015
	 */	
	private static final String DIRECTOR_DE_PREVENCION_DE_LA_CONTAMINACION_AMBIENTAL_ENTE = "AUTORIDAD AMBIENTAL";
	/**
	 * FIN Parámetro don el rol de la autoridad
	 */
	
	
	/**
	 * CF: rol para notificación de procesos de participación social rechazados.
	 */
	private static final String NOTIFICACIONES_FACILITADORES_PPS = "NOTIFICACIONES FACILITADORES PPS";
	@EJB
	private UsuarioFacade usuarioFacade;

	public Usuario getSubsecretarioCalidadAmbiental(Area areaResponsable) throws ServiceException {
		List<String> roles = new ArrayList<String>(2);
		roles.add(DIRECTOR_PROVINCIAL);
		roles.add(GAD_MUNICIPAL);
		Usuario usuario = getUsuarioRolNombreArea(roles, areaResponsable.getAreaName());
		if (usuario != null) {
			return usuario;
		}
		throw new ServiceException("No se encontró persona en el área con los roles solicitados.");
	}

	public Persona getDirectorPrevencionContaminacionAmbiental() throws ServiceException {
		return getPersonaRol(DIRECTOR_DE_PREVENCION_DE_LA_CONTAMINACION_AMBIENTAL);
	}

	public Persona getSubsecretarioCalidadAmbiental() throws ServiceException {
		return getPersonaRol(SUBSECRETARIO_DE_CALIDAD_AMBIENTAL);
	}

	public Usuario getUsuarioDirectorPrevencionContaminacionAmbiental() throws ServiceException {
		return getUsuarioRol(DIRECTOR_DE_PREVENCION_DE_LA_CONTAMINACION_AMBIENTAL);
	}

	public Usuario getUsuarioSubsecretariaPatrimonioNatural() throws ServiceException {
		return getUsuarioRol(SUBSECRETARIA_DE_PATRIMONIO_NATURAL);
	}

	public Usuario getUsuarioDirectorBiodiversidad() throws ServiceException {
		return getUsuarioRol(DIRECTOR_BIODIVERSIDAD);
	}

	public Usuario getUsuarioDirectorForestal() throws ServiceException {
		return getUsuarioRol(DIRECTOR_FORESTAL);
	}

	private Persona getPersonaRol(String rol) throws ServiceException {
		List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(rol);
		if (!usuarios.isEmpty()) {
			Persona persona = usuarios.get(0).getPersona();
			persona.getTipoTratos().getNombre();
			return persona;
		} else {
			throw new ServiceException("No existe el usuario con el rol " + rol);
		}

	}
	
	private Usuario getUsuarioRol(String rol) throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRol(rol);
        if (!usuarios.isEmpty()) {
            return usuarios.get(0);

        } else {
            throw new ServiceException("No existe el usuario con el rol " + rol);
        }

    }

	private Usuario getUsuarioRolNombreArea(List<String> rol, String nombreArea) throws ServiceException {
		List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRolNombreArea(rol, nombreArea);
		if (!usuarios.isEmpty()) {
			return usuarios.get(0);

		} else {
			throw new ServiceException("No existe el usuario con el rol " + rol);
		}

	}
	/**
     * Nombre:SUIA
     * Descripción: Para obterner el usuario de los entes deacuerdo al rol. 
	 * @param rol
	 * @param nombreArea
	 * @return
	 * @throws ServiceException
     * Fecha:16/08/2015
	 */
	@SuppressWarnings("unused")
	private Persona getPersonaRolEnte(String rol,String nombreArea) throws ServiceException {
        List<Usuario> usuarios = usuarioFacade.buscarUsuarioPorRolNombreArea(rol, nombreArea);
        if (!usuarios.isEmpty()) {
            Persona persona = usuarios.get(0).getPersona();
            persona.getTipoTratos().getNombre();
            return persona;
        } else {
            throw new ServiceException("No existe el usuario con el rol " + rol);
        }
    }
	/**
	  * FIN Para obterner el usuario de los entes deacuerdo al rol.
	  */
	
	/**
	 * Nombre: SUIA
	 * Descripción: Envía el área para obtener la persona
	 * @param area
	 * @return
	 * @throws ServiceException
	 * Fecha:16/08/2015
	 */
	public Persona getAutoridadAmbientalEnte(String area) throws ServiceException {
        return getPersonaRolEnte(DIRECTOR_DE_PREVENCION_DE_LA_CONTAMINACION_AMBIENTAL_ENTE,area);
    }
	/**
	 * FIN Envía el área para obtener la persona
	 */
	
	/**
	 * CF: Se obtiene a la persona que tiene ese rol asignado
	 */
	public Persona getTecnicoNotificacionesPPS() throws ServiceException {
		return getPersonaRol(NOTIFICACIONES_FACILITADORES_PPS);
	}
	
}
