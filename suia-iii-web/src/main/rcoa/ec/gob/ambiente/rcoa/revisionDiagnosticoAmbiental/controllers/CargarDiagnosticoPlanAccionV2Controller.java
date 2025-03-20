package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.jfree.util.Log;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.dto.EntityPlanAccion;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.HallazgoPlanAccionFacade;
import ec.gob.ambiente.rcoa.facade.MedioVerificacionPlanAccionFacade;
import ec.gob.ambiente.rcoa.facade.NotificacionDiagnosticoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.PlanAccionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.HallazgoPlanAccion;
import ec.gob.ambiente.rcoa.model.MedioVerificacionPlanAccion;
import ec.gob.ambiente.rcoa.model.PlanAccion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.util.UtilGenerarDocumentoSinFormato;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class CargarDiagnosticoPlanAccionV2Controller implements Serializable{

	 private static final long serialVersionUID = -875087443147320594L;
	  
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
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private PlanAccionFacade planAccionFacade;
	
	@EJB
	private HallazgoPlanAccionFacade hallazgoPlanAccionFacade;
	
	@EJB
	private MedioVerificacionPlanAccionFacade medioVerificacionPlanAccionFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
	private NotificacionDiagnosticoAmbientalFacade notificacionDiagnosticoAmbientalFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private EnvioNotificacionesMailFacade envioNotificacionesMailFacade;

	@Getter
	@Setter
	private DocumentosCOA documentoPlan;

	@Getter
	@Setter
	private DocumentosCOA documentoCertificacion, documentoManual, nuevoDocumentoVerificacion;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private List<DocumentosCOA> listaDocumentosDiagnostico, listaDocumentosEliminar, listaInstrumentos;
	
	@Getter
	@Setter
	private List<HallazgoPlanAccion> listaHallazgos, listaHallazgosEliminar;
	
	@Getter
	@Setter
	private List<MedioVerificacionPlanAccion> listaMedios, listaMediosEliminar;
	
	@Getter
	@Setter
	private PlanAccion planAccion;
	
	@Getter
	@Setter
	private HallazgoPlanAccion hallazgoPlanAccion, hallazgoPlanAccionSeleccionado;

	@Getter
	@Setter
	private MedioVerificacionPlanAccion nuevoMedioVerificacion;
	
	@Getter
	@Setter
	private EntityPlanAccion entityPlanAccion;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite, mensaje, mensajeDiagnostico, mensajePlan;
	
	@Getter
	@Setter
	private Boolean documentoDescargado = false, firmaSoloToken, habilitarFirmar, datosGuardados, esTramiteNotificado, esTramiteAnteriorSinPronunciamiento;

	@Getter
	@Setter
	private boolean token, documentoSubido;
	
	@Getter
	@Setter
	private Boolean documentoGuardado, habilitarEnviar, habilitarGuardar, visualizarCargarPlan, verNuevoHallazgo, editarHallazgo, editarMedio;
	
	@Getter
	@Setter
	private String urlAlfresco, documentoPath, nombreDocumento, nombreDocumentoFirmado, usuarioFirma, descripcionMedioVerificacion;
	
	@Getter
    @Setter
    private File filePlan;
	
	@Getter
    @Setter
    private byte[] archivoDocumento;
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		try {

			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");

			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			tramite = (String) variables.get("u_tramite");
			esTramiteNotificado = variables.containsKey("notificacionDiagnostico") ? (Boolean.valueOf((String) variables.get("notificacionDiagnostico"))) : false;

			listaDocumentosEliminar = new ArrayList<>();
			listaDocumentosDiagnostico = new ArrayList<>();
			listaHallazgosEliminar = new ArrayList<>();
			listaMediosEliminar = new ArrayList<>();
			
			nuevoDocumentoVerificacion = new DocumentosCOA();
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			listaDocumentosDiagnostico = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL,"Diagnostico ambiental");		
			
			mensaje = "<i>De conformidad a lo establecido en el Reglamento al Código Orgánico del Ambiente: “Art. 457. – Diagnóstico Ambiental. - Los operadores que se encuentren ejecutando obras, proyectos o actividades sin autorización administrativa, deberán presentar a la Autoridad Ambiental Competente un diagnóstico ambiental y, de ser necesario, su respectivo plan de acción para subsanar los incumplimientos normativos identificados, conforme a la norma técnica expedida para el efecto por la Autoridad Ambiental Nacional”, la guía estará disponible una vez que se emita la norma técnica correspondiente.</i>";
			
			mensajeDiagnostico = "<i>El Diagnóstico Ambiental deberá contener la siguiente información:"
					+ "<ul>"
					+ "<li> INFORMACIÓN GENERAL</li>"
					+ "<li> DESCRIPCIÓN DEL PROYECTO </li>"
					+ "<li> VERIFICACIÓN DE CUMPLIMIENTO NORMATIVO </li>"
					+ "<li> ANEXOS (Registros fotográficos, coordenadas geográficas del lugar de implantación y de todas las instalaciones que dispone la actividad) </li>"
					+ "<li> FIRMA (S) DE RESPONSABILIDAD (Documentos firmados electrónicamente) </li>"
					+ "</ul>"
					+ "</i>";
			
			mensajePlan = "El Plan de Acción se elaborará de conformidad a lo establecido Art. 506 del Reglamento al Código Orgánico del Ambiente, publicado en el Registro Oficial Suplemento Nro. 507 del 12 de junio de 2019:"
					+ "<br /><br />"
					+ "<i>Art. 506: Contenido de los planes de acción.- Los planes de acción deben contener, al menos:"
					+ "<ol>"
					+ "<li style=\"list-style-type:lower-alpha\">Hallazgos;</li>"
					+ "<li style=\"list-style-type:lower-alpha\">Medidas correctivas;</li>"
					+ "<li style=\"list-style-type:lower-alpha\">Cronograma que indique las fechas de inicio y finalización de las medidas correctivas a implementarse, incluyendo responsables y costos;</li>"
					+ "<li style=\"list-style-type:lower-alpha\">Indicadores y medios de verificación; y,</li>"
					+ "<li style=\"list-style-type:lower-alpha\">Instrumentos de avance o cumplimiento del plan.</li>"
					+ "</ol></i>";
			
			datosGuardados = false;
			visualizarCargarPlan = false;
			verNuevoHallazgo = false;
			habilitarEnviar = false;
			habilitarGuardar = false;
			
			cargarDatos();
			verificaToken();

			if(esTramiteNotificado && proyecto.getDiagnosticoCumpleNormativa() == null) {
				proyecto.setDiagnosticoCumpleNormativa(true);
				RequestContext context = RequestContext.getCurrentInstance();
		        context.execute("PF('dlgInfoCumple').show();");
			}
			
			esTramiteAnteriorSinPronunciamiento = false;
			if(!esTramiteNotificado && JsfUtil.getCurrentTask().getTaskSummary().getDescription().contains("cargarDiagnosticoAmbiental")) {
				esTramiteAnteriorSinPronunciamiento = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		if(esTramiteNotificado) {
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), "/pages/rcoa/revisionDiagnosticoAmbiental/cargarDiagnosticoAmbiental.jsf");
		} else if(esTramiteAnteriorSinPronunciamiento) {
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), "/pages/rcoa/revisionDiagnosticoAmbiental/cargarDiagnosticoAmbiental.jsf");
		} else {
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), "/pages/rcoa/revisionDiagnosticoAmbiental/cargarDiagnosticoPlanAccionV2.jsf");
		}
	}
	
	public void cargarDatos() throws Exception {
		planAccion = planAccionFacade.getPorProyecto(proyecto.getId());
		
		if(planAccion != null && planAccion.getId() != null) {
			listaHallazgos = hallazgoPlanAccionFacade.getPorPlan(planAccion.getId());
			
			for (HallazgoPlanAccion hallazgo : listaHallazgos) {
				List<MedioVerificacionPlanAccion> listaMedios = medioVerificacionPlanAccionFacade.getPorHallazgo(hallazgo.getId());
				
				if(listaMedios != null) {
					for (MedioVerificacionPlanAccion medio : listaMedios) {
						List<DocumentosCOA> listaAdjuntos = documentosFacade.documentoXTablaIdXIdDoc(medio.getId(), 
								TipoDocumentoSistema.RCOA_MEDIO_VERIFICACION_PLAN_ACCION, MedioVerificacionPlanAccion.class.getSimpleName());
						
						if(listaAdjuntos != null && listaAdjuntos.size() > 0)
							medio.setDocumentoAdjunto(listaAdjuntos.get(0));
					}
				}
				
				hallazgo.setListaMedios(listaMedios);
				
				List<DocumentosCOA> listaInstrumentos = documentosFacade.documentoXTablaIdXIdDoc(hallazgo.getId(), 
						TipoDocumentoSistema.RCOA_INSTRUMENTO_AVANCE_PLAN_ACCION,"Instrumentos de avance");
				hallazgo.setListaInstrumentos(listaInstrumentos);
			}
			
			generarPlanAccion(true);
		}
		
		if(proyecto.getDiagnosticoCumpleNormativa() != null && !proyecto.getDiagnosticoCumpleNormativa()) {
			habilitarIngresoPlanAccion();
		} else if(proyecto.getDiagnosticoCumpleNormativa() != null && proyecto.getDiagnosticoCumpleNormativa()) {
			habilitarEnviar = true;
			habilitarGuardar = false;

			visualizarCargarPlan = false;
		}
		
		buscarUsuarioForma();
	}

	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(firmaSoloToken)
			token = true;
		
		return token;
	}	

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
	
	public void uploadDiagnostico(FileUploadEvent event) {
		String docFirmado = documentosFacade.verificarFirma(event.getFile().getContents());
		
		if(docFirmado.equals("true")) {
			DocumentosCOA documentoLocal = new DocumentosCOA();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoLocal.setContenidoDocumento(contenidoDocumento);
			documentoLocal.setNombreDocumento(event.getFile().getFileName());
			documentoLocal.setExtencionDocumento(".pdf");		
			documentoLocal.setTipo("application/pdf");
			documentoLocal.setNombreTabla("Diagnostico ambiental");
			documentoLocal.setIdTabla(proyecto.getId());
			documentoLocal.setProyectoLicenciaCoa(proyecto);
			
			listaDocumentosDiagnostico.add(documentoLocal);
		
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else {
			String mensaje = (docFirmado.equals("false")) ? "Los documentos deben ser firmados electrónicamente." : docFirmado;
			JsfUtil.addMessageError(mensaje);
		}
	}

	public void uploadMedioVerificacion(FileUploadEvent event) {
		String[] split=event.getFile().getContentType().split("/");
        String extension = "."+split[split.length-1];

		nuevoDocumentoVerificacion = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		nuevoDocumentoVerificacion.setContenidoDocumento(contenidoDocumento);
		nuevoDocumentoVerificacion.setNombreDocumento(event.getFile().getFileName());
		nuevoDocumentoVerificacion.setExtencionDocumento(extension);		
		nuevoDocumentoVerificacion.setTipo(event.getFile().getContentType());
		nuevoDocumentoVerificacion.setNombreTabla(MedioVerificacionPlanAccion.class.getSimpleName());
		nuevoDocumentoVerificacion.setIdTabla(hallazgoPlanAccion.getId());
		nuevoDocumentoVerificacion.setProyectoLicenciaCoa(proyecto);
	}

	public void uploadInstrumentos(FileUploadEvent event) {
		DocumentosCOA documentoLocal = new DocumentosCOA();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoLocal.setContenidoDocumento(contenidoDocumento);
		documentoLocal.setNombreDocumento(event.getFile().getFileName());
		documentoLocal.setExtencionDocumento(".pdf");		
		documentoLocal.setTipo("application/pdf");
		documentoLocal.setNombreTabla("Instrumentos de avance");
		documentoLocal.setIdTabla(hallazgoPlanAccion.getId());
		documentoLocal.setProyectoLicenciaCoa(proyecto);
			
		listaInstrumentos.add(documentoLocal);
		
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void eliminarDocumento(Integer lista, DocumentosCOA documento) {
		try {
			if (documento.getId() != null) {
				documento.setEstado(false);
				listaDocumentosEliminar.add(documento);
			}
			
			if(lista.equals(1))
				listaDocumentosDiagnostico.remove(documento);
			else if(lista.equals(4))
				listaInstrumentos.remove(documento);
		} catch (Exception e) {
			Log.debug(e.toString());
		}
	}
	
	public StreamedContent descargarDocumento(DocumentosCOA documento) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade
						.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getContenidoDocumento()), documento.getExtencionDocumento());
			content.setName(documento.getNombreDocumento());
		}
		
		documentoDescargado = true;
		return content;

	}

	public void seleccionarCumpleNormativa() {
		if (proyecto.getDiagnosticoCumpleNormativa()) {
			habilitarEnviar = true;
			habilitarGuardar = false;

			visualizarCargarPlan = false;

			RequestContext context = RequestContext.getCurrentInstance();
			context.update("form");
		} else {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgInfoPlanAccion').show();");
		}

		return;
	}
	
	public void enviar() {
		try {
			
			if(proyecto.getDiagnosticoCumpleNormativa()) {
		        RequestContext context = RequestContext.getCurrentInstance();
		        context.execute("PF('dlgInfoCumple').show();");
			} else {
				//validar firma
				//finalizar tarea
				if (token) {
					String idAlfrescoInforme = documentoPlan.getIdAlfresco();

					if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
						JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
						return;
					}
				} else {
					if(documentoSubido) {
						documentoPlan = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 0L, documentoManual, TipoDocumentoSistema.RCOA_PLAN_ACCION_GENERADO);
					} else {
						JsfUtil.addMessageError("Debe adjuntar el documento firmado.");
						return;
					}
				}
				
				String tecnicoResponsable = null;
				if(esTramiteNotificado || esTramiteAnteriorSinPronunciamiento) {
					tecnicoResponsable = buscarTecnico();
					if(tecnicoResponsable == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
						return;
					}
				}

				documentoPlan.setIdProceso(bandejaTareasBean.getProcessId());
				documentoPlan = documentosFacade.guardar(documentoPlan);

				if(esTramiteNotificado || esTramiteAnteriorSinPronunciamiento) {
					Map<String, Object> parametros = new HashMap<>();
					parametros.put("cumpleTiempoSubsanacion", true);
					parametros.put("u_tecnicoResponsable", tecnicoResponsable);
					procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId(), parametros);
					
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
					notificacionDiagnosticoAmbientalFacade.finalizarDiagnostico(tramite);
				} else {
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				}
				
				notificarIngresoDiagnostico(2);
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void finalizarCumpleNormativa() {
		try {
			if(listaDocumentosDiagnostico.size() == 0) {
				JsfUtil.addMessageError("Debe ingresar el documento(s) de Diagnóstico Ambiental");
				return;
			}
			
			String tecnicoResponsable = null;
			if(esTramiteNotificado || esTramiteAnteriorSinPronunciamiento) {
				tecnicoResponsable = buscarTecnico();
				if(tecnicoResponsable == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					return;
				}
			}
			
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			if(planAccion != null && planAccion.getId() != null) {
				planAccion.setEstado(false);
				planAccionFacade.guardar(planAccion);
			}
			
			for (DocumentosCOA documento : listaDocumentosDiagnostico) {
				if(documento.getId() == null)
					documento = documentosFacade
							.guardarDocumentoAlfresco(tramite, "DIAGNOSTICO_AMBIENTAL", 0L, documento,TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL);
				
				if(documento.getIdProceso() == null) {
					documento.setIdProceso(bandejaTareasBean.getProcessId());
					documento = documentosFacade.guardar(documento);
				}
			}
			listaDocumentosDiagnostico = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL,"Diagnostico ambiental");
			
			if(listaDocumentosEliminar.size() > 0) {
				for (DocumentosCOA documento: listaDocumentosEliminar) {
					documento.setEstado(false);
					documentosFacade.guardar(documento);
				}
			}
			
			if(esTramiteNotificado || esTramiteAnteriorSinPronunciamiento) {
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("cumpleTiempoSubsanacion", true);
				parametros.put("u_tecnicoResponsable", tecnicoResponsable);
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				notificacionDiagnosticoAmbientalFacade.finalizarDiagnostico(tramite);
			} else {
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			}
			
			notificarIngresoDiagnostico(1);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public String buscarTecnico() {
		String tecnicoResponsable = null;
		//busqueda de rol
		Area areaResponsable = proyecto.getAreaResponsable();
		String tipoRol = "role.esia.cz.tecnico.responsable";

		if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
			
			Integer idSector = actividadPrincipal.getTipoSector().getId();
			
			tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
		} else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
			tipoRol = "role.esia.gad.tecnico.responsable";
		}

		String rolTecnico = Constantes.getRoleAreaName(tipoRol);
		
		//buscar usuarios por rol y area
		List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
		if (listaUsuario == null || listaUsuario.size() == 0) {
			System.out.println("No se encontró usuario " + rolTecnico + " en "+ areaResponsable.getAreaName());
			return null;
		}

		//recuperar tecnico de bpm y validar si el usuario existe en el listado anterior
		String usrTecnico = (String) variables.get("u_tecnicoResponsable");
		Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
		if (usuarioTecnico != null && usuarioTecnico.getEstado().equals(true)) {
			if(listaUsuario != null && listaUsuario.size() >= 0 && listaUsuario.contains(usuarioTecnico)) {
				tecnicoResponsable = usuarioTecnico.getNombre();
			}
		}
		
		//si no se encontró el usuario se realiza la busqueda de uno nuevo y se actualiza en el bpm
		if(tecnicoResponsable == null) {
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario, Constantes.RCOA_REGISTRO_PRELIMINAR);
			tecnicoResponsable = listaTecnicosResponsables.get(0).getNombre();
		}
		
		return tecnicoResponsable;
	}
	
	public void notificarIngresoDiagnostico(Integer tipo) throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = proyecto.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		List<Contacto> contactos = usuarioOperador.getPersona().getContactos();
		String emailDestino = "";
		for (Contacto contacto : contactos) {
			if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
				emailDestino=contacto.getValor();
				break;
			}
		}
		
		String notificacionOperador = (tipo.equals(1) ? "bodyNotificacionCargarDiagnosticoAmbiental" : "bodyNotificacionCargarPlanAccion") ;
		String notificacionAutoridad = (tipo.equals(1) ? "bodyNotificacionCargarDiagnosticoAmbientalAutoridad" : "bodyNotificacionCargarPlanAccionAutoridad") ;
		
		Object[] parametrosCorreo = new Object[] {nombreOperador, nombreOperador, proyecto.getNombreProyecto(),
				proyecto.getCodigoUnicoAmbiental()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(notificacionOperador);
		String notificacionNuevo = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(emailDestino, mensajeNotificacion.getAsunto(), notificacionNuevo, tramite);
		
		//buscar autoridad competente para envio de notificación por ingreso de diagnostico y/o plan de acción
		Area areaTramite = proyecto.getAreaResponsable();
		String tipoRol = "role.esia.cz.autoridad";
		if(areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
			tipoRol = "role.esia.cz.autoridad";
			areaTramite = areaTramite.getArea();
		}else if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
			tipoRol = "role.esia.pc.autoridad";
		}else if(areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
			tipoRol = "role.esia.ga.autoridad";
		}
		
		String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
		Usuario usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaTramite).get(0);
		
		parametrosCorreo = new Object[] {usuarioAutoridad.getPersona().getNombre(), nombreOperador, proyecto.getNombreProyecto(),
				proyecto.getCodigoUnicoAmbiental()};
		
		mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(notificacionAutoridad);
		String contenidoAutoridad = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		String asunto = String.format(mensajeNotificacion.getAsunto(), new Object[] {proyecto.getCodigoUnicoAmbiental()});
		
		List<Contacto> contactosAutoridad = usuarioAutoridad.getPersona().getContactos();
		String emailAutoridad = "";
		for (Contacto contacto : contactosAutoridad) {
			if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
				emailAutoridad=contacto.getValor();
				break;
			}
		}
		
		Email.sendEmail(emailAutoridad, asunto, contenidoAutoridad, tramite);
	}
	
	public void finalizarNoCumpleNormativa() {
		try {
			proyecto.setDiagnosticoCumpleNormativa(null);
			visualizarCargarPlan = false;
			
			habilitarEnviar = false;
			habilitarGuardar = false;
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("form");
			
			context.execute("PF('dlgInfoCumple').hide();");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public void habilitarIngresoPlanAccion() {
		habilitarEnviar = false;
		habilitarFirmar = false;
		habilitarGuardar = true;
		visualizarCargarPlan = true;
		editarHallazgo = false;

		if(planAccion == null || planAccion.getId() == null) {
			planAccion = new PlanAccion();
			listaHallazgos = new ArrayList<HallazgoPlanAccion>();
		}
		
		generarPlanAccion(true);
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("form:pnlButtons");
		context.update("form:pnlPlanDocumento");
	}
	
	public void abrirNuevoHallazgo() {
		verNuevoHallazgo = true;
		habilitarGuardar = false;
		
		hallazgoPlanAccion = new HallazgoPlanAccion();
		nuevoDocumentoVerificacion = new DocumentosCOA();
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("form:pnlHallazgos");
		context.update("form:pnlButtons");
		
		listaMedios = new ArrayList<MedioVerificacionPlanAccion>();
		listaInstrumentos = new ArrayList<DocumentosCOA>();
	}
	
	public void validateDatosHallazgos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		if (listaMedios == null || listaMedios.size() == 0)
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Medio de verificación' es requerido", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validateDatosPlanAccion(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		if (listaHallazgos == null || listaHallazgos.size() == 0)
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Hallazgos' es requerido", null));
		else {
			for (HallazgoPlanAccion hallazgo : listaHallazgos) {
				if(hallazgo.getListaMedios() == null || hallazgo.getListaMedios().size() == 0 ) {
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Medio de verificación' es requerido para todos los hallazgos", null));
					break;
				}
			}
		}
		
		if (listaDocumentosDiagnostico == null || listaDocumentosDiagnostico.size() == 0)
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Documento(s) de Diagnóstico Ambiental' es requerido", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarNuevoHallazgo() {
		hallazgoPlanAccion.setListaMedios(listaMedios);
		hallazgoPlanAccion.setListaInstrumentos(listaInstrumentos);
		
		listaHallazgos.add(hallazgoPlanAccion);
		
		generarPlanAccion(true);
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("form:tblHallazgos");
		context.update("form:pnlPlanDocumento");
		
		limpiarNuevoHallazgo(false);
		datosGuardados = false;
	}
	
	public void limpiarNuevoHallazgo(Boolean cancelar) {
		
		if(cancelar && editarHallazgo) {
			listaHallazgos.add(hallazgoPlanAccionSeleccionado);
		}
		
		habilitarGuardar = true;
		verNuevoHallazgo = false;
		editarHallazgo = false;
		hallazgoPlanAccion = new HallazgoPlanAccion();
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("form:pnlHallazgos");
		context.update("form:pnlButtons");
		
		listaMedios = new ArrayList<MedioVerificacionPlanAccion>();
		listaInstrumentos = new ArrayList<DocumentosCOA>();
	}

	public void editarHallazgo(HallazgoPlanAccion hallazgo) {
		listaHallazgos.remove(hallazgo);
		
		habilitarGuardar = false;
		
		editarHallazgo = true;
		hallazgoPlanAccion = hallazgo;
		hallazgoPlanAccionSeleccionado = (HallazgoPlanAccion) SerializationUtils.clone(hallazgo);

		listaMedios = hallazgoPlanAccion.getListaMedios();
		listaInstrumentos = hallazgoPlanAccion.getListaInstrumentos();
		
		verNuevoHallazgo = true;
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("form:pnlHallazgos");
		context.update("form:pnlButtons");
	}

	public void eliminarHallazgo(HallazgoPlanAccion hallazgo) {
		try {
			if (hallazgo.getId() != null) {
				hallazgo.setEstado(false);
				listaHallazgosEliminar.add(hallazgo);
			}
			
			listaHallazgos.remove(hallazgo);
			datosGuardados = false;
		} catch (Exception e) {
			e.printStackTrace();
			Log.debug(e.toString());
		}
	}

	public void guardarDatos() {
		try {
			datosGuardados = false;
			
			proyectoLicenciaCoaFacade.guardar(proyecto);
			
			for (DocumentosCOA documento : listaDocumentosDiagnostico) {
				if(documento.getId() == null)
					documento = documentosFacade
							.guardarDocumentoAlfresco(tramite, "DIAGNOSTICO_AMBIENTAL", 0L, documento,TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL);
				
				documento.setIdProceso(bandejaTareasBean.getProcessId());
				documento = documentosFacade.guardar(documento);
			}
			listaDocumentosDiagnostico = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL,"Diagnostico ambiental");
			
			if(listaDocumentosEliminar.size() > 0) {
				for (DocumentosCOA documento: listaDocumentosEliminar) {
					documento.setEstado(false);
					documentosFacade.guardar(documento);
				}
			}
			
			planAccion.setProyectoLicenciaCoa(proyecto);
			
			planAccionFacade.guardar(planAccion);
			
			for (HallazgoPlanAccion hallazgo : listaHallazgos) {
				List<MedioVerificacionPlanAccion> mediosVerificacion = new ArrayList<>();
				List<DocumentosCOA> instrumentos = new ArrayList<>();
				
				mediosVerificacion.addAll(hallazgo.getListaMedios());
				instrumentos.addAll(hallazgo.getListaInstrumentos());
				
				hallazgo.setPlanAccion(planAccion);
				hallazgoPlanAccionFacade.guardar(hallazgo);
				
				for (MedioVerificacionPlanAccion medio: mediosVerificacion) {
					medio.setHallazgoPlanAccion(hallazgo);
					medioVerificacionPlanAccionFacade.guardar(medio);
					
					DocumentosCOA documento = medio.getDocumentoAdjunto();
					if(documento != null) {
						if(documento.getId() == null && documento.getContenidoDocumento() != null ) {
							documento.setIdTabla(medio.getId());
							documento = documentosFacade
									.guardarDocumentoAlfresco(tramite, "DIAGNOSTICO_AMBIENTAL", 0L, documento, TipoDocumentoSistema.RCOA_MEDIO_VERIFICACION_PLAN_ACCION);
						}
						
						documento.setIdProceso(bandejaTareasBean.getProcessId());
						documento = documentosFacade.guardar(documento);
						
						medio.setDocumentoAdjunto(documento);
					}
				}
				
				for (DocumentosCOA documento: instrumentos) {
					if(documento.getId() == null) {
						documento.setIdTabla(hallazgo.getId());
						documento = documentosFacade
								.guardarDocumentoAlfresco(tramite, "DIAGNOSTICO_AMBIENTAL", 0L, documento, TipoDocumentoSistema.RCOA_INSTRUMENTO_AVANCE_PLAN_ACCION);
					}
					documento.setIdProceso(bandejaTareasBean.getProcessId());
					documento = documentosFacade.guardar(documento);
				}
				
				List<MedioVerificacionPlanAccion> listaMedios = medioVerificacionPlanAccionFacade.getPorHallazgo(hallazgo.getId());
				
				for (MedioVerificacionPlanAccion medio : listaMedios) {
					List<DocumentosCOA> listaAdjuntos = documentosFacade.documentoXTablaIdXIdDoc(medio.getId(), 
							TipoDocumentoSistema.RCOA_MEDIO_VERIFICACION_PLAN_ACCION, MedioVerificacionPlanAccion.class.getSimpleName());
					
					if(listaAdjuntos != null && listaAdjuntos.size() > 0)
						medio.setDocumentoAdjunto(listaAdjuntos.get(0));
				}
				
				hallazgo.setListaMedios(listaMedios);
				
				List<DocumentosCOA> listaInstrumentos = documentosFacade.documentoXTablaIdXIdDoc(hallazgo.getId(), 
						TipoDocumentoSistema.RCOA_INSTRUMENTO_AVANCE_PLAN_ACCION,"Instrumentos de avance");
				hallazgo.setListaInstrumentos(listaInstrumentos);
			}
			
			if(listaHallazgosEliminar != null && listaHallazgosEliminar.size() > 0) {
				for (HallazgoPlanAccion item : listaHallazgosEliminar) {
					hallazgoPlanAccionFacade.guardar(item);
				}
				
				listaHallazgosEliminar = new ArrayList<>();
			}
			
			if(listaMediosEliminar != null && listaMediosEliminar.size() > 0) {
				for (MedioVerificacionPlanAccion item : listaMediosEliminar) {
					medioVerificacionPlanAccionFacade.guardar(item);
				}
				
				listaMediosEliminar = new ArrayList<>();
			}
			
			generarPlanAccion(true);
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("form:pnlPlanDocumento");
			context.update("form:pnlButtons");
			
			datosGuardados = true;
			habilitarFirmar = true;
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
		}
	}

	public void generarPlanAccion(Boolean marcaAgua) {
		try {
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_PLAN_ACCION_GENERADO);
			
			cargarDatosPlanAccion();
			
			nombreDocumento = "Plan accion " + proyecto.getCodigoUnicoAmbiental().replace("/","-") + ".pdf";
			
			File oficioPdfAux = UtilGenerarDocumentoSinFormato.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					nombreDocumento, false, entityPlanAccion);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			String reporteHtmlfinal = oficioPdf.getName();
			archivoDocumento = Files.readAllBytes(path);
			filePlan = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(filePlan);
			file.write(Files.readAllBytes(path));
			file.close();
			documentoPath = JsfUtil.devolverContexto("/reportesHtml/"+ reporteHtmlfinal);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void cargarDatosPlanAccion() {
		try {
			DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
			
			List<String> datosOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario());
			
			String parroquia = "";
			String canton = "";
			String provincia = "";
			List<UbicacionesGeografica> proyectoUbicaciones = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyecto);
			for (UbicacionesGeografica row : proyectoUbicaciones) {
				String nombreCanton = row.getUbicacionesGeografica().getNombre();
				String nombreProvincia = row.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				
				parroquia = (parroquia == "") ? row.getNombre() : parroquia + ", " + row.getNombre();
				canton = (canton == "") ? nombreCanton : canton + ", " + nombreCanton;
				provincia = (provincia == "") ? nombreProvincia : provincia + ", " + nombreProvincia;
			}

			String ubicacionCompleta = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 80%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
					+ "<tbody><tr BGCOLOR=\"#B2B2B2\">"	;
				ubicacionCompleta += "<td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				for (UbicacionesGeografica objUbicacion : proyectoUbicaciones) {
					ubicacionCompleta += "<tr><td>" + objUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td><td>"
						+ objUbicacion.getUbicacionesGeografica().getNombre() + "</td><td>"
						+ objUbicacion.getNombre() + "</td></tr>";
				}
				ubicacionCompleta += "</tbody></table></center><br/>";
			
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			
			String autoridadCompetente = proyecto.getAreaResponsable().getAreaName();
			Area areaTramite = proyecto.getAreaResponsable();
			if(areaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				autoridadCompetente = autoridadCompetente +" - " + areaTramite.getArea();
			}

			entityPlanAccion = new EntityPlanAccion();
			
			entityPlanAccion.setCodigo(proyecto.getCodigoUnicoAmbiental());
			entityPlanAccion.setFechaRegistro(proyecto.getFechaGeneracionCua() != null ? formatoFechaEmision.format(proyecto.getFechaGeneracionCua()) : "");
			entityPlanAccion.setOperador(datosOperador.get(0));
			entityPlanAccion.setEnteResponsable(autoridadCompetente);
			entityPlanAccion.setSector(proyectoCiuuPrincipal.getCatalogoCIUU().getTipoSector().getNombre());
			entityPlanAccion.setSuperficie(proyecto.getSuperficie().toString());
			entityPlanAccion.setProvincia(provincia);
			entityPlanAccion.setCanton(canton);
			entityPlanAccion.setParroquia(parroquia);
			entityPlanAccion.setRepresentanteLegal((datosOperador.get(1).equals("") ? datosOperador.get(0) : datosOperador.get(1)));
			entityPlanAccion.setCedula(datosOperador.get(3));
			entityPlanAccion.setUbicacion(ubicacionCompleta);
			
			entityPlanAccion.setTablaPlan(cargarTablaPlan());
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Oficio.");
		}
	}
	
	public String cargarTablaPlan() {
		SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyy");
		
		String headerTabla = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;table-layout: fixed\"> ";
		
		String tablaPlan = headerTabla;
		
		String objetivo = (planAccion.getObjetivo() == null) ? "" : planAccion.getObjetivo();
		String responsable = (planAccion.getResponsable() == null) ? "": planAccion.getResponsable();
		
		tablaPlan += "<tr><td><strong>OBJETIVO: </strong>" + objetivo + "</td></tr>";
		tablaPlan += "<tr><td><strong>RESPONSABLE: </strong>" + responsable + "</td></tr>";
		tablaPlan += "</table>";
		
		
		tablaPlan += headerTabla;
		tablaPlan += "<col style=\"width:5%\"/>"
				+ "<col style=\"width:95%\"/>";
		
		tablaPlan += "<tr>"
				+ "<td style=\"text-align: justify;\" colspan=\"2\"><strong>HALLAZGOS:</strong></td>"
				+ "</tr>";
		
		
		String columnasTablaInterna = "<col style=\"width:25%\"/>"
				+ "<col style=\"width:6%\"/>"
				+ "<col style=\"width:4%\"/>"
				+ "<col style=\"width:6%\"/>"
				+ "<col style=\"width:4%\"/>"
				+ "<col style=\"width:6%\"/>"
				+ "<col style=\"width:4%\"/>"
				+ "<col style=\"width:13%\"/>"
				+ "<col style=\"width:16%\"/>"
				+ "<col style=\"width:16%\"/>";
		
		int i = 1;
		for (HallazgoPlanAccion hallazgo : listaHallazgos) {
			String tablaInterna = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:8px;table-layout: fixed\"> ";
			
			tablaInterna += columnasTablaInterna;
			
			tablaInterna += "<tr>" 
					+ "<td><strong>DESCRIPCIÓN</strong></td>"
					+ "<td style=\"text-align: justify;\" colspan=\"9\">" + hallazgo.getDescripcion() + "</td>"
					+ "</tr>";
			
			tablaInterna += "<tr>"
					+ "<td><strong>CALIFICACIÓN</strong></td>"
					+ "<td style=\"text-align: center; width: 5%\"><strong>NC+</strong></td>"
					+ "<td style=\"text-align: center\">" + (hallazgo.getCalificacion().equals(1) ? "X" : " ") + "</td>"
					+ "<td style=\"text-align: center; width: 5%\"><strong>NC-</strong></td>"
					+ "<td style=\"text-align: center\">" + (hallazgo.getCalificacion().equals(2) ? "X" : " ") + "</td>"
					+ "<td style=\"text-align: center; width: 5%\"><strong>O</strong></td>" 
					+ "<td style=\"text-align: center\">" + (hallazgo.getCalificacion().equals(3) ? "X" : " ") + "</td>"
					+ "<td style=\"text-align: center; width: 10%\"> </td>"
					+ "<td style=\"text-align: center; width: 5%;\"><strong>COSTOS (USD $)</strong></td>"
					+ "<td>" + hallazgo.getCosto() + "</td>" 
					+ "</tr>";
			
			tablaInterna += "<tr>"
					+ "<td><strong>MEDIDAS CORRECTIVAS</strong></td>"
					+ "<td style=\"text-align: justify;\" colspan=\"9\">" + hallazgo.getMedidasCorrectivas() + "</td>"
					+ "</tr>";
			
			tablaInterna += "<tr>"
					+ "<td><strong>CRONOGRAMA</strong></td>"
					+ "<td style=\"text-align: center; width: 5%;\" colspan=\"3\"><strong>INICIO (DD/MM/AAAA)</strong></td>"
					+ "<td style=\"text-align: center; width: 5%;\" colspan=\"3\">" + fechaFormat.format(hallazgo.getFechaInicio()) + "</td>"
					+ "<td style=\"text-align: center; width: 5%;\"> </td>"
					+ "<td style=\"text-align: center; width: 5%;\"><strong>FIN (DD/MM/AAAA)</strong></td>"
					+ "<td style=\"text-align: center; width: 5%;\">" + fechaFormat.format(hallazgo.getFechaFin()) + "</td>" 
					+ "</tr>";
			
			tablaInterna += "<tr>"
					+ "<td><strong>INDICADORES</strong></td>"
					+ "<td style=\"text-align: justify;\" colspan=\"9\">" + hallazgo.getIndicadores() + "</td>"
					+ "</tr>";
			
			tablaInterna += "<tr>"
					+ "<td><strong>MEDIOS DE VERIFICACIÓN</strong></td>"
					+ "<td style=\"text-align: justify;\" colspan=\"9\">" + cargarMediosVerificacion(hallazgo.getListaMedios()) + "</td>"
					+ "</tr>";
			
			tablaInterna += "<tr>"
					+ "<td style=\"word-wrap:break-word;\"><strong>INSTRUMENTOS DE AVANCE O CUMPLIMIENTO DEL PLAN</strong></td>"
					+ "<td style=\"text-align: justify;\" colspan=\"9\">" + hallazgo.getDocsInstrumentos() + "</td>"
					+ "</tr>";
			
			tablaInterna += "</table>";
			
			tablaPlan += "<tr>"
					+ "<td style=\"text-align: center;\">" + i + "</td>" 
					+ "<td style=\"text-align: justify;\">" + tablaInterna + "</td>"
					+ "</tr>";
			
			i++;
		}

		tablaPlan += "</table>";
		
		return tablaPlan;
	}
	
	public String cargarMediosVerificacion(List<MedioVerificacionPlanAccion> listaMedios) {
		String tablaMedios = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 80%; border-collapse:collapse;font-size:8px;table-layout: fixed\"> ";
		
		tablaMedios += "<col style=\"width:75%\"/>"
				+ "<col style=\"width:25%\"/>";
		
		tablaMedios += "<tr>"
				+ "<td><strong>Descripción</strong></td>"
				+ "<td><strong>Documento</strong></td>"
				+ "</tr>";
		
		if(listaMedios != null  && listaMedios.size() > 0) {
			for(MedioVerificacionPlanAccion medio : listaMedios) {
				String nombreDoc = (medio.getDocumentoAdjunto() != null) ? medio.getDocumentoAdjunto().getNombreDocumento() : " ";
				tablaMedios += "<tr>"
					+ "<td>" + medio.getDescripcion() + "</td>" 
					+ "<td>" + nombreDoc + "</td>"
					+ "</tr>";
			}
		}
		tablaMedios += "</table>";
		
		return tablaMedios;
	}

	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (fileContent != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
            		fileContent),  "application/pdf");
            content.setName(name);
        }
        return content;
    }
	
	public void buscarUsuarioForma() throws ServiceException {
		usuarioFirma = "";
        Organizacion organizacion = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());
        if (organizacion != null) {
        	//Cuando el representante legal es otra organizacion
			Organizacion organizacionRep = organizacion;				
			while (organizacionRep.getPersona().getPin().length() == 13) {
				Organizacion orgaAux = organizacionFacade.buscarPorRuc(organizacionRep.getPersona().getPin());
				if (orgaAux != null) {
					organizacionRep = orgaAux;
				} else {
					break;
				}
			}
			usuarioFirma = organizacionRep.getPersona().getPin();			
		} else {
			usuarioFirma = JsfUtil.getLoggedUser().getNombre();				
		}
        
        if(usuarioFirma.length()==13 && usuarioFirma.endsWith("001")) {
        	usuarioFirma = usuarioFirma.substring(0, 10);
        }
	}

	public String firmarPlanToken() {
		try {

			if(datosGuardados && documentoPlan == null) {
				generarPlanAccion(false);
				documentoPlan = new DocumentosCOA();
				documentoPlan.setNombreDocumento(nombreDocumento);
				documentoPlan.setExtencionDocumento(".pdf");		
				documentoPlan.setTipo("application/pdf");
				documentoPlan.setContenidoDocumento(archivoDocumento);
				documentoPlan.setNombreTabla(PlanAccion.class.getSimpleName());
				documentoPlan.setIdTabla(planAccion.getId());
				documentoPlan.setProyectoLicenciaCoa(proyecto);

				documentoPlan = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 0L, documentoPlan, TipoDocumentoSistema.RCOA_PLAN_ACCION_GENERADO);
			}
			
			if(documentoPlan != null) {
				String documentoPendiente = documentosFacade.direccionDescarga(documentoPlan);
				
				habilitarEnviar = true;
				RequestContext context = RequestContext.getCurrentInstance();
				context.update("form:pnlButtons");
				
				return DigitalSign.sign(documentoPendiente, usuarioFirma);
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}

	public void firmarPlan() {
		try {
			documentoDescargado = false;
			Boolean generar = false;
            

			if (documentoPlan == null || documentoPlan.getId() == null) {
				generar = true;
			} else {
				if (documentoPlan.getIdProceso() == null) {
					documentoPlan.setEstado(false);
					documentosFacade.guardar(documentoPlan);

					generar = true;
				}
			}

			if(generar) {
				generarPlanAccion(false);
				documentoPlan = new DocumentosCOA();
				documentoPlan.setNombreDocumento(nombreDocumento);
				documentoPlan.setExtencionDocumento(".pdf");		
				documentoPlan.setTipo("application/pdf");
				documentoPlan.setContenidoDocumento(archivoDocumento);
				documentoPlan.setNombreTabla(PlanAccion.class.getSimpleName());
				documentoPlan.setIdTabla(planAccion.getId());
				documentoPlan.setProyectoLicenciaCoa(proyecto);

				documentoPlan = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 0L, documentoPlan, TipoDocumentoSistema.RCOA_PLAN_ACCION_GENERADO);
			}

			if(documentoPlan != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoPlan);
				urlAlfresco = DigitalSign.sign(documentOffice, usuarioFirma);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
		
		RequestContext.getCurrentInstance().update("formDialogs:");
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('signDialog').show();");

	}
	
	public StreamedContent descargarDocumentofirmar() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documentoPlan != null) {
			if (documentoPlan.getContenidoDocumento() == null) {
				documentoPlan.setContenidoDocumento(documentosFacade
						.descargar(documentoPlan.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					documentoPlan.getContenidoDocumento()), documentoPlan.getExtencionDocumento());
			content.setName(documentoPlan.getNombreDocumento());

			documentoDescargado = true;
		}
		return content;

	}

	public void uploadListenerPlanFirmado(FileUploadEvent event) {
		if(documentoDescargado) {
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual = new DocumentosCOA();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombreDocumento(event.getFile().getFileName());
			documentoManual.setExtencionDocumento(".pdf");		
			documentoManual.setTipo("application/pdf");
			documentoManual.setIdTabla(planAccion.getId());
			documentoManual.setNombreTabla(PlanAccion.class.getSimpleName());
			documentoManual.setProyectoLicenciaCoa(proyecto);
			
			documentoSubido = true;
	        nombreDocumentoFirmado = event.getFile().getFileName();
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
    }
	
	public void abrirNuevoMedio() {
		editarMedio = false;
		
		nuevoMedioVerificacion = new MedioVerificacionPlanAccion();
		nuevoDocumentoVerificacion = new DocumentosCOA();
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("formMedioVerificacion:dlgMedioVerificacion");
		context.execute("PF('dlgMedioVerificacion').show();");
	}
	
	public void agregarNuevoMedio() {
		if(nuevoDocumentoVerificacion != null 
			&& (nuevoDocumentoVerificacion.getContenidoDocumento() != null
			|| nuevoDocumentoVerificacion.getId() != null))
			nuevoMedioVerificacion.setDocumentoAdjunto(nuevoDocumentoVerificacion);

		if(!editarMedio)
			listaMedios.add(nuevoMedioVerificacion);
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("form:tblMediosVerificacion");
		context.execute("PF('dlgMedioVerificacion').hide();");
	}

	public void editarMedioVerificacion(MedioVerificacionPlanAccion medioVerificacion) {
		editarMedio = true;
		
		nuevoMedioVerificacion = medioVerificacion;

		nuevoDocumentoVerificacion = new DocumentosCOA();
		nuevoDocumentoVerificacion = medioVerificacion.getDocumentoAdjunto();
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("formMedioVerificacion:dlgMedioVerificacion");
		context.execute("PF('dlgMedioVerificacion').show();");
	}

	public void eliminarMedioVerificacion(MedioVerificacionPlanAccion medioVerificacion) {
		try {
			if (medioVerificacion.getId() != null) {
				medioVerificacion.setEstado(false);
				listaMediosEliminar.add(medioVerificacion);
			}
			
			listaMedios.remove(medioVerificacion);
		} catch (Exception e) {
			Log.debug(e.toString());
		}
	}
	
	public void verMedioVerificacion(HallazgoPlanAccion hallazgo) {
		try {
			listaMedios = new ArrayList<>();
			
			listaMedios = hallazgo.getListaMedios();
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("formMedioVerificacion:dlgVerMedioVerificacion");
			context.execute("PF('dlgVerMedioVerificacion').show();");
			
		} catch (Exception e) {
			Log.debug(e.toString());
		}
	}

}
