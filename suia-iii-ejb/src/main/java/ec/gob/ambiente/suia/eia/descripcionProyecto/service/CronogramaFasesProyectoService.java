package ec.gob.ambiente.suia.eia.descripcionProyecto.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ConsultorNoCalificado;
import ec.gob.ambiente.suia.domain.CronogramaFasesProyectoEia;

@Stateless
public class CronogramaFasesProyectoService implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2618641391920131523L;

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarCronogramaFasesProyectoProyecto(
			CronogramaFasesProyectoEia cronogramaFases) throws Exception {
		crudServiceBean.saveOrUpdate(cronogramaFases);		
	}

	@SuppressWarnings({ "unchecked", "null" })
	public List<CronogramaFasesProyectoEia> obtenerCronogramaFasesPorEIA(
			Integer idEIA) {
		List<CronogramaFasesProyectoEia> listaCronogramas = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM CronogramaFasesProyectoEia c where c.estudioAmbiental.id=:paramIdEIA AND c.idHistorico = null")
				.setParameter("paramIdEIA", idEIA).getResultList();
		if (listaCronogramas != null || !listaCronogramas.isEmpty()) {
			return listaCronogramas;
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "null" })
	public List<CronogramaFasesProyectoEia> obtenerCronogramaFasesPorHistorico(Integer idHistorico, Integer numeroNotificaciones) {
		List<CronogramaFasesProyectoEia> listaCronogramas = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT c FROM CronogramaFasesProyectoEia c where c.idHistorico = :idHistorico AND "
						+ "c.numeroNotificacion = :numNotificacion order by 1 desc")
				.setParameter("idHistorico", idHistorico).setParameter("numNotificacion", numeroNotificaciones).getResultList();
		if (listaCronogramas != null || !listaCronogramas.isEmpty()) {
			return listaCronogramas;
		}
		return null;
	}
	
	public CronogramaFasesProyectoEia cronogramaFasesPorId(Integer id) throws Exception{
		return crudServiceBean.find(CronogramaFasesProyectoEia.class, id);
	}
	
}
