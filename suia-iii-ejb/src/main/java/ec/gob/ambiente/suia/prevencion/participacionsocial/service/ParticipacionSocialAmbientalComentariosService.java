package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbientalComentarios;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;

@Stateless
public class ParticipacionSocialAmbientalComentariosService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public ParticipacionSocialAmbientalComentarios guardar(ParticipacionSocialAmbientalComentarios participacionSocialAmbientalComentarios) {
		return crudServiceBean.saveOrUpdate(participacionSocialAmbientalComentarios);

	}
	
	@SuppressWarnings("unchecked")
	public List<ParticipacionSocialAmbientalComentarios> getCommentsByProjectId(ProyectoLicenciamientoAmbiental  proyectoLicenciamientoAmbiental){

		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("prenId", proyectoLicenciamientoAmbiental.getId());			
			List<ParticipacionSocialAmbientalComentarios> result = (List<ParticipacionSocialAmbientalComentarios>) crudServiceBean
					.findByNamedQuery(
							ParticipacionSocialAmbientalComentarios.FIND_BY_PROJECT,
							params);

			return result;
		} catch (Exception e) {
			return null;
		}
	}

}
