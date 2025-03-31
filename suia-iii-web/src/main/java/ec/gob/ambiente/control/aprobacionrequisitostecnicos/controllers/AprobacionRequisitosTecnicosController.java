/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ArtGeneralBean;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.DesechoPeligrosoTransporteFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadCoprocesamientoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadDisposicionFinalFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadIncineracionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadReciclajeFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadReusoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadTratamientoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ModalidadesFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.RecoleccionTransporteDesechosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.ValidacionesPagesAprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.AprobacionRequisitosTecnicosService;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.MenuAprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.tramiteresolver.AprobacionRequisitosTecnicosTramiteResolver;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> Clase controlador para el tipo de tramite. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 05/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AprobacionRequisitosTecnicosController implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(AprobacionRequisitosTecnicosController.class);

	public static final String UBICACION = "control/aprobacionRequisitosTecnicos";

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	@EJB
	private AprobacionRequisitosTecnicosService aprobacionRequisitosTecnicosService;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	private List<String> seccionesRequeridas;

	@Getter
	@Setter
	private List<String> secciones;

	@EJB
	private ModalidadesFacade modalidadesFacade;

	@EJB
	private ValidacionesPagesAprobacionRequisitosTecnicosFacade validacionesPagesAprobacionRequisitosTecnicosFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	private Map<String, Object> variables;
	
	@EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
    private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private EntidadResponsableFacade entidadResponsableFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private MensajeNotificacionFacade mensajeFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal;
	
	@Getter
	@Setter
	private Boolean validarSeleccionModalidades;
	
	@Getter
	@Setter
	private List<String> listaModalidadesPermitidasCiiu;

	@PostConstruct
	public void init() {
		try {

			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			validarSeleccionModalidades = false;
			
			Boolean esproyectoCoa = false;
			try {
				esproyectoCoa = Boolean.parseBoolean(variables.get("vieneRcoa").toString()) ;
			} catch (Exception e) {
				esproyectoCoa = false;
			}
			
			AprobacionRequisitosTecnicos atr = aprobacionRequisitosTecnicosService
					.getAprobacionRequisitosTecnicos(bandejaTareasBean.getTarea().getProcedure());
			if(atr == null) {
				atr = aprobacionRequisitosTecnicosService.getAprobacionRequisitosTecnicosPorSolicitud(bandejaTareasBean.getTarea().getProcedure());
			}
			
			ProyectoLicenciaCoa proyecto = new ProyectoLicenciaCoa();
			if(esproyectoCoa) {
				if(atr.getVoluntario() == null) {
					atr.setVoluntario(false); // actualiza el campo para proyetos RCOA que iniciaron ART desde el registro preliminar
					aprobacionRequisitosTecnicosFacade.guardar(atr);
				}
				
				String variableDP = (String) variables.get("esDireccionProvincial");//verifico si la varibale ya existe para no actualizar cada vez que se ingresa al formulario
				Boolean actualizarVariablesBPM = (variableDP != null) ? false : true;
				
				proyecto = proyectoLicenciaCoaFacade.buscarProyecto(bandejaTareasBean.getTarea().getProcedure());
				
				Area areaResponsable = atr.getAreaResponsable();
				if(atr.getAreaResponsable() == null) {
					ProyectoLicenciaCoaUbicacion ubicacionProyecto = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto);
					UbicacionesGeografica ubicacionPrincipal = ubicacionProyecto.getUbicacionesGeografica();
					
					areaResponsable = areaFacade.getAreaCoordinacionZonal(ubicacionPrincipal.getUbicacionesGeografica());
					
					atr.setAreaResponsable(areaResponsable);
					aprobacionRequisitosTecnicosFacade.guardar(atr);
					actualizarVariablesBPM = true;
				}
				
				//inicializo el tipo de requisitos que vienen desde informacion preliminar
				atr.setTransporte(proyecto.getTransportaSustanciasQuimicas());
				atr.setGestion(proyecto.getGestionDesechos());
				aprobacionRequisitosTecnicosFacade.guardar(atr);
				
				if(actualizarVariablesBPM) {
					Map<String, Object> params = new ConcurrentHashMap<String, Object>();
					params.put("esDireccionProvincial", true);
					params.put("poseeLicencia", true); //--suia_iii.categories
					params.put("esHidrocarburo", false);//--suia_iii.sector_types st
					params.put("esCategoriaII",false);//--
					int tipoEstudio = 1;//--
					params.put("tipoEstudio", tipoEstudio);//--
					params.put("areaResponsable", 25);//--
					MaeLicenseResponse registroAmbiental = new MaeLicenseResponse();
					params.putAll(agregarParametrosNotificacionesProceso(registroAmbiental.getCodigoProyecto(), areaResponsable));
					params.putAll(agregarResponsablesProyecto(areaResponsable));
					params.put(Constantes.VARIABLE_PROCESO_TRAMITE_RESOLVER, AprobacionRequisitosTecnicosTramiteResolver.class.getName());//--
					params.put("nombreProyecto", "APROBACION DE REQUISITOS TECNICOS");

					params.put("esRequisitosPrevios", false);
					params.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
					params.put("urlAsignacionAutomatica", Constantes.getWorkloadServiceURL());
					params.put("area", areaResponsable.getAreaName());
					params.put("rol", Constantes.getRoleAreaName("role.area.tecnico.registro.generador"));
					params.put("requestBody",
							aprobacionRequisitosTecnicosService.getRequestBodyWS(
									Constantes.getRoleAreaName("role.area.tecnico.registro.generador"), areaResponsable));
					String numeroSolicitud = generarNumeroSolicitud();
					params.put(AprobacionRequisitosTecnicos.VARIABLE_NUMERO_SOLICITUD, atr.getSolicitud());
					
					try {
//						Long idProceso = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS,
//								numeroSolicitud, params);
						procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
					} catch (JbpmException e) {
						throw new ServiceException(e);
					}
				}
			}

			 
			aprobacionRequisitosTecnicosBean.setAprobacionRequisitosTecnicos(atr);
			cargarModalidadesSeleccionadas();
			aprobacionRequisitosTecnicosBean.setModalidades(modalidadesFacade.getModalidades());
			cargarDatosRequeridos();
			aprobacionRequisitosTecnicosBean.setHabilitarTipoRequisitos(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos().isIniciadoPorNecesidad());
			if(esproyectoCoa) {
				aprobacionRequisitosTecnicosBean.setHabilitarTipoRequisitos(false);
				proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
				buscarModalidadesPorActividadCiiu();
			}
			validarOtros();
			validarCompletado();
			ArtGeneralBean artGeneralBean = JsfUtil.getBean(ArtGeneralBean.class);
			artGeneralBean.setAprobacion(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("No se logró cargar los datos iniciales..");
		}
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
		if (areaResponsable.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			directorResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getDirectorProvincial(
					areaResponsable.getArea()).getNombre());
		else
			directorResponsable = usuarioFacade.buscarUsuarioCompleta(areaFacade.getDirectorProvincial(
					areaResponsable).getNombre());

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
		if (areaResponsable.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			params.put("directorControl", areaFacade.getDirectorProvincial(areaResponsable.getArea()).getNombre());
		else	
			params.put("directorControl", areaFacade.getDirectorProvincial(areaResponsable).getNombre());
		
		params.put("subsecretaria", areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental").getNombre());
		
		if (areaResponsable.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			params.put("responsableJuridico", areaFacade.getUsuarioPorRolArea("role.area.coordinador.Juridico", areaResponsable.getArea()).getNombre());
		else
			params.put("responsableJuridico", areaFacade.getUsuarioPorRolArea("role.area.coordinador.Juridico", areaResponsable).getNombre());
		return params;
	}

	public List<MenuAprobacionRequisitosTecnicos> creacionMenu(boolean isModoVer) {
		try {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			if (params.containsKey("tipo")) {
				aprobacionRequisitosTecnicosBean.setTipo(params.get("tipo"));
				if (params.get("tipo").equals("revisar")) {
					aprobacionRequisitosTecnicosBean.setRevisar(true);
				} else {
					aprobacionRequisitosTecnicosBean.setRevisar(false);
				}
			}
			String extensionModo = isModoVer ? "Ver.jsf" : ".jsf";
			String extensionModoDefault = isModoVer ? "AnalisisRevision.jsf" : ".jsf";
			aprobacionRequisitosTecnicosBean.setMenu(new ArrayList<MenuAprobacionRequisitosTecnicos>());
			aprobacionRequisitosTecnicosBean.getMenu().add(
					new MenuAprobacionRequisitosTecnicos(UBICACION + "/default" + extensionModoDefault,
							"Tipo de requisitos"));

			if ((aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte())
					|| (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades() != null && aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos().getModalidades()
							.contains(modalidadesFacade.getModalidadTransporte()))) {
				aprobacionRequisitosTecnicosBean
						.getMenu()
						.add(new MenuAprobacionRequisitosTecnicos(UBICACION + "/informacionPatioManiobra"
								+ extensionModo,
								"Información general de la ubicación del patio de maniobra , almacenamiento temporal u oficina matriz"));
				aprobacionRequisitosTecnicosBean.getMenu().add(
						new MenuAprobacionRequisitosTecnicos(UBICACION + "/requisitosVehiculo" + extensionModo,
								"Requisitos para el Vehículo"));
				aprobacionRequisitosTecnicosBean.getMenu().add(
						new MenuAprobacionRequisitosTecnicos(UBICACION + "/requisitosConductor" + extensionModo,
								"Requisitos para el Conductor"));
				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte()) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/sustanciasQuimicasPeligrosas"
									+ extensionModo, "Sustancias químicas peligrosas"));
				}
				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()
						&& aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
								.contains(modalidadesFacade.getModalidadTransporte())) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/desechoPeligrosoTransporte"
									+ extensionModo, "Desechos peligrosos"));
					if (validacionesPagesAprobacionRequisitosTecnicosFacade
							.isPageRecoleccionTransporteDesechosVisible(aprobacionRequisitosTecnicosBean
									.getAprobacionRequisitosTecnicos())) {
						aprobacionRequisitosTecnicosBean.getMenu().add(
								new MenuAprobacionRequisitosTecnicos(UBICACION + "/recoleccionYTransporteDesechos"
										+ extensionModo, "Recolección y trasporte desechos"));
					}
				}

				if (!isModoVer) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/informativo" + extensionModo,
									"Informativo"));
				}
			}
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {

				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().size() > 1
						|| (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().size() > 0 
								&& !(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
								.contains(modalidadesFacade.getModalidadTransporte())))) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/recepcionDesechosPeligrosos"
									+ extensionModo, "Recepción de desechos peligrosos"));
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/gestion/almacenamientoTemporal"
									+ extensionModo, "Almacenamiento temporal"));

					if (!(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()
							.isGestionSoloModalidadOtros() || aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos().isGestionSoloConModalidadOtrosYTransporte())) {
						aprobacionRequisitosTecnicosBean
								.getMenu()
								.add(new MenuAprobacionRequisitosTecnicos(UBICACION
										+ "/gestion/eliminacionDisposicionFinal" + extensionModo,
										"Tipo de eliminación o disposición final de los desechos generados en la gestión"));
					}

				}

				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
						.contains(modalidadesFacade.getModalidadReciclaje()))
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/modalidadReciclaje" + extensionModo,
									"Modalidad reciclaje"));

				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
						.contains(modalidadesFacade.getModalidadReuso()))
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/modalidadReuso" + extensionModo,
									"Modalidad reuso"));

				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
						.contains(modalidadesFacade.getModalidadTratamiento())) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/modalidadTratamiento" + extensionModo,
									"Modalidad tratamiento"));
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION
									+ "/modalidadTratamientoCalendarioActividades" + extensionModo,
									"Programa calendarizado de actividades  para el tratamiento"));
				}
				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
						.contains(modalidadesFacade.getModalidadIncineracion())) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/modalidadIncineracion" + extensionModo,
									"Modalidad incineración"));
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION
									+ "/modalidadIncineracionCalendarioActividades" + extensionModo,
									"Programa calendarizado de actividades  para la incineración"));
				}

				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
						.contains(modalidadesFacade.getModalidadCoprocesamiento())) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/modalidadCoprocesamiento"
									+ extensionModo, "Modalidad coprocesamiento"));
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION
									+ "/modalidadCoprocesamientoCalendarioActividades" + extensionModo,
									"Programa calendarizado de actividades  para el coprocesamiento"));
				}
				if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
						.contains(modalidadesFacade.getModalidadDisposicionFinal())) {
					aprobacionRequisitosTecnicosBean.getMenu().add(
							new MenuAprobacionRequisitosTecnicos(UBICACION + "/modalidadDisposicionFinal"
									+ extensionModo, "Modalidad disposición final"));
				}

			}

			if (!isModoVer)
				aprobacionRequisitosTecnicosBean.getMenu().add(
						new MenuAprobacionRequisitosTecnicos(UBICACION + "/envioAprobacionRequisitos" + extensionModo,
								"Enviar aprobación requisitos técnicos"));

		} catch (Exception e) {
			LOG.error("Error en la creación del menú", e);
		}
		return aprobacionRequisitosTecnicosBean.getMenu();
	}

	/**
	 * 
	 * <b> Metodo que valida si se selecciono otros en las modalidades. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 08/06/2015]
	 * </p>
	 * 
	 */
	public void validarOtros() {
		//aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().setOtraModalidad("");
		aprobacionRequisitosTecnicosBean.setMostrarOtros(false);
		for (ModalidadGestionDesechos catalogo : aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas()) {
			if (catalogo.equals(modalidadesFacade.getModalidadOtros())) {
				aprobacionRequisitosTecnicosBean.setMostrarOtros(true);
			}
		}
	}

	/**
	 * 
	 * <b> Metodo para ir al correspondient menu. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 08/06/2015]
	 * </p>
	 * 
	 * @return String: url de la pagina
	 */
	public String continuar(boolean isModoVer) {
		String extensionModo = isModoVer ? "Ver.jsf" : ".jsf";

		if (aprobacionRequisitosTecnicosBean.getMenu().size() <= 2) {
			JsfUtil.addMessageError("Debe seleccionar un tipo de requisito y guardar la página para continuar.");
			return "";
		}

		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte()
				|| aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
						.contains(modalidadesFacade.getModalidadTransporte())) {
			return JsfUtil.actionNavigateTo("/" + UBICACION + "/informacionPatioManiobra" + extensionModo);
		} else if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
			return JsfUtil.actionNavigateTo("/" + UBICACION + "/recepcionDesechosPeligrosos" + extensionModo);
		} else {
			JsfUtil.addMessageError("Seleccionar el tipo de requisito.");
			return "";
		}

	}

	public void guardarPagina() {
		buscarModalidadesPorActividadCiiu();
		aprobacionRequisitosTecnicosFacade.guardar(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
				aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas());
		creacionMenu(false);
		try {
			/************************************************************************************************************/
			/**
			 * ELIMINAR LAS SECCIONES CORRESPONDIENTES A LAS MODALIDADES
			 * DESMARCADAS
			 **/
			boolean eliminarModalidadReciclaje = true;
			boolean eliminarModalidadTratamiento = true;
			boolean eliminarModalidadCoprocesamiento = true;
			boolean eliminarModalidadTransporte = true;
			boolean eliminarModalidadReuso = true;
			boolean eliminarModalidadIncineracion = true;
			boolean eliminarModalidadDisposicionFinal = true;
			boolean existenAsociacionesDesechoModalidadEliminada = false;

			for (ModalidadGestionDesechos catalogo : aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas()) {
				switch (catalogo.getId()) {
				case ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE:
					eliminarModalidadReciclaje = false;
					break;
				case ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO:
					eliminarModalidadTratamiento = false;
					break;
				case ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO:
					eliminarModalidadCoprocesamiento = false;
					break;
				case ModalidadGestionDesechos.ID_MODALIDAD_TRANSPORTE:
					eliminarModalidadTransporte = false;
					break;
				case ModalidadGestionDesechos.ID_MODALIDAD_REUSO:
					eliminarModalidadReuso = false;
					break;
				case ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION:
					eliminarModalidadIncineracion = false;
					break;
				case ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL:
					eliminarModalidadDisposicionFinal = false;
					break;
				}
			}

			if (eliminarModalidadReciclaje) {

				existenAsociacionesDesechoModalidadEliminada = existenAsociacionesDesechoModalidadEliminada
						| modalidadesFacade.eliminarDeschosPantallaTipoEliminacionSiExisten(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
								ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE);
				BeanLocator.getInstance(ModalidadReciclajeFacade.class).eliminarModalidadReciclajeExistente(
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

			}
			if (eliminarModalidadTratamiento) {
				existenAsociacionesDesechoModalidadEliminada = existenAsociacionesDesechoModalidadEliminada
						| modalidadesFacade.eliminarDeschosPantallaTipoEliminacionSiExisten(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
								ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO);
				BeanLocator.getInstance(ModalidadTratamientoFacade.class).eliminarModalidadTratamientoExistente(
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			}
			if (eliminarModalidadCoprocesamiento) {
				existenAsociacionesDesechoModalidadEliminada = existenAsociacionesDesechoModalidadEliminada
						| modalidadesFacade.eliminarDeschosPantallaTipoEliminacionSiExisten(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
								ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO);
				BeanLocator.getInstance(ModalidadCoprocesamientoFacade.class)
						.eliminarModalidadCoprocesamientoExistente(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			}
			if (eliminarModalidadTransporte) {
				BeanLocator.getInstance(DesechoPeligrosoTransporteFacade.class)
						.eliminarListaDesechoPeligrosoTransporteExistentes(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
				BeanLocator.getInstance(DesechoPeligrosoTransporteFacade.class)
						.eliminarDesechoEspecialRecoleccionExistentes(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

				BeanLocator.getInstance(RecoleccionTransporteDesechosFacade.class)
						.eliminarListaLavadoContenedorExistentes(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			}
			if (eliminarModalidadReuso) {
				existenAsociacionesDesechoModalidadEliminada = existenAsociacionesDesechoModalidadEliminada
						| modalidadesFacade.eliminarDeschosPantallaTipoEliminacionSiExisten(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
								ModalidadGestionDesechos.ID_MODALIDAD_REUSO);
				BeanLocator.getInstance(ModalidadReusoFacade.class).eliminarModalidadReusoExistente(
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());

			}
			if (eliminarModalidadIncineracion) {
				existenAsociacionesDesechoModalidadEliminada = existenAsociacionesDesechoModalidadEliminada
						| modalidadesFacade.eliminarDeschosPantallaTipoEliminacionSiExisten(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
								ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION);
				BeanLocator.getInstance(ModalidadIncineracionFacade.class).eliminarModalidadIncineracionExistente(
						aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			}
			if (eliminarModalidadDisposicionFinal) {
				existenAsociacionesDesechoModalidadEliminada = existenAsociacionesDesechoModalidadEliminada
						| modalidadesFacade.eliminarDeschosPantallaTipoEliminacionSiExisten(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
								ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL);
				BeanLocator.getInstance(ModalidadDisposicionFinalFacade.class)
						.eliminarModalidadDisposicionFinalExistente(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos());
			}
			if (existenAsociacionesDesechoModalidadEliminada) {

				JsfUtil.addMessageWarning("Por favor recuerde que los desechos asociados a las modaldiades que desmacó deben ser asociadas a otro modalidad.");

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

			} else {
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}

			/************************************************************************************************************/

		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	private void cargarModalidadesSeleccionadas() {
		aprobacionRequisitosTecnicosBean.setModalidadesSeleccionadas(aprobacionRequisitosTecnicosBean
				.getAprobacionRequisitosTecnicos().getModalidades());
	}

	public void cargarDatosRequeridos() {
		seccionesRequeridas = new ArrayList<>();
		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isTransporte()) {
			seccionesRequeridas.add("informacionPatioManiobra");
			seccionesRequeridas.add("requisitosVehiculo");
			seccionesRequeridas.add("requisitosConductor");
			seccionesRequeridas.add("sustanciasQuimicasPeligrosas");
			seccionesRequeridas.add("desechoPeligrosoTransporte");
			seccionesRequeridas.add("recoleccionYTransporteDesechos");
		}
		/******************/
		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
			seccionesRequeridas.add("recepcionDesechosPeligrosos");
			seccionesRequeridas.add("almacenamientoTemporal");
			seccionesRequeridas.add("eliminacionDisposicionFinal");
			seccionesRequeridas.add("modalidadReciclaje");
			seccionesRequeridas.add("modalidadReuso");
			seccionesRequeridas.add("modalidadTratamiento");
			seccionesRequeridas.add("modalidadTratamientoCalendarioActividades");
			seccionesRequeridas.add("modalidadIncineracion");
			seccionesRequeridas.add("modalidadIncineracionCalendarioActividades");
			seccionesRequeridas.add("modalidadCoprocesamiento");
			seccionesRequeridas.add("modalidadCoprocesamientoCalendarioActividades");
			seccionesRequeridas.add("modalidadDisposicionFinal");
		}
		List<String> secciones = new ArrayList<String>();
		for (String s : seccionesRequeridas) {
			secciones.add(s);
		}
		seccionesRequeridas.clear();
		for (String varSecc : secciones) {
			boolean tieneSeccion = false;
			if (aprobacionRequisitosTecnicosBean.getMenu() != null) {
				for (MenuAprobacionRequisitosTecnicos menu : aprobacionRequisitosTecnicosBean.getMenu()) {
					tieneSeccion = menu.getPage().contains(varSecc);
					if (tieneSeccion) {
						seccionesRequeridas.add(varSecc);
						break;
					}
				}
			}
		}
	}

	public Boolean seccionCompletada(String seccion) {
		boolean tieneSeccion = false;
		boolean esCompleta = true;
		for (MenuAprobacionRequisitosTecnicos menu : aprobacionRequisitosTecnicosBean.getMenu()) {
			tieneSeccion = menu.getPage().contains(seccion);
			if (tieneSeccion) {
				esCompleta = secciones.contains(seccion);
				break;
			}
		}
		return esCompleta;
	}

	public void validarCompletado() {
		validarNumeroVehiculoYRequisitos();
		validarQueTodoDeschoEstenAlmacenados();
		validarDesechosPorModalidad();
		secciones = validacionSeccionesFacade.listaSeccionesPorClase("AprobacionRequisitosTecnicos",
				aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getId().toString());
		aprobacionRequisitosTecnicosBean.setCompletado(true);
		int i = seccionesRequeridas.size();
		while (aprobacionRequisitosTecnicosBean.isCompletado() && i > 0) {
			if (!secciones.contains(seccionesRequeridas.get(--i))) {
				aprobacionRequisitosTecnicosBean.setCompletado(false);
			}
		}
	}

	public void mostrarDialogoSeccionesIncompletas() {
		if (!aprobacionRequisitosTecnicosBean.isCompletado())
			RequestContext.getCurrentInstance().execute("PF('aprobacionRequisitosIncompleto').show();");
	}

	public String recuperarPageSiguiente(boolean isModoVer) {
		String extensionModo = isModoVer ? "Ver.jsf" : ".jsf";

		if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().size() >= 1
				&& aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(0).getId() != ModalidadGestionDesechos.ID_MODALIDAD_TRANSPORTE) {
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(0).getId() == ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE) {
				return "/control/aprobacionRequisitosTecnicos/modalidadReciclaje" + extensionModo
						+ "?faces-redirect=true";
			}
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(0).getId() == ModalidadGestionDesechos.ID_MODALIDAD_REUSO) {
				return "/control/aprobacionRequisitosTecnicos/modalidadReuso" + extensionModo + "?faces-redirect=true";
			}

			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(0).getId() == ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO) {
				return "/control/aprobacionRequisitosTecnicos/modalidadCoprocesamiento" + extensionModo
						+ "?faces-redirect=true";
			}
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(0).getId() == ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL) {
				return "/control/aprobacionRequisitosTecnicos/modalidadDisposicionFinal" + extensionModo
						+ "?faces-redirect=true";
			}
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(0).getId() == ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION) {
				return "/control/aprobacionRequisitosTecnicos/modalidadIncineracion" + extensionModo
						+ "?faces-redirect=true";
			}
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(0).getId() == ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO) {
				return "/control/aprobacionRequisitosTecnicos/modalidadTratamiento" + extensionModo
						+ "?faces-redirect=true";
			}
		}
		return "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos" + extensionModo
				+ "?faces-redirect=true";

	}

	public boolean isVisibleOpcionNavegarSiguienteEnMenuModoVer(Integer idModalidad, boolean siguienteEsCalendario,
			boolean isModoVer) {
		String page = recuperarPageSiguiente(idModalidad, siguienteEsCalendario, isModoVer);
		return !(page.contains("envioAprobacionRequisitosVer"));
	}

	public String recuperarPageSiguiente(Integer idModalidad, boolean siguienteEsCalendario, boolean isModoVer) {
		String extensionModo = isModoVer ? "Ver.jsf" : ".jsf";
		if (siguienteEsCalendario) {
			return redireccionarCalendario(idModalidad, isModoVer);
		} else {

			String page = "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos" + extensionModo
					+ "?faces-redirect=true";
			int indexActual = 0;
			for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos().getModalidades()) {
				if (modalidad.getId() == idModalidad) {
					break;
				}
				indexActual++;
			}

			ModalidadGestionDesechos modalidadSiguiente;
			if (existeUnaSiguienteModalidad(indexActual)
					&& !aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().isEmpty()) {
				modalidadSiguiente = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()
						.getModalidades().get(indexActual + 1);
			} else {
				modalidadSiguiente = null;
			}

			if (modalidadSiguiente != null && modalidadSiguiente.getId() != 7) {
				page = retornarPagePorModalidad(modalidadSiguiente, extensionModo);
			}
			return page;
		}
	}

	private boolean existeUnaSiguienteModalidad(int indexActual) {
		return ((indexActual + 1) < aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
				.size());
	}

	private boolean existeUnaAnteriorModalidad(int indexActual) {
		return (indexActual - 1) >= 0;
	}

	private String redireccionarCalendario(Integer idModalidad, boolean isModoVer) {
		String page = "";
		String extensionModo = isModoVer ? "Ver.jsf" : ".jsf";
		switch (idModalidad) {
		case 5:
			page = "/control/aprobacionRequisitosTecnicos/modalidadCoprocesamientoCalendarioActividades"
					+ extensionModo + "?faces-redirect=true";
			break;
		case 4:
			page = "/control/aprobacionRequisitosTecnicos/modalidadIncineracionCalendarioActividades" + extensionModo
					+ "?faces-redirect=true";
			break;
		case 3:
			page = "/control/aprobacionRequisitosTecnicos/modalidadTratamientoCalendarioActividades" + extensionModo
					+ "?faces-redirect=true";
			break;
		default:
			break;
		}
		return page;
	}

	public void validarTareaBpm() {

		String url = "/control/aprobacionRequisitosTecnicos/documentos/informeTecnicoArt.jsf";
		// if (!tipo.isEmpty()) {
		// url += "?tipo=" + tipo;
		// }
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
	}

	public String recuperarPageAtras(Integer idModalidad, boolean isModoVer) {
		String extensionModo = isModoVer ? "Ver.jsf" : ".jsf";

		String page = "/control/aprobacionRequisitosTecnicos/gestion/eliminacionDisposicionFinal" + extensionModo
				+ "?faces-redirect=true";
		int indexActual = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().size();
		int i = indexActual - 1;
		for (; i >= 0; i--) {
			if (aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().get(i).getId() == idModalidad) {
				break;
			}
		}

		indexActual = i;

		ModalidadGestionDesechos modalidadAnterior;
		if (existeUnaAnteriorModalidad(indexActual)
				&& !aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades().isEmpty()) {
			modalidadAnterior = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().getModalidades()
					.get(indexActual - 1);
		} else {
			modalidadAnterior = null;
		}

		if (modalidadAnterior != null && modalidadAnterior.getId() != 7) {

			if (modalidadTieneCalendario(modalidadAnterior)) {
				return redireccionarCalendario(modalidadAnterior.getId(), isModoVer);
			} else {
				page = retornarPagePorModalidad(modalidadAnterior, extensionModo);
			}

		}
		return page;

	}

	private boolean modalidadTieneCalendario(ModalidadGestionDesechos modalidad) {
		if (modalidad.getId().equals(ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION)
				|| modalidad.getId().equals(ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO)
				|| modalidad.getId().equals(ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO)) {
			return true;
		} else
			return false;
	}

	private String retornarPagePorModalidad(ModalidadGestionDesechos modalidad, String extensionModoPage) {
		String page = null;
		switch (modalidad.getId()) {
		case 1:
			page = "/control/aprobacionRequisitosTecnicos/modalidadReciclaje" + extensionModoPage
					+ "?faces-redirect=true";
			break;
		case 2:
			page = "/control/aprobacionRequisitosTecnicos/modalidadReuso" + extensionModoPage + "?faces-redirect=true";
			break;
		case 3:
			page = "/control/aprobacionRequisitosTecnicos/modalidadTratamiento" + extensionModoPage
					+ "?faces-redirect=true";
			break;
		case 4:
			page = "/control/aprobacionRequisitosTecnicos/modalidadIncineracion" + extensionModoPage
					+ "?faces-redirect=true";
			break;
		case 5:
			page = "/control/aprobacionRequisitosTecnicos/modalidadCoprocesamiento" + extensionModoPage
					+ "?faces-redirect=true";
			break;
		case 6:
			page = "/control/aprobacionRequisitosTecnicos/modalidadDisposicionFinal" + extensionModoPage
					+ "?faces-redirect=true";
			break;
		case 8:
			page = "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos" + extensionModoPage
					+ "?faces-redirect=true";
			break;

		default:
			page = "/control/aprobacionRequisitosTecnicos/envioAprobacionRequisitos" + extensionModoPage
					+ "?faces-redirect=true";
		}
		return page;
	}

	public void limpiarSeleccionModalidades() {
		if (!aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()) {
			aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas().clear();
		}

	}

	private void validarNumeroVehiculoYRequisitos() {
		try {
			if (validacionesPagesAprobacionRequisitosTecnicosFacade
					.isNumVehiculosPatioDiferenteRequisitosVehiculoConductores(aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos())) {
				aprobacionRequisitosTecnicosBean
						.setHabilitarMensajeNumVehiculoConductores(validacionesPagesAprobacionRequisitosTecnicosFacade
								.isPageRequisitosConductorRequerida(aprobacionRequisitosTecnicosBean
										.getAprobacionRequisitosTecnicos())
								|| validacionesPagesAprobacionRequisitosTecnicosFacade
										.isPageRequisitosVehiculoRequerida(aprobacionRequisitosTecnicosBean
												.getAprobacionRequisitosTecnicos()));
				validacionesPagesAprobacionRequisitosTecnicosFacade
						.guardarComoPaginasIncompletasRequisitosVehiculoConductor(aprobacionRequisitosTecnicosBean
								.getAprobacionRequisitosTecnicos());
			} else {
				aprobacionRequisitosTecnicosBean.setHabilitarMensajeNumVehiculoConductores(false);
				validacionesPagesAprobacionRequisitosTecnicosFacade
						.guardarComoPaginasCompletasRequisitosVehiculoConductor(
								aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos(),
								bandejaTareasBean.getProcessId(), loginBean.getUsuario());
			}

		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void validarDesechosPorModalidad() {
		try {
			List<String> errores = modalidadesFacade
					.validarDesechosCompletosPorModalidad(aprobacionRequisitosTecnicosBean
							.getAprobacionRequisitosTecnicos());
			StringBuilder mensaje = new StringBuilder();
			for (String error : errores) {
				mensaje.append(error);
			}
			aprobacionRequisitosTecnicosBean.setMensajesError(mensaje.toString());
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void validarQueTodoDeschoEstenAlmacenados() {
		try {
			String error = modalidadesFacade.validarQueTodoDeschoEstenAlmacenados(aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos());
			if (error != null) {
				aprobacionRequisitosTecnicosBean.setMensajesError(error);
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	public void descargarLicencia() {
		try {
			UtilDocumento.descargarFile(aprobacionRequisitosTecnicosFacade
					.descargarFile(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos()
							.getDocumentoLicenciaFisica()), aprobacionRequisitosTecnicosBean
					.getAprobacionRequisitosTecnicos().getDocumentoLicenciaFisica().getNombre());
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}

	}
	
	public void buscarModalidadesPorActividadCiiu() {
		if(aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion()){
			String modalidadesPredeterminadas = null;
			String modalidadesPermitidas = null;
			if(proyectoCiuuPrincipal != null && proyectoCiuuPrincipal.getCombinacionSubActividades() != null) {
				modalidadesPredeterminadas = proyectoCiuuPrincipal.getCombinacionSubActividades().getModalidadesArtPredeterminadas();
				
				modalidadesPermitidas = proyectoCiuuPrincipal.getCombinacionSubActividades().getModalidadesArtPermitidas();
				
			} else if(proyectoCiuuPrincipal != null && proyectoCiuuPrincipal.getSubActividad() != null && proyectoCiuuPrincipal.getSubActividad().getId() != null) {
				modalidadesPredeterminadas = proyectoCiuuPrincipal.getSubActividad().getModalidadesArtPredeterminadas();
				
				modalidadesPermitidas = proyectoCiuuPrincipal.getSubActividad().getModalidadesArtPermitidas();
			}
			
			if(modalidadesPredeterminadas != null && !modalidadesPredeterminadas.isEmpty()){
				List<String> listaModalidadesPredeterminadas = new ArrayList<String>(Arrays.asList(modalidadesPredeterminadas.split(",")));
				for(String predeterminada : listaModalidadesPredeterminadas) {
					Boolean existeModalidad = false;
					if(aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas() != null && 
							aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas().size() > 0) {
						for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas()) {
							if(predeterminada.equals(modalidad.getId().toString()))
								existeModalidad = true;
						}
					}
					
					if(!existeModalidad) {
						ModalidadGestionDesechos modalidad = modalidadesFacade.getModalidadPorId(Integer.valueOf(predeterminada));
						aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas().add(modalidad);
					}
					
					for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicosBean.getModalidades()) {
						if(predeterminada.equals(modalidad.getId().toString())) {
							modalidad.setDeshabilitado(true);
							break;
						}
					}
				}
			}
			
			if(modalidadesPermitidas != null && !modalidadesPermitidas.isEmpty()){
				if(!modalidadesPermitidas.equals("0")) {
					validarSeleccionModalidades = true;
					listaModalidadesPermitidasCiiu = new ArrayList<String>(Arrays.asList(modalidadesPermitidas.split(",")));
					for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicosBean.getModalidades()) {
						if(listaModalidadesPermitidasCiiu.contains(modalidad.getId().toString()))
							modalidad.setDeshabilitado(false);
						else 
							modalidad.setDeshabilitado(true);
					}
				} else {
					for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicosBean.getModalidades()) {
						modalidad.setDeshabilitado(true);
					}
				}
			}
		}
	}
	
	public void validateDatos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		

		if(!validarSeleccionModalidades && aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos().isGestion() && 
				(aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas() == null || 
				aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas().size() == 0))
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Modalidad' es requerido.", null));
		else if(validarSeleccionModalidades && (aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas() != null || 
				aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas().size() > 0)) {
			Boolean modalidadRequeridaSeleccionada = false;
			for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicosBean.getModalidadesSeleccionadas()) {
				if(listaModalidadesPermitidasCiiu.contains(modalidad.getId().toString()))
					modalidadRequeridaSeleccionada = true;
			}
			
			if(!modalidadRequeridaSeleccionada)
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar al menos una modalidad de las habilitadas.", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);		
	}

}
