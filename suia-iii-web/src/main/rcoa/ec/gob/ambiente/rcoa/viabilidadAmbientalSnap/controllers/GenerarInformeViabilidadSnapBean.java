package ec.gob.ambiente.rcoa.viabilidadAmbientalSnap.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PreguntasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.RespuestasFormularioFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CabeceraFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.PreguntaFormulario;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.RespuestaFormularioSnap;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class GenerarInformeViabilidadSnapBean implements Serializable {

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
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private	DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	
	@EJB
    private AsignarTareaFacade asignarTareaFacade;

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
	private DocumentoViabilidad documentoInformeAlfresco;
	
	@Getter
	@Setter
	private List<AreasSnapProvincia> areasInterseca;

	@Getter
	@Setter
	private Boolean zonaProteccion, zonaRecuperacion, zonaUsoPublico, zonaUsoSostenible, zonaManejo;

	@Getter
	@Setter
	private String tipoPronunciamiento, textoAyudaConclusiones;

	@Getter
	@Setter
	private Integer totalCalificacion, idProyecto, idViabilidad, idTarea, idInformeAnterior;

	@Getter
	@Setter
	private String urlInforme, nombreInforme, cargoAdminArea;
	
	@Getter
	@Setter
	private Boolean camposSoloLectura;

	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	private Map<String, Object> variables;

	@PostConstruct
	private void iniciar() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
	
			String idProyectoString=(String)variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			
			cargoAdminArea = "ADMINISTRADOR DEL ÁREA PROTEGIDA";
			
			totalCalificacion = 0;
			
			plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_II_INFORME_TECNICO);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			Boolean esSnapMae = Boolean.parseBoolean(variables.get("esAdministracionDirecta").toString());
			
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(idProyecto, esSnapMae);
			idViabilidad = viabilidadProyecto.getId();
			
			idTarea = (int)bandejaTareasBean.getTarea().getTaskId();
			
			informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if (!JsfUtil.getCurrentTask().getTaskName().contains("Revisar")) {
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
				}
			} 
			
			cargarPreguntas();
			
			cargarDatos();
	
			actualizarTotalViabilidad();
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeInspeccionBiodiversidad.");
		}
	}

	public void cargarPreguntas() {
		List<CabeceraFormulario> preguntasInspeccionSnap = preguntasFormularioFacade.getListaCabecerasPorTipo(4);
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
		textoAyudaConclusiones = "Después de la inspección realizada en el sector de la propuesta del proyecto, obra o actividad (INCLUIR NOMBRE DEL PROYECTO, OBRA O ACTIVIDAD) ubicada en el área protegida (INCLUIR NOMBRE DEL ÁREA PROTEGIDA), se concluye que el mismo cuenta con una viabilidad ambiental (FAVORABLE/NO FAVORABLE), determinándose así la (FACTIBILIDAD O NO) de la realización de la obra, proyecto o actividad. \n\nDetallar conclusiones asociadas a los criterios establecidos en el análisis de viabilidad ambiental para pronunciamiento no favorable";
		
		Integer idInforme = (informeInspeccion != null) ? informeInspeccion.getId() : 0;
		if(idInformeAnterior != null)
			idInforme = idInformeAnterior;
		
		for (CabeceraFormulario cabecera : listaZonasAreas) {
			for (PreguntaFormulario preguntaForm : cabecera.getListaPreguntas()) {
				RespuestaFormularioSnap respuesta = respuestasFormularioFacade.getPorViabilidadPregunta(viabilidadProyecto.getId(), preguntaForm.getId(), idInforme);
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
			tipoPronunciamiento = "PRONUNCIAMIENTO FAVORABLE";
		else
			tipoPronunciamiento = "PRONUNCIAMIENTO NO FAVORABLE";
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
				
				if (preguntaForm.getTipo().equals(5)
						&& respuesta != null
						&& respuesta.getRespValueRadio() != null) {
					Integer valor = respuesta.getRespValueRadio();
					switch (valor) {
					case 1:
						Integer valorRespuesta = preguntaForm.getValorTrue();
						if(viabilidadProyecto.getAreaSnap().getValorZonificacionArea() != null) {
							valorRespuesta = viabilidadProyecto.getAreaSnap().getValorZonificacionArea();
						}
						
						totalCalificacion += valorRespuesta;
						respuesta.setRespInteger(valorRespuesta);
						break;
					case 2:
						totalCalificacion += preguntaForm.getValorFalse();
						respuesta.setRespInteger(preguntaForm.getValorFalse());
						break;
					case 3:
						totalCalificacion += preguntaForm.getOtroValor();
						respuesta.setRespInteger(preguntaForm.getOtroValor());
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {

			informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(informeInspeccion == null) {
				informeInspeccion = new InformeInspeccionBiodiversidad();
				informeInspeccion.setNombreFichero("InformeViabilidad.pdf");
			} else {
				informeInspeccion.setNombreFichero("InformeViabilidad " + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
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

			urlInforme = informeInspeccion.getInformePath();
			nombreInforme = informeInspeccion.getNombreReporte();
			archivoInforme = informeInspeccion.getArchivoInforme();

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	
	public InformeInspeccionEntity cargarDatosDocumento() {
		InformeInspeccionEntity informeEntity = new InformeInspeccionEntity();
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		String razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
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
		informeEntity.setNombreAreaProtegida(interseccionViabilidadCoaFacade.getNombresAreasProtegidasZonaSnap(idProyecto, 2));
		informeEntity.setNombreTecnico(JsfUtil.getLoggedUser().getPersona().getNombre());
		informeEntity.setCargoTecnico(cargoAdminArea + " " + viabilidadProyecto.getAreaSnap().getNombreAreaCompleto());
		
		String otrosResponsables = recuperarOtrasAreas();
		informeEntity.setOtrosResponsables(otrosResponsables);
		
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
						
						if(pregunta.getTipo().equals(3) ) {
							String valorRespuesta = (respuesta.getRespBoolean() != null && respuesta.getRespBoolean()) ? "X" : "";
							
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
						
						if(pregunta.getTipo().equals(5)) {
							valorSi = (respuesta.getRespValueRadio() != null && respuesta.getRespValueRadio().equals(1)) ? "X" : "";
							valorNo = (respuesta.getRespValueRadio() != null && respuesta.getRespValueRadio().equals(2)) ? "X" : "";
							String valorOtro = (respuesta.getRespValueRadio() != null && respuesta.getRespValueRadio().equals(3)) ? "X" : "";
							
							stringBuilder.append("<tr>");
							stringBuilder.append("<td style=\"text-align: center;\">SI</td>");
							stringBuilder.append("<td style=\"text-align: center; width: 30%;\"><strong>"+ valorSi +"</strong></td>");
							stringBuilder.append("</tr>");
							stringBuilder.append("<tr>");
							stringBuilder.append("<td style=\"text-align: center;\">NO</td>");
							stringBuilder.append("<td style=\"text-align: center;\"><strong>"+ valorNo +"</strong></td>");
							stringBuilder.append("</tr>");
							stringBuilder.append("<tr>");
							stringBuilder.append("<td style=\"text-align: center;\">Sin Información</td>");
							stringBuilder.append("<td style=\"text-align: center;\"><strong>"+ valorOtro +"</strong></td>");
							stringBuilder.append("</tr>");
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
	
	public String recuperarOtrasAreas() {
		areasInterseca = new ArrayList<AreasSnapProvincia>();
		
		if(viabilidadProyecto.getAreaSnap().getTipoSubsitema().equals(1)) {
			List<DetalleInterseccionProyectoAmbiental> interseccionesProyecto = interseccionViabilidadCoaFacade.getDetalleInterseccionSnap(idProyecto);
			if(interseccionesProyecto != null && interseccionesProyecto.size() > 1) {
				int item = 0;
				for (DetalleInterseccionProyectoAmbiental detalle : interseccionesProyecto) {
					if(item > 0) {
						List<AreasSnapProvincia> ubicacionesCapa = interseccionViabilidadCoaFacade.getAreaSnapPorCodigoZona(detalle.getCodigoUnicoCapa(), detalle.getZona());
						AreasSnapProvincia areaSnap = ubicacionesCapa.get(0);
						Usuario tecnicoResponsable = null;
						if(areaSnap.getTipoSubsitema().equals(1)) {
							//recuperar los usuarios responsables del área
							List<String> usuariosArea = new ArrayList<>();
							for (AreasSnapProvincia area : ubicacionesCapa) {
								if(area.getUsuario() != null && !usuariosArea.contains(area.getUsuario().getNombre()))
									usuariosArea.add(area.getUsuario().getNombre());
							}
							
							if(usuariosArea.size() > 1) {
								//si existen dos usuarios asignados a la misma área y provincia. Se asigna el usuario con menor carga de trabajo
								List<Usuario> listaTecnicosResponsables = asignarTareaFacade
										.getCargaLaboralPorNombreUsuariosProceso(usuariosArea,
												Constantes.RCOA_PROCESO_VIABILIDAD_SNAP);
								if(listaTecnicosResponsables != null && listaTecnicosResponsables.size() >0)
									tecnicoResponsable = listaTecnicosResponsables.get(0);
							} else {
								tecnicoResponsable = areaSnap.getUsuario();
							}
							
							areaSnap.setTecnicoResponsable(tecnicoResponsable);
							areasInterseca.add(areaSnap);
						}
					}
					
					item++;
				}
			}
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		if(areasInterseca.size() > 0) {
			stringBuilder.append("<p style=\"text-align: justify;\">Revisado por:</p>");
			for (AreasSnapProvincia area : areasInterseca) {
				stringBuilder.append("<p style=\"text-align: justify;\"><br />");
				stringBuilder.append(area.getTecnicoResponsable().getPersona().getNombre());
				stringBuilder.append("<br />");
				stringBuilder.append(cargoAdminArea + " " + area.getNombreAreaCompleto());
				stringBuilder.append("</p>");
			}
		}
		
		return stringBuilder.toString();
	}

	public void guardarInforme() {
		guardarDataInforme();
		
		generarInforme(true);
		
		urlInforme = informeInspeccion.getInformePath();
		nombreInforme = informeInspeccion.getNombreReporte();
		archivoInforme = informeInspeccion.getArchivoInforme();
	}
	
	public void guardarDataInforme() {
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
	
	public DocumentoViabilidad guardarDocumentos() throws Exception {
		guardarDataInforme();
		
		generarInforme(false);
		
		urlInforme = informeInspeccion.getInformePath();
		nombreInforme = informeInspeccion.getNombreReporte();
		archivoInforme = informeInspeccion.getArchivoInforme();
		
		documentoInformeAlfresco = new DocumentoViabilidad();
		documentoInformeAlfresco.setNombre("InformeInspeccion_" + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
		documentoInformeAlfresco.setContenidoDocumento(informeInspeccion.getArchivoInforme());
		documentoInformeAlfresco.setMime("application/pdf");
		documentoInformeAlfresco.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_SNAP_II_INFORME_TECNICO.getIdTipoDocumento());
		documentoInformeAlfresco.setIdViabilidad(viabilidadProyecto.getId());

		documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoInformeAlfresco, 2, JsfUtil.getCurrentProcessInstanceId());
		
		if(documentoInformeAlfresco.getId() != null) {
			return documentoInformeAlfresco;
		}
		
		return null;
	}
}
