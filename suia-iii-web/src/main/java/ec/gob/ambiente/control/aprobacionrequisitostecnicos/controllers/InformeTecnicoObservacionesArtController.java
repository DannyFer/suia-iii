package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeTecnicoArtBean.PRONUNCIAMENTO;
import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeTecnicoArtBean.REVISAR;
import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean.DIRECCION_PROVINCIAL;
import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean.GESTION;
import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean.GESTION_TRANSPORTE;
import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean.OBSERVACION;
import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean.TRANSPORTE;
import static ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean.URL_BANDEJA_ENTRADA;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeTecnicoArtBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.oficios.bean.OficioAprobacionRtBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.SendCopyBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoAproReqTec;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.OficioAproReqTec;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityOficioAproReqTec;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

/**
 * *
 * 
 * <b> Metodo que obtiene los datos para el pdf del informe tecnico. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 07/08/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class InformeTecnicoObservacionesArtController implements Serializable {

	public static final String TIPO = "tipo";
	public static final String INFORME_TECNICO_APROBACION = "Informe_Tecnico_Aprobacion_Requisitos";
	public static final String TIPO_PDF = ".pdf";
	public static final String URL_INFORME_TECNICO = "/control/aprobacionRequisitosTecnicos/documentos/informeTecnicoObservacionArt.jsf";
	public static final String URL_OFICIO_APROBACION = "/control/aprobacionRequisitosTecnicos/documentos/oficioAprobacionArt.jsf";
	public static final String INFORME_APROBACION_REQUISITO_OBSERVACIONES = "informeAprobacionRequisitos";
	public static final String OFICIO_APROBACION_REQUISITOS_OBSERVACIONES = "OficioAprobacionRequisitos";
	/**
     *
     */
	private static final long serialVersionUID = -4595034866077116442L;

	private static final Logger LOGGER = org.apache.log4j.Logger
			.getLogger(InformeTecnicoObservacionesArtController.class);

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private InformeOficioArtFacade informeOficioFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private AreaFacade areaFacade;

	@EJB
	ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@EJB
	ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesController}")
	private ObservacionesController observacionesController;

	@Getter
	@Setter
	private String tipo = "";

	@Getter
	@Setter
	private String informePath, pathTotal;

	@Getter
	@Setter
	private String oficioPath;

	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	private boolean requiereModificaciones;

	@Getter
	@Setter
	private InformeTecnicoAproReqTec informeTecnicoArt;

	@Getter
	@Setter
	private String nombreReporte;

	@Getter
	@Setter
	private List<ObservacionesFormularios> listaObservaciones;

	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	private OficioAproReqTec oficioArt;

	@EJB
	private UsuarioFacade usuarioFace;

	private String nombreEmpresa;

	private String nombreEmpresaMostrar, path;
	private String cargoRepresentanteLegalMostrar;
	@Getter
	@Setter
	private Usuario usuarioAutoridad;

	@Getter
	@Setter
	private File oficioPdf;
	@Getter
	@Setter
	private byte[] archivoOficioAprobacion;
	@EJB
	private ObservacionesFacade observacionesFacade;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	private List<String> listaErroresObservaciones;

	@Getter
	@Setter
	private Boolean existeCorreciones, esproyectoCoa, documentoDescargado;
	
	@Getter
	@Setter
	private Documento documentoSubido, documentoInformacionManual;
	
	@Getter
	@Setter
	private boolean token, ambienteProduccion, firmaSoloToken, informacionSubida;
	
	@Getter
	@Setter
	public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	public boolean soloEnviar = false, guardado = false, esCoordinador = false;

	@PostConstruct
	public void init() {
		try {
			
			path = "";
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			token = true;
			
			if (!ambienteProduccion) {
				verificaToken();
				documentoDescargado = true;
			}
			
			
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(bandejaTareasBean.getProcessId(),
							loginBean.getUsuario());
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getProcessInstanceId());
			Object contadorBandejaTecnicoValue=null;
			int contadorBandejaTecnico=0;
			if (variables.containsKey("cantidadObservaciones")) {
				contadorBandejaTecnicoValue = variables.get("cantidadObservaciones").toString();
			}
			//obtengo si es un proyecto Rcoa o no
			esproyectoCoa=false;
			try {
				esproyectoCoa = Boolean.parseBoolean(variables.get("vieneRcoa").toString()) ;
			} catch (Exception e) {
			}
			
			if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
				contadorBandejaTecnico = 0;
			} else {
				try {
					contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());
				} catch (Exception e) {
					LOGGER.error("Error recuperado cantidad de observaciones en registro de generador", e);
				}
			}
			
			
			informeTecnicoArt = informeOficioFacade.obtenerInformeTecnicoPorArt(
					TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART, aprobacionRequisitosTecnicos.getId(),contadorBandejaTecnico);
			oficioArt = informeOficioFacade.obtenerOficioAprobacionPorArt(
					TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART, aprobacionRequisitosTecnicos.getId(),contadorBandejaTecnico);
		

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
			
//			usuarioAutoridad = usuarioFace.buscarUsuarioWithOutFilters(cedulaAutoridad);
			
			Map<String, Object> parametro = new ConcurrentHashMap<String, Object>();
			if (pronunciamiento && !direccionProvincial) {
				usuarioAutoridad =areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
				parametro.put("subsecretaria", usuarioAutoridad.getNombre());
			}
			else {
				if(aprobacionRequisitosTecnicos.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
					usuarioAutoridad =areaFacade.getDirectorProvincial(aprobacionRequisitosTecnicos.getAreaResponsable());
				else 
					usuarioAutoridad =areaFacade.getDirectorProvincial(aprobacionRequisitosTecnicos.getAreaResponsable().getArea());
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
			{
				
			}
			
			JsfUtil.getBean(InformeTecnicoArtBean.class).visualizarOficio();
			setInformePath(JsfUtil.getBean(InformeTecnicoArtBean.class).getInformePath());
			
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			if (params.containsKey(TIPO)) {
				tipo = params.get(TIPO);
				if (params.get(TIPO).equals(REVISAR)) {
					revisar = true;
				}
			}
			
			esCoordinador = false;
			for(RolUsuario rol : JsfUtil.getLoggedUser().getRolUsuarios()){
				if(rol.getRol().getNombre().equals(Constantes.getRoleAreaName("role.area.coordinador"))){
					esCoordinador = true;
					break;
				}
			}

			if(tipo.equals("revisar") && esCoordinador){
				soloEnviar = false;
			}else{
				soloEnviar = true;
			}
			
			cargarDatosEmpresa();
			visualizarOficio();
			obtenerPath(informeTecnicoArt);
			listaErroresObservaciones = new ArrayList<String>();
			documentoDescargado = false;
			
		} catch (Exception e) {
			LOGGER.error("Error al inicializar: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}
	}
	
	public boolean verificaToken() {
		if (firmaSoloToken) {
			token = true;
			return token;
		}

		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
	
	private void cargarDatosEmpresa() throws ServiceException{
		String[] cargoEmpresa = informeOficioFacade.getEmpresa(aprobacionRequisitosTecnicos);
		if (cargoEmpresa != null) {
			nombreEmpresa = cargoEmpresa[0] + " de la empresa " + cargoEmpresa[1];
			nombreEmpresaMostrar = cargoEmpresa[1];
			cargoRepresentanteLegalMostrar = cargoEmpresa[0];
		}
	}

	public void obtenerPath(InformeTecnicoAproReqTec informeTecnicoArt) throws Exception {
		try {
			byte[] oficioDoc = documentosFacade.descargar(informeTecnicoArt.getDocumentoInformeTecnicoAprobacion().getIdAlfresco());
			nombreReporte = informeTecnicoArt.getDocumentoInformeTecnicoAprobacion().getNombre();
			String reporteHtmlfinal = nombreReporte.replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioDoc);
			file.close();
			informePath = archivoFinal.getAbsolutePath();
			informePath = (JsfUtil.devolverContexto("/reportesHtml/" + nombreReporte));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error al cargar el informe tecnico", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
	}
	
	public StreamedContent descargarInformacion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;

			tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;
			documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(informeTecnicoArt.getId(),
					InformeTecnicoAproReqTec.class.getSimpleName(), tipoDocumento);
			byte[] informe = documentosFacade
					.descargar(documentoSubido.getIdAlfresco());
			
			if (informe != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(informe));
				content.setName("informeTecnico");
				documentoDescargado = true;
				
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}

		catch (Exception e) {
			JsfUtil.addMessageError(
					"Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public boolean validarSoloInformeFirmado() {
		// Si el usuario es "director", no validamos ni el informe ni el oficio
		if ("director".equals(tipo)) {
			return true; // No requiere firma ni del informe ni del oficio
		}

		// Validación estándar para otros tipos de usuarios
		boolean informeFirmado = validarFirmaDocumento(informeTecnicoArt.getDocumentoInformeTecnicoAprobacion());
		boolean oficioFirmado = validarFirmaDocumento(oficioArt.getDocumentoOficio());

		if (!informeFirmado) {
			JsfUtil.addMessageError("El informe técnico no ha sido firmado. Debe firmarlo antes de continuar.");
			return false;
		}
		if (oficioFirmado) {
			JsfUtil.addMessageError("El oficio no debe estar firmado.");
			return false;
		}

		return true;
	}
	
	public boolean validarFirmaDocumento(Documento documento) {
		try {
			// Supongamos que el estado de la firma está en un campo del documento.
			return documento != null;
		} catch (Exception e) {
			LOGGER.error("Error al verificar la firma del documento", e);
			return false;
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
	public String visualizarOficio() {
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
				plantillaReporte = informeOficioFacade
						.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART.getIdTipoDocumento());
				nombreReporte = "OficioAprobacionRequisitosTecnicos.pdf";
			}

			oficioArt.setFechaOficio(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
			
//			String anioCodigoOficio=oficioArt.getNumeroOficio().substring(10, 4);
//			String anioActual=secuenciasFacade.getCurrentYear();
//			if(!anioCodigoOficio.equals(anioActual)){
//				oficioArt.setNumeroOficio(oficioArt.getNumeroOficio().replace(anioCodigoOficio, anioActual));
//				informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
//			}			
			
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
							modalidades = mgd.getNombre();
						} else {
							modalidades += ", " + mgd.getNombre();
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
						+ " &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; </strong>";

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
				entityOficio.setNombreEmpresaMostrar("<p><strong>Empresa:&nbsp; </strong>" + nombreEmpresaMostrar
						+ "</p>");
				entityOficio
						.setCargoRepresentanteLegalMostrar("<p><strong>Cargo:</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
								+ cargoRepresentanteLegalMostrar + "</p>");
			}
			entityOficio.setSujetoControlEncabez(sujetoControlEncabez);
			entityOficio.setNumeroOficio(oficioArt.getNumeroOficio());
			// incluir informacion de la sede de la zonal en el documento
			String canton="";
			if(aprobacionRequisitosTecnicos.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				canton = aprobacionRequisitosTecnicos.getAreaResponsable().getArea().getAreaSedeZonal()+", ";
			else if(aprobacionRequisitosTecnicos.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
				// incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				canton = usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+", ";
				if(esproyectoCoa){
					ProyectoLicenciaCoa proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(aprobacionRequisitosTecnicos.getProyecto());
					if(proyectoLicenciaCoa!=null && proyectoLicenciaCoa.getId() != null){
						canton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null)+", ";
					}
				}else{
					ProyectoLicenciamientoAmbiental proyecto = this.proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(this.aprobacionRequisitosTecnicos.getProyecto());
					if(proyecto!=null && proyecto.getId() != null){
						canton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTOSUIAIII", null, proyecto, null)+", ";
					}
				}
			}else
				canton = usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+", ";
			entityOficio.setFechaOficio(canton+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
			entityOficio.setProponente(oficioArt.getProponente());
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
			entityOficio.setAutoridad(usuarioAutoridad.getPersona().getNombre());
			entityOficio.setCargoAutoridad(obtenerRolesUsuarioAutoridad(usuarioAutoridad));

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

			setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + getOficioPdf().getName()));

			pathPdf = informePdf.getParent();
		} catch (Exception e) {
			LOGGER.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return pathPdf;
	}
	
	public boolean isInformeTecnicoFirmado() {
		return validarFirmaDocumento(informeTecnicoArt.getDocumentoInformeTecnicoAprobacion());
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
                        tablaObservaciones = "<table  class=\"tablaConBorde\" border=\"1 solid black\" bordercolor=\"#000000\" cellpadding=\"5\" cellspacing=\"0\" style=\"font-size:10px\" width=\"100%\">"
                        + "<tr style=\"text-align:justify;\"> <th style=\"width:5%\">Item</th> <th style=\"width:25%\">Sección</th> <th style=\"width:7%\">Cumple</th>"
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
                            String seccionObtenida = secciones[secciones.length - 1]
                                    .replaceAll("(.)(\\p{Lu})", "$1 $2");
                            tablaObservaciones += "<tr style=\"text-align:justify;\"> <td>" + item + "</td> <td>" + seccionObtenida + "</td> <td>"
                                    + cumple + "</td>  <td>" + observaciones.getCampo() + "</td>  <td>"
                                    + observaciones.getDescripcion() + " </td>" + "</tr>";

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

	public void validarTareaBpm() {
		String url = URL_INFORME_TECNICO;
		if (!tipo.isEmpty()) {
			url += "?tipo=" + tipo;
		}
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
	}

	public void continuar() {
		try {
			if (validarObservacionesInforme(!getExisteCorreciones())) {
				informeTecnicoArt.setExisteCorrecciones(getExisteCorreciones());
				informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);
			}
		} catch (Exception e) {
			LOGGER.error("Error al guardar informe técnico", e);
		}
	}

	/**
	 * <b> Metodo para guardar el oficio. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 */
	public void guardar() {
		try {
			continuar();
			oficioArt.setExisteCorrecciones(false);
			informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
			if (getExisteCorreciones() != null && getExisteCorreciones()) {
				requiereModificaciones = true;
			} else {
				requiereModificaciones = false;
			}
			guardarVariablesBpm();
			String url = URL_OFICIO_APROBACION;
			if (!tipo.isEmpty()) {
				url += "?tipo=" + tipo;
			}
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.getBean(SendCopyBean.class).guardarFilesCopies(oficioArt.getClass().getSimpleName(),
					oficioArt.getId(), getUserName());
			JsfUtil.redirectTo(url);
		} catch (Exception e) {
			LOGGER.error("Error al guardar informe técnico", e);
			JsfUtil.addMessageError("Error al guardar informe técnico");
		}
	}

	/**
	 * <b> Metodo que guarda las varibles en el bpm. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 */
	public void guardarVariablesBpm() {

		try {
			Map<String, Object> params = new ConcurrentHashMap<>();
			params.put("existeCorrecciones", requiereModificaciones);
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
					.getProcessInstanceId(), params);
		} catch (Exception e) {
			LOGGER.error("Error al guardar el informe técnico", e);
			JsfUtil.addMessageError("Error al guardar el informe técnico");
		}

	}

	public String getUserName() {
		return JsfUtil.getLoggedUser().getNombre();
	}

	public Boolean validarObservacionesInforme(Boolean estado) {
		List<ObservacionesFormularios> observaciones = observacionesController.getListaObservacionesBB().get("tit")
				.getMapaSecciones().get(INFORME_APROBACION_REQUISITO_OBSERVACIONES);
		if (observaciones == null || observaciones.isEmpty()) {
			observaciones = new ArrayList<ObservacionesFormularios>();

		}
		LOGGER.info(observaciones.size());

		if (estado) {
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
				listaErroresObservaciones
						.add("No existen observaciones sin corregir del informe técnico. Por favor rectifique los datos.");
				// JsfUtil.addMessageError("No existen observaciones sin corregir del informe técnico. Por favor rectifique los datos.");
				return false;
			}

		} else {
			for (ObservacionesFormularios observacion : observaciones) {
				if (observacion.getUsuario().equals(loginBean.getUsuario()) && !observacion.isObservacionCorregida()) {
					listaErroresObservaciones
							.add("Existen observaciones sin corregir del informe técnico. Por favor rectifique los datos.");
					// JsfUtil.addMessageError("Existen observaciones sin corregir del informe técnico. Por favor rectifique los datos.");
					return false;
				}
			}

		}

		return true;
	}

	public String obtenerRolesUsuarioAutoridad(Usuario usuario) {
		String cargo = "";
		if (usuario.getPersona() != null && usuario.getPersona().getPosicion() != null) {
			cargo = usuario.getPersona().getPosicion();
		}
		return cargo;
	}

	/**
	 * <b> Metodo para completar la tarea. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return
	 */
//	public String completarTarea() {
//		try {
//			List<String> errors = new ArrayList<>();
//			if (oficioArt.getNormativa() != null && oficioArt.getNormativa().trim().isEmpty()) {
//				errors.add("El campo '2. NORMATIVA' es requerido.");
//			}
//
//			if (oficioArt.getTxtDisposicionesPresentancionResultados() != null
//					&& oficioArt.getTxtDisposicionesPresentancionResultados().trim().isEmpty()) {
//				errors.add("El campo '4. DISPOSICIONES ADICIONALES' es requerido.");
//			}
//			if (!errors.isEmpty()) {
//				JsfUtil.addMessageError(errors);
//				return "";
//			}
//
//			if (validar()) {
//				
//				String url = JsfUtil.getRequest().getRequestURI();
//				if (url.equals("/suia-iii/control/aprobacionRequisitosTecnicos/documentos/informeTecnicoObservacionArt.jsf")
//					&& (Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL") 
//					|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE")) && !getExisteCorreciones()) {
//					subirDocumentoAlfresco();					
//					JsfUtil.getBean(InformeTecnicoArtBean.class).subirDocumentoAlfresco();
//				}
//				
//				visualizarOficio();
//				this.informeOficioFacade.guardarOficioAprobacionArt(oficioArt);
//				if (getExisteCorreciones() != null && getExisteCorreciones()) {
//					requiereModificaciones = true;
//
//				} else {
//					requiereModificaciones = false;
//				}
//				guardarVariablesBpm();
//				continuar();
//				aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(bandejaTareasBean.getTarea()
//						.getProcessInstanceId(), loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId());
//				JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
//				JsfUtil.getBean(SendCopyBean.class).sendFilesCopies(oficioArt.getClass().getSimpleName(),
//					oficioArt.getId(), getUserName(), new String[] { oficioArt.getOficioRealPath() });
//				return JsfUtil.actionNavigateTo(URL_BANDEJA_ENTRADA);
//
//			}
//
//		} catch (JbpmException e) {
//			LOGGER.error("Ocurrió un error al enviar la información.", e);
//			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
//		} catch (Exception e) {
//			LOGGER.error("Ocurrió un error al enviar la información.", e);
//			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
//		}
//		return "";
//	}
	
	public String completarTarea() {
		try {
//			
			if(!soloEnviar){
				if (token) {
					String idAlfrescoInforme = documentoSubido.getIdAlfresco();

					if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
						JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
						return "";
					}
				} else {
					if (informacionSubida) {		
						TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;
						tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;
						documentoInformacionManual = documentosFacade.guardarDocumentoAlfrescoSinProyecto(String.valueOf(informeTecnicoArt.getId()),
						String.valueOf(bandejaTareasBean.getTarea().getProcessInstanceId()),
						Long.valueOf(bandejaTareasBean.getTarea().getTaskId()), documentoInformacionManual,
						tipoDocumento);					

					} else {
						JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
						return "";
					}			
				}	
			}					

			// Validar campos requeridos del oficio
			List<String> errors = new ArrayList<>();
			if (oficioArt.getNormativa() == null || oficioArt.getNormativa().trim().isEmpty()) {
				errors.add("El campo '2. NORMATIVA' es requerido.");
			}

			if (oficioArt.getTxtDisposicionesPresentancionResultados() == null
					|| oficioArt.getTxtDisposicionesPresentancionResultados().trim().isEmpty()) {
				errors.add("El campo '4. DISPOSICIONES ADICIONALES' es requerido.");
			}

			if (!errors.isEmpty()) {
				JsfUtil.addMessageError(errors);
				return "";
			}

			// Si pasa la validación, continuar con la lógica
			if (validar()) {
				// Lógica para enviar o guardar
				visualizarOficio();
				this.informeOficioFacade.guardarOficioAprobacionArt(oficioArt);

			} else {
				requiereModificaciones = false;
			}
			guardarVariablesBpm();
			continuar();
			aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(
					bandejaTareasBean.getTarea().getProcessInstanceId(), loginBean.getUsuario(),
					bandejaTareasBean.getTarea().getTaskId());
			JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
			JsfUtil.getBean(SendCopyBean.class).sendFilesCopies(oficioArt.getClass().getSimpleName(), oficioArt.getId(),
					getUserName(), new String[] { oficioArt.getOficioRealPath() });
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		}

		catch (Exception e) {
			LOGGER.error("Ocurrió un error al enviar la información.", e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
		}
		return "";
	}

	public boolean validar() {
		boolean correciones = false;

		if (getExisteCorreciones()) {
			correciones = (validarObservaciones(getExisteCorreciones()) || validarObservacionesInforme(getExisteCorreciones()));
			if (correciones) {
				listaErroresObservaciones.clear();

			} else {
				for (String errorObservcion : listaErroresObservaciones) {
					JsfUtil.addMessageError(errorObservcion);
				}

			}

		} else {
			correciones = (validarObservaciones(getExisteCorreciones()) && validarObservacionesInforme(getExisteCorreciones()));
			for (String errorObservcion : listaErroresObservaciones) {
				JsfUtil.addMessageError(errorObservcion);
			}

		}

		listaErroresObservaciones.clear();
		return correciones;

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
					data, informeTecnicoArt.getPronunciamiento().equals(OBSERVACION)?oficioArt.getNombreOficio().replace("AprobacionRequisitos", "ObservacionRequisitos"):oficioArt.getNombreOficio()), oficioArt.getAprobacionRequisitosTecnicos().getSolicitud(),
					bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), OficioAproReqTec.class
							.getSimpleName(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART);

			oficioArt.setDocumentoOficio(documento);
			return documento;
		} catch (IOException | ServiceException | CmisAlfrescoException e) {
			LOGGER.error("Error al guardar el Oficio", e);
			JsfUtil.addMessageError("Error al guardar el Oficio");
			return null;
		}
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
		if (observaciones == null || observaciones.isEmpty()) {
			observaciones = new ArrayList<ObservacionesFormularios>();

		}
		LOGGER.info(observaciones.size());
		if (estado) {
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
				listaErroresObservaciones
						.add("No existen observaciones sin corregir en el oficio. Por favor rectifique los datos.");
				// JsfUtil.addMessageError("No existen observaciones sin corregir en el oficio. Por favor rectifique los datos.");
				return false;
			}

		} else {
			for (ObservacionesFormularios observacion : observaciones) {
				if (observacion.getUsuario().equals(loginBean.getUsuario()) && !observacion.isObservacionCorregida()) {
					listaErroresObservaciones
							.add("Existen observaciones sin corregir del Oficio. Por favor rectifique los datos.");

					// JsfUtil.addMessageError("Existen observaciones sin corregir del Oficio. Por favor rectifique los datos.");
					return false;
				}
			}

		}
		return true;
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
			LOGGER.info(getExisteCorreciones());
			if (getExisteCorreciones() != null && getExisteCorreciones()) {
				requiereModificaciones = true;

			} else {
				requiereModificaciones = false;

			}
			informeTecnicoArt.setExisteCorrecciones(getExisteCorreciones());
			informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);

			visualizarOficio();
			guardarVariablesBpm();
			JsfUtil.getBean(SendCopyBean.class).guardarFilesCopies(oficioArt.getClass().getSimpleName(),
					oficioArt.getId(), getUserName());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			guardado = true;
		} catch (Exception e) {
			LOGGER.error("Error al guardar informe técnico", e);
			JsfUtil.addMessageError("Error al guardar informe técnico");
		}
	}
	
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
			documentoInformacionManual.setNombre("informe_aprobacion_requisitos.pdf");

			documentoInformacionManual.setIdTable(informeTecnicoArt.getId());
			documentoInformacionManual.setNombreTabla(InformeTecnicoAproReqTec.class.getSimpleName());
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);
			informeOficioFacade.guardarInformeTecnicoArt(informeTecnicoArt);

			informacionSubida = true;

		} else {
			JsfUtil.addMessageError("No ha descargado el documento para la firmas");
		}
	}
	
	@SuppressWarnings("static-access")
	public String firmarOficio() {
		try {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;

			tipoDocumento = TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.TIPO_INFORME_TECNICO_ART.getIdTipoDocumento());
			documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(informeTecnicoArt.getId(),
					InformeTecnicoAproReqTec.class.getSimpleName(), tipoDocumento);

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
	
	public void enviarCorregir(){
		if(existeCorreciones){
			soloEnviar = true;
		}else{
			if(tipo.equals("revisar") && esCoordinador){
				soloEnviar = false;
			}else{
				soloEnviar = true;
			}
		}
	}

}
