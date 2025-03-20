/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.identificacionHallazgos.facade;

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
import ec.gob.ambiente.suia.domain.IdentificacionHallazgosEia;
import ec.gob.ambiente.suia.domain.InfraestructuraAfectada;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class IdentificacionHallazgosEiaFacade {


    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<IdentificacionHallazgosEia> listaIdentificaionHallazgoEia, final List<IdentificacionHallazgosEia> listaIdentificaionHallazgoEiaEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaIdentificaionHallazgoEia);
            if (!listaIdentificaionHallazgoEiaEliminados.isEmpty()) {
                crudServiceBean.delete(listaIdentificaionHallazgoEiaEliminados);
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
    public List<IdentificacionHallazgosEia> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<IdentificacionHallazgosEia> lista = (List<IdentificacionHallazgosEia>) crudServiceBean.findByNamedQuery(IdentificacionHallazgosEia.LISTAR_POR_EIA, params);
         /*
            for (IdentificacionHallazgosEia analisis : lista) {
                analisis.getResultados();
                analisis.getRiesgo().getNombre();
                analisis.getRiesgo().getSubTipo().getNombre();
                analisis.getRiesgo().getSubTipo().getTipo();
            }
            */
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public void guardarHistorico(final List<IdentificacionHallazgosEia> listaIdentificaionHallazgoEia, final List<IdentificacionHallazgosEia> listaIdentificaionHallazgoEiaEliminados, Integer numeroNotificacion) throws ServiceException {
        try {
        	
        	for(IdentificacionHallazgosEia hallazgo : listaIdentificaionHallazgoEia){
        		
        		if(hallazgo.getId() != null){
        			IdentificacionHallazgosEia hallazgoBdd = crudServiceBean.find(IdentificacionHallazgosEia.class, hallazgo.getId());
            		
            		if(hallazgo.getArticulo().equals(hallazgoBdd.getArticulo()) && hallazgo.getConformidad().equals(hallazgoBdd.getConformidad()) && 
            				((hallazgo.getDocumento() == null && hallazgoBdd.getDocumento() == null) || hallazgo.getDocumento() != null && 
            				hallazgoBdd.getDocumento() != null && hallazgo.getDocumento().equals(hallazgoBdd.getDocumento())) && 
            				hallazgo.getEvidencia().equals(hallazgoBdd.getEvidencia()) && hallazgo.getNormativa().equals(hallazgoBdd.getNormativa())){
            			//es igual
            		}else{            			
            			
            			List<IdentificacionHallazgosEia> listaHallazgoHistoricos = obtenerHistorico(hallazgo.getId(), numeroNotificacion);
                		
                		if(hallazgo.getNumeroNotificacion() == null || (hallazgo.getNumeroNotificacion() != null && hallazgo.getNumeroNotificacion() < numeroNotificacion)){                			
                			
                			if(listaHallazgoHistoricos == null){
                				IdentificacionHallazgosEia hallazgoHistorico = hallazgoBdd.clone();
                				hallazgoHistorico.setIdHistorico(hallazgoBdd.getId());
                				hallazgoHistorico.setNumeroNotificacion(numeroNotificacion);
                				hallazgoHistorico.setFechaCreacion(new Date());
                				crudServiceBean.saveOrUpdate(hallazgoHistorico);
                			}
                		}
            		}            		
        		}
        		else{
        			hallazgo.setNumeroNotificacion(numeroNotificacion);
        		}
        		crudServiceBean.saveOrUpdate(hallazgo);
        	}
        	        	
            //crudServiceBean.saveOrUpdate(listaIdentificaionHallazgoEia);        	
        	
            if (!listaIdentificaionHallazgoEiaEliminados.isEmpty()) {
            	
            	for(IdentificacionHallazgosEia hallazgoEliminar : listaIdentificaionHallazgoEiaEliminados){
            		if(hallazgoEliminar.getId() != null){
            			List<IdentificacionHallazgosEia> listaHallazgoHistoricos = obtenerHistorico(hallazgoEliminar.getId(), numeroNotificacion);
                		
                		if(hallazgoEliminar.getNumeroNotificacion() == null || (hallazgoEliminar.getNumeroNotificacion() != null && hallazgoEliminar.getNumeroNotificacion() < numeroNotificacion)){                			
                			if(listaHallazgoHistoricos == null ){
                				IdentificacionHallazgosEia hallazgoHistorico = hallazgoEliminar.clone();
                				hallazgoHistorico.setIdHistorico(hallazgoEliminar.getId());
                				hallazgoHistorico.setNumeroNotificacion(numeroNotificacion);
                				hallazgoHistorico.setFechaCreacion(new Date());
                				crudServiceBean.saveOrUpdate(hallazgoHistorico);
                			}
                		}
            		}
            	}
            	
                crudServiceBean.delete(listaIdentificaionHallazgoEiaEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    public List<IdentificacionHallazgosEia> obtenerHistorico(Integer id, Integer numeroNotificacion){
    	
    	try {
    		
    		Query query = crudServiceBean.getEntityManager().createQuery("SELECT i from IdentificacionHallazgosEia i where i.idHistorico = :id and "
    				+ "i.numeroNotificacion = :notificacion order by 1 desc");
    		query.setParameter("id", id);
    		query.setParameter("notificacion", numeroNotificacion);
    		
    		List<IdentificacionHallazgosEia> identificacionHallazgosList = query.getResultList();
    		
    		if(identificacionHallazgosList != null && !identificacionHallazgosList.isEmpty())
    			return identificacionHallazgosList;
    		else
    			return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}    	
    	
    }

    public List<IdentificacionHallazgosEia> listarTodosRegistrosPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<IdentificacionHallazgosEia> lista = (List<IdentificacionHallazgosEia>) crudServiceBean.findByNamedQuery(IdentificacionHallazgosEia.LISTAR_TODOS_POR_EIA, params);

            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
