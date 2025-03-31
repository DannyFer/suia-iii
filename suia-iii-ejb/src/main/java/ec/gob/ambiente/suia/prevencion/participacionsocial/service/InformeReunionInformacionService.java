package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class InformeReunionInformacionService {

	@EJB
	private CrudServiceBean crudServiceBean;


	@SuppressWarnings("unchecked")
	public List<RegistroMediosParticipacionSocial> getRecordsByProjectId(ParticipacionSocialAmbiental  participacionSocialAmbiental){

		
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("enspId", participacionSocialAmbiental.getId());
			params.put("spmtId", 2);
			List<RegistroMediosParticipacionSocial> result = (List<RegistroMediosParticipacionSocial>) crudServiceBean
					.findByNamedQuery(
							RegistroMediosParticipacionSocial.FIND_BY_PROJECT,
							params);

			return result;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public RegistroMediosParticipacionSocial guardarMedioVerificacion(RegistroMediosParticipacionSocial registroMediosParticipacionSocial) {
		return crudServiceBean.saveOrUpdate(registroMediosParticipacionSocial);

	}
	
	@SuppressWarnings("unchecked")
	public RegistroMediosParticipacionSocial eliminarMedioVerificacion(RegistroMediosParticipacionSocial registroMediosParticipacionSocial) {
		return crudServiceBean.delete(registroMediosParticipacionSocial);

	}

}
