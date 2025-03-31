package ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.ArtGeneralBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.BuscarProyectoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityInformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

/**
 *
 * <b> Clase para la generacion del informe tecnico de aprobacion de requisitos
 * tecnicos. </b>
 *
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 13/08/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class InformeTecnicoArtBean implements Serializable {

	public static final String TIPO = "tipo";
	public static final String REVISAR = "revisar";
	public static final String PROPONENTE = "proponente";
	public static final String NOMBRE = "nombre";
	public static final String FAVORABLE = "Favorable";
	public static final String OBSERVACION = "Observacion";
	public static final String PRONUNCIAMENTO = "pronunciamento";

	public static final String INFORME_APROBACION_REQUISITO_OBSERVACIONES = "informeAprobacionRequisitos";
	public static final String URL_OFICIO = "/control/aprobacionRequisitosTecnicos/documentos/oficioAprobacionArt.jsf";
	public static final String URL_INFORME = "/control/aprobacionRequisitosTecnicos/documentos/informeTecnicoArt.jsf";


	private static final long serialVersionUID = 165683211928358047L;
	private final Logger LOG = Logger.getLogger(InformeTecnicoArtBean.class);
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;
	@Getter
	@Setter
	private File informePdf;
	@Getter
	@Setter
	private byte[] archivoInforme;
	@Getter
	@Setter
	private String informePath;
	@Getter
	@Setter
	private String nombreReporte;
	@Getter
	@Setter
	private InformeTecnicoAproReqTec informeTecnicoArt;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
	@Setter
	@Getter
	private MaeLicenseResponse registroAmbiental;
	@Getter
	@Setter
	private Usuario usuario;
	@Getter
	@Setter
	private Organizacion organizacion;
	@Getter
	@Setter
	private UbicacionesGeografica parroquia;

	@EJB
	private InformeOficioArtFacade informeOficioFacade;

	@EJB
	private UsuarioFacade usuarioFace;
	@EJB
	private ObservacionesFacade observacionesFacade;
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private BuscarProyectoFacade buscarProyectoFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

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
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	private String tipo = "";

	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesController}")
	private ObservacionesController observacionesController;

	@Getter
	@Setter
	@ManagedProperty(value = "#{artGeneralBean}")
	private ArtGeneralBean artGeneralBean;

	@Getter
	@Setter
	private Boolean pronunciamiento;

	@Getter
	@Setter
	private List<ObservacionesFormularios> listaObservaciones;

	@Getter
	@Setter
	private int contadorBandejaTecnico;

	private Map<String,String> sessions;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	/************************
	 * FIRMA ELECTRONICA *
	 ***********************/
	@Getter
	@Setter
	private boolean token, ambienteProduccion, firmaSoloToken, documentoDescargado, informacionSubida, guardado;
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
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;
	
	@PostConstruct
	public void init() {
		try {

			chargeSession();
			
			/**
			 * INIT DE FIRMA ELECTRONICA
			 */
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			
			token = true;

			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado = true;
			}
			urlAlfresco = "";

			this.tipoDocumento = new TipoDocumento();
			this.tipoDocumento.setId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART.getIdTipoDocumento());
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(bandejaTareasBean.getProcessId(),
							loginBean.getUsuario());
			artGeneralBean.setAprobacion(aprobacionRequisitosTecnicos);
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();

			boolean existeCorrecciones = false;

			if (params.containsKey(TIPO)) {
				tipo = params.get(TIPO);
				if (params.get(TIPO).equals(REVISAR)) {
					revisar = true;
				}
				Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
						bandejaTareasBean.getTarea().getProcessInstanceId());

				if (variables.containsKey(PRONUNCIAMENTO)) {
					pronunciamiento = Boolean.parseBoolean(variables.get(PRONUNCIAMENTO).toString());
				}

				if (variables.containsKey("existeCorrecciones")) {
					existeCorrecciones = Boolean.parseBoolean(variables.get("existeCorrecciones").toString());
				}
				// contador de observaciones de proceso
				Object contadorBandejaTecnicoValue=null;
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
				// fin contador de observaciones de proceso
			}
			informeTecnicoArt = informeOficioFacade.obtenerInformeTecnicoPorArt(
					TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART, this.aprobacionRequisitosTecnicos.getId(),contadorBandejaTecnico);

			if (informeTecnicoArt == null) {
				generarNuevoInformeTecnico(contadorBandejaTecnico);
			} else {
				if (informeTecnicoArt.getPronunciamiento() != null && !informeTecnicoArt.getPronunciamiento().isEmpty()) {
					pronunciamiento = informeTecnicoArt.getPronunciamiento().equals(FAVORABLE);
				}
				//para actualizar el codigo del informe con la nueva área del proyecto
				if(!informeTecnicoArt.getNumeroOficio().contains(aprobacionRequisitosTecnicos.getAreaResponsable().getAreaAbbreviation())) {
					informeTecnicoArt.setNumeroOficioAnterior(informeTecnicoArt.getNumeroOficio());
					informeTecnicoArt.setNumeroOficio(informeOficioFacade.generarCodigoInforme(aprobacionRequisitosTecnicos));
										
					informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
				}
				
				if(tipo.equals("corregir")) {
					Calendar cal = Calendar.getInstance();
					String anioActual = Integer.toString(cal.get(Calendar.YEAR));
					if(!informeTecnicoArt.getNumeroOficio().contains("-" + anioActual + "-")) {
						informeTecnicoArt.setNumeroOficioAnterior(informeTecnicoArt.getNumeroOficio());
						informeTecnicoArt.setNumeroOficio(informeOficioFacade.generarCodigoInforme(aprobacionRequisitosTecnicos));
						
						informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
					}
				}
				
				// Si el proceso indica que hay que hacer correcciones y si el
				// documento requiere correcciones
				/*
				 * if (existeCorrecciones &&
				 * informeTecnicoArt.getExisteCorrecciones()) {
				 * actualizarCodigoInformeTecnico(informeTecnicoArt); }
				 */

				/*if (informeTecnicoArt.getPronunciamiento() == null || informeTecnicoArt.getPronunciamiento().isEmpty()) {
				}*/

			}
			visualizarOficio();
			guardado = false;
		} catch (Exception e) {
			LOG.error("Error al inicializar: InformeTecnicoArtBean: ", e);
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

	/**
	 *
	 * <b> Metodo para generar el nuevo informe Tecnico. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 *
	 */
	public void generarNuevoInformeTecnico(Integer contadorBandeja) {

		informeTecnicoArt = new InformeTecnicoAproReqTec();
		informeTecnicoArt.setContadorBandejaTecnico(contadorBandeja);
		informeTecnicoArt.setTipoDocumento(tipoDocumento);
		informeTecnicoArt.setAprobacionRequisitosTecnicos(aprobacionRequisitosTecnicos);
		informeTecnicoArt.setTecnico(loginBean.getNombreUsuario().toLowerCase());
		informeTecnicoArt.setNumeroProyecto(aprobacionRequisitosTecnicos.getProyecto());
		informeTecnicoArt.setNombreProyecto(aprobacionRequisitosTecnicos.getNombreProyecto());
		informeTecnicoArt.setCiudadEmision(loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		if (aprobacionRequisitosTecnicos.getTieneLicenciaFisica() != null
				&& aprobacionRequisitosTecnicos.getTieneLicenciaFisica()) {
			informeTecnicoArt.setParroquiaProyecto("");
			informeTecnicoArt.setCantonProyecto("");
		} else {
			ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade
					.getProyectoPorCodigo(aprobacionRequisitosTecnicos.getProyecto());
			if (proyecto == null) {
				informeTecnicoArt.setParroquiaProyecto("");
				informeTecnicoArt.setCantonProyecto("");

			} else {
				try {
					informeTecnicoArt.setParroquiaProyecto(proyecto.getPrimeraParroquia().getNombre());
					informeTecnicoArt.setCantonProyecto(proyecto.getPrimeraParroquia().getUbicacionesGeografica()
							.getNombre());
				} catch (Exception ex) {
					LOG.error("Error al inicializar: InformeTecnicoArtBean: ", ex);
					JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
				}
			}

		}
		informeTecnicoArt.setProvinciaProyecto(aprobacionRequisitosTecnicos.getProvincia().getNombre());
		informeTecnicoArt.setNumeroOficio(informeOficioFacade.generarCodigoInforme(aprobacionRequisitosTecnicos));
		informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);

	}

	/**
	 *
	 * <b> Metodo para actualizar con un nuevo número un informe Tecnico. </b>
	 * <p>
	 * [Author: jgras, Date: 29/09/2015]
	 * </p>
	 *
	 */
	public void actualizarCodigoInformeTecnico(InformeTecnicoAproReqTec informeTecnicoArt) {
		try {
			informeTecnicoArt.setNumeroOficio(informeTecnicoArt.getNumeroOficio()!=null?informeTecnicoArt.getNumeroOficio():informeOficioFacade.generarCodigoInforme(aprobacionRequisitosTecnicos));
			informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
		} catch (Exception e) {
			LOG.error("Error al actualizar: InformeTecnicoArtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}
	}

	/**
	 *
	 * <b> Metodo para visualizar el oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 *
	 * @return String: path
	 */
	public String visualizarOficio() {
		String pathPdf = null;
		try {
			PlantillaReporte plantillaReporte = this.informeOficioFacade.obtenerPlantillaReporte(getTipoDocumento()
					.getId());
			
			if(!revisar){
				informeTecnicoArt.setFechaEvaluacion(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
			}
//			informeTecnicoArt.setFechaEvaluacion(JsfUtil.devuelveFechaEnLetrasSinHora(informeTecnicoArt.getFechaCreacion()));

			EntityInformeTecnicoAproReqTec entityInforme = new EntityInformeTecnicoAproReqTec();
			entityInforme.setTipoTramite("");

			entityInforme.setModalidad("");
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
						}else {
							if(mgd.getNombre().equals("Otro")){
								modalidades += ", Otro (".concat(aprobacionRequisitosTecnicos.getOtraModalidad().concat(")"));
							}else{
								modalidades += ", " + mgd.getNombre();
							}
						}
						i++;
					}
					entityInforme.setModalidad(modalidades);
				}
			}
			if (aprobacionRequisitosTecnicos.isGestion() && aprobacionRequisitosTecnicos.isTransporte()) {
				entityInforme.setTipoTramite(" / Transporte de sustancias químicas peligrosas");
			} else if (aprobacionRequisitosTecnicos.isTransporte()) {
				entityInforme.setTipoTramite(" / Transporte de sustancias químicas peligrosas");
			}

			if (listaObservaciones == null || listaObservaciones.isEmpty()) {
				listaObservaciones = this.observacionesFacade.listarPorIdClaseNombreClase(
						aprobacionRequisitosTecnicos.getId(), AprobacionRequisitosTecnicos.class.getSimpleName());
			}
			String tablaObservaciones = "No existen observaciones";
			if (listaObservaciones != null && !listaObservaciones.isEmpty()) {

				tablaObservaciones = "<table border=\"1 solid black\" bordercolor=\"#000000\" cellpadding=\"5\" cellspacing=\"0\" style=\"font-size:10px\" width=\"100%\">"
						+ "<tr> <th style=\"width:5%\">Item</th> <th style=\"width:25%\">Sección</th> <th style=\"width:7%\">Cumple</th>"
						+ "<th style=\"width:25%\">Campo</th> <th style=\"width:38%\">Observaciones / Criterio técnico</th> </tr>";

				int item = 0;
				int pronunciamiento = 0;
				for (ObservacionesFormularios observaciones : listaObservaciones) {
					String cumple;
					item++;
					if (observaciones.isObservacionCorregida()) {
						cumple = "Si";
						pronunciamiento++;
					} else {
						cumple = "No";
					}

					String[] secciones = observaciones.getSeccionFormulario().split("\\.");
					//Hereeeeee
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

			entityInforme.setObservaciones(tablaObservaciones);
			Usuario usu = new Usuario();
			if(revisar)
				usu = usuarioFace.buscarUsuario(informeTecnicoArt.getTecnico());
			else {
				if (informeTecnicoArt.getTecnico() != loginBean.getNombreUsuario()){
					informeTecnicoArt.setTecnico(loginBean.getNombreUsuario());
					 usu = usuarioFace.buscarUsuario(loginBean.getNombreUsuario());
					 crudServiceBean.saveOrUpdate(informeTecnicoArt);
				}else{
					 usu = usuarioFace.buscarUsuario(informeTecnicoArt.getTecnico());
				}
			}
			
			
			if (usu != null) {
				entityInforme.setTecnico(usu.getPersona().getNombre());
			}else{
				usu = loginBean.getUsuario();
				entityInforme.setTecnico(usu.getPersona().getNombre());
				informeTecnicoArt.setTecnico(usu.getNombre());
				crudServiceBean.saveOrUpdate(informeTecnicoArt);
			}

			String articulo = " ";
			Persona persona = usuarioFace.buscarUsuarioPorIdFull(aprobacionRequisitosTecnicos.getUsuario().getId())
					.getPersona();

			if (persona.getTipoTratos() != null) {
				articulo += obtenerArticuloSegunTratamiento(persona.getTipoTratos().getNombre()) + " ";
			}
			String sujetoControl = articulo +" Sra./Sr. ";

			if (persona.getTipoTratos() != null && persona.getTipoTratos().getNombre() != null
					&& !persona.getTipoTratos().getNombre().isEmpty()) {
				sujetoControl = persona.getTipoTratos().getNombre();
			}

			sujetoControl = articulo + sujetoControl;

			/*if (persona.getTitulo() != null && !persona.getTitulo().trim().isEmpty()) {
				sujetoControl += " " + persona.getTitulo() + ",";
			}*/

			if (persona.getNombre() != null) {
				sujetoControl += " " + persona.getNombre();
			}

			entityInforme.setNumeroOficio(informeTecnicoArt.getNumeroOficio());
			entityInforme.setFechaEvaluacion(informeTecnicoArt.getFechaEvaluacion());


			if (informeTecnicoArt.getCantonProyecto() != null && !informeTecnicoArt.getCantonProyecto().isEmpty()) {
				entityInforme.setProvinciaProyecto(informeTecnicoArt.getProvinciaProyecto()+",");
				entityInforme.setCantonProyecto(""+informeTecnicoArt.getCantonProyecto()+",") ;
			} else {
				entityInforme.setProvinciaProyecto(informeTecnicoArt.getProvinciaProyecto());
				entityInforme.setCantonProyecto("");
			}
			if (informeTecnicoArt.getParroquiaProyecto() != null && !informeTecnicoArt.getParroquiaProyecto().isEmpty()) {
				entityInforme.setParroquiaProyecto("" + informeTecnicoArt.getParroquiaProyecto());
			} else {
				entityInforme.setParroquiaProyecto("");
			}



			entityInforme.setNumeroProyecto(informeTecnicoArt.getNumeroProyecto());

			entityInforme.setFechaSolicitud(JsfUtil.devuelveFechaEnLetrasSinHora(aprobacionRequisitosTecnicos
					.getFechaCreacion()));

			//Ciudad Emisión Informe técnico 
			entityInforme.setCiudadEmision((informeTecnicoArt.getCiudadEmision()!=null || !informeTecnicoArt.getCiudadEmision().isEmpty())
			?informeTecnicoArt.getCiudadEmision():loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());

			entityInforme.setSolicitud(aprobacionRequisitosTecnicos.getSolicitud());
			entityInforme.setText("validar el campo");
			//entityInforme.setSujetoControl(sujetoControl);
			String proponente = getProponente(sujetoControl);
			if(informeTecnicoArt.getEmpresa()!=null && proponente.contains(informeTecnicoArt.getEmpresa()))
				proponente = informeTecnicoArt.getEmpresa();

			informeTecnicoArt.setProponente(proponente);
			entityInforme.setSujetoControl(proponente);
			entityInforme.setProponente(proponente);

			entityInforme.setConclusiones(informeTecnicoArt.getConclusiones());
			entityInforme.setConclusionesAdicional(informeTecnicoArt.getConclusionesAdicional());
			entityInforme.setConclusionesRecomendaciones(informeTecnicoArt.getRecomendaciones());
			entityInforme.setObjetivos(informeTecnicoArt.getObjetivos());
			entityInforme.setAlcance(informeTecnicoArt.getAlcance());
			entityInforme.setAntecedentes(informeTecnicoArt.getAntecedentes());
			entityInforme.setEmpresa(informeTecnicoArt.getEmpresa());
			entityInforme.setFase(informeTecnicoArt.getFase());
			entityInforme.setNormaVigente(informeTecnicoArt.getNormaVigente());
			entityInforme.setAnalisisTecnico(informeTecnicoArt.getAnalisisTecnico());
			entityInforme.setCumpleMostrar(informeTecnicoArt.getPronunciamiento() == null
					|| informeTecnicoArt.getPronunciamiento().isEmpty() ? "PENDIENTE" : informeTecnicoArt
					.getPronunciamiento().equalsIgnoreCase(FAVORABLE) ? "CUMPLE" : "NO CUMPLE");
			nombreReporte = "InformeTecnico.pdf";
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);

			setInformePdf(informePdf);

			Path path = Paths.get(getInformePdf().getAbsolutePath(), new String[0]);
			this.archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(getInformePdf().getName()));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(this.archivoInforme);
			file.close();

			setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + getInformePdf().getName()));

			pathPdf = informePdf.getParent();
		} catch (Exception e) {
			this.LOG.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return pathPdf;
	}

	/**
	 *
	 * <b> Metodo para guardar el informe. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 *
	 */
	public void guardar() {
		try {
			informeTecnicoArt.setPronunciamiento(pronunciamiento ? FAVORABLE : OBSERVACION);
			informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
			visualizarOficio();
			subirDocumentoAlfresco();
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			guardado = true;
		} catch (Exception e) {
			this.LOG.error("Error al guardar informe técnico", e);
		}
	}

	/**
	 *
	 * <b> Metodo que guarda las varibles en el bpm. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 *
	 */
	@EJB ConexionBpms conexionBpms;
	
	public void guardarVariablesBpm() {

		try {
			Map<String, Object> params = new ConcurrentHashMap<>();
			params.put(PRONUNCIAMENTO, informeTecnicoArt.getPronunciamiento().equals(FAVORABLE));
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
					.getProcessInstanceId(), params);
//			for (Map.Entry<String, Object> parametros : params.entrySet()) {
//                conexionBpms.updateVariables(JsfUtil.getCurrentProcessInstanceId(), parametros.getKey(),parametros.getValue().toString());
//            }
		} catch (Exception e) {
			LOG.error("Error al guardar el informe técnico", e);
			JsfUtil.addMessageError("Error al guardar el informe técnico");
		}
	}

	/**
	 *
	 * <b> Metodo para continuar con el oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 *
	 */
//	public String continuar() {
//		String url = URL_OFICIO;
//		try {
//
//			informeTecnicoArt.setPronunciamiento(pronunciamiento ? FAVORABLE : OBSERVACION);
//			List<String> errors = new ArrayList<>();
//			if (informeTecnicoArt.getNormaVigente() != null && informeTecnicoArt.getNormaVigente().trim().isEmpty()) {
//				errors.add("El campo '3. ANTECEDENTES' es requerido.");
//			}
//
//			if (informeTecnicoArt.getConclusiones() != null && informeTecnicoArt.getConclusiones().trim().isEmpty()) {
//				errors.add("El campo '6. CONCLUSIONES' es requerido.");
//			}
//
//			if (!pronunciamiento && !validarExistenciaObservacionesProponente()) {
//				errors.add("El campo 'Los requisitos, en base a normativa 026 y sus anexos c y b, cumplen con las normas técnicas y legales' debe tener el valor 'Si', porque existen observaciones dirigidas al proponente sin corregir.");
//			}
//
//			if (pronunciamiento && validarExistenciaObservacionesProponente()) {
//				errors.add("El campo 'Los requisitos, en base a normativa 026 y sus anexos c y b, cumplen con las normas técnicas y legales' debe tener el valor 'No', porque existen observaciones dirigidas al proponente sin corregir.");
//			}
//
//			if (!errors.isEmpty()) {
//				JsfUtil.addMessageError(errors);
//				return "";
//			}
//			visualizarOficio();
//			String urlActual = JsfUtil.getRequest().getRequestURI();
//			if (urlActual.equals("/suia-iii/control/aprobacionRequisitosTecnicos/documentos/informeTecnicoObservacionArt.jsf")
//				&& (Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL") 
//				|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE"))) {
//				subirDocumentoAlfresco();
//			} 
//			
//			informeTecnicoArt.setPronunciamiento(pronunciamiento ? FAVORABLE : OBSERVACION);
//			informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
//			guardarVariablesBpm();
//			if (!tipo.isEmpty()) {
//				url += "?tipo=" + tipo;
//			}
//			JsfUtil.redirectTo(url);
//			
//
//		} catch (Exception e) {
//			LOG.error("Error al guardar el informe técnico", e);
//			JsfUtil.addMessageError("Error al guardar el informe técnico");
//		}
//		return url;
//	}
	
	public String continuar() {
	    String url = URL_OFICIO;
	    try {
	        // Verificar si el informe ha sido firmado
	        if (!documentoFirmado()) {
	            JsfUtil.addMessageError("El informe debe estar firmado antes de continuar.");
	            return ""; // No avanzar a la siguiente página
	        }

	        informeTecnicoArt.setPronunciamiento(pronunciamiento ? FAVORABLE : OBSERVACION);
	        List<String> errors = new ArrayList<>();
	        if (informeTecnicoArt.getNormaVigente() != null && informeTecnicoArt.getNormaVigente().trim().isEmpty()) {
	            errors.add("El campo '3. ANTECEDENTES' es requerido.");
	        }

	        if (informeTecnicoArt.getConclusiones() != null && informeTecnicoArt.getConclusiones().trim().isEmpty()) {
	            errors.add("El campo '6. CONCLUSIONES' es requerido.");
	        }

	        if (!pronunciamiento && !validarExistenciaObservacionesProponente()) {
	            errors.add("El campo 'Los requisitos, en base a normativa 026 y sus anexos c y b, cumplen con las normas técnicas y legales' debe tener el valor 'Si', porque existen observaciones dirigidas al proponente sin corregir.");
	        }

	        if (pronunciamiento && validarExistenciaObservacionesProponente()) {
	            errors.add("El campo 'Los requisitos, en base a normativa 026 y sus anexos c y b, cumplen con las normas técnicas y legales' debe tener el valor 'No', porque existen observaciones dirigidas al proponente sin corregir.");
	        }

	        if (!errors.isEmpty()) {
	            JsfUtil.addMessageError(errors);
	            return "";
	        }
	        
	        visualizarOficio();
	        informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
	        guardarVariablesBpm();

	        if (!tipo.isEmpty()) {
	            url += "?tipo=" + tipo;
	        }
	        JsfUtil.redirectTo(url);

	    } catch (Exception e) {
	        LOG.error("Error al guardar el informe técnico", e);
	        JsfUtil.addMessageError("Error al guardar el informe técnico");
	    }
	    return url;
	}
	
	/**
	 * Método que verifica si el documento ya ha sido firmado.o
	 * @return true si el documento ha sido firmado, false en caso contrario.
	 */
	private boolean documentoFirmado() {
		
		if (token && documentosFacade.verificarFirmaVersion(documentoSubido.getIdAlfresco())) {
			return true;
		}else{
			if(!token){
				 return documentoInformacionManual != null && documentoInformacionManual.getIdAlfresco() != null;
			}
		}		
	   return false;
	}

	/**
	 *
	 * <b> Metodo que valida la tarea con la pagina. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 *
	 */
	public void validarTareaBpm() {

		String url = URL_INFORME;
		if (!tipo.isEmpty()) {
			url += "?tipo=" + tipo;
		}
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
	}

	/**
	 *
	 * <b> Metodo que guarda el informe en el alfresco. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 *
	 * @return documento: Documento
	 */
	public Documento subirDocumentoAlfresco() {
		try {
			byte[] data = Files.readAllBytes(Paths.get(getInformePdf().getAbsolutePath()));
			Documento documentoaux = UtilDocumento.generateDocumentPDFFromUpload(data, informeTecnicoArt.getPronunciamiento().equals(OBSERVACION)?informeTecnicoArt.getNombreInforme().replace("AprobacionRequisitos", "ObservacionRequisitos"):informeTecnicoArt.getNombreInforme());
			documentoaux.setIdTable(informeTecnicoArt.getId());
			Documento documento = informeOficioFacade.subirFileAlfresco(
					documentoaux,
					informeTecnicoArt.getAprobacionRequisitosTecnicos().getSolicitud(),
					bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(),
					InformeTecnicoAproReqTec.class.getSimpleName(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART);

			informeTecnicoArt.setDocumentoInformeTecnicoAprobacion(documento);
			return documento;
		} catch (IOException | ServiceException | CmisAlfrescoException e) {
			LOG.error("Error al guardar el informe técnico", e);
			JsfUtil.addMessageError("Error al guardar el informe técnico");
			return null;
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

	private String getProponente(String label) throws ServiceException {
		if (label == null) {
			label = "";
		}

		//if(proyectosBean.getProyecto().getId()!= null){// proceso no voluntario
			//if ( proyectosBean.getProyecto().getUsuario().getPersona() != null) {
				if (aprobacionRequisitosTecnicos.getUsuario().getPersona().getIdNacionalidad() != null && aprobacionRequisitosTecnicos.getUsuario().getNombre().length()<=10) {

					return aprobacionRequisitosTecnicos.getUsuario().getPersona().getNombre();
				} else {

					Organizacion organizacion = organizacionFacade.buscarPorPersona(aprobacionRequisitosTecnicos.getUsuario()
							.getPersona(), aprobacionRequisitosTecnicos.getUsuario().getNombre());
					if (organizacion != null) {
						return organizacion.getNombre();
					}
					/*organizacion = organizacionFacade.buscarPorPersona(proyectosBean.getProyecto()
							.getUsuario().getPersona(),proyectosBean.getProyecto().getUsuario().getNombre());

					if(organizacion==null){
						ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(proyectosBean.getProyecto()
								.getUsuario().getPersona().getIdUbicacionGeografica());
						proyectosBean.setProponente(proyectosBean.getProyecto().getUsuario().getPersona().getNombre());
					}else{
						proyectosBean.setProponente(organizacion.getNombre());
						ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(organizacion
								.getIdUbicacionGeografica());
					}*/
				}
			//}

		//}

		//if(aprobacionRequisitosTecnicos.getUsuario().getPersona().getIdNacionalidad()!=null)

		/*Organizacion organizacion = organizacionFacade.buscarPorPersona(aprobacionRequisitosTecnicos.getUsuario()
				.getPersona(), aprobacionRequisitosTecnicos.getUsuario().getNombre());
		if (organizacion != null) {
			return organizacion.getNombre();
		}*/
		return label;
	}

	public boolean validarExistenciaObservacionesProponente() {
		boolean existenObservacionesProponente = true;
		List<ObservacionesFormularios> listaObservacionesNoCorregidas;
		try {
			listaObservacionesNoCorregidas = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
					aprobacionRequisitosTecnicos.getId(), AprobacionRequisitosTecnicos.class.getSimpleName());
			if (listaObservacionesNoCorregidas == null || listaObservacionesNoCorregidas.isEmpty()) {
				existenObservacionesProponente = false;
			}
		} catch (ServiceException exception) {
			LOG.error(
					"Ocurrió un error al recuperar las observaciones dirigidas al proponente en el proceso Registro de Generador",
					exception);
		}
		return existenObservacionesProponente;
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
			// TODO Auto-generated catch block
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
			visualizarOficio();			
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;

			tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART.getIdTipoDocumento());
			documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(informeTecnicoArt.getId(),
					InformeTecnicoAproReqTec.class.getSimpleName(), tipoDocumento);

			System.out.println("proyecto=====>   ");
			if (documentoSubido != null && documentoSubido.getIdAlfresco() != null) {
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
		informeTecnicoArt.setPronunciamiento(pronunciamiento ? FAVORABLE : OBSERVACION);
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
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;

			tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART.getIdTipoDocumento());

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
				documentoInformacionManual = documentosFacade.guardarDocumentoAlfrescoSinProyecto(String.valueOf(informeTecnicoArt.getId()),
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

			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(informeTecnicoArt.getId(),
					InformeTecnicoAproReqTec.class.getSimpleName(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART);

			documentoContent = documentosFacade.descargar(auxdoc.getIdAlfresco());
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(auxdoc.getNombre());
				documentoDescargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		}catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

}