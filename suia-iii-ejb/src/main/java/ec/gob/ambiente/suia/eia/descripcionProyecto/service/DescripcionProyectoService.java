package ec.gob.ambiente.suia.eia.descripcionProyecto.service;

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

import ec.gob.ambiente.suia.coordenadageneral.service.CoordenadaGeneralService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class DescripcionProyectoService implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -2618641391920131523L;

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ActividadLicenciamientoService actividadLicenciamientoService;
	@EJB
	private CoordenadaGeneralService coordenadaGeneralService;
	@EJB
	private CronogramaFasesProyectoService cronogramaFasesProyectoService;


	public void guardarDescripcionProyecto(EstudioImpactoAmbiental estudio,
			List<ActividadLicenciamiento> actividadesLicenciamiento,
			List<ActividadLicenciamiento> actividadesLicenciamientoEliminar,
		    List<ActividadesPorEtapa> actividadesPorEtapas,
		    List<ActividadesPorEtapa> actividadesPorEtapasEliminar,
			List<CoordenadaGeneral> listaCoordenadaGeneralesEliminar,
			List<SustanciaQuimicaEia> sustanciaQuimicaEiaEliminar,
			List<CronogramaFasesProyectoEia> cronogramaEliminar)
			throws Exception {
		if (!actividadesLicenciamiento.isEmpty()) {
			for (ActividadLicenciamiento actividadLicenciamiento : actividadesLicenciamiento) {
				actividadLicenciamiento.setEstudioImpactoAmbiental(estudio);
				actividadLicenciamiento.setFechaCreacion(new Date());
				List<CoordenadaGeneral> coordenadasGenerales = new ArrayList<CoordenadaGeneral>();
				coordenadasGenerales.addAll(actividadLicenciamiento.getCoordenadasGeneral());
				crudServiceBean.saveOrUpdate(actividadLicenciamiento);
				if (!coordenadasGenerales.isEmpty()) {
					for (CoordenadaGeneral coordenadaGeneral : coordenadasGenerales) {
						coordenadaGeneral.setActividadLicenciamiento(actividadLicenciamiento);
						coordenadaGeneral.setIdTable(actividadLicenciamiento.getId());
						coordenadaGeneral.setNombreTabla("actividadLicenciamiento");
						crudServiceBean.saveOrUpdate(coordenadaGeneral);
					}
				}
			}
		}
		if(!actividadesPorEtapas.isEmpty()){
			for(ActividadesPorEtapa actE : actividadesPorEtapas){
				actE.setEstudioImpactoAmbiental(estudio);
				crudServiceBean.saveOrUpdate(actividadesPorEtapas);
			}
		}
		crudServiceBean.delete(actividadesLicenciamientoEliminar);
		crudServiceBean.delete(listaCoordenadaGeneralesEliminar);
		crudServiceBean.delete(sustanciaQuimicaEiaEliminar);
		crudServiceBean.delete(actividadesPorEtapasEliminar);
		crudServiceBean.delete(cronogramaEliminar);
	}



	public boolean isActividadEnUso(ActividadLicenciamiento actividad) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("paramActividad",actividad);
		List<EvaluacionAspectoAmbiental> evaluacionesDependientes = (List<EvaluacionAspectoAmbiental>)crudServiceBean.findByNamedQuery(EvaluacionAspectoAmbiental.FIND_BY_ACTIVIDAD,params);
		return !evaluacionesDependientes.isEmpty();


	}

	public List<EtapasProyecto> getEtapas(Integer idFase, Integer idzona) throws ServiceException {
		List<EtapasProyecto> etapasFase = (List<EtapasProyecto>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select zpf.etapasProyectos From ZonaPorFase zpf where zpf.fase.id =:idFase and zpf.zonaPorFase.id =:idzona ")
				.setParameter("idFase", idFase).setParameter("idzona", idzona).getResultList();

		if (etapasFase != null && !etapasFase.isEmpty()) {
			return etapasFase;
		} else {
			throw new ServiceException("Error al recuperar las etapas de la fase y zona.");
		}
	}
	
	/**
	 * CF: Metodo para guardar el historico
	 * @param estudio
	 * @param estudioHistorico
	 * @param actividadesLicenciamiento
	 * @param actividadesLicenciamientoEliminar
	 * @param actividadesPorEtapas
	 * @param actividadesPorEtapasEliminar
	 * @param listaCoordenadaGeneralesEliminar
	 * @param sustanciaQuimicaEiaEliminar
	 * @throws Exception
	 */
	public void guardarDescripcionProyectoHistorico(EstudioImpactoAmbiental estudio,
			EstudioImpactoAmbiental estudioHistorico,
			List<ActividadLicenciamiento> actividadesLicenciamiento,
			List<ActividadLicenciamiento> actividadesLicenciamientoEliminar,
		    List<ActividadesPorEtapa> actividadesPorEtapas,
		    List<ActividadesPorEtapa> actividadesPorEtapasEliminar,
			List<CoordenadaGeneral> listaCoordenadaGeneralesEliminar,
			List<SustanciaQuimicaEia> sustanciaQuimicaEiaEliminar,
			Integer numeroNotificacion, List<CronogramaFasesProyectoEia> cronogramaFasesEliminar)
			throws Exception {
		
		if (!actividadesLicenciamiento.isEmpty()) {
			for (ActividadLicenciamiento actividadLicenciamiento : actividadesLicenciamiento) {
				
				if(actividadLicenciamiento.getId() != null){
					List<CoordenadaGeneral> listaCoordenadasGuardar = new ArrayList<CoordenadaGeneral>();
					ActividadLicenciamiento actividadLicenciamientoBdd = crudServiceBean.find(ActividadLicenciamiento.class, actividadLicenciamiento.getId());
					
					List<CoordenadaGeneral> coordenadasGeneralesBdd = new ArrayList<CoordenadaGeneral>();
					List<CoordenadaGeneral> coordenadasGenerales = new ArrayList<CoordenadaGeneral>();
					
					coordenadasGeneralesBdd.addAll(actividadLicenciamientoBdd.getCoordenadasGeneral());					
					coordenadasGenerales.addAll(actividadLicenciamiento.getCoordenadasGeneral());
					
					//tres casos si no tienen elementos, si solo lo tiene una, si las dos tienen elementos
					//si las dos tienen elementos entonces se debe ver si cambio o se aumento elementos
					Boolean actividadGuardada = false;
					
					if(coordenadasGeneralesBdd.isEmpty() && coordenadasGenerales.isEmpty()){
						//no se guarda
						listaCoordenadasGuardar = new ArrayList<CoordenadaGeneral>();
					}else if(coordenadasGeneralesBdd.isEmpty() && !coordenadasGenerales.isEmpty()){
						for(CoordenadaGeneral coordenada : coordenadasGenerales){
							coordenada.setActividadLicenciamiento(actividadLicenciamiento);
							coordenada.setIdTable(actividadLicenciamiento.getId());
							coordenada.setNombreTabla("actividadLicenciamiento");
							coordenada.setNumeroNotificacion(numeroNotificacion);
							crudServiceBean.saveOrUpdate(coordenada);
							
							if(!actividadGuardada){								
								guardarActividadPorCoordenadasModificadas(actividadLicenciamiento, actividadLicenciamientoBdd, numeroNotificacion, estudio);								
								actividadLicenciamiento.setEstudioImpactoAmbiental(estudio);
								actividadLicenciamiento.setFechaCreacion(new Date());
								crudServiceBean.saveOrUpdate(actividadLicenciamiento);								
								actividadGuardada = true;
							}							
						}
					}else if(!coordenadasGeneralesBdd.isEmpty() && !coordenadasGenerales.isEmpty()){
						listaCoordenadasGuardar = obtenerModificaciones(coordenadasGeneralesBdd, coordenadasGenerales, numeroNotificacion);					
					}
					
					
					if(!listaCoordenadasGuardar.isEmpty()){
						ActividadLicenciamiento actividadLicHistorico = new ActividadLicenciamiento();
						for(CoordenadaGeneral coordenadaGuardar : listaCoordenadasGuardar){
							if(coordenadaGuardar.getIdHistorico() == null){
								coordenadaGuardar.setActividadLicenciamiento(actividadLicenciamiento);
								coordenadaGuardar.setIdTable(actividadLicenciamiento.getId());
								coordenadaGuardar.setNombreTabla("actividadLicenciamiento");
								crudServiceBean.saveOrUpdate(coordenadaGuardar);
								
								if(!actividadGuardada){
									guardarActividadPorCoordenadasModificadas(actividadLicenciamiento, actividadLicenciamientoBdd, numeroNotificacion, estudio);	
									actividadLicenciamiento.setEstudioImpactoAmbiental(estudio);
									crudServiceBean.saveOrUpdate(actividadLicenciamiento);								
									actividadGuardada = true;								
								}
								
							}else{
								
								if(!actividadGuardada){
									
									List<ActividadLicenciamiento> actividadHistoricoList = actividadLicenciamientoService.actividadesLicenciamientoHistorico(actividadLicenciamientoBdd.getId(), numeroNotificacion);
									
									if(actividadHistoricoList == null){
										actividadLicHistorico = actividadLicenciamientoBdd.clone();
										actividadLicHistorico.setEstudioImpactoAmbiental(estudio);
										actividadLicHistorico.setIdHistorico(actividadLicenciamientoBdd.getId());
										actividadLicHistorico.setNumeroNotificacion(numeroNotificacion);
										actividadLicHistorico.setCoordenadasGeneral(null);
										actividadLicHistorico.setEvaluacionAspectoAmbientales(null);
										crudServiceBean.saveOrUpdate(actividadLicHistorico);
									}
									
									actividadLicenciamiento.setEstudioImpactoAmbiental(estudio);
									crudServiceBean.saveOrUpdate(actividadLicenciamiento);
									
									actividadGuardada = true;
								}							
								
								coordenadaGuardar.setActividadLicenciamiento(actividadLicenciamiento);
								coordenadaGuardar.setIdTable(actividadLicenciamiento.getId());
								coordenadaGuardar.setNombreTabla("actividadLicenciamiento");
								crudServiceBean.saveOrUpdate(coordenadaGuardar);								
							}
						}
					}
					
					if(!actividadGuardada){
						if(actividadLicenciamiento.getNombreActividad().equals(actividadLicenciamientoBdd.getNombreActividad()) && 
								actividadLicenciamiento.getCatalogoCategoriaFase().equals(actividadLicenciamientoBdd.getCatalogoCategoriaFase())){
							continue;
						}else{
							
							List<ActividadLicenciamiento> actividadHistoricoList = actividadLicenciamientoService.actividadesLicenciamientoHistorico(actividadLicenciamientoBdd.getId(), numeroNotificacion);
							
							if(actividadLicenciamiento.getNumeroNotificacion() == null || 
									(actividadLicenciamiento.getNumeroNotificacion() != null && actividadLicenciamiento.getNumeroNotificacion() < numeroNotificacion)){
								
								if(actividadHistoricoList == null){
									ActividadLicenciamiento actividadHistorico = actividadLicenciamientoBdd.clone();														
									actividadHistorico.setEstudioImpactoAmbiental(estudio);
									actividadHistorico.setIdHistorico(actividadLicenciamientoBdd.getId());
									actividadHistorico.setNumeroNotificacion(numeroNotificacion);
									actividadHistorico.setCoordenadasGeneral(null);
									actividadHistorico.setEvaluacionAspectoAmbientales(null);
									crudServiceBean.saveOrUpdate(actividadHistorico);
								}								
							}
							
							actividadLicenciamiento.setEstudioImpactoAmbiental(estudio);
							crudServiceBean.saveOrUpdate(actividadLicenciamiento);
						}							
					}					
				}else{
								
					actividadLicenciamiento.setEstudioImpactoAmbiental(estudio);
					List<CoordenadaGeneral> coordenadasGenerales = new ArrayList<CoordenadaGeneral>();
					coordenadasGenerales.addAll(actividadLicenciamiento.getCoordenadasGeneral());
					actividadLicenciamiento.setNumeroNotificacion(numeroNotificacion);
					crudServiceBean.saveOrUpdate(actividadLicenciamiento);
					if (!coordenadasGenerales.isEmpty()) {
						for (CoordenadaGeneral coordenadaGeneral : coordenadasGenerales) {
							coordenadaGeneral.setActividadLicenciamiento(actividadLicenciamiento);
							coordenadaGeneral.setIdTable(actividadLicenciamiento.getId());
							coordenadaGeneral.setNombreTabla("actividadLicenciamiento");
							crudServiceBean.saveOrUpdate(coordenadaGeneral);
						}
					}
				}	
				
			}//fin de for de actividades
		}
		if(!actividadesPorEtapas.isEmpty()){
			for(ActividadesPorEtapa actE : actividadesPorEtapas){
				actE.setEstudioImpactoAmbiental(estudio);
				crudServiceBean.saveOrUpdate(actividadesPorEtapas);
			}
		}
		
		if(actividadesLicenciamientoEliminar != null && !actividadesLicenciamientoEliminar.isEmpty()){
			for(ActividadLicenciamiento actividadEliminar : actividadesLicenciamientoEliminar){
				
				List<ActividadLicenciamiento> listaActividadesHistorico = actividadLicenciamientoService.actividadesLicenciamientoHistorico(actividadEliminar.getId(), numeroNotificacion);
				
				if(actividadEliminar.getNumeroNotificacion() == null || (actividadEliminar.getNumeroNotificacion() != null && 
						actividadEliminar.getNumeroNotificacion() < numeroNotificacion)){
					if(listaActividadesHistorico == null){
						ActividadLicenciamiento actividadHistorico = actividadEliminar.clone();														
						actividadHistorico.setEstudioImpactoAmbiental(estudio);
						actividadHistorico.setIdHistorico(actividadEliminar.getId());
						actividadHistorico.setNumeroNotificacion(numeroNotificacion);
						actividadHistorico.setCoordenadasGeneral(null);
						actividadHistorico.setEvaluacionAspectoAmbientales(null);
						crudServiceBean.saveOrUpdate(actividadHistorico);	
					}
				}			
				
				crudServiceBean.delete(actividadEliminar);
			}
		}
		
		if(listaCoordenadaGeneralesEliminar != null && !listaCoordenadaGeneralesEliminar.isEmpty()){
			for(CoordenadaGeneral coordenadaEliminar : listaCoordenadaGeneralesEliminar){
				
				List<CoordenadaGeneral> listaCoordenadaHistorico = coordenadaGeneralService.coordenadaGeneralPorHistorico(coordenadaEliminar.getId(), numeroNotificacion);
				
				if(coordenadaEliminar.getNumeroNotificacion() == null || 
						(coordenadaEliminar.getNumeroNotificacion() != null && coordenadaEliminar.getNumeroNotificacion() < numeroNotificacion)){
					if(listaCoordenadaHistorico == null){
						CoordenadaGeneral coordenadaHistorico = coordenadaEliminar.clone();						
						coordenadaHistorico.setIdHistorico(coordenadaEliminar.getId());
						coordenadaHistorico.setNumeroNotificacion(numeroNotificacion);
						crudServiceBean.saveOrUpdate(coordenadaHistorico);	
					}
				}
				crudServiceBean.delete(coordenadaEliminar);
			}
			
		}
		
		if(sustanciaQuimicaEiaEliminar != null && !sustanciaQuimicaEiaEliminar.isEmpty()){
			for(SustanciaQuimicaEia sustanciaEliminar : sustanciaQuimicaEiaEliminar){
				
				if(sustanciaEliminar.getNumeroNotificacion() == null || 
						(sustanciaEliminar.getNumeroNotificacion() != null && sustanciaEliminar.getNumeroNotificacion() < numeroNotificacion)){
					SustanciaQuimicaEia sustanciaHistorico = sustanciaEliminar.clone();
					sustanciaHistorico.setIdHistorico(sustanciaEliminar.getId());
					sustanciaHistorico.setNumeroNotificacion(numeroNotificacion);
					crudServiceBean.saveOrUpdate(sustanciaHistorico);
				}				
				crudServiceBean.delete(sustanciaEliminar);
			}			
		}
		
		if(cronogramaFasesEliminar != null && !cronogramaFasesEliminar.isEmpty()){
			for(CronogramaFasesProyectoEia cronogramaEliminar : cronogramaFasesEliminar){
				
				if(cronogramaEliminar.getNumeroNotificacion() == null || 
						(cronogramaEliminar.getNumeroNotificacion() != null && cronogramaEliminar.getNumeroNotificacion() < numeroNotificacion)){
					List<CronogramaFasesProyectoEia> cronogramaHistoricoList = cronogramaFasesProyectoService.obtenerCronogramaFasesPorHistorico(cronogramaEliminar.getId(), numeroNotificacion); 
					
					if(cronogramaHistoricoList == null || cronogramaHistoricoList.isEmpty()){
						CronogramaFasesProyectoEia cronogramaFasesHistorico = cronogramaEliminar.clone();
						cronogramaFasesHistorico.setIdHistorico(cronogramaEliminar.getId());
						cronogramaFasesHistorico.setNumeroNotificacion(numeroNotificacion);
						cronogramaFasesProyectoService.guardarCronogramaFasesProyectoProyecto(cronogramaFasesHistorico);
					}
				}
				crudServiceBean.delete(cronogramaEliminar);
			}
		}		
				
		
		//crudServiceBean.delete(actividadesLicenciamientoEliminar);
		//crudServiceBean.delete(listaCoordenadaGeneralesEliminar);
		//crudServiceBean.delete(sustanciaQuimicaEiaEliminar);
		crudServiceBean.delete(actividadesPorEtapasEliminar);
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
								coordenadaEncontrada.getY().equals(coordenadaNueva.getY()) &&
								coordenadaEncontrada.getDescripcion().equals(coordenadaNueva.getDescripcion())){
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
	
	private void guardarActividadPorCoordenadasModificadas(ActividadLicenciamiento actividadLicenciamiento, ActividadLicenciamiento actividadLicenciamientoBdd, Integer numeroNotificacion, EstudioImpactoAmbiental estudio){
		try {
			ActividadLicenciamiento actividadLicHistorico = new ActividadLicenciamiento();
			List<ActividadLicenciamiento> actividadHistoricoList = actividadLicenciamientoService.actividadesLicenciamientoHistorico(actividadLicenciamiento.getId(), numeroNotificacion);
			
			if(actividadHistoricoList == null){
				if(actividadLicenciamiento.getNumeroNotificacion() == null || 
						(actividadLicenciamiento.getNumeroNotificacion() != null && actividadLicenciamiento.getNumeroNotificacion() < numeroNotificacion)){
					
				actividadLicHistorico = actividadLicenciamientoBdd.clone();
				actividadLicHistorico.setEstudioImpactoAmbiental(estudio);
				actividadLicHistorico.setIdHistorico(actividadLicenciamientoBdd.getId());
				actividadLicHistorico.setNumeroNotificacion(numeroNotificacion);
				actividadLicHistorico.setCoordenadasGeneral(null);
				actividadLicHistorico.setEvaluacionAspectoAmbientales(null);
				crudServiceBean.saveOrUpdate(actividadLicHistorico);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
