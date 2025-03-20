package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;


@Stateless
public class SpecieTaxaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<SpecieTaxa> getByGenero(Integer idGenero) {
		List<SpecieTaxa> lista = new ArrayList<SpecieTaxa>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGenero", idGenero);
		try {
			lista = (List<SpecieTaxa>) crudServiceBean.findByNamedQuery(SpecieTaxa.GET_BY_GENERO,parameters);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public SpecieTaxa getById(Integer idSpecie) throws ServiceException {
		SpecieTaxa result = new SpecieTaxa();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT st FROM SpecieTaxa st WHERE st.id= :idSpecie");
		sql.setParameter("idSpecie", idSpecie);
		if (sql.getResultList().size() > 0)
			result = (SpecieTaxa) sql.getResultList().get(0);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public SpecieTaxa getBySptaScientificName(String sptaScientificName) throws ServiceException {
		SpecieTaxa result = new SpecieTaxa();
		Query sql = crudServiceBean.getEntityManager().createQuery("select c from SpecieTaxa c where upper(c.sptaScientificName) = '" + sptaScientificName + "'");
		if (sql.getResultList().size() > 0) {
			result = (SpecieTaxa) sql.getResultList().get(0);
			return result;
		}
		return null;
	}

}
