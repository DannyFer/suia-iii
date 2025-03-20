package ec.gob.ambiente.suia.eia.pma.facade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIA;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIADetalle;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class PlanManejoAmbientalEIADetalleFacade implements Serializable {

	private static final long serialVersionUID = 177772884628168407L;
	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(final List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalle,
			 final List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleEliminados) throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(listaPlanManejoAmbientalEIADetalle);
			if (!listaPlanManejoAmbientalEIADetalleEliminados.isEmpty()) {
				crudServiceBean.delete(listaPlanManejoAmbientalEIADetalleEliminados);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@SuppressWarnings("unused")
	public void eliminar(List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleEliminados)
	{
		crudServiceBean.delete(listaPlanManejoAmbientalEIADetalleEliminados);
	}

	/**
	 * Recupera la lista de Plan de Manejo Ambiental de acuerdo al Estudio
	 * 
	 * @param estudioImpactoAmbiental
	 *            El estudio de impacto ambiental
	 * @return Lista Plan de Manejo Ambiental
	 * @throws ServiceException
	 *             Si existe un error en la consulta hacia la base de datos
	 */
	@SuppressWarnings("unchecked")
	public List<PlanManejoAmbientalEIADetalle> listarPorTipo(final PlanManejoAmbientalEIA planManejoAmbientalEIA, String tipo)
			throws ServiceException {
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("p_pma", planManejoAmbientalEIA.getId());
			params.put("p_tipo", tipo);
			
			List<PlanManejoAmbientalEIADetalle> lista = (List<PlanManejoAmbientalEIADetalle>) crudServiceBean.findByNamedQuery(
					PlanManejoAmbientalEIADetalle.LISTAR_POR_TIPO, params);
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<PlanManejoAmbientalEIADetalle> listarPorTipoEia(PlanManejoAmbientalEIA planManejoAmbientalEIA)
			throws ServiceException {
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("p_pma", planManejoAmbientalEIA.getId());
			
			List<PlanManejoAmbientalEIADetalle> lista = (List<PlanManejoAmbientalEIADetalle>) crudServiceBean.findByNamedQuery(
					PlanManejoAmbientalEIADetalle.LISTAR_POR_TIPO_EIA, params);
			if(lista.size()>0){
				return lista;
			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<PlanManejoAmbientalEIADetalle> obtenerHistoricoList(Integer id, Integer numeroNotificacion){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT p FROM PlanManejoAmbientalEIADetalle p WHERE "
					+ "p.idHistorico = :idHistorico AND p.numeroNotificacion = :numeroNotificacion");
			query.setParameter("idHistorico", id);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<PlanManejoAmbientalEIADetalle> resultadoList = query.getResultList();
			if(resultadoList != null && !resultadoList.isEmpty()){
				return resultadoList;
			}else
				return null;			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public PlanManejoAmbientalEIADetalle findById(Integer id){
		try {
			PlanManejoAmbientalEIADetalle detalle = crudServiceBean.find(PlanManejoAmbientalEIADetalle.class, id);
			return detalle;			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void guardarEliminadosHistorico(PlanManejoAmbientalEIADetalle planManejoAmbientalEIADetalle) {
		try {
			crudServiceBean.saveOrUpdate(planManejoAmbientalEIADetalle);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Consulta todos los registros del plan ambiental actuales e historicos
	 * @author mariela.guano 
	 * @param planManejoAmbientalEIA
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<PlanManejoAmbientalEIADetalle> listarTodosPorTipoEia(PlanManejoAmbientalEIA planManejoAmbientalEIA)
			throws ServiceException {
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("p_pma", planManejoAmbientalEIA.getId());
			
			List<PlanManejoAmbientalEIADetalle> lista = (List<PlanManejoAmbientalEIADetalle>) crudServiceBean.findByNamedQuery(
					PlanManejoAmbientalEIADetalle.LISTAR_TODOS_POR_TIPO_EIA, params);
			if(lista.size()>0){
				return lista;
			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
}
