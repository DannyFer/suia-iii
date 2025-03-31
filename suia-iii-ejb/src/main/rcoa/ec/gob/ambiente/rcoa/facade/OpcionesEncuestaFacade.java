package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
//import ec.gob.ambiente.rcoa.model.OpcionesEncuesta;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.OpcionesEncuesta;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class OpcionesEncuestaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<OpcionesEncuesta> obtenerCatalogoOpciones() {

		List<OpcionesEncuesta> resultList = new ArrayList<OpcionesEncuesta>();
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM OpcionesEncuesta o order by id");
			resultList = (List<OpcionesEncuesta>) query.getResultList();
		} catch (NoResultException nre) {
			// TODO
		} catch (Exception e) {
			e.printStackTrace();

		}
		return resultList;
	}

}