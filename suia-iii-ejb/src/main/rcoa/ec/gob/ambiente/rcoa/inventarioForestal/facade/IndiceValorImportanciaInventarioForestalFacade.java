package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.inventarioForestal.model.IndiceValorImportanciaInventarioForestal;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;


@Stateless
public class IndiceValorImportanciaInventarioForestalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<IndiceValorImportanciaInventarioForestal> getByIdInventarioForestalRegistro(Integer idInventarioForestalAmbiental) {
		List<IndiceValorImportanciaInventarioForestal> list = new ArrayList<IndiceValorImportanciaInventarioForestal>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT ivi FROM IndiceValorImportanciaInventarioForestal ivi WHERE ivi.inventarioForestalAmbiental.id=:idInventarioForestalAmbiental ORDER BY ivi.id asc");
		sql.setParameter("idInventarioForestalAmbiental", idInventarioForestalAmbiental);
		if (sql.getResultList().size() > 0)
			list = (List<IndiceValorImportanciaInventarioForestal>) sql.getResultList();
		return list;
	}

	public IndiceValorImportanciaInventarioForestal guardar(IndiceValorImportanciaInventarioForestal indiceValorImportanciaInventarioForestal){
		return crudServiceBean.saveOrUpdate(indiceValorImportanciaInventarioForestal);
	}

}
