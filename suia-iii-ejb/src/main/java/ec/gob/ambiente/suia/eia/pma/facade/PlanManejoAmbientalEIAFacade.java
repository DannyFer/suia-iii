package ec.gob.ambiente.suia.eia.pma.facade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaPmaEia;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIA;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIADetalle;
import ec.gob.ambiente.suia.domain.TablasPlanMonitoreo;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class PlanManejoAmbientalEIAFacade implements Serializable {

	private static final long serialVersionUID = 154642884628168407L;
	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(PlanManejoAmbientalEIA planManejoAmbientalEIA,
			final PlanManejoAmbientalEIA planManejoAmbientalEIAEliminados) throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(planManejoAmbientalEIA);
			if (planManejoAmbientalEIAEliminados!=null) {
				crudServiceBean.delete(planManejoAmbientalEIAEliminados);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
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
	public List<PlanManejoAmbientalEIA> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental)
			throws ServiceException {
		try {
			List<PlanManejoAmbientalEIA> lista = new ArrayList<PlanManejoAmbientalEIA>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
			params.put("tipoPlan", "EIA");
			lista = (List<PlanManejoAmbientalEIA>) crudServiceBean.findByNamedQuery(
					PlanManejoAmbientalEIA.LISTAR_POR_EIA, params);
			
				for (PlanManejoAmbientalEIA planManejoAmbientalEIA : lista) {
					planManejoAmbientalEIA.getPlanManejoAmbientalEIADetalle().size();
					planManejoAmbientalEIA.getCronogramaPmaEIA().size();
					break;
				}
			
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Cris F: Metodo para guardar el cronograma en el objeto de PlanManejoAmbientalEIA
	 * 
	 * @param planManejoAmbientalEIA
	 * @param planManejoAmbientalEIAEliminados
	 * @throws ServiceException
	 */
	public void guardarHistorico(PlanManejoAmbientalEIA planManejoAmbientalEIA, final PlanManejoAmbientalEIA planManejoAmbientalEIAEliminados, 
			Integer numeroNotificacion) throws ServiceException {
		try {
			
			PlanManejoAmbientalEIA planManejoAmbientalEIABdd = crudServiceBean.find(PlanManejoAmbientalEIA.class, planManejoAmbientalEIA.getId());
			List<CronogramaPmaEia> listaCronogramaGuardar = new ArrayList<CronogramaPmaEia>();
			
			listaCronogramaGuardar = obtenerCronogramaGuardar(planManejoAmbientalEIA.getCronogramaPmaEIA(), 
					planManejoAmbientalEIABdd.getCronogramaPmaEIA(), numeroNotificacion);			
			
			//planManejoAmbientalEIA.setCronogramaPmaEIA(listaCronogramaGuardar);
			crudServiceBean.saveOrUpdate(listaCronogramaGuardar);
			
			//crudServiceBean.saveOrUpdate(planManejoAmbientalEIA);
			
			if (planManejoAmbientalEIAEliminados!=null) {
								
				crudServiceBean.delete(planManejoAmbientalEIAEliminados);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	private List<CronogramaPmaEia> obtenerCronogramaGuardar(List<CronogramaPmaEia> listaCronogramaMod, List<CronogramaPmaEia> listaCronogramaBdd, Integer numeroNotificacion){
		
		try {
			
			List<CronogramaPmaEia> listaCronogramaGuardar = new ArrayList<CronogramaPmaEia>();
			for(CronogramaPmaEia cronogramaMod : listaCronogramaMod){
				
				Comparator<CronogramaPmaEia> c = new Comparator<CronogramaPmaEia>() {
					
					@Override
					public int compare(CronogramaPmaEia o1, CronogramaPmaEia o2) {						
						return o1.getId().compareTo(o2.getId());
					}
				};
				
				Collections.sort(listaCronogramaBdd, c);
				
				if(cronogramaMod.getId() != null){
					
					int index = Collections.binarySearch(listaCronogramaBdd, new CronogramaPmaEia(cronogramaMod.getId()), c);
					
					if(index >=0){
						CronogramaPmaEia cronogramaEncontrado = listaCronogramaBdd.get(index);
						
						if(cronogramaMod.getMes1().equals(cronogramaEncontrado.getMes1()) && cronogramaMod.getMes2().equals(cronogramaEncontrado.getMes2())&& 
								cronogramaMod.getMes3().equals(cronogramaEncontrado.getMes3()) && cronogramaMod.getMes4().equals(cronogramaEncontrado.getMes4()) && 
								cronogramaMod.getMes5().equals(cronogramaEncontrado.getMes5()) && cronogramaMod.getMes6().equals(cronogramaEncontrado.getMes6()) && 
								cronogramaMod.getMes7().equals(cronogramaEncontrado.getMes7()) && cronogramaMod.getMes8().equals(cronogramaEncontrado.getMes8()) && 
								cronogramaMod.getMes9().equals(cronogramaEncontrado.getMes9()) && cronogramaMod.getMes10().equals(cronogramaEncontrado.getMes10()) && 
								cronogramaMod.getMes11().equals(cronogramaEncontrado.getMes11()) && cronogramaMod.getMes12().equals(cronogramaEncontrado.getMes12()) && 
								cronogramaMod.getOrden().equals(cronogramaEncontrado.getOrden()) && cronogramaMod.getPresupuesto().equals(cronogramaEncontrado.getPresupuesto()) && 
								cronogramaMod.getTipoPlanManejoAmbiental().equals(cronogramaEncontrado.getTipoPlanManejoAmbiental())){
							listaCronogramaGuardar.add(cronogramaMod);
							continue;
						}else{
							if(cronogramaMod.getNumeroNotificacion() == null || 
									(cronogramaMod.getNumeroNotificacion() != null && cronogramaMod.getNumeroNotificacion() < numeroNotificacion)){
								List<CronogramaPmaEia> listaHistoricoCro = obtenerListaHistorico(cronogramaMod.getId(), numeroNotificacion);
								
								if(listaHistoricoCro == null){									
									CronogramaPmaEia cronogramaHistorico = cronogramaEncontrado.clone();
									cronogramaHistorico.setIdHistorico(cronogramaEncontrado.getId());
									cronogramaHistorico.setNumeroNotificacion(numeroNotificacion);
									cronogramaHistorico.setFechaCreacion(new Date());
									//cronogramaHistorico.setPmaEIA(null);
									listaCronogramaGuardar.add(cronogramaHistorico);
								}
							}
							listaCronogramaGuardar.add(cronogramaMod);
						}
					}else{
						cronogramaMod.setNumeroNotificacion(numeroNotificacion);
						listaCronogramaGuardar.add(cronogramaMod);
					}
				}else{
					cronogramaMod.setNumeroNotificacion(numeroNotificacion);
					listaCronogramaGuardar.add(cronogramaMod);
				}				
			}
			return listaCronogramaGuardar;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<CronogramaPmaEia> obtenerListaHistorico(Integer idHistorico, Integer numeroNotificacion){
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("Select c from CronogramaPmaEia c where idHistorico = :idHistorico and "
					+ "numeroNotificacion = :numeroNotificacion");
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<CronogramaPmaEia> resultado = query.getResultList();
			if(resultado != null && !resultado.isEmpty())
				return resultado;
			else
				return null;			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
