package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoMediosParticipacionSocial;

@Stateless
public class CatalogoMediosParticipacionSocialService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<CatalogoMediosParticipacionSocial> buscarCatalogoMediosParticipacionSocialPorId(Integer id) {

		List<CatalogoMediosParticipacionSocial> catalogoMediosParticipacionSociales = crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM CatalogoMediosParticipacionSocial u where u.tipoCatalogo.id =:id order by u.nombreMedio")
				.setParameter("id", id).getResultList();
		return catalogoMediosParticipacionSociales;

	}

	public List<CatalogoMediosParticipacionSocial> obtenerCatalogo()
	{
		List<CatalogoMediosParticipacionSocial> catalogoMediosParticipacionSociales =
				(List<CatalogoMediosParticipacionSocial>) crudServiceBean
				.findByNamedQuery(
						CatalogoMediosParticipacionSocial.GET_ALL,
						null);
		return catalogoMediosParticipacionSociales;
	}

}
