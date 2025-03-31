package ec.gob.ambiente.suia.catalogos.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoComponenteParticipacion;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class CatalogoComponenteParticipacionService {

	@EJB
	private CrudServiceBean crudServiceBean;


	public List<CatalogoComponenteParticipacion> obtenerListaCatalogoComponenteParticipacion() {

		return crudServiceBean.getEntityManager()
				.createQuery("From CatalogoComponenteParticipacion c order by c.peso")
				.getResultList();
	}

	public List<CatalogoComponenteParticipacion> obtenerListaCatalogoComponenteParticipacionNotHidro() {

		return crudServiceBean.getEntityManager()
				.createQuery("From CatalogoComponenteParticipacion c where c.peso!=4 and c.peso!=5 and c.peso!=6 order by c.peso")
				.getResultList();
	}

}