package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PreguntasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.RespuestasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.UtilViabilidadSnap;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CabeceraFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PreguntaFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.RespuestaFormularioSnap;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class IngresarFormularioController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(IngresarFormularioController.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

	@EJB
	private PreguntasFormularioFacade preguntasFormularioFacade;

	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;

	@EJB
	private RespuestasFormularioFacade respuestasFormularioFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade; 
	
	@Getter
	@Setter
	private List<DocumentoViabilidad> listaDocumentosAclaratoria, listaDocumentosEliminar;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoSustento, documentoFormulario;

	@Getter
	@Setter
	private Boolean verGeneral, form1Guardado, form2Guardado, documentoGenerado;
	
	@Getter
	@Setter
	private Boolean  documentoDescargado, isGuardado, esRevision, esRevisionComplementaria, requiereAclaratoria;

	@Getter
	@Setter
	Integer idProyecto;
	
	private String notaSustento, notaAclaratoria;

	@Getter
	@Setter
	private List<CabeceraFormulario> preguntasForm1, preguntasForm2;
	
	private Map<String, Object> variables;


	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString=(String)variables.get("idProyecto");
			
			idProyecto = Integer.valueOf(idProyectoString); 
			verGeneral = true;
			form1Guardado = false;
			form2Guardado = false;			
			isGuardado = false;
			
			esRevision = false;
			esRevisionComplementaria = false;
			if (JsfUtil.getCurrentTask().getTaskName().contains("Revisar") 
					|| JsfUtil.getCurrentTask().getTaskName().contains("Ingresar")) {
				esRevision = true;
				documentoDescargado = false;
				
				Boolean esSnapMae = Boolean.parseBoolean(variables.get("esAdministracionDirecta").toString());
				
				viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, esSnapMae);
				
				if (JsfUtil.getCurrentTask().getTaskName().contains("complementaria")) {
					esRevisionComplementaria = true;
				}
			} else {
				viabilidadProyecto = new ViabilidadCoa();
			}
			
			requiereAclaratoria = variables.containsKey("requiereInfoAclaratoria") ? (Boolean.valueOf((String) variables.get("requiereInfoAclaratoria"))) : false;
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, true);
	
			preguntasForm1 = preguntasFormularioFacade.getListaCabecerasPorOrdenTipo(1, 3);
	
			List<CabeceraFormulario> preguntasOrden2 = preguntasFormularioFacade.getListaCabecerasPorOrdenTipo(2, 3);
			List<CabeceraFormulario> preguntasOrden3 = preguntasFormularioFacade.getListaCabecerasPorOrdenTipo(3, 3);
	
			preguntasForm2 = new ArrayList<>();
			if (preguntasOrden2 != null)
				preguntasForm2.addAll(preguntasOrden2);
	
			if (preguntasOrden3 != null)
				preguntasForm2.addAll(preguntasOrden3);
			
			if(proyectoTipoViabilidadCoa == null) {
				proyectoTipoViabilidadCoa = new ProyectoTipoViabilidadCoa();
				proyectoTipoViabilidadCoa.setIdProyectoLicencia(idProyecto);
				proyectoTipoViabilidadCoa.setEsViabilidadSnap(true);
				viabilidadCoaFacade.guardarProyectoTipoViabilidadCoa(proyectoTipoViabilidadCoa);
			}
	
			if (proyectoTipoViabilidadCoa != null) {
				cargarDatos();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al iniciar la tarea.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalSnap/llenarFormulario.jsf");
	}
	
	public void validarTareaRevisionBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalSnap/revisarInformacion.jsf");
	}
	
	public void cargarDatos() {
		for (CabeceraFormulario cabecera : preguntasForm1) {
			for (PreguntaFormulario preguntaForm : cabecera
					.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade
						.getPorTipoProyectoViabilidadPregunta(proyectoTipoViabilidadCoa.getId(), preguntaForm.getId());
				if (respuesta != null) {
					preguntaForm.setRespuesta(respuesta);
				}
			}
		}
		
		for (CabeceraFormulario cabecera : preguntasForm2) {
			for (PreguntaFormulario preguntaForm : cabecera
					.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade
						.getPorTipoProyectoViabilidadPregunta(proyectoTipoViabilidadCoa.getId(), preguntaForm.getId());
				if (respuesta != null) {
					preguntaForm.setRespuesta(respuesta);
				}
			}
		}
		
		List<DocumentoViabilidad> documentos = documentosFacade.getDocumentoPorTipoViabilidadTramite(
				proyectoTipoViabilidadCoa.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_SUSTENTO.getIdTipoDocumento());
		if (documentos.size() > 0) {
			documentoSustento = documentos.get(0);
		}
		
		listaDocumentosAclaratoria = new ArrayList<>();
		listaDocumentosEliminar = new ArrayList<>();
		
		List<DocumentoViabilidad> documentosComplementaria = documentosFacade.getDocumentoPorTipoViabilidadTramite(
						proyectoTipoViabilidadCoa.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_II_COMPLEMENTARIA.getIdTipoDocumento());
		if (documentosComplementaria.size() > 0) {
			listaDocumentosAclaratoria = documentosComplementaria;
		}
	}
	
	public String getNotaSustento(){
		if(notaSustento==null){
			try {						
				notaSustento = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("mensajeViabilidadSustentoSnap");			
			} catch (Exception e) {
				LOG.error("No se recupero mensaje sustento SNAP. "+e.getCause()+" "+e.getMessage());
			}
		}
		return notaSustento;
	}
	
	public String getNotaAclaratoria(){
		if(notaAclaratoria==null){
			try {						
				notaAclaratoria = mensajeNotificacionFacade.recuperarValorMensajeNotificacionSD("mensajeViabilidadAclaratoriaSnap");			
			} catch (Exception e) {
				LOG.error("No se recupero mensaje aclaratoria SNAP. "+e.getCause()+" "+e.getMessage());
			}
		}
		return notaAclaratoria;
	}

	public void guardarCampo(PreguntaFormulario pregunta) {
		try {
			guardarRespuesta(pregunta);
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
		}
	}
	
	public void guardarRespuesta(PreguntaFormulario pregunta) {
		if (pregunta.getRespuesta() != null) {
			RespuestaFormularioSnap respuesta = pregunta.getRespuesta();
			respuesta.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
			respuesta.setIdPregunta(pregunta.getId());

			respuestasFormularioFacade.guardar(respuesta);

			pregunta.setRespuesta(respuesta);
		}
	}

	public void guardarGeneral() {
		try {
			if (guardarInfoFormulario()) {
				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al guardar los datos.");
		}
	}
	
	public Boolean guardarInfoFormulario() throws Exception {
		// si esta activo el panel con las primeras preguntas
		if (verGeneral) {
			for (CabeceraFormulario cabecera : preguntasForm1) {
				for (PreguntaFormulario preguntaForm : cabecera
						.getListaPreguntas()) {
					guardarRespuesta(preguntaForm);
				}
			}
			form1Guardado = true;
			return true;
		} else{
			for (CabeceraFormulario cabecera : preguntasForm2) {
				for (PreguntaFormulario preguntaForm : cabecera
						.getListaPreguntas()) {
					guardarRespuesta(preguntaForm);
				}
			}
			
			form2Guardado = true;
			
			if (documentoSustento != null && documentoSustento.getContenidoDocumento() != null) {
				documentoSustento.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
				documentoSustento = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", documentoSustento, 1, JsfUtil.getCurrentProcessInstanceId());
				
				if(documentoSustento == null || documentoSustento.getId() == null) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return false;
				}
			}
			
			if(requiereAclaratoria && listaDocumentosAclaratoria.size() > 0) {
				for (DocumentoViabilidad documento : listaDocumentosAclaratoria) {
					if (documento.getContenidoDocumento() != null) {
						documento.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
						documento = documentosFacade.guardarDocumentoProceso(
								proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
								"VIABILIDAD_AMBIENTAL", documento, 1, JsfUtil.getCurrentProcessInstanceId());
						
						if(documento == null || documento.getId() == null) {
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
							return false;
						}
					}
				}
				
				for (DocumentoViabilidad documento : listaDocumentosEliminar) {
					documento.setEstado(false);
					documentosFacade.guardar(documento);
				}
			}
			
			return true;
		}
	}
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if(requiereAclaratoria) {
			if (listaDocumentosAclaratoria == null || listaDocumentosAclaratoria.size() == 0)
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe adjuntar al menos un documento de información aclaratoria.", null));
		}
		
		if (!errorMessages.isEmpty()) {
			form2Guardado = false;
			throw new ValidatorException(errorMessages);
		}
	}
	
	public void siguiente(Boolean resultado) {
		verGeneral = resultado;
		
		if(!esRevision) {
			if (!resultado) 
				form1Guardado = false;
			else
				form2Guardado = false;
		}
	}
	
	public void uploadFile(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoSustento = new DocumentoViabilidad();
		documentoSustento.setId(null);
		documentoSustento.setContenidoDocumento(contenidoDocumento);
		documentoSustento.setNombre(event.getFile().getFileName());
		documentoSustento.setMime("application/pdf");
		documentoSustento.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_SUSTENTO.getIdTipoDocumento());
	}
	
	public StreamedContent descargar(DocumentoViabilidad documentoDescarga) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDescarga != null && documentoDescarga.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoDescarga.getIdAlfresco(), documentoDescarga.getFechaCreacion());
			} else if (documentoDescarga.getContenidoDocumento() != null) {
				documentoContent = documentoDescarga.getContenidoDocumento();
			}
			
			if (documentoDescarga != null && documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			if(esRevision && documentoDescarga.getIdTipoDocumento().equals(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_SUSTENTO.getIdTipoDocumento()))
				documentoDescargado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void enviar() {
		try {
			
			if (!guardarInfoFormulario()) {
				return;
			}

			Map<String, Object> parametros = new HashMap<>();			
			Boolean esAdministracionDirecta = false;
			AreasSnapProvincia areaSnap = null;
			Usuario tecnicoResponsable = null;
			
			if(!requiereAclaratoria) {
			
				Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
				
				List<DetalleInterseccionProyectoAmbiental> interseccionesProyecto = interseccionViabilidadCoaFacade.getDetalleInterseccionSnap(idProyecto);
				if(interseccionesProyecto != null) {
					DetalleInterseccionProyectoAmbiental detalle = interseccionesProyecto.get(0);
					
					List<AreasSnapProvincia> ubicacionesCapa = interseccionViabilidadCoaFacade.getAreaSnapPorCodigoZona(detalle.getCodigoUnicoCapa(), detalle.getZona());
					
					if(ubicacionesCapa != null) {
						areaSnap = UtilViabilidadSnap.getInfoAreaSnap(detalle.getCodigoUnicoCapa(), detalle.getZona());
						tecnicoResponsable = (areaSnap != null) ? areaSnap.getTecnicoResponsable() : null;
						
						if(areaSnap == null || tecnicoResponsable == null || !tecnicoResponsable.getEstado()) {
							LOG.error("No se encontro usuario responsable para el área " + areaSnap.getNombreAreaProtegida() + ", verifique que el usuario esté asignado y tenga el rol requerido");
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
							JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
							return;
						}
						
						if(areaSnap.getTipoSubsitema().equals(1)) {
							esAdministracionDirecta = true;
							parametros.put("responsableAreaProtegida", tecnicoResponsable.getNombre());
						} else {
							parametros.put("responsableAreasPC", tecnicoResponsable.getNombre());
						}
						
						
						ViabilidadCoa viabilidadCoa = viabilidadCoaFacade.getViabilidadPorTipoProyecto(proyectoTipoViabilidadCoa.getId());
						if(viabilidadCoa == null) {
							viabilidadCoa = new ViabilidadCoa();
							viabilidadCoa.setIdProyectoLicencia(idProyecto);
							viabilidadCoa.setIdProyectoTipoViabilidad(proyectoTipoViabilidadCoa.getId());
							viabilidadCoa.setEsViabilidadSnap(true);
							viabilidadCoa.setEsAdministracionMae(esAdministracionDirecta);
							viabilidadCoa.setAreaResponsable(areaTramite);
							viabilidadCoa.setViabilidadCompletada(false);
							viabilidadCoa.setAreaSnap(areaSnap);
							viabilidadCoa.setTipoFlujoViabilidad(ViabilidadCoa.flujosIndependientes);
	
							viabilidadCoaFacade.guardar(viabilidadCoa);
						} else {
							viabilidadCoa.setAreaResponsable(areaTramite);
							viabilidadCoa.setAreaSnap(areaSnap);
							viabilidadCoa.setEsAdministracionMae(esAdministracionDirecta);
							viabilidadCoa.setTipoFlujoViabilidad(ViabilidadCoa.flujosIndependientes);
							
							viabilidadCoaFacade.guardar(viabilidadCoa);
						}
						
					} else {
						LOG.error("No se encontro área protegia en tabla province_areas_snap para la capa " + detalle.getIdGeometria() + " - " + detalle.getNombreGeometria() + " zona " + detalle.getZona());
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
						JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
						return;
					}
				} else {
					LOG.error("No se encontro intersecciones para el proyecto " + proyectoLicenciaCoa.getCodigoUnicoAmbiental());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}			
				
				//Generacion de documento de informacion ingresada por el operador, si no se genera mensaje error
				if(!generarDocumentoIngreso()) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
				
				parametros.put("esAdministracionDirecta", esAdministracionDirecta);
				parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
			} else {
				
				//Generacion de documento de informacion ingresada por el operador, si no se genera mensaje error
				if(!generarDocumentoIngreso()) {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
					return;
				}
				
				String tipoUsuario = (viabilidadProyecto.getEsAdministracionMae()) ? "responsableAreaProtegida" : "responsableAreasPC";
				String jefePrincipalBpm = variables.get(tipoUsuario).toString();
				
				areaSnap = UtilViabilidadSnap.getInfoAreaSnap(viabilidadProyecto.getAreaSnap().getCodigoSnap(), viabilidadProyecto.getAreaSnap().getZona());
				tecnicoResponsable = (areaSnap != null) ? areaSnap.getTecnicoResponsable() : null;
				
				if(areaSnap == null || tecnicoResponsable == null || !tecnicoResponsable.getEstado()) {
					System.out.println("No se encontro usuario responsable para el área " + areaSnap.getNombreAreaProtegida());
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
					return;
				}
				
				if(!jefePrincipalBpm.equals(tecnicoResponsable.getNombre())) {
					parametros.put(tipoUsuario, tecnicoResponsable.getNombre());
				}
				
				parametros.put("ingresaAclaratoria", true);
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			enviarNotificacionIngreso(areaSnap, tecnicoResponsable);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void enviarNotificacionIngreso (AreasSnapProvincia areaSnap, Usuario tecnicoResponsable) throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		String tipoRol = (areaSnap.getTipoSubsitema().equals(1)) ? "role.va.cz.tecnico.snap.mae" : "role.va.pc.tecnico.snap.delegada";
		String rol = Constantes.getRoleAreaName(tipoRol);
		Object[] parametrosCorreo = new Object[] {};
		if(requiereAclaratoria) {
			parametrosCorreo = new Object[] {tecnicoResponsable.getPersona().getNombre(), 
					proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), nombreOperador };
		} else {
			parametrosCorreo = new Object[] {tecnicoResponsable.getPersona().getNombre(), rol, areaSnap.getNombreAreaProtegida(),
					nombreOperador, proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), areaSnap.getNombreAreaProtegida() };
		}
		
		String tipoNotificacion = (requiereAclaratoria) ? "bodyNotificacionViabilidadSnapAclaratoriaTecnico" : "bodyNotificacionViabilidadSnapIngresoTecnico";
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(tipoNotificacion);
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(tecnicoResponsable, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}

	public Boolean generarDocumentoIngreso() {
		try {
			
			PlantillaReporte plantillaDocumento = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR);
			
			cargarDatosDocumento();
	
			String nombreFichero = "FormularioViabilidadOperador" + proyectoTipoViabilidadCoa.getId() + ".pdf";
			String nombreReporte = "FormularioViabilidadOperador" + ".pdf";
	
			File oficioPdfAux = UtilGenerarInforme.generarFichero(
					plantillaDocumento.getHtmlPlantilla(),
					nombreReporte, true, viabilidadProyecto);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, " ", BaseColor.GRAY);
	
			Path path = Paths.get(oficioPdf.getAbsolutePath());
			byte[] archivoOficio = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(nombreFichero));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(archivoOficio);
			file.close();
			
			DocumentoViabilidad documentoInforme = new DocumentoViabilidad();
			documentoInforme.setNombre("FormularioViabilidadOperador.pdf");
			documentoInforme.setContenidoDocumento(archivoOficio);
			documentoInforme.setMime("application/pdf");
			documentoInforme.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR.getIdTipoDocumento());
			documentoInforme.setIdProyectoViabilidad(proyectoTipoViabilidadCoa.getId());
	
			documentoInforme = documentosFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", documentoInforme, 1, JsfUtil.getCurrentProcessInstanceId());
			
			if(documentoInforme != null && documentoInforme.getId() != null)
				return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error al generar el formulario del operador SNAP", e);
		}
		
		return false;
	}
	
	public void cargarDatosDocumento() {
		List<CabeceraFormulario> preguntasFormOperador = new ArrayList<>();
		preguntasFormOperador.addAll(preguntasForm1);
		preguntasFormOperador.addAll(preguntasForm2);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		if (!preguntasFormOperador.isEmpty()){
			for (CabeceraFormulario cabecera : preguntasFormOperador) {
				stringBuilder.append("<p style=\"text-align: justify;\"><strong>" + cabecera.getDescripcion() + "</strong></p>");
				
				for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
					RespuestaFormularioSnap respuesta = respuestasFormularioFacade
							.getPorTipoProyectoViabilidadPregunta(proyectoTipoViabilidadCoa.getId(), preguntaForm.getId());
					if (respuesta != null) {
						stringBuilder.append("<p style=\"margin-left: 30px; text-align: justify;\">");
						stringBuilder.append("<em><strong>" + preguntaForm.getDescripcion() + "</strong></em><br/>");
						if(preguntaForm.getTipo().equals(2)) {
							stringBuilder.append(respuesta.getRespText());
						}
						stringBuilder.append("</p>");
					}
				}
			}
		}
		
		viabilidadProyecto.setRespuestasOperadorHtml(stringBuilder.toString());
	}
	
	//para aclaratoria
	public void uploadAclaratoria(FileUploadEvent event) {
		DocumentoViabilidad documentoLocal = new DocumentoViabilidad();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoLocal.setContenidoDocumento(contenidoDocumento);
		documentoLocal.setNombre(event.getFile().getFileName());
		documentoLocal.setExtension(".pdf");		
		documentoLocal.setMime("application/pdf");
		documentoLocal.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_II_COMPLEMENTARIA.getIdTipoDocumento());
		
		listaDocumentosAclaratoria.add(documentoLocal);
	
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void eliminarDocumento(DocumentoViabilidad documento) {
		try {
			if (documento.getId() != null) {
				documento.setEstado(false);
				listaDocumentosEliminar.add(documento);
			}
			
			listaDocumentosAclaratoria.remove(documento);
		} catch (Exception e) {
			Log.debug(e.toString());
		}
	}
	
	
	//*****************REVISION DE TECNICO
	public StreamedContent descargarFormulario() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			DocumentoViabilidad documento = null;
			
			List<DocumentoViabilidad> documentosFormulario = documentosFacade
					.getDocumentoPorTipoViabilidadTramite(
							proyectoTipoViabilidadCoa.getId(),
							TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR
									.getIdTipoDocumento());
			if (documentosFormulario.size() > 0) {
				documento = documentosFormulario.get(0);
			}
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco(), documento.getFechaCreacion());
			} 
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
			if(esRevision)
				documentoDescargado = true;

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void actualizarAclaratoria() {
		if(!viabilidadProyecto.getRequiereAclaratoria()){
			viabilidadProyecto.setDetalleAclaratoria(null);
		}
	}
	
	public void guardarRevision() {
		try {
			viabilidadCoaFacade.guardar(viabilidadProyecto);
			
			isGuardado = true;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void finalizarRevision() {
		try {
			if(!esRevisionComplementaria) {
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("requiereInfoAclaratoria", viabilidadProyecto.getRequiereAclaratoria());
							
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			}
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(!esRevisionComplementaria && viabilidadProyecto.getRequiereAclaratoria()) {
				notificarInformacionAclaratoria();
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public void notificarInformacionAclaratoria() throws Exception {
		String nombreOperador = "";
		Usuario usuarioOperador = proyectoLicenciaCoa.getUsuario();
		
		if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
			nombreOperador = usuarioOperador.getPersona().getNombre();
		} else {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			nombreOperador = organizacion.getNombre();
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		Object[] parametrosCorreo = new Object[] {nombreOperador, proyectoLicenciaCoa.getNombreProyecto(),
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(), viabilidadProyecto.getAreaSnap().getNombreAreaProtegida()};
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionViabilidadSnapRequiereAclaratoria");
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Email.sendEmail(usuarioOperador, mensajeNotificacion.getAsunto(), notificacion, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), JsfUtil.getLoggedUser());
	}
	
}
