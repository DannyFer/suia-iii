package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PoliticaDesechoRgdRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PoliticaDesechoRgdRcoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public PoliticaDesechoRgdRcoa findById(Integer id) {
		try {
			return (PoliticaDesechoRgdRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PoliticaDesechoRgdRcoa o where o.id = :id").setParameter("id", id)
					.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<PoliticaDesechoRgdRcoa> findAll() {
		List<PoliticaDesechoRgdRcoa> lista = new ArrayList<PoliticaDesechoRgdRcoa>();
		try {
			lista = (List<PoliticaDesechoRgdRcoa>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PoliticaDesechoRgdRcoa o where o.estado = true order by orden ").getResultList();

			return lista;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
