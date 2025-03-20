package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.OficioCertificadoInterseccionService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.domain.OficioAproReqTec;
import ec.gob.ambiente.suia.domain.OficioObservacionesReqTec;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeOficioArtFacade {

	@EJB
	CrudServiceBean crudServiceBean;

	@EJB
	OficioCertificadoInterseccionService oficioCertificadoInterseccionService;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	private static final Logger LOGGER = Logger.getLogger(InformeOficioArtFacade.class);

	private static final String CARPETA_INFORME_OFICIO_REQUISITOS_MODALIDAD_DISPOSICION_FINAL = "informe_oficio_aprobacion_requisitos";

	public void guardarInformeTecnicoArt(InformeTecnicoAproReqTec informe) {
		this.crudServiceBean.saveOrUpdate(informe);
	}

	public void guardarOficioAprobacionArt(OficioAproReqTec oficioART) {
		this.crudServiceBean.saveOrUpdate(oficioART);
	}

	public void guardarOficioObservacionesArt(OficioObservacionesReqTec oficioORT, long idProceso, long idTarea,
			TipoDocumentoSistema tipoDocumento) {
		try {
			ProyectoLicenciamientoAmbiental proyecto = new ProyectoLicenciamientoAmbiental();
			proyecto.setCodigo(oficioORT.getNumeroProyecto());
			Documento documento = subirFileAlfresco(oficioORT.getDocumentoOficioObservacion(), oficioORT
					.getAprobacionRequisitosTecnicos().getProyecto(), idProceso, idTarea,
					InformeTecnicoAproReqTec.class.getSimpleName(), tipoDocumento);
			oficioORT.setDocumentoOficioObservacion(documento);
			this.crudServiceBean.saveOrUpdate(oficioORT);
		} catch (ServiceException ex) {
			LOGGER.error(ex);
		} catch (CmisAlfrescoException ex) {
			LOGGER.error(ex);
		}
	}

	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(
					PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public BigInteger obtenerNumeroInforme(String sequenceName, String schema) {
		try {
			return (BigInteger) this.crudServiceBean.getSecuenceNextValue(sequenceName, schema);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	public InformeTecnicoAproReqTec obtenerInformeTecnicoPorArt(TipoDocumentoSistema tipoDocumento, Integer apteId,
			Integer contadorBandeja) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("doty_id", tipoDocumento.getIdTipoDocumento());
			parametros.put("apte_id", apteId);
			parametros.put("trart_time_in_technical", contadorBandeja);

			List<InformeTecnicoAproReqTec> lista = this.crudServiceBean.findByNamedQueryGeneric(
					InformeTecnicoAproReqTec.OBTENER_INFORME_TECNICO_POR_ART, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (InformeTecnicoAproReqTec) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public InformeTecnicoAproReqTec obtenerInformeTecnicoArtPorNumeroInforme(String numeroInforme) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("numeroInforme", numeroInforme);
			
			List<InformeTecnicoAproReqTec> lista = this.crudServiceBean.findByNamedQueryGeneric(
					InformeTecnicoAproReqTec.OBTENER_INFORME_TECNICO_POR_NUM_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (InformeTecnicoAproReqTec) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	
	public OficioAproReqTec obtenerOficioAprobacionPorArt(TipoDocumentoSistema tipoDocumento, Integer apteId,
			Integer contadorBandeja) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("doty_id", tipoDocumento.getIdTipoDocumento());
			parametros.put("apte_id", apteId);
			parametros.put("ofart_time_in_technical", contadorBandeja);

			List<OficioAproReqTec> lista = this.crudServiceBean.findByNamedQueryGeneric(
					OficioAproReqTec.OBTENER_OFICIO_POR_ART, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (OficioAproReqTec) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public OficioObservacionesReqTec obtenerOficioObservacionPorArt(Integer tipoDocumentoId, Integer apteId) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("doty_id", tipoDocumentoId);
			parametros.put("apte_id", apteId);

			List<OficioObservacionesReqTec> lista = this.crudServiceBean.findByNamedQueryGeneric(
					OficioObservacionesReqTec.OBTENER_OFICIO_POR_ORT, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (OficioObservacionesReqTec) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public Documento subirFileAlfresco(Documento documento, String proyecto, long idProceso, long idTarea,
			String nombreTabla, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException {
		if (documento.getIdAlfresco() == null) {
			DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
			documentoTarea.setIdTarea(idTarea);
			documento.setNombreTabla(nombreTabla);
			documentoTarea.setProcessInstanceId(idProceso);
			return documentosFacade.guardarDocumentoAlfrescoSinProyecto(proyecto,
					CARPETA_INFORME_OFICIO_REQUISITOS_MODALIDAD_DISPOSICION_FINAL, 0L, documento, tipoDocumento,
					documentoTarea);
		} else {
			return documento;
		}
	}

	public Map<String, String> obtenerNombreCargo(Persona persona) {
		Map<String, String> datos = new HashMap<String, String>();
		if (isPersonaJuridica(persona)) {
			String cargo = persona.getOrganizaciones().get(0).getCargoRepresentante();
			datos.put("cargo", cargo == null ? "Proponente" : cargo);
			datos.put("nombre", obtenerNombreEmpresa(persona));
		} else {
			datos.put("cargo", "Proponente");
			datos.put("nombre", persona.getNombre());
		}
		return datos;
	}

	private String obtenerNombreEmpresa(Persona representante) {
		return isPersonaJuridica(representante) ? representante.getOrganizaciones().get(0).getNombre() : null;
	}

	private boolean isPersonaJuridica(Persona persona) {
		return (persona.getOrganizaciones() == null || persona.getOrganizaciones().isEmpty()) ? false : true;

	}

	public void completarTarea(long processInstanceIdTask, Usuario usuario, long taskId, boolean isOficioAprobacion)
			throws ServiceException {
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("pronunciamento", isOficioAprobacion);
			variables.put("emiteAclaratoria", !isOficioAprobacion);
			procesoFacade.aprobarTarea(usuario, taskId, processInstanceIdTask, null);
			procesoFacade.modificarVariablesProceso(usuario, processInstanceIdTask, variables);
		} catch (JbpmException e) {
			throw new ServiceException("Error al completar tarea informa.", e);
		}
	}

	public String generarCodigoInforme(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ aprobacionRequisitosTecnicos.getAreaResponsable().getAreaAbbreviation()
					+ "-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"					
					+ secuenciasFacade.getSecuenciaParaInformeTecnicoAreaResponsable(aprobacionRequisitosTecnicos
							.getAreaResponsable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String generarCodigoOficio(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ aprobacionRequisitosTecnicos.getAreaResponsable().getArea()
							.getAreaAbbreviation()
					+ "-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"

					+ secuenciasFacade
							.getSecuenciaParaOficioAreaResponsable(aprobacionRequisitosTecnicos
									.getAreaResponsable().getArea());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public String generarCodigoOficioZonal(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ aprobacionRequisitosTecnicos.getAreaResponsable().getArea().getAreaAbbreviation()
					+ "-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"					
					+ secuenciasFacade.getSecuenciaParaOficioAreaResponsable(aprobacionRequisitosTecnicos
							.getAreaResponsable().getArea());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	 public String[] getEmpresa(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
			Organizacion organizacion = organizacionFacade.buscarPorPersona(aprobacionRequisitosTecnicos.getUsuario()
					.getPersona(), aprobacionRequisitosTecnicos.getUsuario().getNombre());
			if (organizacion != null) {
				String[] cargoEmpresa = new String[2];
				cargoEmpresa[0] = organizacion.getCargoRepresentante();
				cargoEmpresa[1] = organizacion.getNombre();
				return cargoEmpresa;
			}
			return null;
		}
	 
	public OficioAproReqTec obtenerOficioAprobacionPorArt(TipoDocumentoSistema tipoDocumento, Integer apteId) {
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("doty_id", tipoDocumento.getIdTipoDocumento());
			parametros.put("apte_id", apteId);

			List<OficioAproReqTec> lista = this.crudServiceBean
					.findByNamedQueryGeneric(
							OficioAproReqTec.OBTENER_ULTIMO_OFICIO_POR_ART, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (OficioAproReqTec) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}
