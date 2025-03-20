package ec.gob.ambiente.suia.eia.descripcionProyecto.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SustanciaQuimica;

@Stateless
public class SustanciaQuimicaService implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2733838612403571681L;

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarSustanciaQuimica(SustanciaQuimica sustanciaQuimica)
			throws Exception {
		crudServiceBean.saveOrUpdate(sustanciaQuimica);
	}

	@SuppressWarnings({ "unchecked" })
	public List<SustanciaQuimica> obtenerSustanciasQuimicas() {
		List<SustanciaQuimica> sustancias = crudServiceBean.getEntityManager()
				.createQuery("SELECT s FROM SustanciaQuimica s")
				.getResultList();
		return sustancias;
	}

}
