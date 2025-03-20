package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.MecanismoParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;

@Stateless
public class MecanismosParticipacionSocialService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<MecanismoParticipacionSocialAmbiental> consultar(ParticipacionSocialAmbiental participacionSocialAmbiental) {

		List<MecanismoParticipacionSocialAmbiental> mecanismoParticipacionSocialAmbiental = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM MecanismoParticipacionSocialAmbiental u where u.estado = true and u.participacionSocialAmbiental.id = :id order by 1 asc")
				.setParameter("id", participacionSocialAmbiental.getId()).getResultList();
		return mecanismoParticipacionSocialAmbiental;

	}
	
	@SuppressWarnings("unchecked")
	public List<MecanismoParticipacionSocialAmbiental> consultarPublicaPermanente(ParticipacionSocialAmbiental participacionSocialAmbiental) {

		List<MecanismoParticipacionSocialAmbiental> mecanismoParticipacionSocialAmbiental = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM MecanismoParticipacionSocialAmbiental u where u.estado = true and  u.catalogoMedio.id=16  and u.participacionSocialAmbiental.id = :id order by 1 asc")
				.setParameter("id", participacionSocialAmbiental.getId()).getResultList();
		return mecanismoParticipacionSocialAmbiental;

	}
	
	
	@SuppressWarnings("unchecked")
	public List<MecanismoParticipacionSocialAmbiental> consultarInicioCierre(ParticipacionSocialAmbiental participacionSocialAmbiental) {

		List<MecanismoParticipacionSocialAmbiental> mecanismoParticipacionSocialAmbiental = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM MecanismoParticipacionSocialAmbiental u where u.estado = true and u.catalogoMedio.id=16 and u.participacionSocialAmbiental.id = :id order by 1")
				.setParameter("id", participacionSocialAmbiental.getId()).getResultList();
		return mecanismoParticipacionSocialAmbiental;

	}
	

	public void guardar(List<MecanismoParticipacionSocialAmbiental> registros, List<MecanismoParticipacionSocialAmbiental> registrosEliminados)
	{
		 crudServiceBean.saveOrUpdate(registros);

		if(registrosEliminados.size()>0)
		{
			crudServiceBean.delete(registrosEliminados);
		}
	}

}
