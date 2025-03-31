package ec.gob.ambiente.suia.fichatecnica.facade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.coordenadageneral.service.CoordenadaGeneralService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadLicenciamiento;
import ec.gob.ambiente.suia.domain.ConsultorNoCalificado;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FormaCoordenadasEIA;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.ficha.facade.ConsultorNoCalificadoFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class FichaTecnicaFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2725196055341054376L;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	
	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;
	
	@EJB
	private ConsultorNoCalificadoFacade consultorNoCalificadoFacade;
	
	@EJB
	private CoordenadaGeneralService coordenadaGeneralService;
	
	/**
	 * Método para guardar ficha técnica en estudio de impacto ambiental para otros sectores
	 * @param estudio
	 * @param consultorNoCalificados
	 * @param consultorNoCalificadosBdd
	 * @throws Exception
	 */
	public void guardar(EstudioImpactoAmbiental estudio, List<ConsultorNoCalificado> consultorNoCalificados, 
			List<ConsultorNoCalificado> consultorNoCalificadosBdd, List<FormaCoordenadasEIA> listaFormas) throws Exception{
		crudServiceBean.saveOrUpdate(listaFormas);
		if (!consultorNoCalificados.isEmpty() && consultorNoCalificados!=null) {
			crudServiceBean.saveOrUpdate(estudio);
			for (ConsultorNoCalificado consultorNoCalificado : consultorNoCalificados) {
				consultorNoCalificado.setEstudioImpactoAmbiental(estudio);
				crudServiceBean.saveOrUpdate(consultorNoCalificado);
			}
		}
		crudServiceBean.delete(consultorNoCalificadosBdd);
	}
	
	
	/**
	 * Método para almacenar ficha Técnica en estudio de impacto ambiental para mineria
	 * @param estudio
	 * @param consultoresNoCalificados
	 * @param consultoresNoCalificadosEliminar
	 * @param listaCoordenadaGenerales
	 * @param listaCoordenadaGeneralesEliminar
	 * @throws Exception
	 */
	public void guardar(EstudioImpactoAmbiental estudio, List<ConsultorNoCalificado> consultoresNoCalificados,
			List<ConsultorNoCalificado> consultoresNoCalificadosEliminar,
			List<CoordenadaGeneral> listaCoordenadaGenerales, List<CoordenadaGeneral> listaCoordenadaGeneralesEliminar,
			List<FormaCoordenadasEIA> listaFormas)
			throws Exception {
		crudServiceBean.saveOrUpdate(listaFormas);
		if (!consultoresNoCalificados.isEmpty() && consultoresNoCalificados != null && !listaCoordenadaGenerales.isEmpty()
				&& listaCoordenadaGenerales != null) {
			crudServiceBean.saveOrUpdate(estudio);
			for (ConsultorNoCalificado consultorNoCalificado : consultoresNoCalificados) {
				consultorNoCalificado.setEstudioImpactoAmbiental(estudio);
				crudServiceBean.saveOrUpdate(consultorNoCalificado);
			}
			for (CoordenadaGeneral coordenadaGeneral : listaCoordenadaGenerales) {
				coordenadaGeneral.setIdTable(estudio.getId());
				coordenadaGeneral.setNombreTabla(EstudioImpactoAmbiental.PROCESS_NAME);
				crudServiceBean.saveOrUpdate(coordenadaGeneral);
			}
		}
		crudServiceBean.delete(consultoresNoCalificadosEliminar);
		crudServiceBean.delete(listaCoordenadaGeneralesEliminar);
	}
	
	/**
	 * Cris F: Guardar historico de los datos ingresados
	 * @param estudio
	 * @param estudioHistorico
	 * @param consultoresNoCalificados
	 * @param consultoresNoCalificadosEliminar
	 * @param listaCoordenadaGenerales
	 * @param listaCoordenadaGeneralesEliminar
	 * @param numeroNotificaciones
	 * @param esMineriaNoMetalicos
	 * @throws Exception
	 */
	public void guardarHistorico(EstudioImpactoAmbiental estudio,
			EstudioImpactoAmbiental estudioHistorico,
			List<ConsultorNoCalificado> consultoresNoCalificados,
			List<ConsultorNoCalificado> consultoresNoCalificadosEliminar,
			List<CoordenadaGeneral> listaCoordenadaGenerales,
			List<CoordenadaGeneral> listaCoordenadaGeneralesEliminar,
			Integer numeroNotificaciones, boolean esMineriaNoMetalicos) throws Exception {
		
		List<CoordenadaGeneral> listaCoordenadasGeneralesBdd = new ArrayList<CoordenadaGeneral>();
		if (estudioHistorico != null){
			if(!esMineriaNoMetalicos){
				/**
				 * Aqui se ve si el consultor cambio para poder guardar un nuevo estudio para historico, sino se
				 * cambio el consultor entonces no se debe guardar un estudio como historico.
				 */
				if(estudio.getConsultor().equals(estudioHistorico.getConsultor())){
					crudServiceBean.saveOrUpdate(estudioHistorico);
				}				
			}else{
				if(ingresarHistoricoEstudioAmbiental(estudio, estudioHistorico)){
					crudServiceBean.saveOrUpdate(estudioHistorico);
				}
			}
		}
		
		if(esMineriaNoMetalicos){
			listaCoordenadasGeneralesBdd = coordenadaGeneralFacade.coordenadasGeneralXTablaId(estudio.getId(), EstudioImpactoAmbiental.PROCESS_NAME);
		}
		
		crudServiceBean.saveOrUpdate(estudio);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
		if (!consultoresNoCalificados.isEmpty() && consultoresNoCalificados != null) {
			
			for (ConsultorNoCalificado consultorNoCalificado : consultoresNoCalificados) {
				if (consultorNoCalificado.getId() != null) {
					ConsultorNoCalificado consultorBdd = consultorNoCalificadoFacade
							.buscarConsultorNoCalificado(consultorNoCalificado.getId());
					if (consultorNoCalificado.getNombre().equals(consultorBdd.getNombre()) && 
							consultorNoCalificado.getFormacionProfesional().equals(consultorBdd.getFormacionProfesional()) &&
							consultorNoCalificado.getComponentePaticipacion().equals(consultorBdd.getComponentePaticipacion()) &&
							consultorNoCalificado.getDescripcionParticipacionEquipConsultor().
							equals(consultorBdd.getDescripcionParticipacionEquipConsultor())) {
						continue;
					} else {
						List<ConsultorNoCalificado> consultoresHistorico = consultorNoCalificadoFacade
											.buscarConsultorNoCalificadoHistorico(consultorNoCalificado.getId(),numeroNotificaciones);
						if (!consultoresHistorico.isEmpty() && consultoresHistorico != null) {
							crudServiceBean.saveOrUpdate(consultorNoCalificado);
						} else {
							boolean guardarHistorico = false;
							if (consultorNoCalificado.getNumeroNotificacion() == null) { 
								//si se modifica un registro existente
								guardarHistorico = true;
							}else if(!consultorNoCalificado.getNumeroNotificacion().equals(numeroNotificaciones)){
								//si se modifica un registro nuevo ingresado en la correccion
								guardarHistorico = true;
							}							
							
							if (guardarHistorico) {
								ConsultorNoCalificado consultorHistorico = consultorBdd.clone();
								consultorHistorico.setIdHistorico(consultorNoCalificado.getId());
								consultorHistorico.setNumeroNotificacion(numeroNotificaciones);
								crudServiceBean.saveOrUpdate(consultorHistorico);
							}

							crudServiceBean.saveOrUpdate(consultorNoCalificado);
						}
					}

				} else {
					consultorNoCalificado.setNumeroNotificacion(numeroNotificaciones);
					consultorNoCalificado.setEstudioImpactoAmbiental(estudio);
					crudServiceBean.saveOrUpdate(consultorNoCalificado);
				}
			}
		}
		
		/**
		 * cambio para guardar coordenadas
		 */
		
		List<CoordenadaGeneral> listaCoordenadasGuardar = new ArrayList<CoordenadaGeneral>();
		if(listaCoordenadasGeneralesBdd.isEmpty() && listaCoordenadaGenerales.isEmpty()){
			//no se guarda
			listaCoordenadasGuardar = new ArrayList<CoordenadaGeneral>();
		}else if(listaCoordenadasGeneralesBdd.isEmpty() && !listaCoordenadaGenerales.isEmpty()){
			for(CoordenadaGeneral coordenada : listaCoordenadaGenerales){
				coordenada.setIdTable(estudio.getId());
				coordenada.setNombreTabla(EstudioImpactoAmbiental.PROCESS_NAME);
				coordenada.setNumeroNotificacion(numeroNotificaciones);
				crudServiceBean.saveOrUpdate(coordenada);
			}
		}else if(!listaCoordenadasGeneralesBdd.isEmpty() && !listaCoordenadaGenerales.isEmpty()){			
			listaCoordenadasGuardar = obtenerModificaciones(listaCoordenadasGeneralesBdd, listaCoordenadaGenerales, numeroNotificaciones);								
		}
		
		if(!listaCoordenadasGuardar.isEmpty()){
			for(CoordenadaGeneral coordenadaGuardar : listaCoordenadasGuardar){
				if(coordenadaGuardar.getIdHistorico() == null){
					coordenadaGuardar.setIdTable(estudio.getId());
					coordenadaGuardar.setNombreTabla(EstudioImpactoAmbiental.PROCESS_NAME);
					crudServiceBean.saveOrUpdate(coordenadaGuardar);								
				}else{
					coordenadaGuardar.setIdTable(estudio.getId());
					coordenadaGuardar.setNombreTabla(EstudioImpactoAmbiental.PROCESS_NAME);
					crudServiceBean.saveOrUpdate(coordenadaGuardar);								
				}
			}			
		}		
		
		if (!consultoresNoCalificadosEliminar.isEmpty() && consultoresNoCalificadosEliminar != null) {
			for (ConsultorNoCalificado consultorEliminado : consultoresNoCalificadosEliminar) {
				if(consultorEliminado.getEstado()){
					boolean guardarHistorico = false;
					if(consultorEliminado.getNumeroNotificacion() == null){
						guardarHistorico = true;
					}else if(!consultorEliminado.getNumeroNotificacion().equals(numeroNotificaciones)){
						guardarHistorico = true;
					}
					
					if(guardarHistorico){
						ConsultorNoCalificado consultorHistoricoEliminado = consultorEliminado.clone();
						consultorHistoricoEliminado.setIdHistorico(consultorEliminado.getId());
						consultorHistoricoEliminado.setNumeroNotificacion(numeroNotificaciones);
						crudServiceBean.saveOrUpdate(consultorHistoricoEliminado);
					}
				}
			}
			crudServiceBean.delete(consultoresNoCalificadosEliminar);
		}
		
		if(listaCoordenadaGeneralesEliminar != null && !listaCoordenadaGeneralesEliminar.isEmpty()){
			for(CoordenadaGeneral coordenadaEliminar : listaCoordenadaGeneralesEliminar){
				
				List<CoordenadaGeneral> listaCoordenadaHistorico = coordenadaGeneralService.coordenadaGeneralPorHistorico(coordenadaEliminar.getId(), numeroNotificaciones);
				
				if(coordenadaEliminar.getNumeroNotificacion() == null || 
						(coordenadaEliminar.getNumeroNotificacion() != null && coordenadaEliminar.getNumeroNotificacion() < numeroNotificaciones)){
					if(listaCoordenadaHistorico == null){
						CoordenadaGeneral coordenadaHistorico = coordenadaEliminar.clone();						
						coordenadaHistorico.setIdHistorico(coordenadaEliminar.getId());
						coordenadaHistorico.setNumeroNotificacion(numeroNotificaciones);
						crudServiceBean.saveOrUpdate(coordenadaHistorico);	
					}
				}
				crudServiceBean.delete(coordenadaEliminar);
			}			
		}		
		
		//crudServiceBean.delete(listaCoordenadaGeneralesEliminar);
	}

	public List<FormaCoordenadasEIA> getListaFormas(Integer estudioId)throws ServiceException {
		List<FormaCoordenadasEIA> formasCoordenadas = (List<FormaCoordenadasEIA>) crudServiceBean
				.getEntityManager()
				.createQuery(
						"select f.listaformaCoordenadasEIAs From EstudioImpactoAmbiental f where f.id =:estudioId")
				.setParameter("estudioId", estudioId).getResultList();

		if (formasCoordenadas != null && !formasCoordenadas.isEmpty()) {
			return formasCoordenadas;
		} else {
			throw new ServiceException("Error al recuperar las formas de coordenadas del EIA.");
		}
	}	
	
	
	/**
	 * Cris F: métodos para guardar el historial proyecto pequeña minería
	 * @param estudio
	 * @param estudioHistorico
	 * @return
	 */
	
	private boolean ingresarHistoricoEstudioAmbiental(EstudioImpactoAmbiental estudio, EstudioImpactoAmbiental estudioHistorico){
		try {
			if(estudio.getConsultor().equals(estudioHistorico.getConsultor()) && 
					((estudio.getFaseMinera() == null && estudioHistorico.getFaseMinera() == null) || 
							estudio.getFaseMinera() != null && estudioHistorico.getFaseMinera() != null && 
							estudio.getFaseMinera().equals(estudioHistorico.getFaseMinera())) && 
					((estudio.getTieneFasesMineras() == null && estudioHistorico.getTieneFasesMineras() == null) || 
							estudio.getTieneFasesMineras() != null && estudioHistorico.getTieneFasesMineras() != null &&
							estudio.getTieneFasesMineras().equals(estudioHistorico.getTieneFasesMineras())) && 
					((estudio.getActividadBeneficio() == null && estudioHistorico.getActividadBeneficio() == null) || 
							estudio.getActividadBeneficio() != null && estudioHistorico.getActividadBeneficio() != null && 
							estudio.getActividadBeneficio().equals(estudioHistorico.getActividadBeneficio())) && 
					((estudio.getRecuperacionMetalica() == null && estudioHistorico.getRecuperacionMetalica() == null) || 
							estudio.getRecuperacionMetalica() != null && estudioHistorico.getRecuperacionMetalica() != null && 
							estudio.getRecuperacionMetalica().equals(estudioHistorico.getRecuperacionMetalica())) && 
					((estudio.getTieneConstruccionRelaves() == null && estudioHistorico.getTieneConstruccionRelaves() == null) || 
							estudio.getTieneConstruccionRelaves() != null && estudioHistorico.getTieneConstruccionRelaves() != null && 
							estudio.getTieneConstruccionRelaves().equals(estudioHistorico.getTieneConstruccionRelaves())) && 
					((estudio.getLocalizacionRelaves() == null && estudioHistorico.getLocalizacionRelaves() == null) || 
							estudio.getLocalizacionRelaves() != null && estudioHistorico.getLocalizacionRelaves() != null && 
							estudio.getLocalizacionRelaves().equals(estudioHistorico.getLocalizacionRelaves())) && 
					((estudio.getCapacidad() == null && estudioHistorico.getCapacidad() == null) || 
							estudio.getCapacidad() != null && estudioHistorico.getCapacidad() != null && 
							estudio.getCapacidad().equals(estudioHistorico.getCapacidad())) && 
					((estudio.getEstadoBeneficio() == null && estudioHistorico.getEstadoBeneficio() == null) || 
							estudio.getEstadoBeneficio() != null && estudioHistorico.getEstadoBeneficio() != null && 
							estudio.getEstadoBeneficio().equals(estudioHistorico.getEstadoBeneficio())) && 
					((estudio.getMetodoDeExplotacion() == null && estudioHistorico.getMetodoDeExplotacion() == null) || 
							estudio.getMetodoDeExplotacion() != null && estudioHistorico.getMetodoDeExplotacion() != null && 
							estudio.getMetodoDeExplotacion().equals(estudioHistorico.getMetodoDeExplotacion())) && 
					((estudio.getNumeroDeFrentesExplotacion() == null && estudioHistorico.getNumeroDeFrentesExplotacion() == null) || 
							estudio.getNumeroDeFrentesExplotacion() != null && estudioHistorico.getNumeroDeFrentesExplotacion() != null && 
							estudio.getNumeroDeFrentesExplotacion().equals(estudioHistorico.getNumeroDeFrentesExplotacion())) && 
					((estudio.getVolumenDeExplotacion() == null && estudioHistorico.getVolumenDeExplotacion() == null) || 
							estudio.getVolumenDeExplotacion() != null && estudioHistorico.getVolumenDeExplotacion() != null && 
							estudio.getVolumenDeExplotacion().equals(estudioHistorico.getVolumenDeExplotacion())) && 
					((estudio.getUnidadMedidaVolumen() == null && estudioHistorico.getUnidadMedidaVolumen() == null) || 
							estudio.getUnidadMedidaVolumen() != null && estudioHistorico.getUnidadMedidaVolumen() != null && 
							estudio.getUnidadMedidaVolumen().equals(estudioHistorico.getUnidadMedidaVolumen())) && 
					((estudio.getTieneConstruccionEscombreras() == null && estudioHistorico.getTieneConstruccionEscombreras() == null) ||
							estudio.getTieneConstruccionEscombreras() != null && estudioHistorico.getTieneConstruccionEscombreras() != null && 
							estudio.getTieneConstruccionEscombreras().equals(estudioHistorico.getTieneConstruccionEscombreras())) && 
					((estudio.getLocalizacionEscombreras() == null && estudioHistorico.getLocalizacionEscombreras() == null) || 
							estudio.getLocalizacionEscombreras() != null && estudioHistorico.getLocalizacionEscombreras() != null && 
							estudio.getLocalizacionEscombreras().equals(estudioHistorico.getLocalizacionEscombreras())) && 
					((estudio.getCapacidadEscombrera() == null && estudioHistorico.getCapacidadEscombrera() == null) || 
							estudio.getCapacidadEscombrera() != null && estudioHistorico.getCapacidadEscombrera() != null && 
							estudio.getCapacidadEscombrera().equals(estudioHistorico.getCapacidadEscombrera())) && 
					((estudio.getEstadoExplotacion() == null && estudioHistorico.getEstadoExplotacion() == null) || 
							estudio.getEstadoExplotacion() != null && estudioHistorico.getEstadoExplotacion() != null && 
							estudio.getEstadoExplotacion().equals(estudioHistorico.getEstadoExplotacion()))					
							){
				return false;			
			}else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
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

}
