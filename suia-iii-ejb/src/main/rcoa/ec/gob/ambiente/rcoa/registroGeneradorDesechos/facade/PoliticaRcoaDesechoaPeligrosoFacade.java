package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PoliticaRcoaDesechoaPeligroso;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;

@Stateless
public class PoliticaRcoaDesechoaPeligrosoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public PoliticaRcoaDesechoaPeligroso findbyId(Integer id) {
		try {
			return (PoliticaRcoaDesechoaPeligroso) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PoliticaRcoaDesechoaPeligroso o where o.id = :id")
					.setParameter("id", id).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarPorIdPolitica(Integer id) {
		List<DesechoPeligroso> lista = new ArrayList<DesechoPeligroso>();

		try {
			lista = (List<DesechoPeligroso>) crudServiceBean.getEntityManager().createQuery(
					"SELECT p.desechoPeligroso FROM PoliticaRcoaDesechoaPeligroso p where p.politicaDesechoRgdRcoa.id = :id")
					.setParameter("id", id).getResultList();

			return lista;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

}
