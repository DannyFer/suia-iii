package ec.gob.ambiente.suia.eia.descripcionProyecto.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaFasesProyectoEia;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaEia;

@Stateless
public class SustanciaQuimicaEiaService implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -3253040448562745081L;
	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardarSustanciaQuimicaEia(
			List<SustanciaQuimicaEia> sustanciasQuimicasEias) throws Exception {
		crudServiceBean.saveOrUpdate(sustanciasQuimicasEias);
	}

	public void guardarSustanciaQuimicaEia(SustanciaQuimicaEia sustanciaQuimicaEia) throws Exception {
		crudServiceBean.saveOrUpdate(sustanciaQuimicaEia);
	}

	@SuppressWarnings({ "unchecked" })
	public List<SustanciaQuimicaEia> obtenerSustanciasQuimicasPorEia(
			Integer idEia) {
		List<SustanciaQuimicaEia> sustanciasEia = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT s FROM SustanciaQuimicaEia s WHERE s.estudioAmbiental.id=:paramIdEia AND s.idHistorico = null")
				.setParameter("paramIdEia", idEia).getResultList();
		return sustanciasEia;
	}
	
	@SuppressWarnings({ "unchecked" })
	public List<SustanciaQuimicaEia> obtenerSustanciasQuimicasPorHistorico(Integer idEiah, Integer notificacion) {
		List<SustanciaQuimicaEia> sustanciasEia = crudServiceBean.getEntityManager()
				.createQuery("SELECT s FROM SustanciaQuimicaEia s WHERE s.idHistorico =:idEiah AND s.numeroNotificacion = :notificacion order by 1 desc")
				.setParameter("idEiah", idEiah).setParameter("notificacion", notificacion).getResultList();
		return sustanciasEia;
	}
	
	public SustanciaQuimicaEia sustanciaQuimicaEiaPorId(Integer id) throws Exception {
		return crudServiceBean.find(SustanciaQuimicaEia.class, id);
	}

	/**
	 * Busca todas las sustancias de la base de datos 
	 * @author mariela.guano
	 * @param idEia
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<SustanciaQuimicaEia> obtenerSustanciasQuimicasEnBddPorEia(
			Integer idEia) {
		List<SustanciaQuimicaEia> sustanciasEia = crudServiceBean
				.getEntityManager()
				.createQuery(
						"SELECT s FROM SustanciaQuimicaEia s WHERE s.estudioAmbiental.id=:paramIdEia")
				.setParameter("paramIdEia", idEia).getResultList();
		return sustanciasEia;
	}
}