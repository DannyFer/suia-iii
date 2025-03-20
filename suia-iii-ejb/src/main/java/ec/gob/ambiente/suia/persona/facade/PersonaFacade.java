package ec.gob.ambiente.suia.persona.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.EntityPersona;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.service.PersonaServiceBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

@Stateless
public class PersonaFacade {
	@EJB
	private PersonaServiceBean personaServiceBean;
	
	@EJB
    private UsuarioFacade usuarioFacade;

	public Persona buscarPersonaPorUsuario(Integer id) {
		Persona persona = personaServiceBean.buscarPersonaPorUsuario(id);
		return persona;
	}
	
	/**
	 * Obtiene información del representante legal sea persona natural o juridica
	 * @param proyecto
	 * @return
	 * @throws ServiceException
	 */
	public EntityPersona getPersonaRepresentanteProyecto(ProyectoLicenciamientoAmbiental proyecto)
            throws ServiceException {
        EntityPersona personaTemp = new EntityPersona();
        Persona persona = personaServiceBean.getRepresentanteProyectoFull(proyecto.getId());
        personaTemp.setNombre(persona.getNombre());
        personaTemp.setTrato(persona.getTipoTratos().getNombre());
        personaTemp.setPin(usuarioFacade.obtenerCedulaNaturalJuridico(proyecto.getUsuario().getId()));
        return personaTemp;
    }
	
	public Persona buscarPersonaPorPin(String pin) {
		Persona persona = personaServiceBean.buscarPersonaPorPin(pin);
		return persona;
	}
}
