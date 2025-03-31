/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ParametrosPlanMonitoreo;
import ec.gob.ambiente.suia.domain.PlanMonitoreoEia;
import ec.gob.ambiente.suia.domain.PlanMonitoreoHistoricoEia;
import ec.gob.ambiente.suia.domain.TablasPlanMonitoreo;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class PlanMonitoreoEiaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(final List<PlanMonitoreoEia> listaPlanMonitoreoEia,
			final List<PlanMonitoreoEia> listaPlanMonitoreoEiaEliminados) throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(listaPlanMonitoreoEia);
			if (!listaPlanMonitoreoEiaEliminados.isEmpty()) {
				crudServiceBean.delete(listaPlanMonitoreoEiaEliminados);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Recupera la lista de Plan de Monitoreo de acuerdo al Estudio
	 * 
	 * @param estudioImpactoAmbiental
	 *            El estudio de impacto ambiental
	 * @return Lista de Análisis de Riesgo
	 * @throws ServiceException
	 *             Si existe un error en la consulta hacia la base de datos
	 */
	public List<PlanMonitoreoEia> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental)
			throws ServiceException {
		try {
			// List<TablasPlanMonitoreo> tablasPlanMonitoreos1 = new ArrayList<TablasPlanMonitoreo>();
			// List<ParametrosPlanMonitoreo> parametrosPlanMonitoreos = new ArrayList<ParametrosPlanMonitoreo>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
			List<PlanMonitoreoEia> lista = (List<PlanMonitoreoEia>) crudServiceBean.findByNamedQuery(
					PlanMonitoreoEia.LISTAR_POR_EIA, params);
			for (PlanMonitoreoEia planMonitoreoEia : lista) {
				List<TablasPlanMonitoreo> tablasPlanMonitoreos = planMonitoreoEia.getTablasPlanMonitoreo();
				if (tablasPlanMonitoreos != null) {
					tablasPlanMonitoreos.size();
					for (TablasPlanMonitoreo tablasPlanMonitoreo : tablasPlanMonitoreos) {
						List<ParametrosPlanMonitoreo> parametrosPlanMonitoreos = tablasPlanMonitoreo
								.getParametrosPlanMonitoreo();
						if (parametrosPlanMonitoreos != null) {
							parametrosPlanMonitoreos.size();
						}
					}
				}

			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}
	
	
	/**
	 * Cris F: Metodo para guardar Historico
	 * @param estudioImpactoAmbiental
	 * @param listaPlanMonitoreoEia
	 * @param listaPlanMonitoreoEiaEliminados
	 * @param numeroNotificacion
	 * @throws ServiceException
	 */
	public void guardarHistorico(EstudioImpactoAmbiental estudioImpactoAmbiental, final List<PlanMonitoreoEia> listaPlanMonitoreoEia,
			final List<PlanMonitoreoEia> listaPlanMonitoreoEiaEliminados, Integer numeroNotificacion) throws ServiceException {
		try {
			
			List<PlanMonitoreoEia> listaBdd = listarPorEIA(estudioImpactoAmbiental);
			
			List<PlanMonitoreoEia> listaPlanMonitoreoGuardar = new ArrayList<PlanMonitoreoEia>();
			
			listaPlanMonitoreoGuardar = obtenerListaGuardar(listaPlanMonitoreoEia, listaBdd, numeroNotificacion);
			
			crudServiceBean.saveOrUpdate(listaPlanMonitoreoGuardar);
			
			//crudServiceBean.saveOrUpdate(listaPlanMonitoreoEia);
			
			
			if (!listaPlanMonitoreoEiaEliminados.isEmpty()) {
				
			List<PlanMonitoreoEia> listaPlanMonitoreoEiaEliminadosCopy = new ArrayList<PlanMonitoreoEia>();
			listaPlanMonitoreoEiaEliminadosCopy.addAll(listaPlanMonitoreoEiaEliminados);
				
				for(PlanMonitoreoEia eliminar : listaPlanMonitoreoEiaEliminados){
					if(eliminar.getId() != null){
						if(!eliminar.getTablasPlanMonitoreo().isEmpty()){						
							
							for(TablasPlanMonitoreo tablaEliminar : eliminar.getTablasPlanMonitoreo()){
															
								if(tablaEliminar.getId() != null){
									if(tablaEliminar.getNumeroNotificacion() == null || 
											(tablaEliminar.getNumeroNotificacion() != null && tablaEliminar.getNumeroNotificacion() < numeroNotificacion)){
									
										List<TablasPlanMonitoreo> listaHistoricoTablas = obtenerTablasHistorico(tablaEliminar.getId(), numeroNotificacion);
									
										if(listaHistoricoTablas == null){
											TablasPlanMonitoreo tablaPlanHistorico = tablaEliminar.clone();								
											tablaPlanHistorico.setIdHistorico(tablaEliminar.getId());
											tablaPlanHistorico.setNumeroNotificacion(numeroNotificacion);	
											tablaPlanHistorico.setFechaCreacion(new Date());
											crudServiceBean.saveOrUpdate(tablaPlanHistorico);
										}
									}
									
									tablaEliminar.setEstado(false);
								}							
							}
						}
						
						
						if(eliminar.getNumeroNotificacion() == null || 
								(eliminar.getNumeroNotificacion() != null && eliminar.getNumeroNotificacion() < numeroNotificacion)){
							
							List<PlanMonitoreoEia> listaHistorico = obtenerListaHistorico(eliminar.getId(), numeroNotificacion);
							
							if(listaHistorico == null){
								PlanMonitoreoEia planMonitoreoHistorico = eliminar.clone();
								planMonitoreoHistorico.setNumeroNotificacion(numeroNotificacion);
								planMonitoreoHistorico.setIdHistorico(eliminar.getId());
								planMonitoreoHistorico.setTablasPlanMonitoreo(null);
								planMonitoreoHistorico.setFechaCreacion(new Date());
								crudServiceBean.saveOrUpdate(planMonitoreoHistorico);
							}		
						}										
					}else{
						listaPlanMonitoreoEiaEliminadosCopy.remove(eliminar);
					}
				}
				
				crudServiceBean.delete(listaPlanMonitoreoEiaEliminadosCopy);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	
	private List<PlanMonitoreoEia> obtenerListaGuardar(List<PlanMonitoreoEia> listaPlanMonitoreoMod, List<PlanMonitoreoEia> listaPlanMonitoreoBdd, 
			Integer numeroNotificacion){		
		try {
			
			List<PlanMonitoreoEia> listaPlanMonitoreoGuardar = new ArrayList<PlanMonitoreoEia>();
			
			for(PlanMonitoreoEia planMonitoreoNueva : listaPlanMonitoreoMod){
				
				Comparator<PlanMonitoreoEia> c = new Comparator<PlanMonitoreoEia>() {
					
					@Override
					public int compare(PlanMonitoreoEia p1, PlanMonitoreoEia p2) {						
						return p1.getId().compareTo(p2.getId());
					}
				};
				
				Collections.sort(listaPlanMonitoreoBdd, c);
				
				if(planMonitoreoNueva.getId() != null){
					int index = Collections.binarySearch(listaPlanMonitoreoBdd, new PlanMonitoreoEia(planMonitoreoNueva.getId()), c);
					
					if(index >= 0){
						
						PlanMonitoreoEia planMonitoreoEncontrado = listaPlanMonitoreoBdd.get(index);
						
						List<TablasPlanMonitoreo> listaTablasMod = obtenerListaTablasGuardar(planMonitoreoNueva.getTablasPlanMonitoreo(), planMonitoreoEncontrado.getTablasPlanMonitoreo(), numeroNotificacion);
						planMonitoreoNueva.setTablasPlanMonitoreo(listaTablasMod);
						
						if(planMonitoreoNueva.getComponente().equals(planMonitoreoEncontrado.getComponente()) && 
							((planMonitoreoNueva.getCoordenadaX() == null && planMonitoreoEncontrado.getCoordenadaX() == null) || 
								planMonitoreoNueva.getCoordenadaX() != null && planMonitoreoEncontrado.getCoordenadaX() !=null && 
								planMonitoreoNueva.getCoordenadaX().equals(planMonitoreoEncontrado.getCoordenadaX())) && 
							((planMonitoreoNueva.getCoordenadaY() == null && planMonitoreoEncontrado.getCoordenadaY() == null) || 
								planMonitoreoNueva.getCoordenadaY() != null && planMonitoreoEncontrado.getCoordenadaY() != null && 
								planMonitoreoNueva.getCoordenadaY().equals(planMonitoreoEncontrado.getCoordenadaY())) && 
							planMonitoreoNueva.getFrecuencia().equals(planMonitoreoEncontrado.getFrecuencia()) && 
							planMonitoreoNueva.getNormativas().equals(planMonitoreoEncontrado.getNormativas()) && 
							planMonitoreoNueva.getPeriodicidad().equals(planMonitoreoEncontrado.getPeriodicidad()) && 
							planMonitoreoNueva.getTipoComponente().equals(planMonitoreoEncontrado.getTipoComponente())){
							listaPlanMonitoreoGuardar.add(planMonitoreoNueva);
							continue;
						}else{
							if(planMonitoreoNueva.getNumeroNotificacion() == null || 
									(planMonitoreoNueva.getNumeroNotificacion() != null && planMonitoreoNueva.getNumeroNotificacion() < numeroNotificacion)){
								
								List<PlanMonitoreoEia> listaHistorico = obtenerListaHistorico(planMonitoreoNueva.getId(), numeroNotificacion);
								
								if(listaHistorico == null){
									PlanMonitoreoEia planMonitoreoHistorico = planMonitoreoEncontrado.clone();
									planMonitoreoHistorico.setNumeroNotificacion(numeroNotificacion);
									planMonitoreoHistorico.setIdHistorico(planMonitoreoEncontrado.getId());
									planMonitoreoHistorico.setTablasPlanMonitoreo(null);
									planMonitoreoHistorico.setFechaCreacion(new Date());
									listaPlanMonitoreoGuardar.add(planMonitoreoHistorico);
								}
								
								listaPlanMonitoreoGuardar.add(planMonitoreoNueva);
							}else{
								listaPlanMonitoreoGuardar.add(planMonitoreoNueva);
							}
						}
						
						
					}else{
						List<TablasPlanMonitoreo> listaTablasMod = obtenerListaTablasGuardar(planMonitoreoNueva.getTablasPlanMonitoreo(), null, numeroNotificacion);
						planMonitoreoNueva.setTablasPlanMonitoreo(listaTablasMod);
						planMonitoreoNueva.setNumeroNotificacion(numeroNotificacion);
						listaPlanMonitoreoGuardar.add(planMonitoreoNueva);
					}
				}else{
					List<TablasPlanMonitoreo> listaTablasMod = obtenerListaTablasGuardar(planMonitoreoNueva.getTablasPlanMonitoreo(),null, numeroNotificacion);
					planMonitoreoNueva.setTablasPlanMonitoreo(listaTablasMod);
					planMonitoreoNueva.setNumeroNotificacion(numeroNotificacion);
					listaPlanMonitoreoGuardar.add(planMonitoreoNueva);
				}
			}
			
			return listaPlanMonitoreoGuardar;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<PlanMonitoreoEia> obtenerListaHistorico(Integer idHistorico, Integer numeroNotificacion){
		
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT p FROM PlanMonitoreoEia p WHERE p.idHistorico = :idHistorico AND "
					+ "p.numeroNotificacion = :numeroNotificacion");
			
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<PlanMonitoreoEia> resultado = query.getResultList();
			
			if(resultado != null && !resultado.isEmpty()){
				return resultado;
			}else{
				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//metodo para cada elemento de la lista de plan de monitoreo
	private List<TablasPlanMonitoreo> obtenerListaTablasGuardar(List<TablasPlanMonitoreo> listaTablasMod, List<TablasPlanMonitoreo> listaTablasBdd, 
			Integer numeroNotificacion){
		
		try {
			
			List<TablasPlanMonitoreo> listaTablasGuardar = new ArrayList<TablasPlanMonitoreo>();
			for(TablasPlanMonitoreo tablaPlanMod : listaTablasMod){
				
				Comparator<TablasPlanMonitoreo> c = new Comparator<TablasPlanMonitoreo>() {
					
					@Override
					public int compare(TablasPlanMonitoreo o1, TablasPlanMonitoreo o2) {						
						return o1.getId().compareTo(o2.getId());
					}
				};
				
				if(listaTablasBdd != null)
					Collections.sort(listaTablasBdd, c);
				
				if(tablaPlanMod.getId() != null){
					
					int index = Collections.binarySearch(listaTablasBdd, new TablasPlanMonitoreo(tablaPlanMod.getId()), c);
					
					if(index >= 0){
						TablasPlanMonitoreo tablasPlanEncontrado = listaTablasBdd.get(index);
						
						if((tablaPlanMod.getParametros() == null && tablasPlanEncontrado.getParametros() == null) || 
								tablaPlanMod.getParametros() != null && tablasPlanEncontrado.getParametros() != null && 
								tablaPlanMod.getParametros().equals(tablasPlanEncontrado.getParametros())){							
							listaTablasGuardar.add(tablaPlanMod);
							continue;
						}else{
							
							if(tablaPlanMod.getNumeroNotificacion() == null || 
									(tablaPlanMod.getNumeroNotificacion() != null && tablaPlanMod.getNumeroNotificacion() < numeroNotificacion)){
							
								List<TablasPlanMonitoreo> listaHistorico = obtenerTablasHistorico(tablaPlanMod.getId(), numeroNotificacion);
							
								if(listaHistorico == null){							
									TablasPlanMonitoreo tablaPlanHistorico = tablasPlanEncontrado.clone();								
									tablaPlanHistorico.setIdHistorico(tablaPlanMod.getId());
									tablaPlanHistorico.setNumeroNotificacion(numeroNotificacion);	
									tablaPlanHistorico.setFechaCreacion(new Date());
									listaTablasGuardar.add(tablaPlanHistorico);		
								}
								listaTablasGuardar.add(tablaPlanMod);
							}else{
								listaTablasGuardar.add(tablaPlanMod);
							}
						}
					}else{
						tablaPlanMod.setNumeroNotificacion(numeroNotificacion);
						listaTablasGuardar.add(tablaPlanMod);
					}
				}else{
					tablaPlanMod.setNumeroNotificacion(numeroNotificacion);
					listaTablasGuardar.add(tablaPlanMod);
				}				
			}		
			
			return listaTablasGuardar;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<TablasPlanMonitoreo> obtenerTablasHistorico(Integer idHistorico, Integer numeroNotificacion){
		
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT p FROM TablasPlanMonitoreo p WHERE p.idHistorico = :idHistorico AND "
					+ "p.numeroNotificacion = :numeroNotificacion");
			
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<TablasPlanMonitoreo> resultado = query.getResultList();
			
			if(resultado != null && !resultado.isEmpty()){
				return resultado;
			}else{
				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Recupera toda la lista de Plan de Monitoreo de acuerdo al Estudio
	 * @author mariela.guano
	 * @param estudioImpactoAmbiental
	 * @return
	 * @throws ServiceException
	 */
	public List<PlanMonitoreoEia> listarTodosPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental)
			throws ServiceException {
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
			List<PlanMonitoreoEia> lista = (List<PlanMonitoreoEia>) crudServiceBean.findByNamedQuery(
					PlanMonitoreoEia.LISTAR_TODOS_POR_EIA, params);
			for (PlanMonitoreoEia planMonitoreoEia : lista) {
				List<TablasPlanMonitoreo> tablasPlanMonitoreos = planMonitoreoEia.getTablasPlanMonitoreo();
				if (tablasPlanMonitoreos != null) {
					tablasPlanMonitoreos.size();
					for (TablasPlanMonitoreo tablasPlanMonitoreo : tablasPlanMonitoreos) {
						List<ParametrosPlanMonitoreo> parametrosPlanMonitoreos = tablasPlanMonitoreo
								.getParametrosPlanMonitoreo();
						if (parametrosPlanMonitoreos != null) {
							parametrosPlanMonitoreos.size();
						}
					}
				}

			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Recupera las tablas de un plan eliminado
	 * @param idPlan
	 * @return
	 */
	public List<TablasPlanMonitoreo> getTablasPlanHistorico(Integer idPlan){
		
		try {
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT p FROM TablasPlanMonitoreo p "
					+ "WHERE p.planMonitoreoEia.id = :idPlan ");
			query.setParameter("idPlan", idPlan);
			
			List<TablasPlanMonitoreo> resultado = query.getResultList();
			
			if(resultado != null && !resultado.isEmpty()){
				return resultado;
			}else{
				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
