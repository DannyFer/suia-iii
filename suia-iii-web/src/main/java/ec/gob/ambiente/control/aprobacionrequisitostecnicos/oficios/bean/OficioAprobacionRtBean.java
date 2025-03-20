package ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean;

import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeTecnicoArtBean.PRONUNCIAMENTO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.SendCopyBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.OficioAproReqTec;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityOficioAproReqTec;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

/**
 * <b> Clase para la creacion del oficio de aprobacion y observaciones. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 13/08/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class OficioAprobacionRtBean implements Serializable {

	public static final String URL_BANDEJA_ENTRADA = "/bandeja/bandejaTareas.jsf";
	public static final String URL_OFICIO_APROBACION = "/control/aprobacionRequisitosTecnicos/documentos/oficioAprobacionArt.jsf";
	public static final String TIPO = "tipo";
	public static final String PROPONENTE = "proponente";
	public static final String CARGO = "cargo";
	public static final String NOMBRE = "nombre";
	public static final String REVISAR = "revisar";
	public static final String OBSERVACION = "Observacion";
	public static final String DIRECCION_PROVINCIAL = "esDireccionProvincial";
	public static final String DIRECTOR = "directorControl";
	public static final String FORMATO_FECHA = "dd/MM/YYYY";
	public static final String EXISTE_CORRECCIONES = "existeCorrecciones";
	
	public static final String INFORME_TECNICO = "informeTecnicoArt";
	public static final String INFORME_TECNICO_OBSERVACION = "informeTecnicoObservacionArt";
	public static final String OFICIO_APROBACION_REQUISITOS_OBSERVACIONES = "OficioAprobacionRequisitos";
	public static final String GESTION_TRANSPORTE = "Gestión de desechos peligrosos y/o especiales y Transporte de sustancias químicas peligrosas";
	public static final String GESTION = "Gestión de desechos peligrosos y/o especiales";
	public static final String TRANSPORTE = "Transporte de sustancias químicas peligrosas";
	public static final String URL_INFORME = "/control/aprobacionRequisitosTecnicos/documentos/";
	public static final String FIRMA = "<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p style=\"text-align: center;\">Atentamente,</p><p style=\"text-align: center;\">&nbsp;</p><p style=\"text-align: center;\">&nbsp;</p><p style=\"text-align: center;\">&nbsp;</p><p style=\"text-align: center;\">&nbsp;</p><p style=\"text-align: center;\">&nbsp;</p><p style=\"text-align: center;\">&nbsp;</p><p style=\"text-align: center;\">$F{director}</p>";

	private static final long serialVersionUID = 165683211928358047L;
	private final Logger LOG = Logger.getLogger(OficioAprobacionRtBean.class);
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;
	@Getter
	@Setter
	private File oficioPdf;
	@Getter
	@Setter
	private byte[] archivoOficioAprobacion;
	@Getter
	@Setter
	private String informePath;
	@Getter
	@Setter
	private String nombreReporte;
	@Getter
	@Setter
	private OficioAproReqTec oficioArt;
	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@EJB
	private InformeOficioArtFacade informeOficioFacade;

	@EJB
	private UsuarioFacade usuarioFace;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private ObservacionesFacade observacionesFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private AreaFacade areaFacade;

	@Getter
	@Setter
	@ManagedProperty("#{proyectosBean}")
	private ProyectosBean proyectosBean;
	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectolicenciamientoambientalFacade;

	@Getter
	@Setter
	private boolean requiereModificaciones;

	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesController}")
	private ObservacionesController observacionesController;

	@Getter
	@Setter
	private String tipo = "";

	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	private Boolean emiteRespuestaAclaratoria;

	@Getter
	@Setter
	private List<ObservacionesFormularios> listaObservaciones;

	@Getter
	@Setter
	private InformeTecnicoAproReqTec informeTecnicoArt;

	@Getter
	@Setter
	private String urlRegresar;

	@Getter
	@Setter
	private String director;

	@Getter
	@Setter
	private Usuario usuarioAutoridad;

	private String[] cargoEmpresa;

	private String nombreEmpresa;

	private String nombreEmpresaMostrar;

	private String cargoRepresentanteLegalMostrar;

	@Getter
	@Setter
	private int contadorBandejaTecnico;

	private Map<String,String> sessions;
	
	/************************
	 * FIRMA ELECTRONICA *
	 ***********************/
	@Getter
	@Setter
	private boolean token, ambienteProduccion, firmaSoloToken, documentoDescargado, informacionSubida;
	@Getter
	@Setter
	private String nombreDocumentoFirmado, urlAlfresco;
	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	@Getter
	@Setter
	private Documento documentoSubido, documentoInformacionManual;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@PostConstruct
	public void init() {
		try {

			chargeSession();

			tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART.getIdTipoDocumento());
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(bandejaTareasBean.getProcessId(),
							loginBean.getUsuario());

			Object contadorBandejaTecnicoValue = null;
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			if (variables.containsKey("cantidadObservaciones")) {
				contadorBandejaTecnicoValue = variables.get("cantidadObservaciones").toString();
			}

			if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
				contadorBandejaTecnico = 0;
			} else {
				try {
					contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());
				} catch (Exception e) {
					LOG.error("Error recuperado cantidad de observaciones en registro de generador", e);
				}
			}

			informeTecnicoArt = this.informeOficioFacade.obtenerInformeTecnicoPorArt(
					TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART, aprobacionRequisitosTecnicos.getId(),
					contadorBandejaTecnico);
			oficioArt = informeOficioFacade.obtenerOficioAprobacionPorArt(
					TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART, aprobacionRequisitosTecnicos.getId(),
					contadorBandejaTecnico);

			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			if (params.containsKey(TIPO)) {
				tipo = params.get(TIPO);
				if (params.get(TIPO).equals(REVISAR)) {
					revisar = true;
				}

				if (variables.containsKey("existeCorrecciones")) {
					requiereModificaciones = Boolean.parseBoolean(variables.get("existeCorrecciones").toString());
				}

				if (variables.containsKey(DIRECTOR)) {
					if (loginBean.getUsuario().getNombre().equalsIgnoreCase(variables.get(DIRECTOR).toString())) {
						Map<String, String> datos = new HashMap<>();
						Usuario usuario = usuarioFace.buscarUsuarioCompleta(variables.get(DIRECTOR).toString());
						datos = informeOficioFacade.obtenerNombreCargo(usuario.getPersona());
						setDirector(datos.get(NOMBRE));
					}

				}
			}

			boolean pronunciamiento = false;
			if (variables.containsKey(PRONUNCIAMENTO)) {
				pronunciamiento = Boolean.parseBoolean(variables.get(PRONUNCIAMENTO).toString());
			}
			boolean direccionProvincial = false;
			if (variables.containsKey(DIRECCION_PROVINCIAL)) {
				direccionProvincial = Boolean.parseBoolean(variables.get(DIRECCION_PROVINCIAL).toString());
			}

			String cedulaAutoridad = JsfUtil.getCurrentTask()
					.getVariable((pronunciamiento && !direccionProvincial) ? "subsecretaria" : "directorControl")
					.toString();
						
//			usuarioAutoridad = usuarioFacade.buscarUsuarioWithOutFilters(cedulaAutoridad);
//			actualizar director--------------------------------
			Map<String, Object> parametro = new ConcurrentHashMap<String, Object>();
			if (pronunciamiento && !direccionProvincial) {
				usuarioAutoridad =areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
				parametro.put("subsecretaria", usuarioAutoridad.getNombre());
			}
			else {
				if(aprobacionRequisitosTecnicos.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)) 
					usuarioAutoridad =areaFacade.getDirectorProvincial(aprobacionRequisitosTecnicos.getAreaResponsable().getArea());
				else
					usuarioAutoridad =areaFacade.getDirectorProvincial(aprobacionRequisitosTecnicos.getAreaResponsable());
				parametro.put("directorControl", usuarioAutoridad.getNombre());
			}			
			try
			{	
				if(cedulaAutoridad==null  || !cedulaAutoridad.equals(usuarioAutoridad.getNombre()))
				{
					procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getProcessInstanceId(), parametro);	
					JsfUtil.getCurrentTask().updateVariable((pronunciamiento && !direccionProvincial) ? "subsecretaria": "directorControl",usuarioAutoridad.getNombre());
				}

			}
			catch(JbpmException e)
			{}
			
			

			cargoEmpresa = informeOficioFacade.getEmpresa(aprobacionRequisitosTecnicos);
			if (cargoEmpresa != null) {
				nombreEmpresa = cargoEmpresa[0] + " de la empresa " + cargoEmpresa[1];
				nombreEmpresaMostrar = cargoEmpresa[1];
				cargoRepresentanteLegalMostrar = cargoEmpresa[0];
			}
			if (oficioArt == null) {
				generarNuevoOficio(contadorBandejaTecnico);
				// Si el proceso indica que hay que hacer correcciones y si el
				// documento requiere correcciones
			} else {
//				if (!requiereModificaciones && !oficioArt.isExisteCorrecciones()) {
				if(aprobacionRequisitosTecnicos.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)) 
					actualizarOficioZonal(oficioArt);
				else
					actualizarOficio(oficioArt);
				
				if(tipo.equals("corregir")) {
					Calendar cal = Calendar.getInstance();
					String anioActual = Integer.toString(cal.get(Calendar.YEAR));
					if(!oficioArt.getNumeroOficio().contains("-" + anioActual + "-")) {
						oficioArt.setNumeroOficioAnterior(oficioArt.getNumeroOficio());
						if(aprobacionRequisitosTecnicos.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)) 
							oficioArt.setNumeroOficio(informeOficioFacade.generarCodigoOficioZonal(aprobacionRequisitosTecnicos));
						else
							oficioArt.setNumeroOficio(informeOficioFacade.generarCodigoOficio(aprobacionRequisitosTecnicos));
						
					}
				}
			}
			visualizarOficio(true);
		} catch (ServiceException | JbpmException e) {
			this.LOG.error("Error al inicializar: OficioAprobacionRtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}
	}

	private void chargeSession(){

		this.sessions = new HashMap<String, String>();
		sessions.put("informacionpatiomaniobra", "Información Patio Maniobra");
		sessions.put("requisitosvehiculo", "Requisitos Vehículos");
		sessions.put("sustanciaquimicapeligrosatransporte", "Sustacia Química Peligrosa Transporte");
		sessions.put("recepciondesechopeligroso", "Recepción Desecho Peligroso");
		sessions.put("almacen", "Almacén");
		sessions.put("eliminacionrecepcion", "Eliminación Recepción");
		sessions.put("modalidadincineracion", "Modalidad Incineración");
		sessions.put("programacalendarizadoincineracion", "Programa Calendarizado Incineración");
		sessions.put("modalidaddisposicionfinal", "Modalidad Dispoción Final");
	}

	public String getUserName() {
		return JsfUtil.getLoggedUser().getNombre();
	}

	/**
	 * <b> Metodo para generar un nuevo oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 */
	public void generarNuevoOficio(Integer contadorBandeja) {
		Map<String, Object> variables;

		try {
			if (informeTecnicoArt != null) {
				variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
						.getTarea().getProcessInstanceId());
				Map<String, String> datos = new HashMap<>();
				String tipoTratos = "Sra./Sr.";
				if (variables.containsKey(PROPONENTE)) {
					Usuario usuario = usuarioFace.buscarUsuarioCompleta(variables.get(PROPONENTE).toString());
					datos = informeOficioFacade.obtenerNombreCargo(usuario.getPersona());

				}

				Persona persona = usuarioFace.buscarUsuarioPorIdFull(aprobacionRequisitosTecnicos.getUsuario().getId())
						.getPersona();

				if (persona.getTipoTratos() != null && persona.getTipoTratos().getNombre() != null
						&& !persona.getTipoTratos().getNombre().isEmpty()) {
					tipoTratos = persona.getTipoTratos().getNombre();
				}
				oficioArt = new OficioAproReqTec();
				if (nombreEmpresa == null) {
					oficioArt.setProponente(persona.getNombre());
					oficioArt.setCargo("Proponente");
				} else {
					oficioArt.setProponente(persona.getNombre());
					oficioArt.setEmpresa(nombreEmpresaMostrar);
					oficioArt.setCargo(cargoRepresentanteLegalMostrar);
				}
				oficioArt.setTipoTratos(tipoTratos);
				oficioArt.setAprobacionRequisitosTecnicos(this.aprobacionRequisitosTecnicos);
				oficioArt.setTipoDocumentoId(tipoDocumento.getId());
				oficioArt.setNombreProyecto(informeTecnicoArt.getNombreProyecto().toLowerCase());
				oficioArt.setNumeroProyecto(informeTecnicoArt.getNumeroProyecto());
				oficioArt.setNumeroTramite(aprobacionRequisitosTecnicos.getSolicitud());
				SimpleDateFormat fechaInf = new SimpleDateFormat(FORMATO_FECHA);
				oficioArt.setFechaTramite(JsfUtil.devuelveFechaEnLetrasSinHora(aprobacionRequisitosTecnicos
						.getFechaCreacion()));
				oficioArt.setNumeroOficio(informeOficioFacade.generarCodigoOficio(aprobacionRequisitosTecnicos));
				oficioArt.setNumeroInfTecnico(informeTecnicoArt.getNumeroOficio());
				oficioArt.setFechaInfTecnico(fechaInf.format(informeTecnicoArt.getFechaCreacion()));
				oficioArt.setContadorBandejaTecnico(contadorBandeja);
				informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
			}

		} catch (Exception ex) {
			this.LOG.error("Error al generar el oficio", ex);
			JsfUtil.addMessageError("Error al generar el oficio");
		}

	}

	/**
	 * <b> Metodo actualizar un oficio. </b>
	 * <p>
	 * [Author: jgras, Date: 30/09/2015]
	 * </p>
	 */
	public void actualizarOficio(OficioAproReqTec oficioArt) {
		try {
			SimpleDateFormat fechaInf = new SimpleDateFormat(FORMATO_FECHA);
			oficioArt.setFechaInfTecnico(fechaInf.format(informeTecnicoArt.getFechaCreacion()));
			oficioArt.setNumeroOficio(oficioArt.getNumeroOficio()!=null?oficioArt.getNumeroOficio():informeOficioFacade.generarCodigoOficio(aprobacionRequisitosTecnicos));
			oficioArt.setNumeroInfTecnico(informeTecnicoArt.getNumeroOficio());
			oficioArt.setExisteCorrecciones(false);
		} catch (Exception ex) {
			this.LOG.error("Error al generar el oficio", ex);
			JsfUtil.addMessageError("Error al generar el oficio");
		}

	}
	
	public void actualizarOficioZonal(OficioAproReqTec oficioArt) {
		try {
			SimpleDateFormat fechaInf = new SimpleDateFormat(FORMATO_FECHA);
			oficioArt.setFechaInfTecnico(fechaInf.format(informeTecnicoArt.getFechaCreacion()));
			oficioArt.setNumeroInfTecnico(informeTecnicoArt.getNumeroOficio());
			oficioArt.setExisteCorrecciones(false);
			
			if(oficioArt.getNumeroOficio() == null) {
				oficioArt.setNumeroOficio(informeOficioFacade.generarCodigoOficioZonal(aprobacionRequisitosTecnicos));
			} else {
				//para actualizar el codigo del oficio con la nueva área del proyecto
				if(!oficioArt.getNumeroOficio().contains(aprobacionRequisitosTecnicos.getAreaResponsable().getArea().getAreaAbbreviation())) {
					oficioArt.setNumeroOficioAnterior(oficioArt.getNumeroOficio());
					oficioArt.setNumeroOficio(informeOficioFacade.generarCodigoOficioZonal(aprobacionRequisitosTecnicos));
				}
			}
		} catch (Exception ex) {
			this.LOG.error("Error al generar el oficio", ex);
			JsfUtil.addMessageError("Error al generar el oficio");
		}

	}

	/**
	 * <b> Metodo para la visualizacion del oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return String: path
	 */
	public String visualizarOficio(boolean generarDatosAutoridad) {
		String pathPdf = null;
		try {
			PlantillaReporte plantillaReporte = new PlantillaReporte();
			EntityOficioAproReqTec entityOficio = new EntityOficioAproReqTec();
			if (informeTecnicoArt != null && OBSERVACION.equalsIgnoreCase(informeTecnicoArt.getPronunciamiento())) {
				plantillaReporte = informeOficioFacade
						.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_ART.getIdTipoDocumento());
				nombreReporte = "OficioObservacionAprobacionRequisitosTecnicos.pdf";
				entityOficio.setObservaciones(OficioObservaciones());
			} else {
				plantillaReporte = informeOficioFacade.obtenerPlantillaReporte(getTipoDocumento().getId());
				nombreReporte = "OficioAprobacionRequisitosTecnicos.pdf";
			}

			oficioArt.setFechaOficio(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
			entityOficio.setTipoTramite("");
			if (aprobacionRequisitosTecnicos.isGestion() && aprobacionRequisitosTecnicos.isTransporte()) {
				entityOficio.setTipoTramite(GESTION_TRANSPORTE.toUpperCase());
			} else if (aprobacionRequisitosTecnicos.isGestion()) {
				entityOficio.setTipoTramite(GESTION.toUpperCase());
			} else if (aprobacionRequisitosTecnicos.isTransporte()) {
				entityOficio.setTipoTramite(TRANSPORTE.toUpperCase());
			}																								
			entityOficio.setModalidad("");
			if (aprobacionRequisitosTecnicos.isGestion()) {
				if (aprobacionRequisitosTecnicos.getModalidades() != null
						&& !aprobacionRequisitosTecnicos.getModalidades().isEmpty()) {
					int i = 0;
					String modalidades = null;
					for (ModalidadGestionDesechos mgd : aprobacionRequisitosTecnicos.getModalidades()) {
						if (i == 0) {
							if(mgd.getNombre().equals("Otro")){
								modalidades = "Otro (".concat(aprobacionRequisitosTecnicos.getOtraModalidad().concat(")"));
							}else{
								modalidades = mgd.getNombre();
							}
						} else {
							if(mgd.getNombre().equals("Otro")){
								modalidades += ", Otro (".concat(aprobacionRequisitosTecnicos.getOtraModalidad().concat(")"));
							}else{
								modalidades += ", " + mgd.getNombre();
							}
						}
						i++;
					}
					entityOficio.setModalidad("para la(s) modalidad(es) de: " + modalidades);
				}
			}

			String articulo = "";

			Persona persona = usuarioFace.buscarUsuarioPorIdFull(aprobacionRequisitosTecnicos.getUsuario().getId())
					.getPersona();

			if (persona.getTipoTratos() != null) {
				articulo += obtenerArticuloSegunTratamiento(persona.getTipoTratos().getNombre()) + " ";
			}
			String sujetoControl = "Sra./Sr.";
			String sujetoControlEncabez = "<strong>Sra./Sr. &nbsp; &nbsp;&nbsp; </strong>";

			if (persona.getTipoTratos() != null && persona.getTipoTratos().getNombre() != null
					&& !persona.getTipoTratos().getNombre().isEmpty()) {
				sujetoControl = persona.getTipoTratos().getNombre();
				sujetoControlEncabez = "<strong>" + persona.getTipoTratos().getNombre()
						+ " &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;&nbsp; &nbsp; </strong>";

			}

			sujetoControl = articulo + sujetoControl;

			if (persona.getTitulo() != null && !persona.getTitulo().trim().isEmpty()) {
				sujetoControl += " / " + persona.getTitulo() + ",";
			}

			if (persona.getNombre() != null) {
				sujetoControl += " " + persona.getNombre();
				sujetoControlEncabez += persona.getNombre();
			}
			entityOficio.setNombreEmpresaMostrar("");
			entityOficio.setCargoRepresentanteLegalMostrar("");

			if (nombreEmpresa != null) {
				entityOficio.setNombreEmpresaMostrar("<strong>Empresa:&nbsp; </strong>&nbsp;" + nombreEmpresaMostrar);
				entityOficio
						.setCargoRepresentanteLegalMostrar("<strong>Cargo:</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
								+ cargoRepresentanteLegalMostrar);
			}
			entityOficio.setSujetoControlEncabez(sujetoControlEncabez);
			entityOficio.setNumeroOficio(oficioArt.getNumeroOficio());
			entityOficio.setFechaOficio(oficioArt.getFechaOficio());

			String proponente = "";

			if (aprobacionRequisitosTecnicos.getUsuario().getPersona().getIdNacionalidad() != null && aprobacionRequisitosTecnicos.getUsuario().getNombre().length()<=10) {

				proponente = aprobacionRequisitosTecnicos.getUsuario().getPersona().getNombre();
			} else {

				Organizacion organizacion = organizacionFacade.buscarPorPersona(aprobacionRequisitosTecnicos.getUsuario()
						.getPersona(), aprobacionRequisitosTecnicos.getUsuario().getNombre());
				if (organizacion != null) {
					proponente = organizacion.getNombre();
				}
			}

			if(informeTecnicoArt.getEmpresa()!=null && proponente.contains(informeTecnicoArt.getEmpresa()))
				proponente = informeTecnicoArt.getEmpresa();

			entityOficio.setProponente(proponente);
			entityOficio.setCargo(oficioArt.getCargo());
			entityOficio.setTipoTratos(oficioArt.getTipoTratos());
			entityOficio.setTxtOpcional1(oficioArt.getTxtOpcional1());
			entityOficio.setNumeroTramite(oficioArt.getNumeroTramite());
			entityOficio.setFechaTramite(oficioArt.getFechaTramite());
			entityOficio.setNombreProyecto(oficioArt.getNombreProyecto().toLowerCase());
			entityOficio.setNormativa(oficioArt.getNormativa());
			entityOficio.setTxtOpcional2(oficioArt.getTxtOpcional2());
			entityOficio.setNumeroInfTecnico(oficioArt.getNumeroInfTecnico());
			entityOficio.setFechaInfTecnico(JsfUtil.devuelveFechaEnLetrasSinHora(oficioArt.getFechaCreacion()));
			entityOficio.setTxtTipoTramite(oficioArt.getTxtTipoTramite());
			entityOficio.setTxtModalidad(oficioArt.getTxtModalidad());
			entityOficio.setTxtDisposicionesPresentancionResultados(oficioArt
					.getTxtDisposicionesPresentancionResultados());
			// Token
			entityOficio.setAutoridad((generarDatosAutoridad && usuarioAutoridad != null) ? usuarioAutoridad
					.getPersona().getNombre() : "");
			entityOficio
					.setCargoAutoridad((generarDatosAutoridad && usuarioAutoridad != null) ? obtenerRolesUsuarioAutoridad(usuarioAutoridad)
							: "");
			// Token
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityOficio);

			setOficioPdf(informePdf);

			Path path = Paths.get(getOficioPdf().getAbsolutePath(), new String[0]);
			this.archivoOficioAprobacion = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(getOficioPdf().getName()));
			oficioArt.setOficioRealPath(archivoFinal.getAbsolutePath());
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivoOficioAprobacion);
			file.close();

			setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + getOficioPdf().getName()));

			pathPdf = informePdf.getParent();
		} catch (Exception e) {
			LOG.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return pathPdf;
	}

	public String obtenerRolesUsuarioAutoridad(Usuario usuario) {
		String cargo = "";
		if (usuario.getPersona() != null && usuario.getPersona().getPosicion() != null) {
			cargo = usuario.getPersona().getPosicion();
		}
		return cargo;
	}

	/**
	 * <b> Metodo para guardar el oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 */
	public void guardar() {
		try {
			visualizarOficio(true);
			informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
			
			if (informeTecnicoArt.getExisteCorrecciones() != null && informeTecnicoArt.getExisteCorrecciones()) {
				requiereModificaciones = true;
			} else {
				requiereModificaciones = false;
				subirDocumentoAlfresco();
			}
			
			//subirDocumentoAlfresco();
			//guardarVariablesBpm();
//			String url = URL_OFICIO_APROBACION;
//			if (!tipo.isEmpty()) {
//				url += "?tipo=" + tipo;
//			}
//			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
//			JsfUtil.getBean(SendCopyBean.class).guardarFilesCopies(oficioArt.getClass().getSimpleName(),oficioArt.getId(), getUserName());
//			JsfUtil.redirectTo(url);
		} catch (Exception e) {
			LOG.error("Error al guardar informe técnico", e);
			JsfUtil.addMessageError("Error al guardar informe técnico");
		}
	}

	/**
	 * <b> Metodo para guardar el oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 */
	public void guardarAutoridad() {
		try {
			oficioArt.setExisteCorrecciones(false);
			informeOficioFacade.guardarOficioAprobacionArt(oficioArt);

			if (informeTecnicoArt.getExisteCorrecciones() != null && informeTecnicoArt.getExisteCorrecciones()) {
				LOG.info("true");
				requiereModificaciones = true;
			} else {
				LOG.info("false");
				requiereModificaciones = false;
			}
			visualizarOficio(true);
			guardarVariablesBpm();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			LOG.error("Error al guardar informe técnico", e);
			JsfUtil.addMessageError("Error al guardar informe técnico");
		}
	}

	/**
	 * <b> Metodo para completar la tarea. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return
	 */
	public String completarTarea() {
		try {
			List<String> errors = new ArrayList<>();
			if (oficioArt.getNormativa() != null && oficioArt.getNormativa().trim().isEmpty()) {
				errors.add("El campo '2. NORMATIVA' es requerido.");
			}

			if (oficioArt.getTxtDisposicionesPresentancionResultados() != null
					&& oficioArt.getTxtDisposicionesPresentancionResultados().trim().isEmpty()) {
				errors.add("El campo '4. DISPOSICIONES ADICIONALES' es requerido.");
			}
			if (!errors.isEmpty()) {
				JsfUtil.addMessageError(errors);
				return "";
			}

			if (validarObservaciones(!oficioArt.isExisteCorrecciones())) {
				
				String url = JsfUtil.getRequest().getRequestURI();
				if (url.equals("/suia-iii/control/aprobacionRequisitosTecnicos/documentos/informeTecnicoObservacionArt.jsf")
					&& (Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL") 
					|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE")) && !informeTecnicoArt.getExisteCorrecciones()) {
					subirDocumentoAlfresco();
				}
				
				visualizarOficio(true);
				this.informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
				if (informeTecnicoArt.getExisteCorrecciones() != null && informeTecnicoArt.getExisteCorrecciones()) {
					requiereModificaciones = true;
				} else {
					requiereModificaciones = false;
				}
				guardarVariablesBpm();
				aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(bandejaTareasBean.getTarea()
						.getProcessInstanceId(), loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId());
				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
				JsfUtil.getBean(SendCopyBean.class).sendFilesCopies(oficioArt.getClass().getSimpleName(),
						oficioArt.getId(), getUserName(), new String[] { oficioArt.getOficioRealPath() });
				return JsfUtil.actionNavigateTo(URL_BANDEJA_ENTRADA);
			}

		} catch (JbpmException e) {
			LOG.error("Ocurrió un error al enviar la información.", e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
		} catch (Exception e) {
			LOG.error("Ocurrió un error al enviar la información.", e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
		}
		return "";
	}

	/**
	 * <b> Metodo para validar las observaciones. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @param estado
	 *            : estado para revisar
	 * @return Boolean: true existen observaciones sin corregir
	 */
	public Boolean validarObservaciones(Boolean estado) {
		List<ObservacionesFormularios> observaciones = observacionesController.getListaObservacionesBB().get("top")
				.getMapaSecciones().get(OFICIO_APROBACION_REQUISITOS_OBSERVACIONES);

		if (estado) {
			for (ObservacionesFormularios observacion : observaciones) {
				if (observacion.getUsuario().equals(loginBean.getUsuario()) && !observacion.isObservacionCorregida()) {

					JsfUtil.addMessageError("Existen observaciones sin corregir. Por favor rectifique los datos.");
					return false;
				}
			}
		} else {
			int posicion = 0;
			int cantidad = observaciones.size();
			Boolean encontrado = false;
			while (!encontrado && posicion < cantidad) {
				if (observaciones.get(posicion).getUsuario().equals(loginBean.getUsuario())
						&& !observaciones.get(posicion).isObservacionCorregida()) {
					encontrado = true;
				}
				posicion++;
			}
			if (!encontrado && revisar) {
				JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
				return false;
			}
		}
		return true;
	}

	/**
	 * <b> Metodo para guardar el oficio en el alfresco. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return Documento: Documento
	 */
	public Documento subirDocumentoAlfresco() {
		try {
			byte[] data = Files.readAllBytes(Paths.get(getOficioPdf().getAbsolutePath()));
			Documento documento = informeOficioFacade.subirFileAlfresco(UtilDocumento.generateDocumentPDFFromUpload(
					data,informeTecnicoArt.getPronunciamiento().equals(OBSERVACION)?oficioArt.getNombreOficio().replace("AprobacionRequisitos", "ObservacionRequisitos"): oficioArt.getNombreOficio()), oficioArt.getAprobacionRequisitosTecnicos().getSolicitud(),
					bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), OficioAproReqTec.class
							.getSimpleName(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART);

			documento.setIdTable(oficioArt.getId());
			oficioArt.setDocumentoOficio(documento);
			documentosFacade.actualizarDocumento(documento);
			return documento;
		} catch (IOException | ServiceException | CmisAlfrescoException e) {
			LOG.error("Error al guardar el Oficio", e);
			JsfUtil.addMessageError("Error al guardar el Oficio");
			return null;
		}
	}

	/**
	 * <b> Metodo para validar la tarea con la pagina. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 */
	public void validarTareaBpm() {
		validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea());
	}

	/**
	 * <b> Metodo que valida la tarea con la pagina. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @param taskSummaryCustom
	 *            : tarea
	 */
	public void validarPaginasUrlTareasBpm(TaskSummaryCustom taskSummaryCustom) {
		if (taskSummaryCustom == null) {
			JsfUtil.redirectTo(URL_BANDEJA_ENTRADA);
		} else {
			if (taskSummaryCustom.getTaskSummary() == null) {
				JsfUtil.redirectTo(URL_BANDEJA_ENTRADA);
			} else {
				if (taskSummaryCustom.getTaskSummary().getDescription().contains(INFORME_TECNICO_OBSERVACION)) {
					setUrlRegresar(URL_INFORME + INFORME_TECNICO_OBSERVACION + ".jsf");
				} else if (taskSummaryCustom.getTaskSummary().getDescription().contains(INFORME_TECNICO)) {
					setUrlRegresar(URL_INFORME + INFORME_TECNICO + ".jsf");
				} else {
					JsfUtil.redirectTo(URL_BANDEJA_ENTRADA);
				}
			}
		}
	}

	/**
	 * <b> Metodo para las observaciondes del oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return String : tabla de observaciones
	 */
	public String OficioObservaciones() {
		String tablaObservaciones = "No existen observaciones";
		try {
			if (informeTecnicoArt != null && OBSERVACION.equalsIgnoreCase(informeTecnicoArt.getPronunciamiento())) {
				// observaciones
					listaObservaciones = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
							aprobacionRequisitosTecnicos.getId(), AprobacionRequisitosTecnicos.class.getSimpleName());

					if (listaObservaciones != null && !listaObservaciones.isEmpty()) {
						tablaObservaciones = "<table border =\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"font-size:10px\" width=\"100%\">"
								+ "<tr> <th style=\"width:5%\">Item</th> <th style=\"width:25%\">Sección</th> <th style=\"width:7%\">Cumple</th>"
								+ "<th style=\"width:25%\">Campo</th> <th style=\"width:38%\">Observaciones / Criterio técnico</th> </tr>";
						int item = 0;
						for (ObservacionesFormularios observaciones : listaObservaciones) {
							String cumple;
							item++;
							if (observaciones.isObservacionCorregida()) {
								cumple = "Si";
							} else {
								cumple = "No";
							}

							String[] secciones = observaciones.getSeccionFormulario().split("\\.");
							String sessionKey = secciones[secciones.length - 1];

							String seccionObtenida = this.sessions.get(sessionKey.toLowerCase());
							if(seccionObtenida==null)
								seccionObtenida = secciones[secciones.length - 1].replaceAll("(.)(\\p{Lu})", "$1 $2");

							tablaObservaciones += "<tr> <td>" + item + "</td> <td>" + seccionObtenida + "</td> <td>" + cumple
									+ "</td>  <td>" + observaciones.getCampo() + "</td>  <td>" + observaciones.getDescripcion()
									+ " </td>" + "</tr>";

						}
						tablaObservaciones += "</table>";
					}
					return tablaObservaciones;
					// fin
				
			}
		} catch (ServiceException ex) {
			java.util.logging.Logger.getLogger(OficioAprobacionRtBean.class.getName()).log(Level.SEVERE, null, ex);
		}
		return tablaObservaciones;

	}

	/**
	 * <b> Metodo que guarda las varibles en el bpm. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * @throws ServiceException 
	 */
	public void guardarVariablesBpm() throws ServiceException {

		try {
			Map<String, Object> params = new ConcurrentHashMap<>();
//			String proyectos= aprobacionRequisitosTecnicos.getAreaResponsable();
//			ProyectoLicenciamientoAmbiental pro= proyectolicenciamientoambientalFacade.getProyectoPorCodigo(proyectos);
			
			if (areaFacade.getUsuarioPorRolArea("role.area.coordinador", aprobacionRequisitosTecnicos.getAreaResponsable())==null){
				LOG.error("Error al guardar el informe técnico");
				JsfUtil.addMessageError("Error al guardar el informe técnico");				
			}else{
				params.put("coordinadorControl", areaFacade.getUsuarioPorRolArea("role.area.coordinador", aprobacionRequisitosTecnicos.getAreaResponsable()).getNombre());
				params.put("existeCorrecciones", requiereModificaciones);
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
						.getProcessInstanceId(), params);				
			}					
			
		} catch (JbpmException e) {
			LOG.error("Error al guardar el informe técnico", e);
			JsfUtil.addMessageError("Error al guardar el informe técnico");
		}		

	}

	public void guardarRegresar() {
		try {
			String url = getUrlRegresar();
			if (tipo != null && !tipo.isEmpty()) {
				url += "?tipo=" + tipo;
			}
			informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
			JsfUtil.redirectTo(url);
		} catch (Exception e) {
			LOG.error("Error al guardar oficio", e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}

	public String obtenerArticuloSegunTratamiento(String tratamiento) {
		String articulo = "el/la";
		if (tratamiento != null && !tratamiento.trim().isEmpty()) {
			if (tratamiento.contains("a")) {
				articulo = "la";
			} else {
				articulo = "el";
			}
		}
		return articulo;
	}
	
	/************************
	 * FIRMA ELECTRONICA *
	 ***********************/

	/**
	 * METODO INICIAL PARA VERIFICAR EL VALOR DEL TOKEN LOS VALORES DE LA FIRMA
	 * ELECTRONICA
	 */

	public boolean verificaToken() {
		if (firmaSoloToken) {
			token = true;
			return token;
		}

//		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	/**
	 * GUARDA EL VALOR DEL TOKEN
	 */
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * PARA FIRMA CON TOKEN
	 * 
	 * @param event
	 */
	@SuppressWarnings("static-access")
	public String firmarOficio() {
		try {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART;

			tipoDocumento = TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART.getIdTipoDocumento());
			documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(oficioArt.getId(),
					OficioAproReqTec.class.getSimpleName(), tipoDocumento);
			
			if (documentoSubido.getIdAlfresco() != null) {
				String documento = documentosFacade.direccionDescarga(documentoSubido);
				DigitalSign firmaE = new DigitalSign();
				documentoDescargado = true;
				return firmaE.sign(documento, loginBean.getUsuario().getNombre());

			} else
				return "";
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}

	/**
	 * METODO DE TIPO DOCUMENTO SE USA PARA LA FIRMA ELECTRONICA
	 * 
	 * @param codTipo
	 * @return
	 */
	public TipoDocumento obtenerTipoDocumento(Integer codTipo) {
		try {
			Query query = crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM TipoDocumento o WHERE o.estado=true and o.id=:id");
			query.setParameter("id", codTipo);
			return (TipoDocumento) query.getResultList().get(0);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Metodo de carga de archivo
	 */

	public void cargarInformacion() {

		if (informeTecnicoArt == null) {
			JsfUtil.addMessageError("Debe adjuntar el informe para el Certificado de Viabilidad.");

		}
		informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
	}

	/**
	 * Sube la informacion firmada En el panel
	 * 
	 * @param event
	 */
	public void cargarArchivo(FileUploadEvent event) {
		
		if (documentoDescargado) {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART;

			tipoDocumento = TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART.getIdTipoDocumento());
			
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");
			documentoInformacionManual.setNombre("informe_oficio_aprobacion_requisitos.pdf");

			documentoInformacionManual.setIdTable(informeTecnicoArt.getId());
			documentoInformacionManual.setNombreTabla(InformeTecnicoAproReqTec.class.getSimpleName());
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);
			informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);

			try {
				documentosFacade.guardarDocumentoAlfrescoSinProyecto(String.valueOf(informeTecnicoArt.getId()),
						String.valueOf(bandejaTareasBean.getTarea().getProcessInstanceId()),
						Long.valueOf(bandejaTareasBean.getTarea().getTaskId()), documentoInformacionManual,
						tipoDocumento);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}

			informacionSubida = true;

			nombreDocumentoFirmado = event.getFile().getFileName();
		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firmas");
		}
	}

	/**
	 * Descarga la informacion del panel
	 * 
	 * @return
	 * @throws IOException
	 */
	public StreamedContent descargarInformacion() throws IOException {
		
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent;
			Documento auxdoc = new Documento();
			Boolean generar = false;

			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(oficioArt.getId(),
					OficioAproReqTec.class.getSimpleName(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART);

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		}

		catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
}
