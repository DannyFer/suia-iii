package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;


@Stateless
public class HigherClassificationFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<HigherClassification> getByFamilia(Integer idFamilia) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idFamilia", idFamilia);
		try {
			List<HigherClassification> lista = (List<HigherClassification>) crudServiceBean.findByNamedQuery(HigherClassification.GET_BY_FAMILIA,parameters);
			return lista;
		} catch (Exception e) {
			return null;
		}
	}
	
	public HigherClassification getById(Integer idHigher) {
		HigherClassification result = new HigherClassification();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT hc FROM HigherClassification hc WHERE hc.id= :idHigher");
		sql.setParameter("idHigher", idHigher);
		if (sql.getResultList().size() > 0)
			result = (HigherClassification) sql.getResultList().get(0);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<HigherClassification> getFamilia() throws ServiceException {
		List<HigherClassification> lista = new ArrayList<HigherClassification>();
		StringBuilder query = new StringBuilder();
		query.append("select * from biodiversity.higher_classifications where tara_id = 5 and hicl_higher_classification like 'Plantae%' order by hicl_scientific_name");
		lista = (List<HigherClassification>) crudServiceBean.findNativeQuery(query.toString(), HigherClassification.class);
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public HigherClassification getGeneroByHiclScientificName(String hiclScientificName) throws ServiceException {
		HigherClassification result = new HigherClassification();
		StringBuilder query = new StringBuilder();
		query.append("select * from biodiversity.higher_classifications where upper(hicl_scientific_name) = '" + hiclScientificName + "'");
		List<HigherClassification> lista = (List<HigherClassification>) crudServiceBean.findNativeQuery(query.toString(), HigherClassification.class);
		if(lista != null && lista.size() > 0)
			return lista.get(0);
		else return result;
	}

}
