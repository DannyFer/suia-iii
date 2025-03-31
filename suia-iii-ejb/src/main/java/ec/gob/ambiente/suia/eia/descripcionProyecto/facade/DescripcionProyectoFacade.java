package ec.gob.ambiente.suia.eia.descripcionProyecto.facade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.SustanciaQuimicaPeligrosaService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.descripcionProyecto.service.*;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author liliana.chacha
 *
 */
@Stateless
public class DescripcionProyectoFacade implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private DescripcionProyectoService descripcionProyectoService;

	@EJB
	private CronogramaFasesProyectoService cronogramaFasesProyectoService;

	@EJB
	private SustanciaQuimicaPeligrosaService sustanciaQuimicaPeligrosaService;

	@EJB
	private SustanciaQuimicaService sustanciaQuimicaService;

	@EJB
	private SustanciaQuimicaEiaService sustanciaQuimicaEiaService;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private ActividadesPorEtapaService actividadesPorEtapaService;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	private static final String NOMBRE_CARPETA_DESCRIPCION_PROYECTO_EIA = "Descripcion_proyecto_eia";

	public Documento ingresarDocumento(File file, Integer id,
			String codProyecto, long idProceso, long idTarea,
			TipoDocumentoSistema tipoDocumento) throws Exception {
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		Documento documento1 = new Documento();
		documento1.setIdTable(id);
		String ext = getExtension(file.getAbsolutePath());
		documento1.setNombre(file.getName());
		documento1.setExtesion(ext);
		documento1.setNombreTabla(EstudioImpactoAmbiental.class.getSimpleName());
		documento1.setMime(mimeTypesMap.getContentType(file));
		documento1.setContenidoDocumento(data);
		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		documentoTarea.setIdTarea(idTarea);
		documentoTarea.setProcessInstanceId(idProceso);
		return documentosFacade.guardarDocumentoAlfresco(codProyecto,
				"LicenciaAmbiental", idProceso, documento1, tipoDocumento,
				documentoTarea);
	}

	private String getExtension(String fullPath) {
		String extension = "";
		int i = fullPath.lastIndexOf('.');
		if (i > 0) {
			extension = fullPath.substring(i + 1);
		}
		return extension;
	}

	public File descargarDocumento(Integer idProyecto, TipoDocumentoSistema idTipoDocumento)
			throws CmisAlfrescoException, IOException {
		List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(
				idProyecto, EstudioImpactoAmbiental.class.getSimpleName(),
				idTipoDocumento);

		if (documentos.isEmpty()) {
			return null;
		} else {
			Documento descripcionProcesoDoc = documentos.get(0);
			byte[] content = documentosFacade.descargar(descripcionProcesoDoc
					.getIdAlfresco());
			File tempFile = File.createTempFile(
					descripcionProcesoDoc.getNombre(), "."
							+ descripcionProcesoDoc.getExtesion());
			FileOutputStream fos = new FileOutputStream(
					tempFile.getAbsolutePath());
			fos.write(content);
			fos.close();
			return tempFile;
		}
	}

	public void guardar(EstudioImpactoAmbiental estudio,
			List<ActividadLicenciamiento> actividadesLicenciamiento,
			List<ActividadLicenciamiento> actividadesLicenciamientoEliminar,
			List<CoordenadaGeneral> listaCoordenadaGeneralesEliminar,
			List<ActividadesPorEtapa> actividadesPorEtapas,
			List<ActividadesPorEtapa> actividadesPorEtapasEliminar,
			List<SustanciaQuimicaEia> sustanciaQuimicaEiaEliminar, 
			boolean existeDoc1, boolean existeDoc2, List<CronogramaFasesProyectoEia> cronogramaEliminar, boolean esMineriaNoMetalicos)
			throws Exception {
		if(existeDoc1)
		{
		estudio.setDocumentoDescripcion(subirFileAlfresco(
				estudio.getDocumentoDescripcion(),
				estudio.getProyectoLicenciamientoAmbiental(), TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA));
		}
		if(existeDoc2)
		{
		estudio.setDocumentoInsumos(subirFileAlfresco(
				estudio.getDocumentoInsumos(),
				estudio.getProyectoLicenciamientoAmbiental(), TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA));
		}else{
			if(esMineriaNoMetalicos)
				estudio.setDocumentoInsumos(null);
		}
		for(ActividadesPorEtapa actet: actividadesPorEtapas){
			if(actet.getCoordenadaActividad() != null){
				byte[] contenido =  actet.getCoordenadaActividad().getContenidoDocumento();
				actet.setCoordenadaActividad(subirFileAlfresco(actet.getCoordenadaActividad(),
						estudio.getProyectoLicenciamientoAmbiental(), TipoDocumentoSistema.TIPO_COORDENADAS));
				actet.getCoordenadaActividad().setContenidoDocumento(contenido);
			}
		}
		estudioImpactoAmbientalFacade.guardar(estudio);
		descripcionProyectoService.guardarDescripcionProyecto(estudio,
				actividadesLicenciamiento, actividadesLicenciamientoEliminar,
				actividadesPorEtapas, actividadesPorEtapasEliminar,
				listaCoordenadaGeneralesEliminar, sustanciaQuimicaEiaEliminar, cronogramaEliminar);
	}

	public boolean isActividadEnUso(ActividadLicenciamiento actividad) {
		return descripcionProyectoService.isActividadEnUso(actividad);

	}

	public List<CronogramaFasesProyectoEia> obtenerCronogramaFasesPorEIA(
			Integer idEIA) {
		return cronogramaFasesProyectoService
				.obtenerCronogramaFasesPorEIA(idEIA);
	}

	public void guardarCronogramaFasesProyecto(CronogramaFasesProyectoEia cronogramaFases) throws Exception {		
		cronogramaFasesProyectoService.guardarCronogramaFasesProyectoProyecto(cronogramaFases);
	}
	
	/**
	 * Cristina F: cambio para historico
	 * @param cronogramaFases
	 * @param numeroNotificaciones
	 * @throws Exception
	 */
	public void guardarCronogramaFasesProyectoHistorico(CronogramaFasesProyectoEia cronogramaFases, Integer numeroNotificaciones) throws Exception {
		CronogramaFasesProyectoEia cronogramaGuardado = new CronogramaFasesProyectoEia();		
		List<CronogramaFasesProyectoEia> cronogramaHistoricoList = new ArrayList<CronogramaFasesProyectoEia>();
		
		boolean guardarFechaInicio = false;
		boolean guardarFechaFin = false;
		
		if(cronogramaFases.getId() != null){
			cronogramaGuardado = cronogramaFasesProyectoService.cronogramaFasesPorId(cronogramaFases.getId());	
			cronogramaHistoricoList = cronogramaFasesProyectoService.obtenerCronogramaFasesPorHistorico(cronogramaFases.getId(), numeroNotificaciones);
			
			if(cronogramaHistoricoList != null && !cronogramaHistoricoList.isEmpty()){
				guardarFechaInicio = false;
				guardarFechaFin = false;				
			}else{
				if(cronogramaGuardado.getFechaInicio() != null && cronogramaFases.getFechaInicio() != null){
					if(cronogramaGuardado.getFechaInicio().equals(cronogramaFases.getFechaInicio())){
						guardarFechaInicio = false;
					}else{
						guardarFechaInicio = true;
					}
				}else if(cronogramaGuardado.getFechaInicio() == null && cronogramaFases.getFechaInicio() == null){
					guardarFechaInicio = false;
				}else if(cronogramaGuardado.getFechaInicio() == null && cronogramaFases.getFechaInicio() != null){
					guardarFechaInicio = true;
				}
				
				if(cronogramaGuardado.getFechaFin() != null && cronogramaFases.getFechaFin() != null){
					if(cronogramaGuardado.getFechaFin().equals(cronogramaFases.getFechaFin())){
						guardarFechaFin = false;
					}else
						guardarFechaFin = true;
				}else if(cronogramaGuardado.getFechaFin() == null && cronogramaFases.getFechaFin() == null)
					guardarFechaFin = false;
				else if(cronogramaGuardado.getFechaFin() == null && cronogramaFases.getFechaFin() != null)
					guardarFechaFin = true;
			}
			
		}//siempre tendrá id porque no se puede agregar una nueva fase
		else{
			cronogramaFases.setNumeroNotificacion(numeroNotificaciones);
		}
		if(guardarFechaInicio == true || guardarFechaFin == true){
			CronogramaFasesProyectoEia cronogramaFasesHistorico = cronogramaGuardado.clone();
			cronogramaFasesHistorico.setIdHistorico(cronogramaGuardado.getId());
			cronogramaFasesHistorico.setNumeroNotificacion(numeroNotificaciones);
			cronogramaFasesProyectoService.guardarCronogramaFasesProyectoProyecto(cronogramaFasesHistorico);
		}			
		
		cronogramaFasesProyectoService.guardarCronogramaFasesProyectoProyecto(cronogramaFases);
	}

	public List<SustanciaQuimicaPeligrosa> obtenerSustanciasQuimicasPeligrosas() {
		return sustanciaQuimicaPeligrosaService.getListaSustanciasQuimicasPeligrosas(null);
	}

	public List<ActividadesPorEtapa> obtenerActividadesPorEtapas(EstudioImpactoAmbiental estudio) throws Exception {
		List<ActividadesPorEtapa> result = actividadesPorEtapaService.getListaActividadPorEtapa(estudio);
		if(result != null){
			for (ActividadesPorEtapa actividad : result){
				if(actividad.getCoordenadaActividad() != null) {
					actividad.getCoordenadaActividad().setContenidoDocumento(documentosFacade.descargar(actividad.getCoordenadaActividad().getIdAlfresco()));
				}
			}
		} else {
			result = new ArrayList<ActividadesPorEtapa>();
		}
		return result;
	}

	public void guardarSustanciaQuimica(SustanciaQuimica sustanciaQuimica)
			throws Exception {
		sustanciaQuimicaService.guardarSustanciaQuimica(sustanciaQuimica);
	}

	public void guardarSustanciaQuimicaEia(
			List<SustanciaQuimicaEia> sustanciasQuimicasEias) throws Exception {
		sustanciaQuimicaEiaService
				.guardarSustanciaQuimicaEia(sustanciasQuimicasEias);
	}

	public void guardarSustanciaQuimicaEia(
			SustanciaQuimicaEia sustanciaQuimicaEia) throws Exception {
		sustanciaQuimicaEiaService
				.guardarSustanciaQuimicaEia(sustanciaQuimicaEia);
	}

	public List<SustanciaQuimicaEia> obtenerSustanciasQuimicasPorEia(
			Integer idEia) {
		return sustanciaQuimicaEiaService
				.obtenerSustanciasQuimicasPorEia(idEia);
	}

	public byte[] descargarFile(Documento documento)
			throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	private Documento subirFileAlfresco(Documento documento,
			ProyectoLicenciamientoAmbiental proyecto, TipoDocumentoSistema tipo)
			throws Exception, CmisAlfrescoException {

		DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
		documentoTarea.setIdTarea(0L);
		documento.setNombreTabla(EstudioImpactoAmbiental.class.getSimpleName());
		documento.setIdTable(136143);
		documentoTarea.setProcessInstanceId(0L);
documentoTarea.setDocumento(documento);
		return documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
				NOMBRE_CARPETA_DESCRIPCION_PROYECTO_EIA, 0L, documento, tipo,
				documentoTarea);

	}

	public List<EtapasProyecto> getEtapasPorFaseYzonas(Integer idFase, Integer idzona) throws ServiceException {
		return descripcionProyectoService.getEtapas(idFase, idzona);
	}
	
	/**
	 * Cristina F: guardar historico
	 * @throws Exception 
	 */
	
	public void guardarHistorico(EstudioImpactoAmbiental estudio, 
			EstudioImpactoAmbiental estudioHistorico,
			List<ActividadLicenciamiento> actividadesLicenciamiento,
			List<ActividadLicenciamiento> actividadesLicenciamientoEliminar,
			List<CoordenadaGeneral> listaCoordenadaGeneralesEliminar,
			List<ActividadesPorEtapa> actividadesPorEtapas,
			List<ActividadesPorEtapa> actividadesPorEtapasEliminar,
			List<SustanciaQuimicaEia> sustanciaQuimicaEiaEliminar, 
			boolean existeDoc1, boolean existeDoc2, Integer numeroNotificaciones, 
			List<CronogramaFasesProyectoEia> cronogramaFasesEliminar,
			boolean esMineriaNoMetalicos) throws Exception {
		
		if (estudioHistorico != null){
			estudioImpactoAmbientalFacade.guardar(estudioHistorico);
		}
		
		if(existeDoc1){
		estudio.setDocumentoDescripcion(subirFileAlfresco(
				estudio.getDocumentoDescripcion(),
				estudio.getProyectoLicenciamientoAmbiental(), TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA));
		}
		if(existeDoc2){
		estudio.setDocumentoInsumos(subirFileAlfresco(
				estudio.getDocumentoInsumos(),
				estudio.getProyectoLicenciamientoAmbiental(), TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA));
		}else{
			if(esMineriaNoMetalicos){
				estudio.setDocumentoInsumos(null);
			}
		}
		
		
		//aqui se debe buscar las actividades por etapa de hidrocarburos
		
		estudioImpactoAmbientalFacade.guardar(estudio);
		
		for(ActividadesPorEtapa actet: actividadesPorEtapas){
			if(actet.getCoordenadaActividad() != null){
				byte[] contenido =  actet.getCoordenadaActividad().getContenidoDocumento();
				actet.setCoordenadaActividad(subirFileAlfresco(actet.getCoordenadaActividad(),
						estudio.getProyectoLicenciamientoAmbiental(), TipoDocumentoSistema.TIPO_COORDENADAS));
				actet.getCoordenadaActividad().setContenidoDocumento(contenido);
			}
		}
		descripcionProyectoService.guardarDescripcionProyectoHistorico(estudio, estudioHistorico,
				actividadesLicenciamiento, actividadesLicenciamientoEliminar,
				actividadesPorEtapas, actividadesPorEtapasEliminar,
				listaCoordenadaGeneralesEliminar, sustanciaQuimicaEiaEliminar, numeroNotificaciones, cronogramaFasesEliminar);
	}
	
	public void guardarSustanciaQuimicaEiaHistorico(SustanciaQuimicaEia sustanciaQuimicaEia, Integer numeroNotificacion) throws Exception {
		//obtenerSustanciasQuimicasPorHistorico
		if(sustanciaQuimicaEia.getId() == null){
			sustanciaQuimicaEia.setNumeroNotificacion(numeroNotificacion);
			sustanciaQuimicaEiaService.guardarSustanciaQuimicaEia(sustanciaQuimicaEia);
		}else{
			List<SustanciaQuimicaEia> listaSustanciasHistorico = sustanciaQuimicaEiaService.obtenerSustanciasQuimicasPorHistorico(sustanciaQuimicaEia.getId(),numeroNotificacion);
			
			if(listaSustanciasHistorico != null && !listaSustanciasHistorico.isEmpty()){				
				sustanciaQuimicaEiaService.guardarSustanciaQuimicaEia(sustanciaQuimicaEia);
			}else{				
				SustanciaQuimicaEia sustanciaHistorico = sustanciaQuimicaEia.clone();
				sustanciaHistorico.setIdHistorico(sustanciaQuimicaEia.getId());
				sustanciaHistorico.setNumeroNotificacion(numeroNotificacion);
				sustanciaQuimicaEiaService.guardarSustanciaQuimicaEia(sustanciaHistorico);
				
				sustanciaQuimicaEiaService.guardarSustanciaQuimicaEia(sustanciaQuimicaEia);
				
			}
			
			
		}
	}
	
	/**
	 * MarielaG
	 * Consultar las sustancias originales
	 */
	public List<SustanciaQuimicaEia> buscarSustanciasModificadas(Integer estudio, Integer numeroNotificacion){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT s FROM SustanciaQuimicaEia s WHERE s.estudioAmbiental.id = :estudio "
					+ "and s.numeroNotificacion = :numeroNotificacion");
			query.setParameter("estudio", estudio);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			return (List<SustanciaQuimicaEia>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * MarielaG
	 * Consultar el cronograma original por actividad
	 */
	public List<CronogramaFasesProyectoEia> buscarCronogramaFasesModificadas(Integer estudio, Integer numeroNotificacion){
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT c FROM CronogramaFasesProyectoEia c where c.estudioAmbiental.id = :estudio "
					+ "and c.numeroNotificacion = :numeroNotificacion");
			query.setParameter("estudio", estudio);
			query.setParameter("numeroNotificacion", numeroNotificacion);
			
			return (List<CronogramaFasesProyectoEia>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * MarielaG
	 * Consultar el cronograma existente en la bdd
	 */
	public List<CronogramaFasesProyectoEia> obtenerCronogramaFasesEnBddPorEIA(Integer idEIA) {
		try{
			Query query = crudServiceBean.getEntityManager().createQuery(
					"SELECT c FROM CronogramaFasesProyectoEia c where c.estudioAmbiental.id = :estudio ");
			query.setParameter("estudio", idEIA);
			
			return (List<CronogramaFasesProyectoEia>) query.getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
