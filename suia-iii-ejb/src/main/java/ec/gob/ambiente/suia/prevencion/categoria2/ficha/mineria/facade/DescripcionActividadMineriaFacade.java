/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

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

import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.suia.catalogos.service.UnidadMedidaService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.CatalogoActividadComercial;
import ec.gob.ambiente.suia.domain.CatalogoHerramientaArtesanal;
import ec.gob.ambiente.suia.domain.CatalogoInstalacion;
import ec.gob.ambiente.suia.domain.CatalogoTecnica;
import ec.gob.ambiente.suia.domain.CatalogoTipoMaterial;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.DescripcionActividadMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaMineriaInsumos;
import ec.gob.ambiente.suia.domain.HerramientaMinera;
import ec.gob.ambiente.suia.domain.Instalacion;
import ec.gob.ambiente.suia.domain.ProcesoMinero;
import ec.gob.ambiente.suia.domain.UnidadMedida;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class DescripcionActividadMineriaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private UnidadMedidaService unidadMedidaService;

    public void guardar(final DescripcionActividadMineria descripcionActividadMineria, final List<ActividadMinera> listaActividadesSeleccionadas, final List<Instalacion> listaInstalaciones, final List<HerramientaMinera> listaHerramientas, final List<FichaMineriaInsumos> listaInsumos) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(descripcionActividadMineria);
            descripcionActividadMineria.getFichaAmbientalMineria();
            for (ActividadMinera a : listaActividadesSeleccionadas) {
                a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
            }
            for (Instalacion a : listaInstalaciones) {
                a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
            }
            for (HerramientaMinera a : listaHerramientas) {
                a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
            }
            for (FichaMineriaInsumos a : listaInsumos) {
                a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
            }
            List<EntidadBase> lista = new ArrayList<EntidadBase>();
            lista.addAll(listaActividadesSeleccionadas);
            lista.addAll(listaInstalaciones);
            lista.addAll(listaHerramientas);
            lista.addAll(listaInsumos);
            crudServiceBean.saveOrUpdate(lista);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public DescripcionActividadMineria obtenerPorFichaMineria(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idFicha", fichaAmbientalMineria.getId());
            return crudServiceBean.findByNamedQuerySingleResult(DescripcionActividadMineria.LISTAR_POR_FICHA, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public CatalogoTipoMaterial obtenerTipoMaterialPorId(Integer id) {
        return crudServiceBean.find(CatalogoTipoMaterial.class, id);
    }

    public ProcesoMinero obtenerProcesoPorId(Integer id) {
        return crudServiceBean.find(ProcesoMinero.class, id);
    }

    public CatalogoHerramientaArtesanal obtenerCatalogoHerramientaPorId(Integer id) {
        return crudServiceBean.find(CatalogoHerramientaArtesanal.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<CatalogoActividadComercial> getCatalogoActividadComercial(Integer catalogoCategoriaFaseId){
    	return (List<CatalogoActividadComercial>) crudServiceBean
                .getEntityManager()
                .createQuery("From CatalogoActividadComercial c where c.categoriaFase.id =:catalogoCategoriaFaseId and c.estado = true ORDER BY c.nombreActividad")
                .setParameter("catalogoCategoriaFaseId", catalogoCategoriaFaseId)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ProcesoMinero> getProcesosMinerosXTipoObtencion(Integer idTipoObtencion) throws ServiceException {
        try {
            List<ProcesoMinero> catalogo = new ArrayList<ProcesoMinero>();
            catalogo = (List<ProcesoMinero>) crudServiceBean.getEntityManager().createQuery(
                    "From ProcesoMinero p where p.catalogoTipoMaterial.id =:idTipoObtencion and p.estado = TRUE order by p.nombre")
                    .setParameter("idTipoObtencion", idTipoObtencion).getResultList();
            return catalogo;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<CatalogoHerramientaArtesanal> getHerramientasPorProceso(Integer idProceso) throws ServiceException {
        try {
            List<CatalogoHerramientaArtesanal> catalogo = new ArrayList<CatalogoHerramientaArtesanal>();
            catalogo = (List<CatalogoHerramientaArtesanal>) crudServiceBean.getEntityManager().createQuery(
                    "From CatalogoHerramientaArtesanal c where c.procesoMinero.id =:idProceso and c.estado = TRUE order by c.orden,c.nombre")
                    .setParameter("idProceso", idProceso).getResultList();
            return catalogo;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<HerramientaMinera> listarHerramientaPorFichaAmbiental(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idMineria", fichaAmbientalMineria.getId());
            return (List<HerramientaMinera>) crudServiceBean.findByNamedQuery(HerramientaMinera.findByRecord, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<CatalogoInstalacion> getCatalogoInstalaciones(String codigoSubsector) throws ServiceException {
        try {
            List<CatalogoInstalacion> catalogo = new ArrayList<CatalogoInstalacion>();
            catalogo = (List<CatalogoInstalacion>) crudServiceBean.getEntityManager().createQuery(
                    "From CatalogoInstalacion c where c.estado = TRUE and c.tipoSubsector.codigo =:p_codigo order by c.nombre")
                    .setParameter("p_codigo", codigoSubsector).getResultList();
            return catalogo;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Instalacion> listarInstalacionesPorFichaAmbiental(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idFicha", fichaAmbientalMineria.getId());
            return (List<Instalacion>) crudServiceBean.findByNamedQuery(Instalacion.findByRecord, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<FichaMineriaInsumos> listarPorFicha(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idMineria", fichaAmbientalMineria.getId());
            return (List<FichaMineriaInsumos>) crudServiceBean.findByNamedQuery(FichaMineriaInsumos.LISTAR_POR_FICHA, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ConcesionMinera> concesionesMinerasPorProyecto(Integer idProyecto) throws ServiceException {
        try {
            List<ConcesionMinera> concesiones = new ArrayList<ConcesionMinera>();
            concesiones = (List<ConcesionMinera>) crudServiceBean.getEntityManager().createQuery(
                    "From ConcesionMinera c where c.proyectoLicenciamientoAmbiental.id =:idProyecto and c.estado = TRUE")
                    .setParameter("idProyecto", idProyecto).getResultList();
            return concesiones;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public List<UnidadMedida> catalogoUnidadMedida() throws ServiceException{
    	try {
			return unidadMedidaService.listaUnidadesMedida();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
    }
    
    @SuppressWarnings("unchecked")
    public List<CatalogoTecnica> getCatalogoTecnicas() throws ServiceException {
        try {
            List<CatalogoTecnica> catalogo = new ArrayList<CatalogoTecnica>();
            catalogo = (List<CatalogoTecnica>) crudServiceBean.getEntityManager().createQuery(
                    "From CatalogoTecnica c where c.estado = TRUE order by c.id")
                    .getResultList();
            return catalogo;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Cris F: Nuevo metodo guardar para historial
     */
    @EJB
    private ActividadMineriaFacade actividadMineriaFacade;  
    
    public void guardarConHistorial(final DescripcionActividadMineria descripcionActividadMineria, final List<ActividadMinera> listaActividadesSeleccionadas, final List<Instalacion> listaInstalaciones, final List<HerramientaMinera> listaHerramientas, final List<FichaMineriaInsumos> listaInsumos) throws ServiceException {
        try {
        	
        	DescripcionActividadMineria descripcionActividadMineriaBdd = new DescripcionActividadMineria();
        	
        	if(descripcionActividadMineria.getId() != null){
        		descripcionActividadMineriaBdd = crudServiceBean.find(DescripcionActividadMineria.class, descripcionActividadMineria.getId());
        	}
        	
        	if(descripcionActividadMineriaBdd != null && descripcionActividadMineriaBdd.getId() != null){
        		if(!comparacionDescripcionMineria(descripcionActividadMineriaBdd, descripcionActividadMineria)){
            		
            		DescripcionActividadMineria descripcionActividadMineriaHistorial = new DescripcionActividadMineria();
            		descripcionActividadMineriaHistorial = (DescripcionActividadMineria)SerializationUtils.clone(descripcionActividadMineriaBdd);
            		descripcionActividadMineriaHistorial.setId(null);
            		descripcionActividadMineriaHistorial.setFechaHistorico(new Date());
            		descripcionActividadMineriaHistorial.setIdRegistroOriginal(descripcionActividadMineriaBdd.getId());
            		crudServiceBean.saveOrUpdate(descripcionActividadMineriaHistorial);
            	}
        	}       	    	
        	
            crudServiceBean.saveOrUpdate(descripcionActividadMineria);
            
            descripcionActividadMineria.getFichaAmbientalMineria();
            
            List<ActividadMinera> listaActividadesSeleccionadasAux = new ArrayList<ActividadMinera>();
            List<Instalacion> listaInstalacionesAux = new ArrayList<Instalacion>();
            List<HerramientaMinera> listaHerramientasAux = new ArrayList<HerramientaMinera>();
            List<FichaMineriaInsumos> listaInsumosAux = new ArrayList<FichaMineriaInsumos>();
            
            
            List<ActividadMinera> listaActividadMineraBdd = new ArrayList<ActividadMinera>();
            listaActividadMineraBdd = actividadMineriaFacade.listarPorFichaAmbiental(descripcionActividadMineria.getFichaAmbientalMineria());
            
            if(listaActividadMineraBdd != null && !listaActividadMineraBdd.isEmpty()){
            	
            	for (ActividadMinera a : listaActividadesSeleccionadas) {
            		
            		 a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
            		
            		Comparator<ActividadMinera> c = new Comparator<ActividadMinera>() {
						
						@Override
						public int compare(ActividadMinera o1, ActividadMinera o2) {							
							return o1.getId().compareTo(o2.getId());
						}
					};
            		
					Collections.sort(listaActividadMineraBdd, c);
										            		
            		if(a.getId() != null){
            			
            			int index = Collections.binarySearch(listaActividadMineraBdd, new ActividadMinera(a.getId()), c);
            			
            			if(index >= 0){
            				ActividadMinera aBdd = listaActividadMineraBdd.get(index);
            				
            				if(((aBdd.getActividadComercial() == null && a.getActividadComercial() == null) || 
            						(aBdd.getActividadComercial() != null && a.getActividadComercial() != null && 
            						aBdd.getActividadComercial().equals(a.getActividadComercial()))) && 
            						((aBdd.getDescripcion() == null && a.getDescripcion() == null) ||
            						(aBdd.getDescripcion() != null && a.getDescripcion() != null && 
            						aBdd.getDescripcion().equals(a.getDescripcion()))) && 
            						((aBdd.getDiasDuracion() == null && a.getDiasDuracion() == null) ||
            						(aBdd.getDiasDuracion() != null && a.getDiasDuracion() != null && 
            						aBdd.getDiasDuracion().equals(a.getDiasDuracion()))) && 
            						(aBdd.getEstado().equals(a.getEstado()))){
            					//son iguales
            					continue;
            				}else{
            					ActividadMinera actividadHistorico = new ActividadMinera();
            					actividadHistorico = (ActividadMinera)SerializationUtils.clone(aBdd);
            					actividadHistorico.setId(null);
            					actividadHistorico.setFechaHistorico(new Date());
            					actividadHistorico.setIdRegistroOriginal(aBdd.getId());            					
            					listaActividadesSeleccionadasAux.add(actividadHistorico);
            					listaActividadesSeleccionadasAux.add(a);            				
            				}
            			}else{
            				a.setFechaHistorico(new Date());
            				listaActividadesSeleccionadasAux.add(a);
            			}
            		}else{
            			a.setFechaHistorico(new Date());
            			listaActividadesSeleccionadasAux.add(a);
            		}
            	}            	
            }else{
            	for (ActividadMinera a : listaActividadesSeleccionadas) {
                    a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
                    listaActividadesSeleccionadasAux.add(a);
                }
            }
            
            //instalaciones
                        
            List<Instalacion> listaInstalacionesBdd = new ArrayList<Instalacion>();
            listaInstalacionesBdd = listarInstalacionesPorFichaAmbiental(descripcionActividadMineria.getFichaAmbientalMineria());
            
            if(listaInstalacionesBdd != null && !listaInstalacionesBdd.isEmpty()){
            	
            	 for (Instalacion a : listaInstalaciones) {
                     a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
                     
                     Comparator<Instalacion> c = new Comparator<Instalacion>() {
						
						@Override
						public int compare(Instalacion o1, Instalacion o2) {							
							return o1.getId().compareTo(o2.getId());
						}
					};
					
					Collections.sort(listaInstalacionesBdd, c);
					
					if(a.getId() != null){
						int index = Collections.binarySearch(listaInstalacionesBdd, new Instalacion(a.getId()), c);
						
						if(index >= 0){
							Instalacion instalacionBdd = listaInstalacionesBdd.get(index);
							
							if(((instalacionBdd.getCatalogoInstalacion() == null && a.getCatalogoInstalacion() == null) ||
									(instalacionBdd.getCatalogoInstalacion() != null && a.getCatalogoInstalacion() != null && 
									instalacionBdd.getCatalogoInstalacion().equals(a.getCatalogoInstalacion()))) && 
									((instalacionBdd.getDescripcion() == null && a.getDescripcion() == null) || 
									(instalacionBdd.getDescripcion() != null && a.getDescripcion() != null && 
									instalacionBdd.getDescripcion().equals(a.getDescripcion()))) &&
									(instalacionBdd.getEstado().equals(a.getEstado()))){
								//son iguales
								continue;
							}else{
								Instalacion instalacionHistorico = new Instalacion();
								instalacionHistorico = (Instalacion)SerializationUtils.clone(instalacionBdd);
								instalacionHistorico.setId(null);
								instalacionHistorico.setFechaHistorico(new Date());
								instalacionHistorico.setIdRegistroOriginal(instalacionBdd.getId());
								listaInstalacionesAux.add(instalacionHistorico);
								listaInstalacionesAux.add(a);
							}							
						}else{
							a.setFechaHistorico(new Date());
							listaInstalacionesAux.add(a);
						}
					}else{
						a.setFechaHistorico(new Date());
						listaInstalacionesAux.add(a);
					}
                 }
            	
            }else{
            	 for (Instalacion a : listaInstalaciones) {
                     a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
                     listaInstalacionesAux.add(a);
                 }
            }
            
            //Herramienta Minera
            
            List<HerramientaMinera> listaHerramientaMineraBdd = listarHerramientaPorFichaAmbiental(descripcionActividadMineria.getFichaAmbientalMineria());
            
            if(listaHerramientaMineraBdd != null && !listaHerramientaMineraBdd.isEmpty()){
            	for (HerramientaMinera a : listaHerramientas) {
                    a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
                    
                    Comparator<HerramientaMinera> c = new Comparator<HerramientaMinera>() {
						
						@Override
						public int compare(HerramientaMinera o1, HerramientaMinera o2) {							
							return o1.getId().compareTo(o2.getId());
						}
					};
					
					Collections.sort(listaHerramientaMineraBdd, c);
					
					if(a.getId() != null){
						
						int index = Collections.binarySearch(listaHerramientaMineraBdd, new HerramientaMinera(a.getId()), c);
						
						if(index >= 0){
							
							HerramientaMinera herramientaBdd = listaHerramientaMineraBdd.get(index);
							
							if(((herramientaBdd.getCantidadHerramientas() == null && a.getCantidadHerramientas() == null) ||
								(herramientaBdd.getCantidadHerramientas() != null && a.getCantidadHerramientas() != null && 
								herramientaBdd.getCantidadHerramientas().equals(a.getCantidadHerramientas()))) && 
								((herramientaBdd.getCatalogoHerramienta() == null && a.getCatalogoHerramienta() == null) ||
								herramientaBdd.getCatalogoHerramienta() != null && a.getCatalogoHerramienta() != null && 
								herramientaBdd.getCatalogoHerramienta().equals(a.getCatalogoHerramienta())) && 
								((herramientaBdd.getTipoObtencion() == null && a.getTipoObtencion() == null) ||
								(herramientaBdd.getTipoObtencion() != null && a.getTipoObtencion() != null && 
								herramientaBdd.getTipoObtencion().equals(a.getTipoObtencion()))) && 
								((herramientaBdd.getProcesoMinero() == null && a.getProcesoMinero() == null) || 
								(herramientaBdd.getProcesoMinero() != null && a.getProcesoMinero() != null && 
								herramientaBdd.getProcesoMinero().equals(a.getProcesoMinero()))) && 
								((herramientaBdd.getDescripcion() == null && a.getDescripcion() == null) ||
								(herramientaBdd.getDescripcion() != null && a.getDescripcion() != null && 
								herramientaBdd.getDescripcion().equals(a.getDescripcion()))) && 
								(herramientaBdd.getEstado().equals(a.getEstado()))){
								//son iguales
								continue;
							}else{
								HerramientaMinera herramientaHistorico = new HerramientaMinera();
								herramientaHistorico = (HerramientaMinera)SerializationUtils.clone(herramientaBdd);
								herramientaHistorico.setId(null);
								herramientaHistorico.setFechaHistorico(new Date());
								herramientaHistorico.setIdRegistroOriginal(herramientaBdd.getId());
								listaHerramientasAux.add(herramientaHistorico);
								listaHerramientasAux.add(a);
							}
						}else{
							a.setFechaHistorico(new Date());
							listaHerramientasAux.add(a);
						}
					}else{
						a.setFechaHistorico(new Date());
						listaHerramientasAux.add(a);
					}
                }
            }else{
            	for (HerramientaMinera a : listaHerramientas) {
                    a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
                    listaHerramientasAux.add(a);
                }
            }
            
            //Insumos
            List<FichaMineriaInsumos> listaInsumosBdd = listarPorFicha(descripcionActividadMineria.getFichaAmbientalMineria());
            
            if(listaInsumosBdd != null && !listaInsumosBdd.isEmpty()){
            	
            	for (FichaMineriaInsumos a : listaInsumos) {
                    a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
                    
                    Comparator<FichaMineriaInsumos> c = new Comparator<FichaMineriaInsumos>() {
						
						@Override
						public int compare(FichaMineriaInsumos o1, FichaMineriaInsumos o2) {							
							return o1.getId().compareTo(o2.getId());
						}
					};
					
					Collections.sort(listaInsumosBdd, c);
					
					if(a.getId() != null){
						
						int index = Collections.binarySearch(listaInsumosBdd, new FichaMineriaInsumos(a.getId()), c);
						
						if(index >= 0){
							FichaMineriaInsumos insumoBdd = listaInsumosBdd.get(index);
							
							if(((insumoBdd.getCatalogoInsumo() == null && a.getCatalogoInsumo() == null) ||
								(insumoBdd.getCatalogoInsumo() != null && a.getCatalogoInsumo() != null && 
								insumoBdd.getCatalogoInsumo().equals(a.getCatalogoInsumo()))) && 
								((insumoBdd.getCatalogoHijoInsumo() == null && a.getCatalogoHijoInsumo() == null) ||
								(insumoBdd.getCatalogoHijoInsumo() != null && a.getCatalogoHijoInsumo() != null && 
								insumoBdd.getCatalogoHijoInsumo().equals(a.getCatalogoHijoInsumo()))) && 
								((insumoBdd.getCatalogoInsumo() == null && a.getCatalogoInsumo() == null) ||
								(insumoBdd.getCatalogoInsumo() != null && a.getCatalogoInsumo() != null && 
								insumoBdd.getCatalogoInsumo().equals(a.getCatalogoInsumo()))) && 
								((insumoBdd.getHijoInsumoOtro() == null && a.getHijoInsumoOtro() == null) ||
								(insumoBdd.getHijoInsumoOtro() != null && a.getHijoInsumoOtro() != null && 
								insumoBdd.getHijoInsumoOtro().equals(a.getHijoInsumoOtro()))) && 
								((insumoBdd.getIdCatalogoHijoInsumo() == null && a.getIdCatalogoHijoInsumo() == null) ||
								(insumoBdd.getIdCatalogoHijoInsumo() != null && a.getIdCatalogoHijoInsumo() != null && 
								insumoBdd.getIdCatalogoHijoInsumo().equals(a.getIdCatalogoHijoInsumo()))) && 
								((insumoBdd.getIdCatalogoInsumo() == null && a.getIdCatalogoInsumo() == null) ||
								(insumoBdd.getIdCatalogoInsumo() != null && a.getIdCatalogoInsumo() != null && 
								insumoBdd.getIdCatalogoInsumo().equals(a.getIdCatalogoInsumo()))) && 
								((insumoBdd.getInsumoOtro() == null && a.getInsumoOtro() == null) ||
								insumoBdd.getInsumoOtro() != null && a.getInsumoOtro() != null && 
								insumoBdd.getInsumoOtro().equals(a.getInsumoOtro())) && 
								((insumoBdd.getUnidadMedida() == null && a.getUnidadMedida() == null) ||
								insumoBdd.getUnidadMedida() != null && a.getUnidadMedida() != null && 
								insumoBdd.getUnidadMedida().equals(a.getUnidadMedida()) && 
								(insumoBdd.getEstado().equals(a.getEstado())))
								){
								//son iguales
								continue;
							}else{
								
								FichaMineriaInsumos insumoHistorico = new FichaMineriaInsumos();
								insumoHistorico = (FichaMineriaInsumos)SerializationUtils.clone(insumoBdd);
								insumoHistorico.setId(null);
								insumoHistorico.setFechaHistorico(new Date());
								insumoHistorico.setIdRegistroOriginal(insumoBdd.getId());
								listaInsumosAux.add(insumoHistorico);
								listaInsumosAux.add(a);
							}							
						}else{
							a.setFechaHistorico(new Date());
							listaInsumosAux.add(a);
						}						
					}else{
						a.setFechaHistorico(new Date());
						listaInsumosAux.add(a);
					}                    
                }            	
            }else{
            	for (FichaMineriaInsumos a : listaInsumos) {
                    a.setFichaAmbientalMineria(descripcionActividadMineria.getFichaAmbientalMineria());
                    listaInsumosAux.add(a);
                }
            }
            
            List<EntidadBase> lista = new ArrayList<EntidadBase>();
            lista.addAll(listaActividadesSeleccionadasAux);
            lista.addAll(listaInstalacionesAux);
            lista.addAll(listaHerramientasAux);
            lista.addAll(listaInsumosAux);
            crudServiceBean.saveOrUpdate(lista);
        } catch (Exception e) {
        	e.printStackTrace();
//            throw new ServiceException(e);
        }
    }
    
    private boolean comparacionDescripcionMineria(DescripcionActividadMineria descripcionActividadMineriaBdd, DescripcionActividadMineria descripcionActividadMineria){
    	try{
    		if(((descripcionActividadMineriaBdd.getNumeroPersonasLaboran() == null && descripcionActividadMineria.getNumeroPersonasLaboran() == null) ||
        			(descripcionActividadMineriaBdd.getNumeroPersonasLaboran() != null && descripcionActividadMineria.getNumeroPersonasLaboran() != null && 
        			descripcionActividadMineriaBdd.getNumeroPersonasLaboran().equals(descripcionActividadMineria.getNumeroPersonasLaboran()))) && 
        			((descripcionActividadMineriaBdd.getMontoInversion() == null && descripcionActividadMineria.getMontoInversion() == null) || 
        			(descripcionActividadMineriaBdd.getMontoInversion() != null && descripcionActividadMineria.getMontoInversion() != null &&
        			descripcionActividadMineriaBdd.getMontoInversion().equals(descripcionActividadMineria.getMontoInversion()))) && 
        			((descripcionActividadMineriaBdd.getVolumenProduccionDiario() == null && descripcionActividadMineria.getVolumenProduccionDiario() == null) ||
        			(descripcionActividadMineriaBdd.getVolumenProduccionDiario() != null && descripcionActividadMineria.getVolumenProduccionDiario() != null && 
        			descripcionActividadMineriaBdd.getVolumenProduccionDiario().equals(descripcionActividadMineria.getVolumenProduccionDiario()))) && 
        			((descripcionActividadMineriaBdd.getNombrePlantaBeneficio() == null && descripcionActividadMineria.getNombrePlantaBeneficio() == null) ||
        			(descripcionActividadMineriaBdd.getNombrePlantaBeneficio() != null && descripcionActividadMineria.getNombrePlantaBeneficio() != null && 
        			descripcionActividadMineriaBdd.getNombrePlantaBeneficio().equals(descripcionActividadMineria.getNombrePlantaBeneficio()))) && 
        			(descripcionActividadMineriaBdd.isTieneLicenciaAmbiental() == descripcionActividadMineria.isTieneLicenciaAmbiental()) &&
        			((descripcionActividadMineriaBdd.getNumeroObservacionLicenciaAmbiental() == null && descripcionActividadMineria.getNumeroObservacionLicenciaAmbiental() == null) ||
        			(descripcionActividadMineriaBdd.getNumeroObservacionLicenciaAmbiental() != null && descripcionActividadMineria.getNumeroObservacionLicenciaAmbiental() != null && 
        			descripcionActividadMineriaBdd.getNumeroObservacionLicenciaAmbiental().equals(descripcionActividadMineria.getNumeroObservacionLicenciaAmbiental())))
        			){
        		return true;
        	}else{
        		return false;
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    		return true;
    	}
    }
    
    @SuppressWarnings("unchecked")
    public List<DescripcionActividadMineria> obtenerPorFichaMineriaHistorial(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {        	
        	Query query = crudServiceBean.getEntityManager().createQuery("SELECT f FROM DescripcionActividadMineria f WHERE f.idFichaAmbiental = :idFicha AND f.estado = true and idRegistroOriginal != null");
        	query.setParameter("idFicha", fichaAmbientalMineria.getId());
        	
        	List<DescripcionActividadMineria> resultado = query.getResultList();
        	
        	if(resultado != null && !resultado.isEmpty()){
        		return resultado;
        	}
        	return null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<HerramientaMinera> listarHerramientaPorFichaAmbientalHistorial(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
        	Query query = crudServiceBean.getEntityManager().createQuery("SELECT t FROM HerramientaMinera t WHERE t.estado = true AND t.fichaAmbientalMineria.id = :idMineria and idRegistroOriginal != null");
        	query.setParameter("idMineria", fichaAmbientalMineria.getId());
        	
        	List<HerramientaMinera> resultado = query.getResultList();
        	
        	if(resultado != null && !resultado.isEmpty()){
        		return resultado;
        	}
            return null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Instalacion> listarInstalacionesPorFichaAmbientalHistorial(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            
        	Query query = crudServiceBean.getEntityManager().createQuery("SELECT i FROM Instalacion i WHERE i.estado = true AND i.fichaAmbientalMineria.id = :idFicha and idRegistroOriginal != null");
        	query.setParameter("idFicha", fichaAmbientalMineria.getId());
        	
        	List<Instalacion> resultado = query.getResultList();
        	
        	if(resultado != null && !resultado.isEmpty()){
        		return resultado;
        	}
        	
        	return null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<FichaMineriaInsumos> listarPorFichaHistorial(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
        	Query query = crudServiceBean.getEntityManager().createQuery("SELECT a FROM FichaMineriaInsumos a WHERE a.estado = true AND a.idFichaMineria = :idMineria and idRegistroOriginal != null");
        	query.setParameter("idMineria", fichaAmbientalMineria.getId());
        	
        	List<FichaMineriaInsumos> resultado = query.getResultList();
        	
        	if(resultado != null && !resultado.isEmpty()){
        		return resultado;
        	}
        	
            return null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
