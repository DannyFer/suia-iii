/**
 * Copyright (c) 2015 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.eia.areaInfluencia;

import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Clase que gestiona la fachada para la pantalla Determinación de areas de influencia del EIA
 *
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */

@Stateless
public class AreaInfluenciaFacade {

    private static final Logger LOG = Logger.getLogger(AreaInfluenciaFacade.class);

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ValidacionSeccionesFacade validacionSeccionesFacade;

    /**
     * Retorna las areas de influencia, infraestrucuturas afectadas y distancias desde la base de datos
     * @param estudio
     * @return
     */
    @SuppressWarnings("unchecked")
    public AreaInfluencia getAreaInfluencia(EstudioImpactoAmbiental estudio) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("paramId", estudio.getId());
        AreaInfluencia areaInfluencia = (AreaInfluencia) crudServiceBean.findByNamedQuerySingleResult(AreaInfluencia.FIND_BY_ESTUDIO, parameters);
        return areaInfluencia;
    }
    @SuppressWarnings("unchecked")
    public List<AreaInfluencia> getListaAreaInfluencia(EstudioImpactoAmbiental estudio) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("paramId", estudio.getId());
        List<AreaInfluencia> listaAreaInfluencia = (List<AreaInfluencia>) crudServiceBean.findByNamedQuery(AreaInfluencia.FIND_BY_ESTUDIO, parameters);
        return listaAreaInfluencia;
    }

    /**
     * Guarda
     * @param estudio
     * @param areaInfluencia
     * @param infreaestructurasAfectadasAlmacenadas
     * @param infraestructuras
     * @param documento
     * @throws ServiceException
     */
    public void guardar(List<AreaInfluencia>listaAreaInfluencia, EstudioImpactoAmbiental estudio, AreaInfluencia areaInfluencia,
                        List<InfraestructuraAfectada> infreaestructurasAfectadasAlmacenadas, List<InfraestructuraAfectada> infraestructuras,
                        Documento documento, List<DistanciaElementoSensible> listaDistanciasEliminar) throws ServiceException {
        try {
            List<DistanciaElementoSensible> distancias = new ArrayList<DistanciaElementoSensible>();
            List<InfraestructuraAfectada> infraestructuraNoVerificadas = new ArrayList<InfraestructuraAfectada>();
            List<DistanciaElementoSensible> distanciaElementoSensiblesNoVerificadas = new ArrayList<DistanciaElementoSensible>();

            if (infreaestructurasAfectadasAlmacenadas != null) {
                infraestructuraNoVerificadas.addAll(infreaestructurasAfectadasAlmacenadas);
                for (InfraestructuraAfectada infr : infraestructuraNoVerificadas) {
                    distanciaElementoSensiblesNoVerificadas.addAll(infr.getDistanciaElementoSensibles());
                }
            }
            if(listaAreaInfluencia!=null){
                if (!listaAreaInfluencia.isEmpty()){
                    for(AreaInfluencia area: listaAreaInfluencia){
                        area.setEstudioImpactoAmbiental(estudio);
                        crudServiceBean.saveOrUpdate(area);
                    }
                }

            }else{
                if (areaInfluencia!=null){
                    areaInfluencia.setEstudioImpactoAmbiental(estudio);
                    crudServiceBean.saveOrUpdate(areaInfluencia);
                }
            }

            if(!listaDistanciasEliminar.isEmpty()){
                crudServiceBean.delete(listaDistanciasEliminar);
            }

            for (InfraestructuraAfectada infraestructura : infraestructuras) {
                infraestructura.setEstudioImpactoAmbiental(estudio);
                if (infraestructura.getDistanciaElementoSensibles() != null) {
                    for (DistanciaElementoSensible dist : infraestructura.getDistanciaElementoSensibles()) {
                        dist.setInfraestructuraAfectada(infraestructura);
                    }
                    distancias.addAll(infraestructura.getDistanciaElementoSensibles());
                }
                crudServiceBean.saveOrUpdate(infraestructura);

                infraestructuraNoVerificadas.remove(infraestructura);
            }



            for (DistanciaElementoSensible distanciaElementoSensible : distancias) {
                crudServiceBean.saveOrUpdate(distanciaElementoSensible);
                distanciaElementoSensiblesNoVerificadas.remove(distanciaElementoSensible);
            }
            crudServiceBean.delete(distanciaElementoSensiblesNoVerificadas);
            crudServiceBean.delete(infraestructuraNoVerificadas);
            for (InfraestructuraAfectada infraestructura : infraestructuras) {
                if (infraestructura.getDistanciaElementoSensibles() != null) {
                    infraestructura.getDistanciaElementoSensibles().size();
                }
            }
            salvarDocumento(documento, estudio);
            validacionSeccionesFacade.guardarValidacionSeccion("EIA", "areaInfluencia", estudio.getId().toString());

        } catch (RuntimeException e) {
            throw new ServiceException("No se pudo almacenar las areas de influencia", e);
        }
    }


    private void salvarDocumento(Documento doc, EstudioImpactoAmbiental estudio) throws ServiceException {
        try {
            doc.setIdTable(estudio.getId());
            doc.setDescripcion("Documento General");
            doc.setEstado(true);
            ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoFacade = (ProyectoLicenciamientoAmbientalFacade)BeanLocator.getInstance(ProyectoLicenciamientoAmbientalFacade.class);
            
            ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoFacade.getProyectoPorId(estudio.getIdProyectoLicenciamientoAmbiental());
            
            documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
                    Constantes.CARPETA_EIA, 0L, doc, TipoDocumentoSistema.DETERMINACION_AREA_INFLUENCIA_GEN, null);
        } catch (Exception e) {
            throw new ServiceException("No se pudo almacenar el documento en Alfresco", e);
        }
    }

    /**
     * Retorna la lista completa de elementos sensibles
     * @return
     */
    public List<ElementoSensible> getElementosSensibles() {
        List<ElementoSensible> elementosSensibles = (List<ElementoSensible>) crudServiceBean.findAll(ElementoSensible.class);
        return elementosSensibles;
    }

    public List<AreaInfluencia> borrarListaAreaInfluencia(List<AreaInfluencia> listaAreaInfluenciaBorrar) {
        for (AreaInfluencia areaInfluencia : listaAreaInfluenciaBorrar) {
            if(areaInfluencia.getId() != null && crudServiceBean.find(AreaInfluencia.class, areaInfluencia.getId()) != null)
                crudServiceBean.delete(areaInfluencia);
        }
        return new ArrayList<AreaInfluencia>();
    }

    public List<InfraestructuraAfectada> findInfraestructurasAfectadasByEstudio(EstudioImpactoAmbiental estudio) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("estudio", estudio);
        List<InfraestructuraAfectada> infraestructuraAfectadas = (List<InfraestructuraAfectada>) crudServiceBean.findByNamedQuery(
                InfraestructuraAfectada.FIND_INFRAESTRUCTURAS_AFECTADAS_BY_ESTUDIO, parameters);
        return infraestructuraAfectadas == null ? new ArrayList<InfraestructuraAfectada>() : infraestructuraAfectadas;
    }
    
    
    /**
     * Cristina Flores: Guardar Historico
     * @throws CloneNotSupportedException 
     */    
	public void guardarHistorico(List<AreaInfluencia> listaAreaInfluencia, EstudioImpactoAmbiental estudio, AreaInfluencia areaInfluencia,
			List<InfraestructuraAfectada> infraestructuras,
			Documento documento, List<DistanciaElementoSensible> listaDistanciasEliminar, Integer numeroNotificacion, 
			Documento documentoHistorico) throws ServiceException, CloneNotSupportedException {
		try {
			List<DistanciaElementoSensible> distancias = new ArrayList<DistanciaElementoSensible>();
			List<InfraestructuraAfectada> infraestructuraNoVerificadas = new ArrayList<InfraestructuraAfectada>();
			List<DistanciaElementoSensible> distanciaElementoSensiblesNoVerificadas = new ArrayList<DistanciaElementoSensible>();

			List<InfraestructuraAfectada> infreaestructurasAfectadasAlmacenadas = findInfraestructurasAfectadasByEstudio(estudio);
			
			if (infreaestructurasAfectadasAlmacenadas != null) {
				infraestructuraNoVerificadas.addAll(infreaestructurasAfectadasAlmacenadas);
				for (InfraestructuraAfectada infr : infraestructuraNoVerificadas) {
					distanciaElementoSensiblesNoVerificadas.addAll(infr.getDistanciaElementoSensibles());
				}
			}
			if (listaAreaInfluencia != null) {
				if (!listaAreaInfluencia.isEmpty()) {
					for (AreaInfluencia area : listaAreaInfluencia) {
						
						AreaInfluencia areaBdd = crudServiceBean.find(AreaInfluencia.class, area.getId());
						
						if(area.getDirectaBioticoDescripcion().equals(areaBdd.getDirectaBioticoDescripcion()) && 
								area.getDirectaBioticoDistancia().equals(areaBdd.getDirectaBioticoDistancia()) && 
								area.getDirectaBioticoDistanciaUnidad().equals(areaBdd.getDirectaBioticoDistanciaUnidad()) && 
								area.getDirectaFisicoDescripcion().equals(areaBdd.getDirectaFisicoDescripcion()) && 
								area.getDirectaFisicoDistancia().equals(areaBdd.getDirectaFisicoDistancia()) && 
								area.getDirectaFisicoDistanciaUnidad().equals(areaBdd.getDirectaFisicoDistanciaUnidad()) &&
								area.getIndirectaBioticoDescripcion().equals(areaBdd.getIndirectaBioticoDescripcion()) &&
								area.getIndirectaBioticoDistancia().equals(areaBdd.getIndirectaBioticoDistancia()) && 
								area.getIndirectaBioticoDistanciaUnidad().equals(areaBdd.getIndirectaBioticoDistanciaUnidad()) && 
								area.getIndirectaFisicoDescripcion().equals(areaBdd.getIndirectaFisicoDescripcion()) &&
								area.getIndirectaFisicoDistancia().equals(areaBdd.getIndirectaFisicoDistancia()) &&
								area.getIndirectaFisicoDistanciaUnidad().equals(areaBdd.getIndirectaFisicoDistanciaUnidad())){	
							//son iguales
							continue;
							
						}else{
							List<AreaInfluencia> areasInfluenciaListHistorico = obtenerHistoricoAreaInfluencia(area.getId(), numeroNotificacion);
							
							if(areasInfluenciaListHistorico != null){
								if(areasInfluenciaListHistorico.size() >= numeroNotificacion){
									area.setEstudioImpactoAmbiental(estudio);
									crudServiceBean.saveOrUpdate(area);
								}else{
									AreaInfluencia areaHistorico = area.clone();
									areaHistorico.setIdHistorico(area.getId());
									areaHistorico.setEstudioImpactoAmbiental(estudio);
									areaHistorico.setFechaCreacion(new Date());
									
									crudServiceBean.saveOrUpdate(areaHistorico);
									
									area.setEstudioImpactoAmbiental(estudio);
									crudServiceBean.saveOrUpdate(area);
								}
							}
						}						
					}
				}

			} else {
				//proyectos normales
				if (areaInfluencia != null) {
					
					if(areaInfluencia.getId() != null){
						AreaInfluencia areaBdd = crudServiceBean.find(AreaInfluencia.class, areaInfluencia.getId());
						
						if(areaInfluencia.getDirectaBioticoDescripcion().equals(areaBdd.getDirectaBioticoDescripcion()) && 
								areaInfluencia.getDirectaBioticoDistancia().equals(areaBdd.getDirectaBioticoDistancia()) && 
								areaInfluencia.getDirectaBioticoDistanciaUnidad().equals(areaBdd.getDirectaBioticoDistanciaUnidad()) && 
								areaInfluencia.getDirectaFisicoDescripcion().equals(areaBdd.getDirectaFisicoDescripcion()) && 
								areaInfluencia.getDirectaFisicoDistancia().equals(areaBdd.getDirectaFisicoDistancia()) && 
								areaInfluencia.getDirectaFisicoDistanciaUnidad().equals(areaBdd.getDirectaFisicoDistanciaUnidad()) &&
								areaInfluencia.getIndirectaBioticoDescripcion().equals(areaBdd.getIndirectaBioticoDescripcion()) &&
								areaInfluencia.getIndirectaBioticoDistancia().equals(areaBdd.getIndirectaBioticoDistancia()) && 
								areaInfluencia.getIndirectaBioticoDistanciaUnidad().equals(areaBdd.getIndirectaBioticoDistanciaUnidad()) && 
								areaInfluencia.getIndirectaFisicoDescripcion().equals(areaBdd.getIndirectaFisicoDescripcion()) &&
								areaInfluencia.getIndirectaFisicoDistancia().equals(areaBdd.getIndirectaFisicoDistancia()) &&
								areaInfluencia.getIndirectaFisicoDistanciaUnidad().equals(areaBdd.getIndirectaFisicoDistanciaUnidad())){
							//son iguales
							
						}else{
							
							List<AreaInfluencia> areasInfluenciaListHistorico = obtenerHistoricoAreaInfluencia(areaInfluencia.getId(), numeroNotificacion);
							
							if(areasInfluenciaListHistorico != null){
								areaInfluencia.setEstudioImpactoAmbiental(estudio);
								crudServiceBean.saveOrUpdate(areaInfluencia);
							}else{
								AreaInfluencia areaHistorico = areaBdd.clone();
								areaHistorico.setIdHistorico(areaBdd.getId());
								areaHistorico.setEstudioImpactoAmbiental(estudio);
								areaHistorico.setNumeroNotificacion(numeroNotificacion);
								areaHistorico.setFechaCreacion(new Date());
								crudServiceBean.saveOrUpdate(areaHistorico);
																
								areaInfluencia.setEstudioImpactoAmbiental(estudio);
								crudServiceBean.saveOrUpdate(areaInfluencia);
							}
						}	
					}else{
						areaInfluencia.setNumeroNotificacion(numeroNotificacion);
						areaInfluencia.setEstudioImpactoAmbiental(estudio);
						crudServiceBean.saveOrUpdate(areaInfluencia);
					}					
				}
			}

//			if (!listaDistanciasEliminar.isEmpty()) {
//								
//				//crudServiceBean.delete(listaDistanciasEliminar);
//			}
			List<InfraestructuraAfectada> infraestructuraEliminarList = new ArrayList<InfraestructuraAfectada>();
			for (InfraestructuraAfectada infraestructura : infraestructuras) {
				
				if(infraestructura.getId() != null){
					InfraestructuraAfectada infraestructuraBdd = crudServiceBean.find(InfraestructuraAfectada.class, infraestructura.getId());
					List<InfraestructuraAfectada> infraestructuraHistoricoList = obtenerHistoricoInfraestructura(infraestructura.getId(), numeroNotificacion);
					
					if(infraestructuraHistoricoList != null){
						
						infraestructura.setEstudioImpactoAmbiental(estudio);
						if (infraestructura.getDistanciaElementoSensibles() != null) {
							for (DistanciaElementoSensible dist : infraestructura.getDistanciaElementoSensibles()) {
								dist.setInfraestructuraAfectada(infraestructura);
							}
							distancias.addAll(infraestructura.getDistanciaElementoSensibles());
						}						
					}else{
						
						if(infraestructura.getNombre().equals(infraestructuraBdd.getNombre()) &&
								infraestructura.getPropietario().equals(infraestructuraBdd.getPropietario()) && 
								infraestructura.getComunidad().equals(infraestructuraBdd.getComunidad()) && 
								infraestructura.getLugar().equals(infraestructuraBdd.getLugar()) && 
								infraestructura.getOtraJurisdiccion().equals(infraestructuraBdd.getOtraJurisdiccion())){
							//son iguales
						}else{
							if(infraestructura.getNumeroNotificacion() != null && infraestructura.getNumeroNotificacion() == numeroNotificacion){
								crudServiceBean.saveOrUpdate(infraestructura);								
							}else{
								InfraestructuraAfectada infraestructuraHistorico = infraestructuraBdd.clone();
								infraestructuraHistorico.setDistanciaElementoSensibles(null);
								infraestructuraHistorico.setNumeroNotificacion(numeroNotificacion);
								infraestructuraHistorico.setIdHistorico(infraestructuraBdd.getId());
								infraestructuraHistorico.setFechaCreacion(new Date());
								crudServiceBean.saveOrUpdate(infraestructuraHistorico);
							}
						}						
						
						infraestructura.setEstudioImpactoAmbiental(estudio);
						if (infraestructura.getDistanciaElementoSensibles() != null) {
							for (DistanciaElementoSensible dist : infraestructura.getDistanciaElementoSensibles()) {
								dist.setInfraestructuraAfectada(infraestructura);
							}
							distancias.addAll(infraestructura.getDistanciaElementoSensibles());
						}						
					}					
				}else{
					
					infraestructura.setEstudioImpactoAmbiental(estudio);
					infraestructura.setNumeroNotificacion(numeroNotificacion);
					if (infraestructura.getDistanciaElementoSensibles() != null) {
						for (DistanciaElementoSensible dist : infraestructura.getDistanciaElementoSensibles()) {
							dist.setInfraestructuraAfectada(infraestructura);
						}
						distancias.addAll(infraestructura.getDistanciaElementoSensibles());
					}
				}							
				
				crudServiceBean.saveOrUpdate(infraestructura);
				infraestructuraEliminarList.add(infraestructura);
				infraestructuraNoVerificadas.remove(infraestructura);
			}

			List<DistanciaElementoSensible> lista = new ArrayList<DistanciaElementoSensible>();
			for (DistanciaElementoSensible distanciaElementoSensible : distancias) {
				
				if(distanciaElementoSensible.getId() != null){
					
					DistanciaElementoSensible distanciaElementoSencibleBdd = crudServiceBean.find(DistanciaElementoSensible.class, distanciaElementoSensible.getId());
					List<DistanciaElementoSensible> distanciaElementoSencibleList = obtenerHistoricoDistanciaElemento(distanciaElementoSensible.getId(), numeroNotificacion);
					
					if(distanciaElementoSencibleList != null){
						crudServiceBean.saveOrUpdate(distanciaElementoSensible);
					}else{
						if(distanciaElementoSensible.getDescripcion().equals(distanciaElementoSencibleBdd.getDescripcion())&& 
								distanciaElementoSensible.getDistancia().equals(distanciaElementoSencibleBdd.getDistancia()) && 
								distanciaElementoSensible.getElementoSensible().equals(distanciaElementoSencibleBdd.getElementoSensible()) && 
								((distanciaElementoSensible.getOtroElemento() == null && distanciaElementoSencibleBdd.getOtroElemento() == null) || 
										distanciaElementoSensible.getOtroElemento() != null && distanciaElementoSencibleBdd.getOtroElemento() != null && 
								distanciaElementoSensible.getOtroElemento().equals(distanciaElementoSencibleBdd.getOtroElemento()))){
							//son iguales
						}else{
							if(distanciaElementoSensible.getNumeroNotificacion() == null || 
									(distanciaElementoSensible.getNumeroNotificacion() != null && distanciaElementoSensible.getNumeroNotificacion() < numeroNotificacion)){
								DistanciaElementoSensible distanciaElementoHistorico = distanciaElementoSencibleBdd.clone();
								distanciaElementoHistorico.setIdHistorico(distanciaElementoSencibleBdd.getId());
								distanciaElementoHistorico.setNumeroNotificacion(numeroNotificacion);	
								distanciaElementoHistorico.setFechaCreacion(new Date());
								crudServiceBean.saveOrUpdate(distanciaElementoHistorico);
							}
						}			
						
						crudServiceBean.saveOrUpdate(distanciaElementoSensible);
					}
					
				}else{
					
					distanciaElementoSensible.setNumeroNotificacion(numeroNotificacion);
					crudServiceBean.saveOrUpdate(distanciaElementoSensible);
				}
								
				//crudServiceBean.saveOrUpdate(distanciaElementoSensible);
				lista.add(distanciaElementoSensible);
				//distanciaElementoSensiblesNoVerificadas.remove(distanciaElementoSensible);
			}
			
			for(DistanciaElementoSensible eliminar: lista){
				//distanciaElementoSensiblesNoVerificadas.remove(eliminar);
				
				Iterator<DistanciaElementoSensible> iteratorDistancia = distanciaElementoSensiblesNoVerificadas.iterator();
				while(iteratorDistancia.hasNext()){
					DistanciaElementoSensible elemento = iteratorDistancia.next();
					
					if(eliminar.getId().equals(elemento.getId()) || elemento.getIdHistorico() != null){
						iteratorDistancia.remove();
					}
				}
			}						
			
			if (!distanciaElementoSensiblesNoVerificadas.isEmpty()) {
				
				for(DistanciaElementoSensible distanciaEliminar : distanciaElementoSensiblesNoVerificadas){						
					
					List<DistanciaElementoSensible> listaHistorico = obtenerHistoricoDistanciaElemento(distanciaEliminar.getId(), numeroNotificacion);
					
					if(distanciaEliminar.getNumeroNotificacion() == null || 
							(distanciaEliminar.getNumeroNotificacion() != null && distanciaEliminar.getNumeroNotificacion() < numeroNotificacion)){
						if(listaHistorico == null){
							DistanciaElementoSensible distanciaElementoHistorico = distanciaEliminar.clone();
							distanciaElementoHistorico.setIdHistorico(distanciaEliminar.getId());
							distanciaElementoHistorico.setNumeroNotificacion(numeroNotificacion);	
							distanciaElementoHistorico.setFechaCreacion(new Date());
							crudServiceBean.saveOrUpdate(distanciaElementoHistorico);
						}
					}
					
					crudServiceBean.delete(distanciaEliminar);
				}
								
				//crudServiceBean.delete(listaDistanciasEliminar);
			}
			
			
			//crudServiceBean.delete(distanciaElementoSensiblesNoVerificadas);
			
			
			for(InfraestructuraAfectada eliminar : infraestructuraEliminarList){
				//distanciaElementoSensiblesNoVerificadas.remove(eliminar);
				
				Iterator<InfraestructuraAfectada> iteratorDistancia = infraestructuraNoVerificadas.iterator();
				while(iteratorDistancia.hasNext()){
					InfraestructuraAfectada elemento = iteratorDistancia.next();
					
					if(eliminar.getId().equals(elemento.getId()) || elemento.getIdHistorico() != null){
						iteratorDistancia.remove();
					}
				}
			}		
			
			if(!infraestructuraNoVerificadas.isEmpty()){
				for(InfraestructuraAfectada infraestructuraEliminar : infraestructuraNoVerificadas){
					
					List<InfraestructuraAfectada> listaHistorico = obtenerHistoricoInfraestructura(infraestructuraEliminar.getId(), numeroNotificacion);
					
					if(infraestructuraEliminar.getNumeroNotificacion() == null || 
							(infraestructuraEliminar.getNumeroNotificacion() != null && infraestructuraEliminar.getNumeroNotificacion() < numeroNotificacion)){
						if(listaHistorico == null){
							InfraestructuraAfectada infraestructuraHistorico = infraestructuraEliminar.clone();
							infraestructuraHistorico.setNumeroNotificacion(numeroNotificacion);
							infraestructuraHistorico.setIdHistorico(infraestructuraEliminar.getId());
							infraestructuraHistorico.setDistanciaElementoSensibles(null);
							infraestructuraHistorico.setFechaCreacion(new Date());
							crudServiceBean.saveOrUpdate(infraestructuraHistorico);
						}		
					}				
					crudServiceBean.delete(infraestructuraEliminar);
				}
			}			
			
			//crudServiceBean.delete(infraestructuraNoVerificadas);
			
			for (InfraestructuraAfectada infraestructura : infraestructuras) {
				if (infraestructura.getDistanciaElementoSensibles() != null) {
					infraestructura.getDistanciaElementoSensibles().size();
				}
			}
			salvarDocumentoHistorico(documento, estudio, documentoHistorico, numeroNotificacion);
			validacionSeccionesFacade.guardarValidacionSeccion("EIA","areaInfluencia", estudio.getId().toString());

		} catch (RuntimeException e) {
			throw new ServiceException(	"No se pudo almacenar las areas de influencia", e);
		}
	}
		
	public List<AreaInfluencia> obtenerHistoricoAreaInfluencia(Integer idHistorico, Integer notificacion){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT a From AreaInfluencia a where a.idHistorico = :idHistorico and a.numeroNotificacion = :notificacion order by 1 desc");
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("notificacion", notificacion);
			
			List<AreaInfluencia> areaInfluenciaList = (List<AreaInfluencia>) query.getResultList();
			
			if(areaInfluenciaList != null && !areaInfluenciaList.isEmpty())
				return areaInfluenciaList;
			else
				return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public List<DistanciaElementoSensible> obtenerHistoricoDistanciaElemento(Integer idHistorico, Integer notificacion){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT d From DistanciaElementoSensible d where d.idHistorico = :idHistorico and d.numeroNotificacion = :notificacion order by 1 desc");
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("notificacion", notificacion);
			
			List<DistanciaElementoSensible> areaInfluenciaList = (List<DistanciaElementoSensible>) query.getResultList();
			
			if(areaInfluenciaList != null && !areaInfluenciaList.isEmpty())
				return areaInfluenciaList;
			else
				return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public List<InfraestructuraAfectada> obtenerHistoricoInfraestructura(Integer idHistorico, Integer notificacion){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT i From InfraestructuraAfectada i where i.idHistorico = :idHistorico and i.numeroNotificacion = :notificacion order by 1 desc");
			query.setParameter("idHistorico", idHistorico);
			query.setParameter("notificacion", notificacion);
			
			List<InfraestructuraAfectada> areaInfluenciaList = (List<InfraestructuraAfectada>) query.getResultList();
			
			if(areaInfluenciaList != null && !areaInfluenciaList.isEmpty())
				return areaInfluenciaList;
			else
				return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	 private void salvarDocumentoHistorico(Documento doc, EstudioImpactoAmbiental estudio, Documento documentoHistorico, Integer numeroNotificacion) throws ServiceException {
	        try {
	        	Documento documentoA = new Documento();
	        	
	        	if(doc.getId() == null){
	        		doc.setIdTable(estudio.getId());
	        		doc.setDescripcion("Documento General");
	        		doc.setEstado(true);
	        		documentoA = documentosFacade.guardarDocumentoAlfresco(estudio.getProyectoLicenciamientoAmbiental().getCodigo(),
	                    Constantes.CARPETA_EIA, 0L, doc, TipoDocumentoSistema.DETERMINACION_AREA_INFLUENCIA_GEN, null);
	        		doc = documentoA;
	        	}
	            
	            if(documentoHistorico != null && documentoA.getId() != null){
	            	documentoHistorico.setIdHistorico(documentoA.getId());
	            	documentoHistorico.setNumeroNotificacion(numeroNotificacion);
					documentosFacade.actualizarDocumento(documentoHistorico);
				}
	        } catch (Exception e) {
	            throw new ServiceException("No se pudo almacenar el documento en Alfresco", e);
	        }
	   }
	 
	/**
	 * Consultar las areas de influencia modificadas por estudio
	 * 
	 * @author mariela.guano
	 * @param estudio
	 * @return
	 */
	public List<AreaInfluencia> getAreaInfluenciaHistorico(Integer idHistorico) {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery(
							"SELECT a From AreaInfluencia a where a.idHistorico = :idHistorico order by 1 asc");
			query.setParameter("idHistorico", idHistorico);

			List<AreaInfluencia> areaInfluenciaList = (List<AreaInfluencia>) query.getResultList();

			if (areaInfluenciaList != null && !areaInfluenciaList.isEmpty())
				return areaInfluenciaList;
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Consultar las infraestructuras en bdd por estudio
	 * @author mariela.guano
	 * @param estudio
	 * @param notificacion
	 * @return
	 */
	public List<InfraestructuraAfectada> getInfraestructuraEnBdd(EstudioImpactoAmbiental estudio) {
		try {
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"select ia FROM InfraestructuraAfectada ia where ia.estudioImpactoAmbiental = :estudio "
							+ "order by id asc");
			query.setParameter("estudio", estudio);

			List<InfraestructuraAfectada> areaInfluenciaList = (List<InfraestructuraAfectada>) query.getResultList();

			if (areaInfluenciaList != null && !areaInfluenciaList.isEmpty())
				return areaInfluenciaList;
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
