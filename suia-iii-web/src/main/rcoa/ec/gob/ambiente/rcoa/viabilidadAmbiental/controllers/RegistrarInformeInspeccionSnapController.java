package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

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
import java.util.Date;
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

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformacionViabilidadLegalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PreguntasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PronunciamientoBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.RespuestasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CabeceraFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformacionViabilidadLegal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PreguntaFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PronunciamientoBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.RespuestaFormularioSnap;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class RegistrarInformeInspeccionSnapController implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
	private InformeInspeccionBiodiversidadFacade informeInspeccionFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private RespuestasFormularioFacade respuestasFormularioFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private	InformacionViabilidadLegalFacade revisionTecnicoJuridicoFacade;
	
	@EJB
	private	DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	
	@EJB
	private PronunciamientoBiodiversidadFacade pronunciamientoBiodiversidadFacade;

	@Getter
	@Setter
	private List<CabeceraFormulario> preguntasZonificacion, preguntasInspeccion, listaZonasAreas;
	
	@Getter
	@Setter
	private InformeInspeccionBiodiversidad informeInspeccion;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	
	@Getter
	@Setter
	private DocumentoViabilidad informeFirmaManual, documentoInformeAlfresco;
	
	@Getter
	@Setter
	private InformacionViabilidadLegal informacionViabilidadLegal;
	
	@Getter
	@Setter
	private PronunciamientoBiodiversidad oficioPronunciamiento;

	@Getter
	@Setter
	private Boolean zonaProteccion, zonaRecuperacion, zonaUsoPublico,
			zonaUsoSostenible, zonaManejo;

	@Getter
	@Setter
	private String tipoPronunciamiento;

	@Getter
	@Setter
	private Integer totalCalificacion, idProyecto, idViabilidad, idTarea, idInformeAnterior;

	@Getter
	@Setter
	private String urlInforme, nombreInforme, nombreDocumentoFirmado;
	
	@Getter
	@Setter
	private Boolean informeGuardado, informeFirmado, mostrarFirma, token, documentoDescargado, subido, habilitarEnviar, soloToken;

	@Getter
	@Setter
	private byte[] archivoInforme;
	
	private Map<String, Object> variables;

	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
	
			String idProyectoString=(String)variables.get("idProyecto");
			
			idProyecto = Integer.valueOf(idProyectoString);
			
			soloToken = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
			
			totalCalificacion = 0;
			informeGuardado = false;
			informeFirmado = false;
			mostrarFirma = false;
			subido = false;
			verificaToken();
			
			plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			String nombre = viabilidadCoaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			Boolean esSnapMae = false;
			if (nombre.contains("SnapMae")) {
				esSnapMae = true;
			}
			
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, esSnapMae);
			idViabilidad = viabilidadProyecto.getId();
			
			idTarea = (int)bandejaTareasBean.getTarea().getTaskId();
			
			informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			oficioPronunciamiento = pronunciamientoBiodiversidadFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(informeInspeccion != null && informeInspeccion.getIdTarea() == null) {
				informeInspeccion.setIdTarea(idTarea);
				informeInspeccionFacade.guardar(informeInspeccion, viabilidadProyecto.getAreaSnap().getAbreviacion());
			}
			
			if(informeInspeccion != null && !informeInspeccion.getIdTarea().equals(idTarea)) {
				idInformeAnterior = informeInspeccion.getId();
				
				InformeInspeccionBiodiversidad informeNuevo = (InformeInspeccionBiodiversidad) SerializationUtils.clone(informeInspeccion);
				informeNuevo.setId(null);
				informeNuevo.setNumeroInforme(null);
				informeNuevo.setIdTarea(idTarea);
				informeInspeccion = informeInspeccionFacade.guardar(informeNuevo, viabilidadProyecto.getAreaSnap().getAbreviacion());
				
				oficioPronunciamiento.setRecomendaciones(null);
				pronunciamientoBiodiversidadFacade.guardar(oficioPronunciamiento);
			}
			
			informacionViabilidadLegal= revisionTecnicoJuridicoFacade.getInformacionViabilidadLegalPorId(viabilidadProyecto.getId());
			
			cargarPreguntas();
			
			cargarDatos();
			
			generarInforme(true);
			
			urlInforme = informeInspeccion.getInformePath();
			nombreInforme = informeInspeccion.getNombreReporte();
			archivoInforme = informeInspeccion.getArchivoInforme();
	
			actualizarTotalViabilidad();
			
			habilitarEnviar = false;
			if(token)
				habilitarEnviar = true;
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeInspeccionBiodiversidad.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/snap/ingresarInformeInspeccionSnap.jsf");
	}
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean verificaToken() {
		token = false;
		habilitarEnviar = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken()) {
			token = true;
			habilitarEnviar = true;
		}
		
		if(soloToken) {
			token = true;
			habilitarEnviar = true;
		}
			
		
		return token;
	}
	
	public void cargarPreguntas() {
		List<CabeceraFormulario> preguntasInspeccionSnap = preguntasFormularioFacade.getListaCabecerasPorTipo(2);
		preguntasZonificacion = new ArrayList<>();
		preguntasInspeccion = new ArrayList<>();
		listaZonasAreas = new ArrayList<>();
		
		for (CabeceraFormulario cabecera : preguntasInspeccionSnap) {
			if (cabecera.getOrden().equals(1))
				preguntasZonificacion.add(cabecera);
			else if(cabecera.getOrden().equals(2))
				listaZonasAreas.add(cabecera);
			else
				preguntasInspeccion.add(cabecera);
		}
	}
	
	public void cargarDatos() {
		Integer idInforme = (informeInspeccion != null) ? informeInspeccion.getId() : 0;
		if(idInformeAnterior != null)
			idInforme = idInformeAnterior;
		
		for (CabeceraFormulario cabecera : listaZonasAreas) {
			for (PreguntaFormulario preguntaForm : cabecera
					.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade
						.getPorViabilidadPregunta(viabilidadProyecto.getId(), preguntaForm.getId(), idInforme);
				if (respuesta != null) {
					
					if(idInformeAnterior != null) {
						RespuestaFormularioSnap respuestaNueva = (RespuestaFormularioSnap) SerializationUtils.clone(respuesta);
						respuestaNueva.setId(null);
						respuestaNueva.setIdInforme(informeInspeccion.getId());
						respuesta = respuestasFormularioFacade.guardar(respuestaNueva);
					}
					
					preguntaForm.setRespuesta(respuesta);
				}
			}
		}
		
		for (CabeceraFormulario cabecera : preguntasZonificacion) {
			for (PreguntaFormulario preguntaForm : cabecera
					.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade
						.getPorViabilidadPregunta(viabilidadProyecto.getId(), preguntaForm.getId(), idInforme);
				if (respuesta != null) {
					if(idInformeAnterior != null) {
						RespuestaFormularioSnap respuestaNueva = (RespuestaFormularioSnap) SerializationUtils.clone(respuesta);
						respuestaNueva.setId(null);
						respuestaNueva.setIdInforme(informeInspeccion.getId());
						respuesta = respuestasFormularioFacade.guardar(respuestaNueva);
					}
					
					preguntaForm.setRespuesta(respuesta);
				}
			}
		}
		
		for (CabeceraFormulario cabecera : preguntasInspeccion) {
			for (PreguntaFormulario preguntaForm : cabecera
					.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade
						.getPorViabilidadPregunta(viabilidadProyecto.getId(), preguntaForm.getId(), idInforme);
				if (respuesta != null) {
					if(idInformeAnterior != null) {
						RespuestaFormularioSnap respuestaNueva = (RespuestaFormularioSnap) SerializationUtils.clone(respuesta);
						respuestaNueva.setId(null);
						respuestaNueva.setIdInforme(informeInspeccion.getId());
						respuesta = respuestasFormularioFacade.guardar(respuestaNueva);
					}
					
					preguntaForm.setRespuesta(respuesta);
				}
			}
		}
		
		zonaProteccion = false;
		zonaRecuperacion = false;
		zonaManejo = false;
		zonaUsoPublico = false;
		zonaUsoSostenible = false;
		List<String> nombresZonas = detalleInterseccionProyectoAmbientalFacade.nombresZonificacionSnap(proyectoLicenciaCoa);
		for (String zona : nombresZonas) {
			if (zona.contains("PROTECCION"))
				zonaProteccion = true;
			else if (zona.contains("RECUPERACION"))
				zonaRecuperacion = true;
			else if (zona.contains("COMUNITARIO"))
				zonaManejo = true;
			else if (zona.contains("TURISMO"))
				zonaUsoPublico = true;
			else if (zona.contains("SOSTENIBLE"))
				zonaUsoSostenible = true;

		}
		
	}

	public void actualizarTotalViabilidad() {
		// realizar el calculo
		totalCalificacion = 0;
		
		sumarRespuestas(preguntasZonificacion);
		sumarRespuestas(preguntasInspeccion);

		if (totalCalificacion >= 11)
			tipoPronunciamiento = "Pronunciamiento FAVORABLE";
		else
			tipoPronunciamiento = "Pronunciamiento NO FAVORABLE";
	}
	
	public void sumarRespuestas (List<CabeceraFormulario> preguntas) {
		for (CabeceraFormulario cabecera : preguntas) {
			for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = preguntaForm.getRespuesta();
				if ((preguntaForm.getTipo().equals(1) || preguntaForm.getTipo().equals(3))
						&& respuesta != null
						&& respuesta.getRespBoolean() != null) {
					if(respuesta.getRespBoolean()) {
						totalCalificacion += preguntaForm.getValorTrue();
						respuesta.setRespInteger(preguntaForm.getValorTrue());
					} else {
						totalCalificacion += preguntaForm.getValorFalse();
						respuesta.setRespInteger(preguntaForm.getValorFalse());
					}
				}
			}
		}
	}
	
	public void actualizarCamposRelacionados(CabeceraFormulario cabecera, PreguntaFormulario pregunta) {
		Integer indice = 0;
		for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
			if(!preguntaForm.getId().equals(pregunta.getId())){
				preguntaForm.getRespuesta().setRespBoolean(null);
				
				RequestContext.getCurrentInstance().update("form:dtgCabecera:6:pnlCabeceras:"+indice+":pnlContentPregunta");
			}
			indice++;
		}
		
		actualizarTotalViabilidad();
	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivoInforme != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					archivoInforme), "application/octet-stream");
			content.setName(informeInspeccion.getNombreReporte());
		}
		return content;
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {

			informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(informeInspeccion == null) {
				informeInspeccion = new InformeInspeccionBiodiversidad();
				informeInspeccion.setNombreFichero("InformeInspeccion.pdf");
			} else {
				informeInspeccion.setNombreFichero("InformeInspeccion_" + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
			}
			informeInspeccion.setNombreReporte(informeInspeccion.getNombreFichero());
			
			InformeInspeccionEntity informeEntity = cargarDatosDocumento();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteInforme.getHtmlPlantilla(),
					informeInspeccion.getNombreReporte(), true, informeEntity);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeInspeccion.setArchivoInforme(Files.readAllBytes(path));
			String reporteHtmlfinal = informeInspeccion.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeInspeccion.getArchivoInforme());
			file.close();
			informeInspeccion.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeInspeccion.getNombreFichero()));

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public InformeInspeccionEntity cargarDatosDocumento() {
		InformeInspeccionEntity informeEntity = new InformeInspeccionEntity();
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		String razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
		String cargoTecnico = (viabilidadProyecto.getEsAdministracionMae()) ? "ADMINISTRADOR DEL ÁREA PROTEGIDA" : "ADMINISTRADOR DEL ÁREA PROTEGIDA";
		
		informeEntity.setNroInforme(informeInspeccion.getNumeroInforme());
		informeEntity.setConclusiones(informeInspeccion.getConclusiones());
		informeEntity.setRecomendaciones(informeInspeccion.getRecomendaciones());
		informeEntity.setFechaElaboracion(dateFormat.format(new Date()));
		if(informeInspeccion.getFechaInspeccion() == null)
			informeEntity.setFechaInspeccion("");
		else
			informeEntity.setFechaInspeccion(dateFormat.format(informeInspeccion.getFechaInspeccion()));
		informeEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		informeEntity.setRazonSocial(razonSocial);
		informeEntity.setNombreAreaProtegida(interseccionViabilidadCoaFacade.getNombresAreasProtegidasSnap(idProyecto, 2));
		informeEntity.setNombreTecnico(JsfUtil.getLoggedUser().getPersona().getNombre());
		informeEntity.setCargoTecnico(cargoTecnico);
		
		informeEntity.setEsZonaProteccion((zonaProteccion) ? "X" : "");
		informeEntity.setEsZonaRecuperacion((zonaRecuperacion) ? "X" : "");
		informeEntity.setEsZonaUsoPublico((zonaUsoPublico) ? "X" : "");
		informeEntity.setEsZonaUsoSostenible((zonaUsoSostenible) ? "X" : "");
		informeEntity.setEsZonaManejo((zonaManejo) ? "X" : "");
		
		List<CabeceraFormulario> preguntasForm = new ArrayList<>();
		preguntasForm.addAll(preguntasZonificacion);
		preguntasForm.addAll(preguntasInspeccion);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		StringBuilder stringBuilderZonas = new StringBuilder();
		
		if (!preguntasForm.isEmpty()){
			for (CabeceraFormulario cabecera : listaZonasAreas) {
				stringBuilderZonas.append("<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 50%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">");
				stringBuilderZonas.append("<tbody>");
				stringBuilderZonas.append("<tr>");
				stringBuilderZonas.append("<td bgcolor=\"#C5C5C5\" style=\"text-align: justify;\" width=\"60%\"> <strong>"+cabecera.getDescripcion()+"</strong></td>");
				stringBuilderZonas.append("<td bgcolor=\"#C5C5C5\" style=\"text-align: center;\"> <strong>"+cabecera.getSubDescripcion()+"</strong></td>");
				stringBuilderZonas.append("</tr>");
				
				for (PreguntaFormulario pregunta : cabecera.getListaPreguntas()) {
					String strRespuesta = "";
					RespuestaFormularioSnap respuesta = respuestasFormularioFacade.getPorViabilidadPregunta(viabilidadProyecto.getId(),pregunta.getId(), informeInspeccion.getId());
					if(respuesta != null && respuesta.getRespBoolean()){
						strRespuesta = "X";
					}
					stringBuilderZonas.append("<tr>");
					stringBuilderZonas.append("<td style=\"text-align: justify;\">"+pregunta.getDescripcion()+"</td>");
					stringBuilderZonas.append("<td style=\"text-align: center;\"> <strong>"+strRespuesta+"</strong></td>");
					stringBuilderZonas.append("</tr>");
				}
			
				stringBuilderZonas.append("</tbody>");
				stringBuilderZonas.append("</table>");
				stringBuilderZonas.append("<p style=\"text-align: center;\"><span style=\"font-size:9px;\"><sup>+</sup> Aplica &uacute;nicamente para las &aacute;reas protegidas marino costeras</span></p>");
			}
			
			for (CabeceraFormulario cabecera : preguntasForm) {
				Boolean sinDetallePregunta = false;
				if(cabecera.getSubDescripcion() != null) {
					stringBuilder.append("<p style=\"text-align: justify;\"><strong>" + cabecera.getDescripcion() + "</strong><br />"+cabecera.getSubDescripcion()+"</p>");
				} else {
					for (PreguntaFormulario pregunta : cabecera.getListaPreguntas()) {
						if(pregunta.getTipo().equals(1) || pregunta.getTipo().equals(3)) {
							sinDetallePregunta = true;
							if(cabecera.getDescripcion().indexOf("a)") <0)
								stringBuilder.append("<p style=\"text-align: justify;\"><strong>" + cabecera.getDescripcion() + "</strong><br />"+pregunta.getDescripcion()+"</p>");
							else
								stringBuilder.append("<p style=\"text-align: justify;\">"+pregunta.getDescripcion()+"</p>");
							
							break;
						}
					}
				}
				
				if(cabecera.getOrden().equals(1)) {
					stringBuilder.append(stringBuilderZonas.toString());
					for (PreguntaFormulario pregunta : cabecera.getListaPreguntas()) {
						stringBuilder.append("<p style=\"text-align: justify;\">"+pregunta.getDescripcion()+"</p>");
						break;
					}
				}
				
				String estiloTabla = "style=\"width: 70%; border-collapse:collapse;font-size:12px;\"";
				if(sinDetallePregunta || cabecera.getOrden().equals(1))
					estiloTabla = "style=\"width: 35%; border-collapse:collapse;font-size:12px;\"";
					
				stringBuilder
						.append("<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" " + estiloTabla + ">");
				for (PreguntaFormulario pregunta : cabecera.getListaPreguntas()) {
					
					RespuestaFormularioSnap respuesta = respuestasFormularioFacade.getPorViabilidadPregunta(viabilidadProyecto.getId(), pregunta.getId(), informeInspeccion.getId());
					
					if(respuesta != null) {
						String valorSi = (respuesta.getRespBoolean() != null && respuesta.getRespBoolean()) ? "X" : "";
						String valorNo = (respuesta.getRespBoolean() != null && !respuesta.getRespBoolean()) ? "X" : "";
						
						if(pregunta.getTipo().equals(1) && cabecera.getSubDescripcion() != null && !cabecera.getOrden().equals(1)) {
							stringBuilder.append("<tr>");
							stringBuilder.append("<td rospan=\"2\" rowspan=\"2\">");
							stringBuilder.append(pregunta.getDescripcion());
							stringBuilder.append("</td>");
							stringBuilder.append("<td style=\"text-align: center; width: 20%\">SI</td>");
							stringBuilder.append("<td style=\"text-align: center; width: 15%\"><strong>"+ valorSi +"</strong></td>");
							stringBuilder.append("</tr>");
							stringBuilder.append("<tr>");
							stringBuilder.append("<td style=\"text-align: center;\">NO</td>");
							stringBuilder.append("<td style=\"text-align: center;\"><strong>"+ valorNo +"</strong></td>");
							stringBuilder.append("</tr>");
						}
						
						if(pregunta.getTipo().equals(1) && (cabecera.getSubDescripcion() == null || cabecera.getOrden().equals(1))) {
							stringBuilder.append("<tr>");
							stringBuilder.append("<td style=\"text-align: center;\">SI</td>");
							stringBuilder.append("<td style=\"text-align: center; width: 30%;\"><strong>"+ valorSi +"</strong></td>");
							stringBuilder.append("</tr>");
							stringBuilder.append("<tr>");
							stringBuilder.append("<td style=\"text-align: center;\">NO</td>");
							stringBuilder.append("<td style=\"text-align: center;\"><strong>"+ valorNo +"</strong></td>");
							stringBuilder.append("</tr>");
						}
						
						String valorRespuesta = (respuesta.getRespBoolean() != null && respuesta.getRespBoolean()) ? "X" : "";
						
						if(pregunta.getTipo().equals(3) ) {
							stringBuilder.append("<tr>");
							stringBuilder.append("<td>");
							stringBuilder.append(pregunta.getDescripcion());
							stringBuilder.append("</td>");
							stringBuilder.append("<td style=\"text-align: center;\"><strong>"+ valorRespuesta +"</strong></td>");
							stringBuilder.append("</tr>");
						}
						
						if(pregunta.getTipo().equals(2) ) {
							stringBuilder.append("<p style=\"text-align: justify;\">" + respuesta.getRespText() +"</p>");
						}
					}
					
					Integer indice = cabecera.getOrden() == 3 ? 1 : 2;
					if(cabecera.getListaPreguntas().size() == 1 || (cabecera.getListaPreguntas().lastIndexOf(pregunta) == (cabecera.getListaPreguntas().size() - indice))){
						stringBuilder.append("</table>");
					}
				}
				
				
			}
		}
		
		informeEntity.setDetalleInforme(stringBuilder.toString());
		
		return informeEntity;
	}
	
	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		for (CabeceraFormulario cabecera : preguntasZonificacion) {
			Boolean existePreguntaTipo3 = false;
			Boolean respuestaPreguntaTipo3 = false;
			for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
				if(preguntaForm.getTipo().equals(3)) {
					existePreguntaTipo3 = true;
					if(preguntaForm.getRespuesta() != null
						&& preguntaForm.getRespuesta().getRespBoolean() != null) {
						respuestaPreguntaTipo3 = true;
						break;
					}
				}
			}
			
			if(existePreguntaTipo3 && !respuestaPreguntaTipo3) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Debe seleccionar una opción para el campo '" + cabecera.getDescripcion()+ "'.", null));
			}
		}
		
		for (CabeceraFormulario cabecera : preguntasInspeccion) {
			Boolean existePreguntaTipo3 = false;
			Boolean respuestaPreguntaTipo3 = false;
			for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
				if(preguntaForm.getTipo().equals(3)) {
					existePreguntaTipo3 = true;
					if(preguntaForm.getRespuesta() != null
						&& preguntaForm.getRespuesta().getRespBoolean() != null) {
						respuestaPreguntaTipo3 = true;
						break;
					}
				}
			}
			
			if(existePreguntaTipo3 && !respuestaPreguntaTipo3) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Debe seleccionar una opción para el campo '" + cabecera.getDescripcion()+ "'.", null));
			}
		}
		
		if(informeInspeccion.getFechaInspeccion()== null)
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Fecha inspección de campo' es requerido", null));
		
		if(informeInspeccion.getConclusiones() == null || informeInspeccion.getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Conclusiones' es requerido", null));
		
		if(informeInspeccion.getConclusiones()== null || informeInspeccion.getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Recomendaciones' es requerido", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			informeGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void guardarInforme() {
		try{
			informeInspeccion.setIdViabilidad(viabilidadProyecto.getId());
			informeInspeccion.setResultadoInforme(totalCalificacion);
			informeInspeccion.setEsPronunciamientoFavorable(totalCalificacion >= 11 ? true : false);
			informeInspeccion.setIdTarea(idTarea);
	
			informeInspeccionFacade.guardar(informeInspeccion, viabilidadProyecto.getAreaSnap().getAbreviacion());
			
			for (CabeceraFormulario cabecera : listaZonasAreas) {
				for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
					if((preguntaForm.getRespuesta() != null && preguntaForm.getRespuesta().getRespBoolean() != null && preguntaForm.getRespuesta().getRespBoolean())
							|| (preguntaForm.getRespuesta() != null && preguntaForm.getRespuesta().getId() != null))
						guardarRespuesta(preguntaForm);
				}
			}
			
			for (CabeceraFormulario cabecera : preguntasZonificacion) {
				for (PreguntaFormulario preguntaForm : cabecera
						.getListaPreguntas()) {
					guardarRespuesta(preguntaForm);
				}
			}
			
			for (CabeceraFormulario cabecera : preguntasInspeccion) {
				for (PreguntaFormulario preguntaForm : cabecera
						.getListaPreguntas()) {
					guardarRespuesta(preguntaForm);
				}
			}
			
			generarInforme(true);
			
			urlInforme = informeInspeccion.getInformePath();
			nombreInforme = informeInspeccion.getNombreReporte();
			archivoInforme = informeInspeccion.getArchivoInforme();
			
			informeGuardado = true;
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void guardarRespuesta(PreguntaFormulario pregunta) {
		if (pregunta.getRespuesta() != null) {
			RespuestaFormularioSnap respuesta = pregunta.getRespuesta();
			respuesta.setIdViabilidad(viabilidadProyecto.getId());
			respuesta.setIdPregunta(pregunta.getId());
			respuesta.setIdInforme(informeInspeccion.getId());

			respuestasFormularioFacade.guardar(respuesta);

			pregunta.setRespuesta(respuesta);
		}
	}

	public void firmarInforme() {
		mostrarFirma = true;
		documentoDescargado = false;
		
		RequestContext.getCurrentInstance().update("pnlObservaciones");
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			subirInforme();
			
			byte[] documentoContent = informeInspeccion.getArchivoInforme();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeInspeccion.getNombreReporte());
			} else {
				JsfUtil.addMessageError("Error al generar el archivo");
			}
			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void subirInforme() throws ServiceException, CmisAlfrescoException {
		generarInforme(false);
		
		DocumentoViabilidad documentoInforme = new DocumentoViabilidad();
		documentoInforme.setNombre("InformeInspeccion_" + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
		documentoInforme.setContenidoDocumento(informeInspeccion.getArchivoInforme());
		documentoInforme.setMime("application/pdf");
		documentoInforme.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION.getIdTipoDocumento());
		documentoInforme.setIdViabilidad(viabilidadProyecto.getId());

		documentoInforme = documentosFacade.guardarDocumentoProceso(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoInforme, 2, JsfUtil.getCurrentProcessInstanceId());
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoDescargado) {
	        byte[] contenidoDocumento = event.getFile().getContents();
	        
	        nombreDocumentoFirmado = event.getFile().getFileName();
	        
	        informeFirmaManual = new DocumentoViabilidad();
	        informeFirmaManual.setId(null);
	        informeFirmaManual.setContenidoDocumento(contenidoDocumento);
	        informeFirmaManual.setNombre(event.getFile().getFileName());
	        informeFirmaManual.setMime("application/pdf");
	        informeFirmaManual.setIdViabilidad(viabilidadProyecto.getId());
	        informeFirmaManual.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION.getIdTipoDocumento());
	        
	        subido = true;
	        habilitarEnviar = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la descarga del oficio");
		}
    }
	
	public String firmarDocumento() {
		try {
			generarInforme(false);
			
			documentoInformeAlfresco = new DocumentoViabilidad();
			documentoInformeAlfresco.setNombre("InformeInspeccion_" + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
			documentoInformeAlfresco.setContenidoDocumento(informeInspeccion.getArchivoInforme());
			documentoInformeAlfresco.setMime("application/pdf");
			documentoInformeAlfresco.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION.getIdTipoDocumento());
			documentoInformeAlfresco.setIdViabilidad(viabilidadProyecto.getId());

			documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", documentoInformeAlfresco, 2, JsfUtil.getCurrentProcessInstanceId());
			
			if(documentoInformeAlfresco.getId() != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInformeAlfresco);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}

	public void aceptar() {
		try {
			if(subido){
				documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", informeFirmaManual, 2, JsfUtil.getCurrentProcessInstanceId());
			}
			
			if (token) {
				String idAlfresco = documentoInformeAlfresco.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else if (!token && !subido) {
				JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
				return;
			}
			
			try {
				Map<String, Object> parametros = new HashMap<>();
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
}
