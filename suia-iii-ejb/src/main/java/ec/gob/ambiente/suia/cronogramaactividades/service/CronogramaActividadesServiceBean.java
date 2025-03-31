package ec.gob.ambiente.suia.cronogramaactividades.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaActividades;

@Stateless
public class CronogramaActividadesServiceBean {

	@EJB
	private CrudServiceBean crudServiceCronogramaActividadesBean;

	@SuppressWarnings("unchecked")
	public List<CronogramaActividades> listarCronogramaActividades() {
		List<CronogramaActividades> cronograma = crudServiceCronogramaActividadesBean.getEntityManager()
				.createQuery("From CronogramaActividades c").getResultList();
		return cronograma;
	}

	public CronogramaActividades buscarCronogramaActividadesPorId(long idCronnogramaActividades) {
		CronogramaActividades cronograma = (CronogramaActividades) crudServiceCronogramaActividadesBean
				.getEntityManager().createQuery("From CronogramaActividades c where c.id =:Identificador")
				.setParameter("Identificador", idCronnogramaActividades).getSingleResult();
		return cronograma;
	}

	public List<CronogramaActividades> buscarCronogramaActividadesPorIdProceso(long idProceso) {
		@SuppressWarnings("unchecked")
		List<CronogramaActividades> cronogramas = crudServiceCronogramaActividadesBean.getEntityManager()
				.createQuery("From CronogramaActividades c where c.idProceso =:Identificador")
				.setParameter("Identificador", idProceso).getResultList();
		return cronogramas;
	}
}
