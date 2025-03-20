package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;

@Stateless
public class RegistroMediosParticipacionSocialService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<RegistroMediosParticipacionSocial> consultar(ParticipacionSocialAmbiental participacionSocialAmbiental) {

		List<RegistroMediosParticipacionSocial> registroMediosParticipacionSocial = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM RegistroMediosParticipacionSocial u where u.estado = true and u.participacionSocialAmbiental.id = :id")
				.setParameter("id", participacionSocialAmbiental.getId()).getResultList();
		return registroMediosParticipacionSocial;

	}

	public void guardar(List<RegistroMediosParticipacionSocial> registros, List<RegistroMediosParticipacionSocial> registrosEliminados)
	{
		 crudServiceBean.saveOrUpdate(registros);

		if(registrosEliminados.size()>0)
		{
			crudServiceBean.delete(registrosEliminados);
		}
	}

}
