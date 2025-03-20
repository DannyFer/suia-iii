/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.planHallazgos.facade;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.PlanHallazgosEia;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class PlanHallazgosEiaFacade {


    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<PlanHallazgosEia> listaPlanHallazgoEia, final List<PlanHallazgosEia> listaPlanHallazgoEiaEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaPlanHallazgoEia);
            if (!listaPlanHallazgoEiaEliminados.isEmpty()) {
                crudServiceBean.delete(listaPlanHallazgoEiaEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de Análisis de Riesgo de acuerdo al Estudio
     * @param estudioImpactoAmbiental El estudio de impacto ambiental
     * @return Lista de Análisis de Riesgo
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<PlanHallazgosEia> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<PlanHallazgosEia> lista = (List<PlanHallazgosEia>) crudServiceBean.findByNamedQuery(PlanHallazgosEia.LISTAR_POR_EIA, params);
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    /*
     * CF: Guarda el historico de los registros.
     */
    public void guardarHistorico(final List<PlanHallazgosEia> listaPlanHallazgoEia, final List<PlanHallazgosEia> listaPlanHallazgoEiaEliminados, Integer numeroNotificacion) throws ServiceException {
        try {
        	
        	for(PlanHallazgosEia planHallazgo : listaPlanHallazgoEia){
        		
        		if(planHallazgo.getId() != null){
        			PlanHallazgosEia planHallazgoBdd = crudServiceBean.find(PlanHallazgosEia.class, planHallazgo.getId());
            		
            		if(planHallazgo.getFechaFin().equals(planHallazgoBdd.getFechaFin()) && planHallazgo.getFechaInicio().equals(planHallazgoBdd.getFechaInicio()) && 
            				planHallazgo.getHallazgos().equals(planHallazgoBdd.getHallazgos()) && planHallazgo.getMedidaPropuesta().equals(planHallazgoBdd.getMedidaPropuesta()) && 
            				planHallazgo.getMedioVerificacion().equals(planHallazgoBdd.getMedioVerificacion()) && planHallazgo.getResponsable().equals(planHallazgoBdd.getResponsable())){
            			//son iguales
            		}else{
            			if(planHallazgo.getNumeroNotificacion() == null || (planHallazgo.getNumeroNotificacion() != null && planHallazgo.getNumeroNotificacion() < numeroNotificacion)){
            				List<PlanHallazgosEia> listaHistorico = obtenerHistorico(planHallazgo.getId(), numeroNotificacion);
            				if(listaHistorico == null){
            					
            					PlanHallazgosEia planHallazgoHistorico = planHallazgoBdd.clone();
            					planHallazgoHistorico.setNumeroNotificacion(numeroNotificacion);
            					planHallazgoHistorico.setIdHistorico(planHallazgo.getId());
            					planHallazgoHistorico.setFechaCreacion(new Date());
            					crudServiceBean.saveOrUpdate(planHallazgoHistorico);
            				}
            			}
            		}
        		}else{
        			planHallazgo.setNumeroNotificacion(numeroNotificacion);
        		}
        		
        		crudServiceBean.saveOrUpdate(planHallazgo);        		
        	}
        	
            //crudServiceBean.saveOrUpdate(listaPlanHallazgoEia);
            if (!listaPlanHallazgoEiaEliminados.isEmpty()) {
            	
            	for(PlanHallazgosEia planHallazgoEliminar : listaPlanHallazgoEiaEliminados){
            		if(planHallazgoEliminar.getId() != null){
            			if(planHallazgoEliminar.getNumeroNotificacion() == null || (planHallazgoEliminar.getNumeroNotificacion() != null && planHallazgoEliminar.getNumeroNotificacion() < numeroNotificacion)){
            				List<PlanHallazgosEia> listaHistorico = obtenerHistorico(planHallazgoEliminar.getId(), numeroNotificacion);
            				if(listaHistorico == null){
            					
            					PlanHallazgosEia planHallazgoHistorico = planHallazgoEliminar.clone();
            					planHallazgoHistorico.setNumeroNotificacion(numeroNotificacion);
            					planHallazgoHistorico.setIdHistorico(planHallazgoEliminar.getId());
            					planHallazgoHistorico.setFechaCreacion(new Date());
            					crudServiceBean.saveOrUpdate(planHallazgoHistorico);
            				}
            			}
            		}            		
            	}
            	
                crudServiceBean.delete(listaPlanHallazgoEiaEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    public List<PlanHallazgosEia> obtenerHistorico(Integer id, Integer numeroNotificacion){
    	
    	try {
    		Query query = crudServiceBean.getEntityManager().createQuery("SELECT p from PlanHallazgosEia p WHERE p.idHistorico = :id and p.numeroNotificacion = :numeroNotificacion");
        	query.setParameter("id", id);
        	query.setParameter("numeroNotificacion", numeroNotificacion);
        	
        	List<PlanHallazgosEia> listaHistorico = query.getResultList();
        	if(listaHistorico != null && !listaHistorico.isEmpty())
        		return listaHistorico;
        	else
        		return null;
        	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}    	
    }

    /**
     * Recupera la lista de todos los registros de Análisis de Riesgo de acuerdo al Estudio existente en la base de datos
     * @param estudioImpactoAmbiental El estudio de impacto ambiental
     * @return Lista de Análisis de Riesgo
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<PlanHallazgosEia> listarTodosRegistrosPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<PlanHallazgosEia> lista = (List<PlanHallazgosEia>) crudServiceBean.findByNamedQuery(PlanHallazgosEia.LISTAR_TODOS_POR_EIA, params);
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
