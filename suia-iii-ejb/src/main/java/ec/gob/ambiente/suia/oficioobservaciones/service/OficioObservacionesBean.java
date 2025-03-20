package ec.gob.ambiente.suia.oficioobservaciones.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.OficioObservaciones;

@Stateless
public class OficioObservacionesBean {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public OficioObservaciones oficioObservacionesPorId(Integer id) {

		return (OficioObservaciones) crudServiceBean.getEntityManager()
				.createQuery(" FROM OficioObservaciones u where u.id =:id ")
				.setParameter("id", id).getSingleResult();

	}

	@SuppressWarnings("unchecked")
	public OficioObservaciones oficioObservacionesPorIdProceso(Long idProceso) {

		return (OficioObservaciones) crudServiceBean
				.getEntityManager()
				.createQuery(
						" FROM OficioObservaciones u where u.proceso =:id ")
				.setParameter("id", idProceso).getSingleResult();

	}
}
