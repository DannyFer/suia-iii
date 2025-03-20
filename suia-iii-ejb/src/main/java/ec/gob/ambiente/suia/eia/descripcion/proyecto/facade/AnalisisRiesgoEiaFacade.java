/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.facade;

import ec.gob.ambiente.suia.administracion.facade.AdjuntosEiaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoRiesgo;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author christian
 */
@LocalBean
@Stateless
public class AnalisisRiesgoEiaFacade {


    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private AdjuntosEiaFacade adjuntosEiaFacade;

    public void guardarConAdjunto(final List<AnalisisRiesgoEia> listaAnalisisRiesgoEia, final EstudioImpactoAmbiental estudioImpactoAmbiental, final EntityAdjunto entityAdjunto, final EiaOpciones eiaOpciones) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaAnalisisRiesgoEia);
            adjuntosEiaFacade.guardarAdjunto(entityAdjunto, estudioImpactoAmbiental.getClass().getSimpleName(), estudioImpactoAmbiental.getId(), eiaOpciones, estudioImpactoAmbiental);
            for (AnalisisRiesgoEia a : listaAnalisisRiesgoEia) {
                if (a.getEntityAdjunto() != null) {
                    EntityAdjunto obj = a.getEntityAdjunto();
                    StringBuilder nombre = new StringBuilder();
                    nombre.append("EIA");
                    nombre.append("AR");
                    nombre.append(a.getId());
                    nombre.append(".").append(a.getEntityAdjunto().getExtension());
                    obj.setNombre(nombre.toString());
                    adjuntosEiaFacade.guardarAdjunto(obj, a.getClass().getSimpleName(), a.getId());
                }
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void guardar(final List<AnalisisRiesgoEia> listaAnalisisRiesgoEia, final List<AnalisisRiesgoEia> listaAnalisisRiesgoEiaEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaAnalisisRiesgoEia);
            if (!listaAnalisisRiesgoEiaEliminados.isEmpty()) {
                crudServiceBean.delete(listaAnalisisRiesgoEiaEliminados);
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
    public List<AnalisisRiesgoEia> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<AnalisisRiesgoEia> lista = (List<AnalisisRiesgoEia>) crudServiceBean.findByNamedQuery(AnalisisRiesgoEia.LISTAR_POR_EIA, params);
            for (AnalisisRiesgoEia analisis : lista) {
                analisis.getResultados();
                analisis.getRiesgo().getNombre();
                analisis.getRiesgo().getSubTipo().getNombre();
                analisis.getRiesgo().getSubTipo().getTipo();

            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Realiza la búsqueda de subtipos de riesgo  por tipo de riesgo
     * @param tipo EL tipo de Riesgo
     * @return
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<SubTipoRiesgo> buscarSubTipoPorTipo(TipoRiesgo tipo) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("paramTipo", tipo);
            List<SubTipoRiesgo> subtipos = (List<SubTipoRiesgo>) crudServiceBean.findByNamedQuery(SubTipoRiesgo.BUSCAR_POR_TIPO, params);
            return subtipos;
        } catch (RuntimeException e) {
            throw new ServiceException("No se pudo recuperar la lista de SubtipoRiesgo", e);
        }
    }

    /**
     * Realiza la búsqueda de Riesgos por subtipo
     * @param subtipo EL subtipo de riesgo
     * @return
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<Riesgo> buscarRiesgoPorSubTipo(SubTipoRiesgo subtipo) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("paramSubTipo", subtipo);
            List<Riesgo> riesgos = (List<Riesgo>) crudServiceBean.findByNamedQuery(Riesgo.BUSCAR_POR_SUBTIPO, params);
            return riesgos;
        } catch (RuntimeException e) {
            throw new ServiceException("No se pudo recuperar la lista de Riesgos", e);
        }
    }

    /**
     * Busca un subtipo dado su clave primaria
     * @param id La clave primaria
     * @return Subtipo
     * @throws ServiceException SI existe un error en la consulta hacia la base de datos
     */
    public SubTipoRiesgo buscarSubTipo(int id) throws ServiceException {
        try {
            return crudServiceBean.find(SubTipoRiesgo.class, id);
        } catch (RuntimeException e) {
            throw new ServiceException("No se encontró el subtipo con el id " + id, e);
        }
    }
    
    /**
     * CF: Guardar Historico
     */
    
    public void guardarHistorico(final List<AnalisisRiesgoEia> listaAnalisisRiesgoEia, final List<AnalisisRiesgoEia> listaAnalisisRiesgoEiaEliminados, Integer numeroNotificacion) throws ServiceException {
        try {
        	
        	if(listaAnalisisRiesgoEia != null && !listaAnalisisRiesgoEia.isEmpty()){
        		for(AnalisisRiesgoEia riesgo : listaAnalisisRiesgoEia){
            		
            		if(riesgo.getId() != null){
            			AnalisisRiesgoEia riesgoBdd = crudServiceBean.find(AnalisisRiesgoEia.class, riesgo.getId());
            			
            			if(((riesgo.getGecaId() == null && riesgoBdd.getGecaId() == null) || riesgo.getGecaId() != null && 
            					riesgoBdd.getGecaId() != null && riesgo.getGecaId().equals(riesgoBdd.getGecaId())) && 
            					((riesgo.getIdCatalogo() == null && riesgoBdd.getIdCatalogo() == null) || 
            							riesgo.getIdCatalogo() != null && riesgoBdd.getIdCatalogo() != null && 
            							riesgo.getIdCatalogo().equals(riesgoBdd.getIdCatalogo())) && 
            					((riesgo.getIdEstudioImpactoAmbiental() == null && riesgoBdd.getIdEstudioImpactoAmbiental() == null) || 
            							riesgo.getIdEstudioImpactoAmbiental() != null && riesgoBdd.getIdEstudioImpactoAmbiental() != null && 
            							riesgo.getIdEstudioImpactoAmbiental().equals(riesgoBdd.getIdEstudioImpactoAmbiental())) &&
            					((riesgo.getOtroRiesgo() == null && riesgoBdd.getOtroRiesgo() == null) || 
            							riesgo.getOtroRiesgo() != null && riesgoBdd.getOtroRiesgo() != null && 
            							riesgo.getOtroRiesgo().equals(riesgoBdd.getOtroRiesgo())) &&
            					((riesgo.getResultados() == null && riesgoBdd.getResultados() == null) || 
            							riesgo.getResultados() != null && riesgoBdd.getResultados() != null && 
            							riesgo.getResultados().equals(riesgoBdd.getResultados())) && 
            					((riesgo.getRiesgo() == null && riesgoBdd.getRiesgo() == null) || 
            							riesgo.getRiesgo() != null && riesgoBdd.getRiesgo() != null && 
            							riesgo.getRiesgo().equals(riesgoBdd.getRiesgo()))){
            				//no hay cambios
            			}else{
            				if(riesgo.getNumeroNotificacion() == null || (riesgo.getNumeroNotificacion() != null && riesgo.getNumeroNotificacion() < numeroNotificacion)){
            					List<AnalisisRiesgoEia> listaHistoricoRiesgo = obtenerHistorico(riesgo.getId(), numeroNotificacion);
            					
            					if(listaHistoricoRiesgo == null){
            						AnalisisRiesgoEia riesgoHistorico = riesgoBdd.clone();
            						riesgoHistorico.setNumeroNotificacion(numeroNotificacion);
            						riesgoHistorico.setIdHistorico(riesgo.getId());
            						riesgoHistorico.setFechaCreacion(new Date());
            						crudServiceBean.saveOrUpdate(riesgoHistorico);
            					}        					
            				}
            			}
            		}else{
            			riesgo.setNumeroNotificacion(numeroNotificacion);
            		}
            		crudServiceBean.saveOrUpdate(riesgo);
            	}
        	}        	      	       	
        	
            //crudServiceBean.saveOrUpdate(listaAnalisisRiesgoEia);
            if (!listaAnalisisRiesgoEiaEliminados.isEmpty()) {
            	
            	for(AnalisisRiesgoEia riesgoEliminar : listaAnalisisRiesgoEiaEliminados){
            		if(riesgoEliminar.getNumeroNotificacion() == null || (riesgoEliminar.getNumeroNotificacion() != null && riesgoEliminar.getNumeroNotificacion() < numeroNotificacion)){
    					List<AnalisisRiesgoEia> listaHistoricoRiesgo = obtenerHistorico(riesgoEliminar.getId(), numeroNotificacion);
    					
    					if(listaHistoricoRiesgo == null){
    						AnalisisRiesgoEia riesgoHistorico = riesgoEliminar.clone();
    						riesgoHistorico.setNumeroNotificacion(numeroNotificacion);
    						riesgoHistorico.setIdHistorico(riesgoEliminar.getId());
    						riesgoHistorico.setFechaCreacion(new Date());
    						crudServiceBean.saveOrUpdate(riesgoHistorico);
    					}        					
                	}         		      	            	
            	}
                crudServiceBean.delete(listaAnalisisRiesgoEiaEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    public List<AnalisisRiesgoEia> obtenerHistorico(Integer idHistorico, Integer numeroNotificacion){
    	try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT a FROM AnalisisRiesgoEia a WHERE a.idHistorico = :idHistorico "
					+ "and a.numeroNotificacion = :numeroNotificacion");
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<AnalisisRiesgoEia> listaHistorico = query.getResultList();
			if(listaHistorico != null && !listaHistorico.isEmpty()){
				return listaHistorico;
			}else
				return null;
    		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
     * Recupera la lista de todos los registros de Análisis de Riesgo de acuerdo al Estudio actuales y respaldos de modificacion
     * @param estudioImpactoAmbiental El estudio de impacto ambiental
     * @return Lista de Análisis de Riesgo
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<AnalisisRiesgoEia> listarTodosRegistrosPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<AnalisisRiesgoEia> lista = (List<AnalisisRiesgoEia>) crudServiceBean.findByNamedQuery(AnalisisRiesgoEia.LISTAR_TODOS_POR_EIA, params);
            for (AnalisisRiesgoEia analisis : lista) {
                analisis.getResultados();
                analisis.getRiesgo().getNombre();
                analisis.getRiesgo().getSubTipo().getNombre();
                analisis.getRiesgo().getSubTipo().getTipo();

            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
