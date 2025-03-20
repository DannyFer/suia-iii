package ec.gob.ambiente.suia.prevencion.tdr.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

/**
 * 
 * @author lili
 *
 */
@Stateless
public class TdrService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4786862263525836249L;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	UsuarioFacade usuarioFacade;
	
	
	public TdrEiaLicencia guardarTdrEia(TdrEiaLicencia tdrEiaLicencia){
		return crudServiceBean.saveOrUpdate(tdrEiaLicencia);
	}

	@SuppressWarnings("unchecked")
	public List<Consultor> obtenerConsultores() {
		return (List<Consultor>) crudServiceBean.findAll(Consultor.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> recuperarDocumentosTdr(String nombreTabla, Integer id) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("nombreTabla", nombreTabla);
		parameters.put("idTabla", id);

		List<Documento> documentos = (List<Documento>) crudServiceBean
				.findByNamedQuery(Documento.LISTAR_POR_ID_NOMBRE_TABLA,
						parameters);
		return documentos;
	}
	
	@SuppressWarnings("unchecked")
	public TdrEiaLicencia tdrEiaLicenciaPorIdProyectoFull(Integer idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		List<TdrEiaLicencia> result = (List<TdrEiaLicencia>) crudServiceBean
				.findByNamedQuery(TdrEiaLicencia.FIND_BY_PROJECT, parameters);
		if (result != null && !result.isEmpty()) {
			TdrEiaLicencia tdr = result.get(0);
			tdr.getProyecto().getProyectoBloques().size();
			tdr.getProyecto().getProyectoUbicacionesGeograficas().size();
			Usuario usuario = tdr.getProyecto().getUsuario();
			try {
				usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
			} catch (ServiceException e) {
			}

			usuario.getPersona().getOrganizaciones().size();
			for (Organizacion organizacion : usuario.getPersona()
					.getOrganizaciones()) {
				organizacion.getContactos().size();
				for (Contacto contacto : organizacion.getContactos()) {
					contacto.getFormasContacto().getId();
				}

			}
			if (usuario.getPersona().getUbicacionesGeografica() != null)
				usuario.getPersona().getUbicacionesGeografica()
						.getUbicacionesGeografica().getUbicacionesGeografica()
						.getNombre();
			tdr.getEquipoTecnico().size();
			return tdr;
		}
		return null;
	}
}
