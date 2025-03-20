/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.AprobacionRequisitosTecnicosService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicosModalidad;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> Clase facade el proceso aprobación requisitos técnicos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 04/06/2015 $]
 *          </p>
 */
@Stateless
public class AprobacionRequisitosTecnicosFacade {

	/**
	* 
	*/
	public static final String ROLE_TECNICO_CALIDAD_AMBIENTAL = "role.area.tecnico.registro.generador";

	/**
	* 
	*/
	private static final String NOMBRE_VARIABLE_REQUEST_BODY = "requestBody";

	/**
	* 
	*/
	private static final String NOMBRE_VARIABLE_URL_ASIGNACION_AUTOMATICA = "urlAsignacionAutomatica";

	/**
	* 
	*/
	public static final String NOMBRE_VARIABLE_TIPO_ESTUDIO = "tipoEstudio";

	/**
	* 
	*/
	private static final String TIPO_CATEGORIA_II = "II";

	/**
	* 
	*/
	public static final String NOMBRE_PROYECTO = "nombreProyecto";

	/**
	* 
	*/
	public static final String NOMBRE_VARIABLE_PROCESO_INICIO_DESDE_REGISTRO = "esRequisitosPrevios";
	public static final String NOMBRE_VARIABLE_AREA_RESPONSABLE = "areaResponsable";

	public static final String NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS = "Aprobacion_requisitos_tecnicos";

	public static final String NOMBRE_CARPETA_MODALIDAD_RECICLAJE = NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS
			+ "/" + "Modalidad_reciclaje";

	public static final String NOMBRE_CARPETA_MODALIDAD_DISPOSICION_FINAL = NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS
			+ "/" + "Modalidad_disposicion_final";

	public static final String NOMBRE_CARPETA_MODALIDAD_REUSO = NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS
			+ "/" + "Modalidad_reuso";

	public static final String NOMBRE_CARPETA_MODALIDAD_INCINERACION = NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS
			+ "/" + "Modalidad_incineracion";

	public static final String NOMBRE_CARPETA_MODALIDAD_TRATAMIENTO = NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS
			+ "/" + "Modalidad_tratamiento";

	public static final String NOMBRE_CARPETA_MODALIDAD_COPROCESAMIENTO = NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS
			+ "/" + "Modalidad_coprocesamiento";

	public static final String NOMBRE_CARPETA_REQUISITOS_VEHICULO = NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS
			+ "/" + "Requisitos_vehiculo";

	public static final String NOMBRE_CARPETA_DESECHOS_BIOLOGICOS = "Desechos_biologicos";

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private AprobacionRequisitosTecnicosService aprobacionRequisitosTecnicosService;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private EntidadResponsableFacade entidadResponsableFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private MensajeNotificacionFacade mensajeFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	public AprobacionRequisitosTecnicos guardar(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos,
			List<ModalidadGestionDesechos> modalidades) {
		List<AprobacionRequisitosTecnicosModalidad> listaAprobacionesModalidad = new ArrayList<AprobacionRequisitosTecnicosModalidad>();
		listaAprobacionesModalidad.addAll(aprobacionRequisitosTecnicos.getAprobacionModalidades());
		crudServiceBean.saveOrUpdate(aprobacionRequisitosTecnicos);
		aprobacionRequisitosTecnicos.setAprobacionModalidades(listaAprobacionesModalidad);
		List<AprobacionRequisitosTecnicosModalidad> lista = aprobacionRequisitosTecnicos.getAprobacionModalidades();
		for (AprobacionRequisitosTecnicosModalidad apm : lista) {
			if (!modalidades.contains(apm.getModalidad())) {
				crudServiceBean.delete(apm);
			}
		}
		List<ModalidadGestionDesechos> listaModalidadesNuevas = new ArrayList<ModalidadGestionDesechos>();
		listaModalidadesNuevas.addAll(modalidades);
		listaModalidadesNuevas.removeAll(aprobacionRequisitosTecnicos.getModalidades());

		for (ModalidadGestionDesechos modalidad : listaModalidadesNuevas) {
			guardarAprobacionRequisitosTecnicosModalidad(aprobacionRequisitosTecnicos, modalidad);
		}
		aprobacionRequisitosTecnicos.setAprobacionModalidades(aprobacionRequisitosTecnicosService
				.getAprobacionRequisitosTecnicosModalidad(aprobacionRequisitosTecnicos));
		return aprobacionRequisitosTecnicos;
	}

	public void guardar(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		crudServiceBean.saveOrUpdate(aprobacionRequisitosTecnicos);
	}

	private AprobacionRequisitosTecnicosModalidad guardarAprobacionRequisitosTecnicosModalidad(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos, ModalidadGestionDesechos modalidad) {
		AprobacionRequisitosTecnicosModalidad aprobMod = new AprobacionRequisitosTecnicosModalidad();
		aprobMod.setModalidad(modalidad);
		aprobMod.setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicos);
		return crudServiceBean.saveOrUpdate(aprobMod);
	}

	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicos(String proyecto) throws ServiceException {
		return aprobacionRequisitosTecnicosService.getAprobacionRequisitosTecnicos(proyecto);
	}
	
	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicosByProyectoLicenciaAmbiental(String proyecto) throws ServiceException {
		return aprobacionRequisitosTecnicosService.findArtByProyectoLicenciaAmbiental(proyecto);
	}
	
	public List<AprobacionRequisitosTecnicos> getListAprobacionRequisitosTecnicosByProyectoLicenciaAmbiental(String proyecto) throws ServiceException {
		return aprobacionRequisitosTecnicosService.findArtByProyectoLicenciaAmbientalList(proyecto);
	}

	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicosPorSolicitud(String solicitud)
			throws ServiceException {
		return aprobacionRequisitosTecnicosService.getAprobacionRequisitosTecnicosPorSolicitud(solicitud);
	}

	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicosBySolicitud(String solicitud)
			throws ServiceException {
		return aprobacionRequisitosTecnicosService.getAprobacionRequisitosTecnicosBySolicitud(solicitud);
	}
	

	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicos() throws ServiceException {
		return aprobacionRequisitosTecnicosService.getAprobacionRequisitosTecnicos("ma");
	}

	public AprobacionRequisitosTecnicos recuperarCrearAprobacionRequisitosTecnicos(long idProceso, Usuario usuario,
			boolean isLicenciaFisica, Documento licencia, Date fechaProyecto, UbicacionesGeografica provincia, boolean voluntario)
			throws ServiceException {
		String proyecto = aprobacionRequisitosTecnicosService.getProyecto(idProceso, usuario);
		String solicitud = getSolicitud(idProceso, usuario);

		AprobacionRequisitosTecnicos aprobacion = aprobacionRequisitosTecnicosService
				.getAprobacionRequisitosTecnicosPorSolicitud(solicitud);
		if (aprobacion == null) {
			aprobacion = new AprobacionRequisitosTecnicos();
			aprobacion.setProyecto(proyecto);
			aprobacion.setUsuario(usuario);
			aprobacion.setTipoEstudio(aprobacionRequisitosTecnicosService.getTipoEstudio(idProceso, usuario));
			aprobacion.setAreaResponsable(aprobacionRequisitosTecnicosService.getAreaResponsable(idProceso, usuario));
			aprobacion.setNombreProyecto(aprobacionRequisitosTecnicosService.getNombreProyecto(idProceso, usuario));
			aprobacion.setIniciadoPorNecesidad(aprobacionRequisitosTecnicosService.isProcesoIniciadoVoluntariamente(
					idProceso, usuario));
			aprobacion.setTieneLicenciaFisica(isLicenciaFisica);
			aprobacion.setFechaProyecto(fechaProyecto);
			aprobacion.setProvincia(provincia);
			aprobacion.setVoluntario(voluntario);
			aprobacion.setSolicitud(solicitud);
			aprobacion = asignarVariablesSiProyectoExiste(proyecto, aprobacion);

			if (isLicenciaFisica) {
				documentosFacade.guardarDocumentoTareaProceso(licencia, idProceso, 0L);
				aprobacion.setDocumentoLicenciaFisica(licencia);
			}
			
			crudServiceBean.saveOrUpdate(aprobacion);
		}
		return aprobacion;
	}

	public AprobacionRequisitosTecnicos recuperarCrearAprobacionRequisitosTecnicos(long idProceso, Usuario usuario)
			throws ServiceException {
		return recuperarCrearAprobacionRequisitosTecnicos(idProceso, usuario, false, null, null, null,false);

	}

	private AprobacionRequisitosTecnicos asignarVariablesSiProyectoExiste(String codigoProyecto,
			AprobacionRequisitosTecnicos aprobacion) throws ServiceException {
		try {
			ProyectoLicenciamientoAmbiental proyecto = aprobacionRequisitosTecnicosService
					.buscarProyecto(codigoProyecto);
			if (proyecto != null) {
				aprobacion.setGestion(proyecto.getGestionaDesechosPeligrosos() != null ? proyecto
						.getGestionaDesechosPeligrosos() : false);
				aprobacion.setTransporte(proyecto.getTransporteSustanciasQuimicasPeligrosos() != null ? proyecto
						.getTransporteSustanciasQuimicasPeligrosos() : false);
				aprobacion.setNombreProyecto(proyecto.getNombre());
				aprobacion.setProvincia(proyecto.getPrimeraProvincia());
				// 1 ex ante , 2 ex post
				aprobacion.setTipoEstudio(proyecto.getTipoEstudio().getId());
				if (aprobacion.getAreaResponsable() == null) {
					aprobacion.setAreaResponsable(entidadResponsableFacade
							.buscarAreaDireccionProvincialPorUbicacion(proyecto.getPrimeraProvincia()));
				}

			}
			return aprobacion;
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un error al recuperar el proyecto.", e);
		}
	}

	public AprobacionRequisitosTecnicos recuperarAprobacionRequisitosTecnicos(long idProceso, Usuario usuario)
			throws ServiceException {
		String proyecto = aprobacionRequisitosTecnicosService.getProyecto(idProceso, usuario);
		AprobacionRequisitosTecnicos aprobacion = aprobacionRequisitosTecnicosService
				.getAprobacionRequisitosTecnicos(proyecto);
		aprobacion.getAreaResponsable();
		return aprobacion;
	}

	public Map<String, Object> obtenerVariablesAprobacionRequisitosTecnicosRequisitosPrevios(Usuario usuario,
			String codigoProyecto, Integer tipoEstudio, String codigoInecProvincia, boolean isCategoriaII,
			String tramiteResolver) throws ServiceException {
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();

		Area areaResponsable = entidadResponsableFacade
				.buscarAreaDireccionProvincialPorUbicacion(ubicacionGeograficaFacade
						.buscarUbicacionPorCodigoInec(codigoInecProvincia));

		Usuario directorResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getDirectorProvincial(
				areaResponsable).getNombre());

		Usuario coordinadorResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getCoordinadorProvincial(
				areaResponsable).getNombre());

		Usuario subsecretarioResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getDirectorPlantaCentral(
				"role.area.subsecretario.calidad.ambiental").getNombre());

		Usuario responsableJuridico = areaFacade
				.getUsuarioPorRolArea("role.area.coordinador.Juridico", areaResponsable);

		params.put("rg_coordinadorControl", coordinadorResponsable.getNombre());
		params.put("rg_directorControl", directorResponsable.getNombre());
		params.put("rg_subsecretaria", subsecretarioResponsable.getNombre());
		params.put("rg_responsableJuridico", responsableJuridico.getNombre());
		params.put("rg_esDireccionProvincial", true);
		params.put("rg_poseeLicencia", false);
		params.put("rg_esCategoriaII", isCategoriaII);
		// 1 ex ante , 2 ex post
		params.put("rg_tipoEstudio", tipoEstudio);
		params.put("rg_areaResponsable", areaResponsable.getId());

		params.put("rg_procedureResolverClass", tramiteResolver);
		// Mensajes
		params.put("rg_bodyNotificacionProcesoAdministrativo", mensajeFacade.recuperarValorMensajeNotificacion(
				"bodyNotificacionProcesoAdministrativoART", new Object[] { codigoProyecto }));
		params.put("rg_asuntoNotificacionProcesoAdministrativo", mensajeFacade.recuperarValorMensajeNotificacion(
				"asuntoNotificacionProcesoAdministrativoART", new Object[] {}));
		params.put("rg_asuntoNotificacionPronunciamiento", mensajeFacade.recuperarValorMensajeNotificacion(
				"asuntoNotificacionPronunciamientoART", new Object[] {}));
		params.put("rg_bodyNotificacionPronunciamiento", mensajeFacade.recuperarValorMensajeNotificacion(
				"bodyNotificacionPronunciamientoART", new Object[] { codigoProyecto }));
		params.put(
				"rg_bodyNotificacionDirector",
				mensajeFacade.recuperarValorMensajeNotificacion("bodyNotificacionDirectorART", new Object[] {
						coordinadorResponsable.getPersona().getNombre(), codigoProyecto }));
		params.put("rg_asuntoNotificacionDirector",
				mensajeFacade.recuperarValorMensajeNotificacion("asuntoNotificacionDirectorART", new Object[] {}));
		params.put(
				"rg_bodyNotificacionCoordinador",
				mensajeFacade.recuperarValorMensajeNotificacion("bodyNotificacionCoordinadorART", new Object[] { " ",
						" " }));
		params.put("rg_asuntoNotificacionCoordinador",
				mensajeFacade.recuperarValorMensajeNotificacion("asuntoNotificacionCoordinadorART", new Object[] {}));
		params.put(
				"rg_bodyNotificacionSubsecretaria",
				mensajeFacade.recuperarValorMensajeNotificacion("bodyNotificacionSubsecretariaART", new Object[] {
						directorResponsable.getPersona().getNombre(), codigoProyecto }));
		params.put("rg_asuntoNotificacionSubsecretaria",
				mensajeFacade.recuperarValorMensajeNotificacion("asuntoNotificacionSubsecretariaART", new Object[] {}));
		params.put("rg_bodyNotificacionProponenteInformacionAclaratoria", mensajeFacade
				.recuperarValorMensajeNotificacion("bodyNotificacionProponenteInformacionAclaratoriaART",
						new Object[] { codigoProyecto }));
		params.put("rg_asuntoNotificacionProponenteInformacionAclaratoria", mensajeFacade
				.recuperarValorMensajeNotificacion("asuntoNotificacionProponenteInformacionAclaratoriaART",
						new Object[] {}));

		params.put("rg_url", Constantes.getWorkloadServiceURL());
		params.put("rg_numSolicitud", generarNumeroSolicitud());

		return params;
	}

	public void iniciarProceso(Usuario usuario, MaeLicenseResponse registroAmbiental, String cedula,
			Class<?> tramiteResolver, boolean isLicenciaFisica, Documento licenciaFisica, Date fechaProyecto)
			throws ServiceException {

		String codigoInecCanton = registroAmbiental.getCodigoInecProvincia(); //se cambio el WS devuelve el codigo de la parroquia en el atributo codigoInecProvincia 
		if(registroAmbiental.getCodigoInecProvincia().length() == 6 )
			codigoInecCanton = codigoInecCanton.substring(0, 4);
			
		UbicacionesGeografica provincia = ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(codigoInecCanton);
		Area areaResponsable = null;
		if(provincia.getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
			areaResponsable = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
		} else {
			areaResponsable = entidadResponsableFacade.buscarAreaDireccionZonalPorUbicacion(provincia);
		}
		
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();

		params.put(Constantes.CODIGO_PROYECTO, registroAmbiental.getCodigoProyecto());
		params.put("proponente", usuario.getNombre());
		params.put("esDireccionProvincial", true);
		params.put("poseeLicencia", registroAmbiental.isLicencia());
		params.put("esHidrocarburo",
				registroAmbiental.isIsHidrocarburos() == null ? false : registroAmbiental.isIsHidrocarburos());
		params.put(
				"esCategoriaII",
				registroAmbiental.getCategoria() == null ? false : (registroAmbiental.getCategoria()
						.equals(TIPO_CATEGORIA_II)));
		// 1 ex ante , 2 ex post
		int tipoEstudio = (registroAmbiental.getCodigoTipoEstudio() != null) ? Integer.valueOf(registroAmbiental
				.getCodigoTipoEstudio()) : 1;
		params.put(NOMBRE_VARIABLE_TIPO_ESTUDIO, tipoEstudio);
		params.put(NOMBRE_VARIABLE_AREA_RESPONSABLE, areaResponsable.getId());
		params.putAll(agregarParametrosNotificacionesProceso(registroAmbiental.getCodigoProyecto(), areaResponsable));
		params.putAll(agregarResponsablesProyecto(areaResponsable));
		params.put(Constantes.VARIABLE_PROCESO_TRAMITE_RESOLVER, tramiteResolver.getName());
		params.put(NOMBRE_PROYECTO, registroAmbiental.getNombreProyecto());

		params.put(NOMBRE_VARIABLE_PROCESO_INICIO_DESDE_REGISTRO, false);
		// SI ES UN PROYECTO EXTERNO, SI ES UN PROYECTO DEL SISTEMA NO INICIAR
		// ESTE PARAMETRO
		params.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, usuario.getNombre());

		params.put(NOMBRE_VARIABLE_URL_ASIGNACION_AUTOMATICA, Constantes.getWorkloadServiceURL());
		params.put(
				NOMBRE_VARIABLE_REQUEST_BODY,
				aprobacionRequisitosTecnicosService.getRequestBodyWS(
						Constantes.getRoleAreaName(ROLE_TECNICO_CALIDAD_AMBIENTAL), areaResponsable));
		String numeroSolicitud = generarNumeroSolicitud();
		params.put(AprobacionRequisitosTecnicos.VARIABLE_NUMERO_SOLICITUD, numeroSolicitud);

		if (isLicenciaFisica) {
			try {
				licenciaFisica.setNombreTabla(AprobacionRequisitosTecnicos.class.getSimpleName());
				licenciaFisica = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
						numeroSolicitud, 
						AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_PROCESO_APROBACION_REQUISITOS_TECNICOS, 0L, 
						licenciaFisica, TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS);
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		
		long idProceso;
		try {
			idProceso = procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS,
					numeroSolicitud, params);
		} catch (JbpmException e) {
			throw new ServiceException(e);
		}
		recuperarCrearAprobacionRequisitosTecnicos(idProceso, usuario, isLicenciaFisica, licenciaFisica, fechaProyecto,
				provincia, true);
	}

	public String generarNumeroSolicitud() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SOL-ART-" + secuenciasFacade.getCurrentYear() + "-"
					+ secuenciasFacade.getNextValueDedicateSequence("SOLICITUD_APROBACION_REQUISITOS_TECNICOS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private Map<String, Object> agregarParametrosNotificacionesProceso(String codigoProyecto, Area areaResponsable)
			throws ServiceException {

		Usuario directorResponsable = null;
		if (areaResponsable.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			directorResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getDirectorProvincial(
				areaResponsable).getNombre());
		} else {
			directorResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getDirectorProvincial(
				areaResponsable.getArea()).getNombre());//el director pertenece a la direccion zonal.
		}

		Usuario coordinadorResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getCoordinadorProvincial(
				areaResponsable).getNombre());

		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put("bodyNotificacionProcesoAdministrativo", mensajeFacade.recuperarValorMensajeNotificacion(
				"bodyNotificacionProcesoAdministrativoART", new Object[]{codigoProyecto }));
		params.put("asuntoNotificacionProcesoAdministrativo", mensajeFacade.recuperarValorMensajeNotificacion(
				"asuntoNotificacionProcesoAdministrativoART", new Object[] {}));
		params.put("asuntoNotificacionPronunciamiento", mensajeFacade.recuperarValorMensajeNotificacion(
				"asuntoNotificacionPronunciamientoART", new Object[] {}));
		params.put("bodyNotificacionPronunciamiento", mensajeFacade.recuperarValorMensajeNotificacion(
				"bodyNotificacionPronunciamientoART", new Object[]{codigoProyecto }));
		params.put(
				"bodyNotificacionDirector",
				mensajeFacade.recuperarValorMensajeNotificacion("bodyNotificacionDirectorART", new Object[]{
						coordinadorResponsable.getPersona().getNombre(), codigoProyecto }));
		params.put("asuntoNotificacionDirector",
				mensajeFacade.recuperarValorMensajeNotificacion("asuntoNotificacionDirectorART", new Object[] {}));

		params.put(
				"bodyNotificacionSubsecretaria",
				mensajeFacade.recuperarValorMensajeNotificacion("bodyNotificacionSubsecretariaART", new Object[]{
						directorResponsable.getPersona().getNombre(), codigoProyecto }));
		params.put("asuntoNotificacionSubsecretaria",
				mensajeFacade.recuperarValorMensajeNotificacion("asuntoNotificacionSubsecretariaART", new Object[] {}));
		params.put("bodyNotificacionProponenteInformacionAclaratoria", mensajeFacade.recuperarValorMensajeNotificacion(
				"bodyNotificacionProponenteInformacionAclaratoriaART", new Object[]{codigoProyecto }));
		params.put("asuntoNotificacionProponenteInformacionAclaratoria", mensajeFacade
				.recuperarValorMensajeNotificacion("asuntoNotificacionProponenteInformacionAclaratoriaART",
						new Object[] {}));
		return params;
	}

	private Map<String, Object> agregarResponsablesProyecto(Area areaResponsable) throws ServiceException {
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put("coordinadorControl", areaFacade.getCoordinadorProvincial(areaResponsable).getNombre());
		Usuario directorResponsable = null;
		if (areaResponsable.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
			directorResponsable = areaFacade.getDirectorProvincial(areaResponsable);
		} else {
			directorResponsable = areaFacade.getDirectorProvincial(areaResponsable.getArea());//el director pertenece a la direccion zonal.
		}
		params.put("directorControl", directorResponsable.getNombre());
		params.put("subsecretaria", areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental").getNombre());
		
		if(areaResponsable.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
			areaResponsable = areaResponsable.getArea();
		params.put("responsableJuridico",
				areaFacade.getUsuarioPorRolArea("role.area.coordinador.Juridico", areaResponsable).getNombre());
		return params;
	}

	private String getSolicitud(long idProceso, Usuario usuario) throws ServiceException {
		try {
			Map<String, Object> parametros = procesoFacade.recuperarVariablesProceso(usuario, idProceso);
			return (String) parametros.get(AprobacionRequisitosTecnicos.VARIABLE_NUMERO_SOLICITUD);
		} catch (JbpmException e) {
			throw new ServiceException("Error al recuperar las variables del proceso", e);
		}
	}

	public void iniciarProcesoRegistroGenerador(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos,
			Usuario usuario, Class<?> tramiteResolver) throws ServiceException {
		String numeroSolicitud = aprobacionRequisitosTecnicos.getProyecto();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sujetoControl", aprobacionRequisitosTecnicos.getUsuario().getNombre());
		params.put("tipoTramite", true);
		params.put("accion", "emision.jsf");
		params.put(Constantes.VARIABLE_PROCESO_TRAMITE_RESOLVER, tramiteResolver.getName());
		params.put(GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD, numeroSolicitud);
		try {
			procesoFacade
					.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, numeroSolicitud, params);
		} catch (JbpmException e) {
			throw new ServiceException(e);
		}
	}

	public void completarTarea(Map<String, Object> parametros, Long idTarea, Long idProceso, Usuario usuario)
			throws JbpmException {
		procesoFacade.aprobarTarea(usuario, idTarea, idProceso, parametros);

	}

	public void enviarAprobacionRequisitosTecnicos(long processInstanceIdTask, Usuario usuario, long taskId )
			throws JbpmException {
		procesoFacade.aprobarTarea(usuario, taskId, processInstanceIdTask, null);
		procesoFacade.envioSeguimientoLicenciaAmbiental(usuario, processInstanceIdTask);
	}

	public void enviarAprobacionRequisitosTecnicosRevision(long processInstanceIdTask, Usuario usuario, long taskId,
			String codigoProyecto) throws JbpmException, ServiceException {
		procesoFacade.aprobarTarea(usuario, taskId, processInstanceIdTask, null);

		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put(
				"bodyNotificacionCoordinador",
				mensajeFacade.recuperarValorMensajeNotificacion("bodyNotificacionCoordinadorART", new Object[] {
						getNombreTecnicoResponsable(processInstanceIdTask, usuario), codigoProyecto }));
		params.put("asuntoNotificacionCoordinador",
				mensajeFacade.recuperarValorMensajeNotificacion("asuntoNotificacionCoordinadorART", new Object[] {}));
		procesoFacade.modificarVariablesProceso(usuario, processInstanceIdTask, params);
	}

	private String getNombreTecnicoResponsable(long idProceso, Usuario usuario) throws ServiceException {
		try {
			Map<String, Object> parametros = procesoFacade.recuperarVariablesProceso(usuario, idProceso);
			String tecnico = (String) parametros.get("tecnico");
			Usuario usuarioTecnico = usuarioFacade.buscarUsuarioCompleta(tecnico);
			return usuarioTecnico.getPersona().getNombre();

		} catch (JbpmException e) {
			throw new ServiceException("Error al recuperar las variables del proceso", e);
		}

	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public boolean obtenerFlujosDeProyecto(String nombreVariableProceso, Usuario usuario) throws Exception {

		List<ProcessInstanceLog> listProcessProject = procesoFacade.getProcessInstancesLogsVariableValue(usuario,
				nombreVariableProceso, usuario.getNombre());
		boolean existe = false;
		for (ProcessInstanceLog processInstanceLog : listProcessProject) {
			if (processInstanceLog.getStatus() == 1) {
				existe = true;
				break;
			}

		}
		return existe;
	}

	public List<TipoEliminacionDesecho> buscarTipoEliminacionDesechosPadres(String filtro, List<Integer> modalidades) {
		List<TipoEliminacionDesecho> desechos = new ArrayList<TipoEliminacionDesecho>();

		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM TipoEliminacionDesecho t WHERE (lower(t.nombre) like lower(:filtro) OR lower(t.clave) like lower(:filtro)) AND t.codigoModalidad IN :modalidades ORDER BY t.id");
			query.setParameter("filtro", "%" + filtro.trim() + "%");
			query.setParameter("modalidades", modalidades);

			desechos = query.getResultList();
		} else {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"FROM TipoEliminacionDesecho t WHERE t.tipoEliminacionDesecho = null ORDER BY t.id");
			desechos = query.getResultList();
		}
		initIsTipoEliminacionFinal(desechos);
		return desechos;
	}

	private void initIsTipoEliminacionFinal(List<TipoEliminacionDesecho> tipoEliminacionDesechos) {
		if (tipoEliminacionDesechos != null) {
			for (TipoEliminacionDesecho tipoEliminacionDesecho : tipoEliminacionDesechos) {
				tipoEliminacionDesecho.isTipoEliminacionFinal();
			}
		}
	}

	public List<TipoEliminacionDesecho> buscarTipoEliminacionDesechoPorPadre(TipoEliminacionDesecho padre) {
		List<TipoEliminacionDesecho> desechos = (List<TipoEliminacionDesecho>) crudServiceBean.getEntityManager()
				.createQuery("From TipoEliminacionDesecho t where t.tipoEliminacionDesecho =:padre order by t.id")
				.setParameter("padre", padre).getResultList();
		initIsTipoEliminacionFinal(desechos);
		return desechos;
	}

	public List<TipoEliminacionDesecho> buscarTipoEliminacionDesechoPorModalidad(Integer idModalidad) {
		List<TipoEliminacionDesecho> desechos = new ArrayList<TipoEliminacionDesecho>();
		Query query = crudServiceBean.getEntityManager().createQuery(
				"FROM TipoEliminacionDesecho t WHERE t.codigoModalidad =:idModalidad ORDER BY t.id");
		query.setParameter("idModalidad", idModalidad);

		desechos = query.getResultList();
		initIsTipoEliminacionFinal(desechos);
		return desechos;
	}

	public Integer getCantidadVecesObservadoInformacion(Usuario usuario, long idProceso) throws ServiceException {
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, idProceso);
			Object contadorBandejaTecnicoValue = null;
			int contadorBandejaTecnico = 0;
			if (variables.containsKey("cantidadObservaciones")) {
				contadorBandejaTecnicoValue = variables.get("cantidadObservaciones").toString();
			}

			if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
				contadorBandejaTecnico = 0;
			} else {

				contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());

			}
			return contadorBandejaTecnico;
		} catch (Exception e) {
			throw new ServiceException("Error recuperado cantidad de observaciones en aprobacion requisitos técnios");
		}
	}

	@SuppressWarnings("unchecked")
	public List<AprobacionRequisitosTecnicos> findARTByCodigoByModo(Usuario usuario, String codigo, boolean esSuia, String listModalidadId){
		String campoBusqueda = "apte_proyect";
		List<AprobacionRequisitosTecnicos> lista = new ArrayList<AprobacionRequisitosTecnicos>();
		String sqlPproyecto="select distinct  atr."+campoBusqueda+", atr.apte_name_proyect, atr.apte_request  "+
							"from suia_iii.approval_technical_requirements atr inner join suia_iii.approval_technical_requirements_modality atrm on atr.apte_id = atrm.apte_id "+
							" where "
							+ "atr."+campoBusqueda+" = '"+codigo+"' and "
									+ "apte_creator_user = '"+usuario.getNombre()+"'";
		if(listModalidadId != null && !listModalidadId.isEmpty()){
			sqlPproyecto += " and atrm.wmmo_id in ("+listModalidadId+") ";
		}

		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
		List<Object>  resultPro = new ArrayList<Object>();
		resultPro= queryProyecto.getResultList();

		if (resultPro!=null) {
			for(Object a: resultPro)
			{
				AprobacionRequisitosTecnicos objART;
				try {
					String solicitud = ((Object[]) a) [2].toString();
					if(esSuia)
						objART = getAprobacionRequisitosTecnicosPorSolicitud(solicitud);
					else
						objART = getAprobacionRequisitosTecnicosBySolicitud(solicitud);
					if(objART != null){
						// verifico si el ART encontrado esta finalizado
						// obtengo los valores de las variables del campo rg_codigoProyecto del bpm para encontrat el processinstanceid
						List<ProcessInstanceLog> listaProces = procesoFacade.getProcessInstancesLogsVariableValue(usuario, "numeroSolicitud", objART.getSolicitud());
						if (listaProces.size() > 0){
							for (ProcessInstanceLog processInstanceLog : listaProces) {
								if (processInstanceLog.getProcessId().equals("Suia.AprobracionRequisitosTecnicosGesTrans2")){
									ProcessInstanceLog  proceso = procesoFacade.getProcessInstanceLog(usuario, processInstanceLog.getProcessInstanceId());
									if (proceso.getStatus().equals(2) ){
										lista.add(objART);
										//break;
									}
								}
							}
						}
					}
				} catch (ServiceException e) {
					e.printStackTrace();
				} catch (JbpmException e) {
					e.printStackTrace();
				}
			}
		}
		return lista;
	}
//	---WR
	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicosPorId(Integer id)
			throws ServiceException {
		return aprobacionRequisitosTecnicosService.getAprobacionRequisitosTecnicosPorId(id);
	}
}
