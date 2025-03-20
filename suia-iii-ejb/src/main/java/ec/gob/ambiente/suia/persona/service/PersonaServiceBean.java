package ec.gob.ambiente.suia.persona.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class PersonaServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	public Persona buscarPersonaPorUsuario(int id) {
		Usuario usuario = crudServiceBean.find(Usuario.class, id);
		return (usuario != null) ? usuario.getPersona() : null;
	}
	
	@SuppressWarnings("unchecked")
	public Persona buscarPersonaPorPin(String pin) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("pin", pin);
		List<Persona> listPersona = new ArrayList<Persona>(); 
		listPersona=(List<Persona>) crudServiceBean.findByNamedQuery(Persona.FIND_BY_PIN, parametros);
		return listPersona.get(0);
	}

	public Persona getPersona(String nombreUsuario) {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("name", nombreUsuario);
		Usuario usuario = crudServiceBean.findByNamedQuerySingleResult(Usuario.FIND_BY_USER, parametros);
		return (usuario != null) ? usuario.getPersona() : null;
	}

	@SuppressWarnings("unchecked")
	public Persona getRepresentanteProyectoFull(int idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		List<Usuario> result = (List<Usuario>) crudServiceBean.findByNamedQuery(
				ProyectoLicenciamientoAmbiental.GET_REPRESENTANTE_PROYECTO, parameters);
		if (result != null && !result.isEmpty()) {
			Persona persona = result.get(0).getPersona();
            if(persona.getTipoTratos()!=null) {
                persona.getTipoTratos().getNombre();
            }
			if (!persona.getOrganizaciones().isEmpty()) {
				persona.getOrganizaciones().size();
			}
			return persona;
		}
		return null;
	}
}
