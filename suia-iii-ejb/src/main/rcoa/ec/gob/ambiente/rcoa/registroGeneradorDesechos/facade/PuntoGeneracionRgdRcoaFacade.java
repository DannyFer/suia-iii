package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionRgdRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PuntoGeneracionRgdRcoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public PuntoGeneracionRgdRcoa findById(Integer id) {
		try {
			return (PuntoGeneracionRgdRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PuntoGeneracionRgdRcoa o where o.id = :id").setParameter("id", id)
					.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<PuntoGeneracionRgdRcoa> findAll() {
		List<PuntoGeneracionRgdRcoa> lista = new ArrayList<PuntoGeneracionRgdRcoa>();
		try {
			lista = (List<PuntoGeneracionRgdRcoa>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PuntoGeneracionRgdRcoa o where o.estado = true").getResultList();
			return lista;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	public PuntoGeneracionRgdRcoa findByNombre(String clave) {
		try {
			return (PuntoGeneracionRgdRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PuntoGeneracionRgdRcoa o where o.clave = :clave")
					.setParameter("clave", clave).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
