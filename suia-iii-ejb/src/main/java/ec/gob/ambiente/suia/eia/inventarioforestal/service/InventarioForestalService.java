/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.inventarioforestal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.administracion.facade.AdjuntosEiaFacade;
import ec.gob.ambiente.suia.coordenadageneral.service.CoordenadaGeneralService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EspeciesBajoCategoriaAmenaza;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InventarioForestal;
import ec.gob.ambiente.suia.domain.InventarioForestalIndice;
import ec.gob.ambiente.suia.domain.InventarioForestalPuntos;
import ec.gob.ambiente.suia.domain.InventarioForestalVolumen;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author ishmael
 */
@Stateless
public class InventarioForestalService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private AdjuntosEiaFacade adjuntosEiaFacade;
	@EJB
	private EiaOpcionesFacade eiaOpcionesFacade;
	@EJB
	private CoordenadaGeneralService coordenadaGeneralService;

	@SuppressWarnings("unchecked")
	public void guardar(InventarioForestal inventarioForestal,List<CoordenadaGeneral> listaCoordenada, List listaRemover) throws ServiceException {
		try {

			List<InventarioForestalPuntos> inventarioForestalPuntosLista = inventarioForestal.getInventarioForestalPuntosList();
			inventarioForestal.setInventarioForestalPuntosList(new ArrayList<InventarioForestalPuntos>());
			InventarioForestal inventarioForestalPersist = crudServiceBean.saveOrUpdate(inventarioForestal);

			for (InventarioForestalPuntos p : inventarioForestalPuntosLista) {
				List<CoordenadaGeneral> coordenadasGenerales = new ArrayList<CoordenadaGeneral>();
				coordenadasGenerales.addAll(p.getCoordenadasGeneral());
				p.setInventarioForestal(inventarioForestalPersist);
				crudServiceBean.saveOrUpdate(p);
								
				for (CoordenadaGeneral c : coordenadasGenerales) {
					c.setInventarioForestalPuntos(p);
					c.setIdTable(p.getId());
					c.setNombreTabla(InventarioForestalPuntos.class	.getSimpleName());
					crudServiceBean.saveOrUpdate(c);
				}
			}
			crudServiceBean.saveOrUpdate(listaRemover);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public void guardar(InventarioForestal inventarioForestal)
			throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(inventarioForestal);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public InventarioForestal obtenerPorEia(
			EstudioImpactoAmbiental estudioImpactoAmbiental)
			throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("estudioImpactoAmbiental", estudioImpactoAmbiental);
		List<InventarioForestal> result = (List<InventarioForestal>) crudServiceBean
				.findByNamedQuery(InventarioForestal.OBTENER_POR_EIA, params);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	public InventarioForestal obtenetInventarioForestal(
			EstudioImpactoAmbiental estudioImpactoAmbiental)
			throws ServiceException {
		InventarioForestal iForestal = obtenerPorEia(estudioImpactoAmbiental);
		if (iForestal == null) {
			return new InventarioForestal();
		} else {
			iForestal
					.setInventarioForestalPuntosList(listarInventarioForestalPuntos(iForestal));
			// iForestal.setInventarioForestalIndiceList(listarInventarioForestalIndice(iForestal));
			// iForestal.setInventarioForestalVolumenList(listarInventarioForestalVolumen(iForestal));
			iForestal
					.setEspeciesBajoCategoriaAmenazaList(listarEspeciesBajoCategoriaAmenazas(iForestal));

			return iForestal;
		}
	}

	public List<InventarioForestalPuntos> listarInventarioForestalPuntos(
			InventarioForestal inventarioForestal) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("inventarioForestal", inventarioForestal);

		List<InventarioForestalPuntos> result = new ArrayList<InventarioForestalPuntos>();
		List<InventarioForestalPuntos> inventarioForestalPuntosLista = (List<InventarioForestalPuntos>) crudServiceBean
				.findByNamedQuery(
						InventarioForestalPuntos.LISTAR_POR_INVENTARIO, params);
		for (InventarioForestalPuntos inventarioForestalPuntos : inventarioForestalPuntosLista) {
			inventarioForestalPuntos
					.setCoordenadasGeneral(listarCoordenadaGeneral(
							InventarioForestalPuntos.class.getSimpleName(),
							inventarioForestalPuntos.getId()));
			result.add(inventarioForestalPuntos);
		}

		return result;
	}

	public List<EspeciesBajoCategoriaAmenaza> listarEspeciesBajoCategoriaAmenazas(
			InventarioForestal inventarioForestal) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("inventarioForestal", inventarioForestal);
		return (List<EspeciesBajoCategoriaAmenaza>) crudServiceBean
				.findByNamedQuery(
						EspeciesBajoCategoriaAmenaza.LISTAR_POR_INVENTARIO,
						params);
	}

	public List<InventarioForestalVolumen> listarInventarioForestalVolumen(
			InventarioForestal inventarioForestal) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("inventarioForestal", inventarioForestal);
		return (List<InventarioForestalVolumen>) crudServiceBean
				.findByNamedQuery(
						InventarioForestalVolumen.LISTAR_POR_INVENTARIO, params);
	}

	public List<InventarioForestalIndice> listarInventarioForestalIndice(
			InventarioForestal inventarioForestal) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("inventarioForestal", inventarioForestal);
		return (List<InventarioForestalIndice>) crudServiceBean
				.findByNamedQuery(
						InventarioForestalIndice.LISTAR_POR_INVENTARIO, params);
	}

	public List<CoordenadaGeneral> listarCoordenadaGeneral(String nombreTabla,
			Integer idTabla) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nombreTabla", nombreTabla);
		params.put("idTabla", idTabla);
		return (List<CoordenadaGeneral>) crudServiceBean.findByNamedQuery(
				CoordenadaGeneral.LISTAR_POR_ID_NOMBRE_TABLA, params);
	}
	
	/**
	 * CF: Guardar Historico
	 */
	@SuppressWarnings("unchecked")
	public void guardarHistorico(InventarioForestal inventarioForestal,List<CoordenadaGeneral> listaCoordenada, List listaRemover,
			Integer numeroNotificacion) throws ServiceException {
		try {
			
			InventarioForestal inventarioForestalBdd = crudServiceBean.find(InventarioForestal.class, inventarioForestal.getId());
			InventarioForestal inventarioForestalPersist = new InventarioForestal();
			List<InventarioForestal> listaHistorico = obtenerHistorial(inventarioForestalBdd.getId(), numeroNotificacion);
						
			List<InventarioForestalPuntos> inventarioForestalPuntosLista = inventarioForestal.getInventarioForestalPuntosList();			
			inventarioForestal.setInventarioForestalPuntosList(new ArrayList<InventarioForestalPuntos>());
			
			List<EspeciesBajoCategoriaAmenaza> listaEspeciesGuardar = new ArrayList<EspeciesBajoCategoriaAmenaza>();
			List<EspeciesBajoCategoriaAmenaza> listaEspecies = inventarioForestal.getEspeciesBajoCategoriaAmenazaList();
			
			for(EspeciesBajoCategoriaAmenaza especies : listaEspecies){
				
				if(especies.getId() == null){
					especies.setNumeroNotificacion(numeroNotificacion);
					listaEspeciesGuardar.add(especies);
				}else{
					EspeciesBajoCategoriaAmenaza especieBdd = crudServiceBean.find(EspeciesBajoCategoriaAmenaza.class, especies.getId());
					
					if(especieBdd.getCatalogoLibroRojo().equals(especies.getCatalogoLibroRojo()) && especieBdd.getFrecuencia().equals(especies.getFrecuencia()) && 
							((especieBdd.getNombreCientifico() == null && especies.getNombreCientifico() == null) || 
							especieBdd.getNombreCientifico() != null && especieBdd.getNombreCientifico() != null &&
							especieBdd.getNombreCientifico().equals(especies.getNombreCientifico())) && 
							especieBdd.getNombreComun().equals(especies.getNombreComun()) &&
							especieBdd.getUsos().equals(especies.getUsos()) && especieBdd.getOtroNombreCientifico().equals(especies.getOtroNombreCientifico())){
						listaEspeciesGuardar.add(especies);
						continue;
					}else{								
						 List<EspeciesBajoCategoriaAmenaza> listaHistoricoEspecies = obtenerHistorialEspecies(especies.getId(), numeroNotificacion);
						 
						 if(especies.getNumeroNotificacion() == null || (especies.getNumeroNotificacion() != null && especies.getNumeroNotificacion() < numeroNotificacion)){
							 if(listaHistoricoEspecies == null){
								 EspeciesBajoCategoriaAmenaza especieHistorico = especieBdd.clone();
								 especieHistorico.setNumeroNotificacion(numeroNotificacion);
								 especieHistorico.setIdHistorico(especieBdd.getId());
								 listaEspeciesGuardar.add(especieHistorico);
							 }							 
						 }
						 listaEspeciesGuardar.add(especies);
					}					
				}
			}
			
			inventarioForestal.setEspeciesBajoCategoriaAmenazaList(listaEspeciesGuardar);	
			
			
			if(compararInventarioForestal(inventarioForestal, inventarioForestalBdd)) {
				//son iguales
				inventarioForestalPersist = crudServiceBean.saveOrUpdate(inventarioForestal);
			}else{
				//Hubo cambios y no existe almacenado el historico
				if(inventarioForestal.getNumeroNotificacion() == null || (inventarioForestal.getNumeroNotificacion() < numeroNotificacion)){
					if( listaHistorico == null){
						InventarioForestal inventarioForestalHistorico = inventarioForestalBdd.clone();
						inventarioForestalHistorico.setIdHistorico(inventarioForestalBdd.getId());
						inventarioForestalHistorico.setNumeroNotificacion(numeroNotificacion);
						inventarioForestalHistorico.setEspeciesBajoCategoriaAmenazaList(null);
						inventarioForestalHistorico.setInventarioForestalIndiceList(null);
						inventarioForestalHistorico.setInventarioForestalPuntosList(null);
						inventarioForestalHistorico.setInventarioForestalVolumenList(null);
						crudServiceBean.saveOrUpdate(inventarioForestalHistorico);
					}										
				}
				inventarioForestalPersist = crudServiceBean.saveOrUpdate(inventarioForestal);
				//InventarioForestal inventarioForestalPersist = crudServiceBean.saveOrUpdate(inventarioForestal);
			}
						

			for (InventarioForestalPuntos p : inventarioForestalPuntosLista) {
				List<CoordenadaGeneral> coordenadasGenerales = new ArrayList<CoordenadaGeneral>();
				List<CoordenadaGeneral> coordenadasGeneralesBdd = new ArrayList<CoordenadaGeneral>();
				List<CoordenadaGeneral> coordenadasGeneralesGuardar = new ArrayList<CoordenadaGeneral>();
				
				coordenadasGenerales.addAll(p.getCoordenadasGeneral());	
				
				if(p.getId() != null){
					InventarioForestalPuntos pBdd = crudServiceBean.find(InventarioForestalPuntos.class, p.getId());
					coordenadasGeneralesBdd.addAll(pBdd.getCoordenadasGeneral());	
					
					boolean inventarioForestalPuntosGuardado = false;	
					if(coordenadasGeneralesBdd.isEmpty() && coordenadasGenerales.isEmpty()){
						coordenadasGeneralesGuardar = new ArrayList<CoordenadaGeneral>();
					}else if(coordenadasGeneralesBdd.isEmpty() && !coordenadasGenerales.isEmpty()){
						for (CoordenadaGeneral c : coordenadasGenerales) {
							c.setInventarioForestalPuntos(p);
							c.setIdTable(p.getId());
							c.setNombreTabla(InventarioForestalPuntos.class	.getSimpleName());
							c.setNumeroNotificacion(numeroNotificacion);
							crudServiceBean.saveOrUpdate(c);
							
							if(!inventarioForestalPuntosGuardado){									
								guardarInventarioForestalPuntos(p, pBdd, numeroNotificacion);									
								p.setInventarioForestal(inventarioForestalPersist);				
								crudServiceBean.saveOrUpdate(p);									
								inventarioForestalPuntosGuardado = true;
							}
						}
					}else{
						coordenadasGeneralesGuardar = obtenerModificaciones(coordenadasGeneralesBdd, coordenadasGenerales, numeroNotificacion);
					}					
								
					if(!coordenadasGeneralesGuardar.isEmpty()){
						for(CoordenadaGeneral coordenadaGuardar : coordenadasGeneralesGuardar){
							if(coordenadaGuardar.getId() == null){
								coordenadaGuardar.setInventarioForestalPuntos(p);
								coordenadaGuardar.setIdTable(p.getId());
								coordenadaGuardar.setNombreTabla(InventarioForestalPuntos.class	.getSimpleName());
								crudServiceBean.saveOrUpdate(coordenadaGuardar);
								
								if(!inventarioForestalPuntosGuardado){									
									guardarInventarioForestalPuntos(p, pBdd, numeroNotificacion);									
									p.setInventarioForestal(inventarioForestalPersist);				
									crudServiceBean.saveOrUpdate(p);									
									inventarioForestalPuntosGuardado = true;
								}
							}else{
								
								if(!inventarioForestalPuntosGuardado){									
									guardarInventarioForestalPuntos(p, pBdd, numeroNotificacion);									
									p.setInventarioForestal(inventarioForestalPersist);				
									crudServiceBean.saveOrUpdate(p);									
									inventarioForestalPuntosGuardado = true;
								}
								
								coordenadaGuardar.setInventarioForestalPuntos(p);
								coordenadaGuardar.setIdTable(p.getId());
								coordenadaGuardar.setNombreTabla(InventarioForestalPuntos.class	.getSimpleName());
								crudServiceBean.saveOrUpdate(coordenadaGuardar);
							}
						}					
					}
					
					if(!inventarioForestalPuntosGuardado){
						
						if(p.getAreaParcela().equals(pBdd.getAreaParcela()) && p.getCodigoParcela().equals(pBdd.getCodigoParcela()) && 
								p.getIndiceShannon().equals(pBdd.getIndiceShannon()) && p.getIndiceSimpson().equals(pBdd.getIndiceSimpson()) && 
								p.getNombreTablaIndiceValoresImportancia().equals(pBdd.getNombreTablaIndiceValoresImportancia()) && 
								p.getNombreTablaVolumen().equals(pBdd.getNombreTablaVolumen())){
							continue;
						}else{
							List<InventarioForestalPuntos> listaHistoricoPuntos = obtenerHistorialIFP(p.getId(), numeroNotificacion);
							
							if(p.getNumeroNotificacion() == null || (p.getNumeroNotificacion() != null && p.getNumeroNotificacion() < numeroNotificacion)){
								if(listaHistoricoPuntos == null){
									InventarioForestalPuntos inventarioPuntosHistorico = pBdd.clone();
									inventarioPuntosHistorico.setIdHistorico(p.getId());
									inventarioPuntosHistorico.setCoordenadasGeneral(null);
									inventarioPuntosHistorico.setNumeroNotificacion(numeroNotificacion);
									crudServiceBean.saveOrUpdate(inventarioPuntosHistorico);
								}					
							}
							
							p.setInventarioForestal(inventarioForestalPersist);				
							crudServiceBean.saveOrUpdate(p);
						}					
					}
				}else{
					
					p.setInventarioForestal(inventarioForestalPersist);			
					p.setNumeroNotificacion(numeroNotificacion);
					crudServiceBean.saveOrUpdate(p);
					
					for (CoordenadaGeneral c : coordenadasGenerales) {
						c.setInventarioForestalPuntos(p);
						c.setIdTable(p.getId());
						c.setNombreTabla(InventarioForestalPuntos.class	.getSimpleName());
						c.setNumeroNotificacion(numeroNotificacion);
						crudServiceBean.saveOrUpdate(c);
					}
				}				
			}		
			
			for(Object o : listaRemover){
								
				if(o.getClass() == EspeciesBajoCategoriaAmenaza.class){
					
					EspeciesBajoCategoriaAmenaza especiesEliminar = (EspeciesBajoCategoriaAmenaza)o;
					List<EspeciesBajoCategoriaAmenaza> listaHistoricoEsp = obtenerHistorialEspecies(especiesEliminar.getId(), numeroNotificacion);
					
					if(listaHistoricoEsp == null){
						EspeciesBajoCategoriaAmenaza especiesHistorico = especiesEliminar.clone();
						especiesHistorico.setNumeroNotificacion(numeroNotificacion);
						especiesHistorico.setIdHistorico(especiesEliminar.getId());	
						especiesHistorico.setEstado(true);
						crudServiceBean.saveOrUpdate(especiesHistorico);
					}					
				}else if(o.getClass() == CoordenadaGeneral.class){					
					CoordenadaGeneral coordenadaEliminar = (CoordenadaGeneral)o;
					List<CoordenadaGeneral> listaHistoricoCoor = coordenadaGeneralService.coordenadaGeneralPorHistorico(coordenadaEliminar.getId(), numeroNotificacion);
					if(listaHistoricoCoor == null){
						CoordenadaGeneral coordenadaHistorico = coordenadaEliminar.clone();
						coordenadaHistorico.setNumeroNotificacion(numeroNotificacion);
						coordenadaHistorico.setIdHistorico(coordenadaEliminar.getId());
						coordenadaHistorico.setEstado(true);
						crudServiceBean.saveOrUpdate(coordenadaHistorico);
					}					
				}else if(o.getClass() == InventarioForestalPuntos.class){					
					InventarioForestalPuntos inventarioEliminar = (InventarioForestalPuntos)o;
					List<InventarioForestalPuntos> listaHistoricoInv = obtenerHistorialIFP(inventarioEliminar.getId(), numeroNotificacion);
					if(listaHistoricoInv == null){
						InventarioForestalPuntos inventarioHistorico = inventarioEliminar.clone();
						inventarioHistorico.setNumeroNotificacion(numeroNotificacion);
						inventarioHistorico.setIdHistorico(inventarioEliminar.getId());
						inventarioHistorico.setEstado(true);
						crudServiceBean.saveOrUpdate(inventarioHistorico);
					}
				}				
			}
			//crudServiceBean.saveOrUpdate(listaRemover);
			crudServiceBean.delete(listaRemover);
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public void guardarHistorico(InventarioForestal inventarioForestal)
			throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(inventarioForestal);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public void guardarNuevoHistorico(InventarioForestal inventarioForestal,List<CoordenadaGeneral> listaCoordenada,
			Integer numeroNotificacion) throws ServiceException {
		try {
			
			InventarioForestal inventarioForestalPersist = new InventarioForestal();
						
			List<InventarioForestalPuntos> inventarioForestalPuntosLista = inventarioForestal.getInventarioForestalPuntosList();			
			inventarioForestal.setInventarioForestalPuntosList(new ArrayList<InventarioForestalPuntos>());
			
			List<EspeciesBajoCategoriaAmenaza> listaEspeciesGuardar = new ArrayList<EspeciesBajoCategoriaAmenaza>();
			List<EspeciesBajoCategoriaAmenaza> listaEspecies = inventarioForestal.getEspeciesBajoCategoriaAmenazaList();
			
			for(EspeciesBajoCategoriaAmenaza especies : listaEspecies){
				if(especies.getId() == null){
					especies.setNumeroNotificacion(numeroNotificacion);
					listaEspeciesGuardar.add(especies);
				}
			}
			
			inventarioForestal.setEspeciesBajoCategoriaAmenazaList(listaEspeciesGuardar);	
			
			inventarioForestal.setNumeroNotificacion(numeroNotificacion);
			inventarioForestalPersist = crudServiceBean.saveOrUpdate(inventarioForestal);						

			for (InventarioForestalPuntos p : inventarioForestalPuntosLista) {
				List<CoordenadaGeneral> coordenadasGenerales = new ArrayList<CoordenadaGeneral>();
				
				coordenadasGenerales.addAll(p.getCoordenadasGeneral());	
				
				if(p.getId() == null){					
					p.setInventarioForestal(inventarioForestalPersist);			
					p.setNumeroNotificacion(numeroNotificacion);
					crudServiceBean.saveOrUpdate(p);
					
					for (CoordenadaGeneral c : coordenadasGenerales) {
						c.setInventarioForestalPuntos(p);
						c.setIdTable(p.getId());
						c.setNombreTabla(InventarioForestalPuntos.class	.getSimpleName());
						c.setNumeroNotificacion(numeroNotificacion);
						crudServiceBean.saveOrUpdate(c);
					}
				}				
			}		
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public List<InventarioForestal> obtenerHistorial(Integer idHistorial, Integer numeroNotificacion){
		try{
			
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT i FROM InventarioForestal i WHERE i.idHistorico = :idHistorial and i.numeroNotificacion = :notificacion");
			query.setParameter("idHistorial", idHistorial);
			query.setParameter("notificacion", numeroNotificacion);
			
			List<InventarioForestal> resultado = query.getResultList();
			
			if(resultado != null && !resultado.isEmpty())
				return resultado;
			else
				return null;			
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	private boolean compararInventarioForestal(InventarioForestal inventarioForestal, InventarioForestal inventarioForestalBdd){
		
		if(inventarioForestal.getTieneInventarioForestal().equals(inventarioForestalBdd.getTieneInventarioForestal()) &&
				((inventarioForestal.getRemocionVegetal() == null && inventarioForestalBdd.getRemocionVegetal() == null) || 
						inventarioForestal.getRemocionVegetal()	!= null && inventarioForestalBdd.getRemocionVegetal() != null && 
						inventarioForestal.getRemocionVegetal().equals(inventarioForestalBdd.getRemocionVegetal())) &&
				((inventarioForestal.getAreaInversionProyecto() == null && inventarioForestalBdd.getAreaInversionProyecto() == null) || 
						inventarioForestal.getAreaInversionProyecto() != null && inventarioForestalBdd.getAreaInversionProyecto() != null && 
						inventarioForestal.getAreaInversionProyecto().equals(inventarioForestalBdd.getAreaInversionProyecto())) &&
				((inventarioForestal.getAreaPorRemocionDeCoverturaVejetal() == null && inventarioForestalBdd.getAreaPorRemocionDeCoverturaVejetal() == null) ||
						inventarioForestal.getAreaPorRemocionDeCoverturaVejetal() != null && inventarioForestalBdd.getAreaPorRemocionDeCoverturaVejetal() != null &&
						inventarioForestal.getAreaPorRemocionDeCoverturaVejetal().equals(inventarioForestalBdd.getAreaPorRemocionDeCoverturaVejetal())) && 
				((inventarioForestal.getArtesanias() == null && inventarioForestalBdd.getArtesanias() == null) || 
						inventarioForestal.getArtesanias() != null && inventarioForestalBdd.getArtesanias() != null && 
						inventarioForestal.getArtesanias().equals(inventarioForestalBdd.getArtesanias())) && 
				((inventarioForestal.getArtesaniasJustificacion() == null && inventarioForestalBdd.getArtesaniasJustificacion() == null) || 
						inventarioForestal.getArtesaniasJustificacion() != null && inventarioForestalBdd.getArtesaniasJustificacion() != null && 
						inventarioForestal.getArtesaniasJustificacion().equals(inventarioForestalBdd.getArtesaniasJustificacion())) && 
				((inventarioForestal.getCapturaDeCarbonJustificacion() == null && inventarioForestalBdd.getCapturaDeCarbonJustificacion() == null) || 
						inventarioForestal.getCapturaDeCarbonJustificacion() != null && inventarioForestalBdd.getCapturaDeCarbonJustificacion() != null && 
						inventarioForestal.getCapturaDeCarbonJustificacion().equals(inventarioForestalBdd.getCapturaDeCarbonJustificacion())) && 
				((inventarioForestal.getCapturaDeCarbono() == null && inventarioForestalBdd.getCapturaDeCarbono() == null) || 
						inventarioForestal.getCapturaDeCarbono() != null && inventarioForestalBdd.getCapturaDeCarbono() != null && 
						inventarioForestal.getCapturaDeCarbono().equals(inventarioForestalBdd.getCapturaDeCarbono())) && 
				((inventarioForestal.getJustificacionEspeciesBajoCategoriaAmenaza() == null && inventarioForestalBdd.getJustificacionEspeciesBajoCategoriaAmenaza() == null) || 
						inventarioForestal.getJustificacionEspeciesBajoCategoriaAmenaza() != null && inventarioForestalBdd.getJustificacionEspeciesBajoCategoriaAmenaza() != null && 
						inventarioForestal.getJustificacionEspeciesBajoCategoriaAmenaza().equals(inventarioForestalBdd.getJustificacionEspeciesBajoCategoriaAmenaza())) && 
				((inventarioForestal.getPlantasMedicinales() == null && inventarioForestalBdd.getPlantasMedicinales() == null) || 
						inventarioForestal.getPlantasMedicinales() != null && inventarioForestalBdd.getPlantasMedicinales() != null && 
						inventarioForestal.getPlantasMedicinales().equals(inventarioForestalBdd.getPlantasMedicinales())) && 
				((inventarioForestal.getPlantasMedicinalesJustificacion() == null && inventarioForestalBdd.getPlantasMedicinalesJustificacion() == null) || 
						inventarioForestal.getPlantasMedicinalesJustificacion() != null && inventarioForestalBdd.getPlantasMedicinalesJustificacion() != null && 
						inventarioForestal.getPlantasMedicinalesJustificacion().equals(inventarioForestalBdd.getPlantasMedicinalesJustificacion())) && 
				((inventarioForestal.getPlantasOrnamentales() == null && inventarioForestalBdd.getPlantasOrnamentales() == null) || 
						inventarioForestal.getPlantasOrnamentales() != null && inventarioForestalBdd.getPlantasOrnamentales() != null && 
						inventarioForestal.getPlantasOrnamentales().equals(inventarioForestalBdd.getPlantasOrnamentales())) && 
				((inventarioForestal.getPlantasOrnamentalesJustificacion() == null && inventarioForestalBdd.getPlantasOrnamentalesJustificacion() == null) || 
						inventarioForestal.getPlantasOrnamentalesJustificacion() != null && inventarioForestalBdd.getPlantasOrnamentalesJustificacion() != null && 
						inventarioForestal.getPlantasOrnamentalesJustificacion().equals(inventarioForestalBdd.getPlantasOrnamentalesJustificacion())) && 
				((inventarioForestal.getProductosForestalesMaderablesYNoMaderables() == null && inventarioForestalBdd.getProductosForestalesMaderablesYNoMaderables() == null) || 
						inventarioForestal.getProductosForestalesMaderablesYNoMaderables() != null && inventarioForestalBdd.getProductosForestalesMaderablesYNoMaderables() != null && 
						inventarioForestal.getProductosForestalesMaderablesYNoMaderables().equals(inventarioForestalBdd.getProductosForestalesMaderablesYNoMaderables())) && 
				((inventarioForestal.getProductosForestalesMaderablesYNoMaderablesJustificacion() == null && inventarioForestalBdd.getProductosForestalesMaderablesYNoMaderablesJustificacion() == null) || 
						inventarioForestal.getProductosForestalesMaderablesYNoMaderablesJustificacion() != null && inventarioForestalBdd.getProductosForestalesMaderablesYNoMaderablesJustificacion() != null && 
						inventarioForestal.getProductosForestalesMaderablesYNoMaderablesJustificacion().equals(inventarioForestalBdd.getProductosForestalesMaderablesYNoMaderablesJustificacion())) && 
				((inventarioForestal.getPromedioAb() == null && inventarioForestalBdd.getPromedioAb() == null) || 
						inventarioForestal.getPromedioAb() != null && inventarioForestalBdd.getPromedioAb() != null && 
						inventarioForestal.getPromedioAb().equals(inventarioForestalBdd.getPromedioAb())) && 
				((inventarioForestal.getRecursoAgua() == null && inventarioForestalBdd.getRecursoAgua() == null) || 
						inventarioForestal.getRecursoAgua() != null && inventarioForestalBdd.getRecursoAgua() != null && 
						inventarioForestal.getRecursoAgua().equals(inventarioForestalBdd.getRecursoAgua())) && 
				((inventarioForestal.getRecursoAguaJustificacion() == null && inventarioForestalBdd.getRecursoAguaJustificacion() == null) || 
						inventarioForestal.getRecursoAguaJustificacion() != null && inventarioForestalBdd.getRecursoAguaJustificacion() != null && 
						inventarioForestal.getRecursoAguaJustificacion().equals(inventarioForestalBdd.getRecursoAguaJustificacion())) && 
				((inventarioForestal.getSuperficieMuestreo() == null && inventarioForestalBdd.getSuperficieMuestreo() == null) || 
						inventarioForestal.getSuperficieMuestreo() != null && inventarioForestalBdd.getSuperficieMuestreo() != null && 
						inventarioForestal.getSuperficieMuestreo().equals(inventarioForestalBdd.getSuperficieMuestreo())) && 
				((inventarioForestal.getTurismoBellezaEscenica() == null && inventarioForestalBdd.getTurismoBellezaEscenica() == null) ||
						inventarioForestal.getTurismoBellezaEscenica() != null && inventarioForestalBdd.getTurismoBellezaEscenica() != null && 
						inventarioForestal.getTurismoBellezaEscenica().equals(inventarioForestalBdd.getTurismoBellezaEscenica())) &&
				((inventarioForestal.getTurismoBellezaEscenicaJustificacion() == null && inventarioForestalBdd.getTurismoBellezaEscenicaJustificacion() == null) || 
						inventarioForestal.getTurismoBellezaEscenicaJustificacion() != null && inventarioForestalBdd.getTurismoBellezaEscenicaJustificacion() != null && 
						inventarioForestal.getTurismoBellezaEscenicaJustificacion().equals(inventarioForestalBdd.getTurismoBellezaEscenicaJustificacion())) && 
				((inventarioForestal.getVolumenComercial() == null && inventarioForestalBdd.getVolumenComercial() == null) || 
						inventarioForestal.getVolumenComercial() != null && inventarioForestalBdd.getVolumenComercial() != null && 
						inventarioForestal.getVolumenComercial().equals(inventarioForestalBdd.getVolumenComercial())) && 
				((inventarioForestal.getVolumenComercialExtrapolado() == null && inventarioForestalBdd.getVolumenComercialExtrapolado() == null) || 
						inventarioForestal.getVolumenComercialExtrapolado() != null && inventarioForestalBdd.getVolumenComercialExtrapolado() != null && 
						inventarioForestal.getVolumenComercialExtrapolado().equals(inventarioForestalBdd.getVolumenComercialExtrapolado())) && 
				((inventarioForestal.getVolumenComercialPromedio() == null && inventarioForestalBdd.getVolumenComercialPromedio() == null) ||
						inventarioForestal.getVolumenComercialPromedio() != null && inventarioForestalBdd.getVolumenComercialPromedio() != null && 
						inventarioForestal.getVolumenComercialPromedio().equals(inventarioForestalBdd.getVolumenComercialPromedio())) && 
				((inventarioForestal.getVolumenTotal() == null && inventarioForestalBdd.getVolumenTotal() == null) || 
						inventarioForestal.getVolumenTotal() != null && inventarioForestalBdd.getVolumenTotal() != null && 
						inventarioForestal.getVolumenTotal().equals(inventarioForestalBdd.getVolumenTotal())) && 
				((inventarioForestal.getVolumenTotalExtrapolado() == null && inventarioForestalBdd.getVolumenTotalExtrapolado() == null) ||
						inventarioForestal.getVolumenTotalExtrapolado() != null && inventarioForestalBdd.getVolumenTotalExtrapolado() != null &&
						inventarioForestal.getVolumenTotalExtrapolado().equals(inventarioForestalBdd.getVolumenTotalExtrapolado())) && 
				((inventarioForestal.getVolumenTotalPromedio() == null && inventarioForestalBdd.getVolumenTotalPromedio() == null) ||
						inventarioForestal.getVolumenTotalPromedio() != null && inventarioForestalBdd.getVolumenTotalPromedio() != null && 
						inventarioForestal.getVolumenTotalPromedio().equals(inventarioForestalBdd.getVolumenTotalPromedio()))
				) {
			//son iguales
			return true;
		}else{
			return false;
		}
	}
	
	private List<CoordenadaGeneral> obtenerModificaciones(List<CoordenadaGeneral> coordenadasGeneralesBdd, 
			List<CoordenadaGeneral> coordenadasGenerales, Integer numeroNotificacion){
		try{
			List<CoordenadaGeneral> listaCoordenadasMod = new ArrayList<CoordenadaGeneral>();	
			
			for(CoordenadaGeneral coordenadaNueva : coordenadasGenerales){
				
				Comparator<CoordenadaGeneral> c = new Comparator<CoordenadaGeneral>(){

					@Override
					public int compare(CoordenadaGeneral c1,CoordenadaGeneral c2) {
						return c1.getId().compareTo(c2.getId());
					}				
				};
							
				Collections.sort(coordenadasGeneralesBdd, c);
				
				if(coordenadaNueva.getId() != null){
					int index = Collections.binarySearch(coordenadasGeneralesBdd, new CoordenadaGeneral(coordenadaNueva.getId()), c);
					
					if(index >= 0){
						CoordenadaGeneral coordenadaEncontrada = coordenadasGeneralesBdd.get(index);
						
						if(coordenadaEncontrada.getX().equals(coordenadaNueva.getX()) && 
								coordenadaEncontrada.getY().equals(coordenadaNueva.getY())){
							continue;
						}else{
							if(coordenadaNueva.getNumeroNotificacion() != null && coordenadaNueva.getNumeroNotificacion() == numeroNotificacion){
								listaCoordenadasMod.add(coordenadaNueva);								
							}else{
								List<CoordenadaGeneral> listaCoordenadaHistorico = coordenadaGeneralService.coordenadaGeneralPorHistorico(coordenadaEncontrada.getId(), numeroNotificacion);
								
								if(listaCoordenadaHistorico == null){
									CoordenadaGeneral coordenadaHistorico = coordenadaEncontrada.clone();						
									coordenadaHistorico.setIdHistorico(coordenadaEncontrada.getId());
									coordenadaHistorico.setNumeroNotificacion(numeroNotificacion);
									listaCoordenadasMod.add(coordenadaHistorico);
								}
								
								listaCoordenadasMod.add(coordenadaNueva);
							}
						}				
					}else{
						coordenadaNueva.setNumeroNotificacion(numeroNotificacion);
						listaCoordenadasMod.add(coordenadaNueva);
					}				
				}else{
					coordenadaNueva.setNumeroNotificacion(numeroNotificacion);
					listaCoordenadasMod.add(coordenadaNueva);
				}
			}
			return listaCoordenadasMod;		
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<InventarioForestalPuntos> obtenerHistorialIFP(Integer idHistorial, Integer numeroNotificacion){
		try{
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT i FROM InventarioForestalPuntos i WHERE i.idHistorico = :idHistorial and "
					+ "i.numeroNotificacion = :numeroNotificacion");
			query.setParameter("idHistorial", idHistorial);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<InventarioForestalPuntos> resultado = query.getResultList();
			
			if(resultado != null && !resultado.isEmpty())
				return resultado;
			else
				return null;			
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	public List<EspeciesBajoCategoriaAmenaza> obtenerHistorialEspecies(Integer idHistorial, Integer numeroNotificacion){
		try{
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT i FROM EspeciesBajoCategoriaAmenaza i WHERE i.idHistorico = :idHistorial and "
					+ "i.numeroNotificacion = :numeroNotificacion");
			query.setParameter("idHistorial", idHistorial);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			List<EspeciesBajoCategoriaAmenaza> resultado = query.getResultList();
			
			if(resultado != null && !resultado.isEmpty())
				return resultado;
			else
				return null;			
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	
	private void guardarInventarioForestalPuntos(InventarioForestalPuntos p, InventarioForestalPuntos pBdd, Integer numeroNotificacion){
		
		try {
			List<InventarioForestalPuntos> listaHistoricoPuntos = obtenerHistorialIFP(p.getId(), numeroNotificacion);
			if(listaHistoricoPuntos == null){
				InventarioForestalPuntos inventarioPuntosHistorico = new InventarioForestalPuntos();			
				if(p.getNumeroNotificacion() == null || (p.getNumeroNotificacion() != null && p.getNumeroNotificacion() < numeroNotificacion)){
					inventarioPuntosHistorico = pBdd.clone();
					inventarioPuntosHistorico.setIdHistorico(pBdd.getId());
					inventarioPuntosHistorico.setCoordenadasGeneral(null);
					inventarioPuntosHistorico.setNumeroNotificacion(numeroNotificacion);
					crudServiceBean.saveOrUpdate(inventarioPuntosHistorico);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}
	
	/**
	 * Busca coordenadas existentes en bdd por tabla 
	 * @param nombreTabla
	 * @param idTabla
	 * @return
	 * @throws ServiceException
	 */
	public List<CoordenadaGeneral> listarCoordenadaGeneralEnBdd(String nombreTabla,
			Integer idTabla) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nombreTabla", nombreTabla);
		params.put("idTabla", idTabla);
		return (List<CoordenadaGeneral>) crudServiceBean.findByNamedQuery(
				CoordenadaGeneral.LISTAR_TODOS_POR_ID_NOMBRE_TABLA, params);
	}
	
	public InventarioForestal obtenerInventarioForestalEnBdd(
			EstudioImpactoAmbiental estudioImpactoAmbiental)
			throws ServiceException {

		InventarioForestal iForestal = obtenerPorEia(estudioImpactoAmbiental);
		if (iForestal == null) {
			return new InventarioForestal();
		} else {
			iForestal
					.setInventarioForestalPuntosList(listarInventarioForestalPuntosEnBdd(iForestal));
			iForestal
					.setEspeciesBajoCategoriaAmenazaList(listarEspeciesBajoCategoriaAmenazas(iForestal));

			return iForestal;
		}
	}
	
	public List<InventarioForestalPuntos> listarInventarioForestalPuntosEnBdd(
			InventarioForestal inventarioForestal) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("inventarioForestal", inventarioForestal);

		List<InventarioForestalPuntos> result = new ArrayList<InventarioForestalPuntos>();
		List<InventarioForestalPuntos> inventarioForestalPuntosLista = (List<InventarioForestalPuntos>) crudServiceBean
				.findByNamedQuery(
						InventarioForestalPuntos.LISTAR_POR_INVENTARIO, params);
		for (InventarioForestalPuntos inventarioForestalPuntos : inventarioForestalPuntosLista) {
			inventarioForestalPuntos
					.setCoordenadasGeneral(listarCoordenadaGeneralEnBdd(
							InventarioForestalPuntos.class.getSimpleName(),
							inventarioForestalPuntos.getId()));
			result.add(inventarioForestalPuntos);
		}

		return result;
	}
}