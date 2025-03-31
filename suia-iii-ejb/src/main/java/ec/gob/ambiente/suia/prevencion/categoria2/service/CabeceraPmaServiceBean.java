package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CabeceraPma;

/**
 * 
 * @author fernando.borja
 * 
 */

@Stateless
public class CabeceraPmaServiceBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	// TODO Si no encuentran uso por favor borrar :D
	@SuppressWarnings("unchecked")
	public List<CabeceraPma> getCabeceraPmaPorFicha(Integer fichaId) {
		String sql = "SELECT c FROM CabeceraPma c WHERE c.fichaAmbientalPma.id = :idFicha";
		List<CabeceraPma> resultList = crudServiceBean.getEntityManager()
				.createQuery(sql).setParameter("idFicha", fichaId)
				.getResultList();
		return resultList;
	}

	public CabeceraPma getCabecera(Integer id) {
		System.out.println(id);
		String sql = "SELECT c FROM CabeceraPma c WHERE c.id = :id";
		CabeceraPma resultado;

		resultado = (CabeceraPma) crudServiceBean.getEntityManager()
				.createQuery(sql).setParameter("id", id).getSingleResult();

		return resultado;
	}
}
