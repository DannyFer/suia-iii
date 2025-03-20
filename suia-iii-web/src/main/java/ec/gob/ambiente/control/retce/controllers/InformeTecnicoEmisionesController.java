package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceException;

import observaciones.ObservacionesController;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import lombok.Getter;
import lombok.Setter;
//import ec.gob.ambiente.mapa.webservices.GenerarImagenMapaWS_Service;
import ec.gob.ambiente.mapa.webservices.GenerarMapaWS;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.retce.model.DatoObtenidoMedicion;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.services.DatoObtenidoMedicionFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DetalleEmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.EmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.InformeTecnicoRetceFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ViewScoped
@ManagedBean
public class InformeTecnicoEmisionesController {
	
	@EJB
	private InformeTecnicoRetceFacade informeTecnicoRetceFacade;
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	@EJB
	private EmisionesAtmosfericasFacade emisionAtmosfericaFacade;
	@EJB
	private InformeOficioFacade informeOficioFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private PersonaFacade personaFacade;
	@EJB
	private DetalleEmisionesAtmosfericasFacade detalleEmisionFacade;
	@EJB
	private DatoObtenidoMedicionFacade datoObtenidoMedicionFacade;
	@EJB
	private DatosLaboratorioFacade datosLaboratorioFacade;
	@EJB
	private OficioRetceFacade oficioRetceFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ObservacionesFacade observacionesFacade;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporte;

	@Getter
	@Setter
	private InformeTecnicoRetce informeTecnicoRetce;

	@Getter
	@Setter
	private String tipo = "";

	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	private FormatoInformeEmisiones formatoInforme;

	@Getter
	@Setter
	private FormatoOficioEmisiones formatoOficio;

	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	private EmisionesAtmosfericas emisionAtmosferica;

	@Getter
	@Setter
	private List<DetalleEmisionesAtmosfericas> listaDetalles;

	@Getter
	@Setter
	private String informePath;

	@Getter
	@Setter
	private String nombreReporte;
	@Getter
	@Setter
	private String nombreFichero;

	@Getter
	@Setter
	private File informePdf, archivoFinal;

	@Getter
	@Setter
	private byte[] archivoInforme;

	@Getter
	@Setter
	private List<DatoObtenidoMedicion> listaDatosObtenidosTotal;

	private Map<String, Object> variables;

	@EJB
	private ProcesoFacade procesoFacade;

	private String tramite;

	@Setter
	@Getter
	private Integer numeroObservaciones, numeroRevision;

	private Usuario usuarioOperador,usuarioElabora,usuarioRevisa;

	@Setter
	@Getter
	private boolean oficioGenerado=false;

	@Getter
	private OficioPronunciamientoRetce oficio, oficioAnterior;

	@Setter
	@Getter
	private boolean habilitarElaborar=false;

	@Setter
	@Getter
	private boolean habilitarObservaciones=true;

	@Setter
	@Getter
	private boolean editarObservaciones=false;

	@Getter
	@Setter
	private boolean habilitarSiguiente = false;

	@Getter
	@Setter
	private boolean requiereModificaciones, requiereModificacionesOficio;

	private String nombreOperador,nombreRepresentanteLegal;

	@Setter
	@Getter
	private String urlPdf,workspace=null;

	private List<ObservacionesFormularios> observacionesTramite;

	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesController}")
	private ObservacionesController observacionesController;

	@Getter
	@Setter
	private Area areaTramite;
	
	@Setter
	@Getter    
    	private Documento imagenCoordenadas;

	@PostConstruct
	public void init(){
		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			String numeroObs = (String)variables.get("numero_observaciones");
			if(numeroObs != null)
				numeroObservaciones= Integer.valueOf(numeroObs);
			String usuarioProponente=(String)variables.get("usuario_operador");
			String usuarioTecnico=(String)variables.get("usuario_tecnico");
			String usuarioCoordinador=(String)variables.get("usuario_coordinador");
			informeTecnicoRetce = new InformeTecnicoRetce();
			String variableModificaciones = (String)variables.get("tiene_observaciones_informe_oficio");
			if(variableModificaciones != null){
				requiereModificaciones = Boolean.parseBoolean(variableModificaciones);
			}

			if(numeroObservaciones==null){
				numeroObservaciones=0;
			}
			
			numeroRevision=1+numeroObservaciones;
			
			usuarioRevisa=usuarioFacade.buscarUsuario(usuarioCoordinador);
			usuarioOperador=usuarioFacade.buscarUsuario(usuarioProponente);
			usuarioElabora=usuarioFacade.buscarUsuario(usuarioTecnico);
			
			String nombreTarea=bandejaTareasBean.getTarea().getTaskName().toUpperCase();
			if(nombreTarea.contains("ELABORAR")){
		    		habilitarElaborar=true;
		    		revisar = false;
		    		editarObservaciones=true;
		    	}else{	    	
		    		revisar = true;
		    		editarObservaciones=false;
		    		oficioGenerado=true;
		    		habilitarObservaciones=true;
		    	}
	    		    	
	    	Organizacion organizacion=organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			if(organizacion!=null){
				nombreOperador=organizacion.getNombre();
				nombreRepresentanteLegal=organizacion.getPersona().getNombre();
			}else{
				nombreOperador=usuarioOperador.getPersona().getNombre();
				nombreRepresentanteLegal="";
			}
						
			emisionAtmosferica = emisionAtmosfericaFacade.findByCodigo(tramite);

    		tipoDocumento = new TipoDocumento();

    		observacionesTramite = new ArrayList<ObservacionesFormularios>();

    		listaDetalles = detalleEmisionFacade.findByEmisionesAtmosfericasOrd(emisionAtmosferica);

    		informacionProyecto = emisionAtmosferica.getInformacionProyecto();

    		informeTecnicoRetce = informeTecnicoRetceFacade.getInforme(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS, numeroRevision);

    		if(informeTecnicoRetce == null){
    			informeTecnicoRetce = new InformeTecnicoRetce(emisionAtmosferica.getCodigo(),
    					TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS,numeroRevision);
    		}

    		oficio = oficioRetceFacade.getOficio(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA, numeroRevision);
    		if(oficio == null){
    			oficio = new OficioPronunciamientoRetce(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA, numeroRevision);    			
    		}
    		oficioAnterior = oficioRetceFacade.getOficio(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA, (numeroRevision - 1));
    		
    		areaTramite = emisionAtmosferica.getInformacionProyecto().getAreaResponsable();
    		
    		visualizarDocumento(true, TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS);
    		
    		 List<ObservacionesFormularios> observaciones = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
						informeTecnicoRetce.getId(), "informeEmisionesRetce");
    		 
    		 if(observaciones != null && !observaciones.isEmpty()){
    			 habilitarObservaciones = true;
    		 }else{
    			 habilitarObservaciones = false;
    		 }
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void visualizarDocumento(boolean marcaAgua, TipoDocumentoSistema tipoDocumentoSistema) {
		try {

			String nombreReporte=null;
			String numeroDocumento=null;
			Integer idtable=null;
			Documento documento = new Documento();
			File file = null;
			
			if(tipoDocumentoSistema== TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS){
				
				tipoDocumento.setId(TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS.getIdTipoDocumento());				
				plantillaReporte = informeOficioFacade.obtenerPlantillaReporte(this.getTipoDocumento().getId());
				
				informeTecnicoRetce.setFechaInforme(informeTecnicoRetce.getFechaModificacion()!=null ? informeTecnicoRetce.getFechaModificacion():informeTecnicoRetce.getFechaCreacion());
				
				formatoInforme = new FormatoInformeEmisiones();
				/*if(informeTecnicoRetce.getNumeroInforme() == null){
					informeTecnicoRetce.setNumeroInforme("MAAE-XXXX-RETCE-XXXX-20XX");
				}*/
				if(informeTecnicoRetce.getFechaInforme() == null){
					informeTecnicoRetce.setFechaInforme(new Date());
				}
				//TODO: Descarga del documento para introducir en el informe
				String path=Constantes.getGenerarImagenCoodenadasWS();
				imagenCoordenadas=documentosFacade.documentoXTablaIdXIdDocUnico(emisionAtmosferica.getId(),EmisionesAtmosfericas.class.getSimpleName(),TipoDocumentoSistema.RETCE_IMAGEN_COORDENADAS);
				if(imagenCoordenadas!=null){
					String idAldresco =imagenCoordenadas.getIdAlfresco();
					String[] parts=idAldresco.split(";");
					String[] subParts=parts[0].split("//");
					String[] subPartsu=subParts[1].split("/");
					workspace= path+subPartsu[1];	
				}

				formatoInforme.setCodigoInforme(informeTecnicoRetce.getNumeroInforme());

				formatoInforme.setFecha_evaluacion(JsfUtil.devuelveFechaEnLetrasSinHora(informeTecnicoRetce.getFechaInforme()));

				if(informacionProyecto.getFaseRetce() != null)
					formatoInforme.setFase_proyecto(informacionProyecto.getFaseRetce().getDescripcion());
				else
					formatoInforme.setFase_proyecto("N/A");

				formatoInforme.setFecha_ingreso(JsfUtil.devuelveFechaEnLetrasSinHora(emisionAtmosferica.getFechaReporte()));
				formatoInforme.setAnalisis_tablas(analisis());
				formatoInforme.setDatos_laboratorios(laboratorios());
				formatoInforme.setFrecuencia_monitoreo(devuelveFrecuenciaMonitoreo());
				formatoInforme.setNombre_proyecto(informacionProyecto.getNombreProyecto());
				formatoInforme.setNumero_tramite(emisionAtmosferica.getCodigo());
				formatoInforme.setPeriodo_medicion(devuelvePeriodoMedicion());
				formatoInforme.setPuntos_monitoreo(puntosMonitoreo());
				formatoInforme.setRazon_social(razonSocial());
				formatoInforme.setRepresentante_legal(representanteLegal == null ? "" : representanteLegal);
				formatoInforme.setSector_proyecto(informacionProyecto.getTipoSector().getNombre());
				if(informeTecnicoRetce.getConclusiones() != null && informeTecnicoRetce.getRecomendaciones() != null){

					formatoInforme.setConclusiones_recomendaciones(informeTecnicoRetce.getConclusiones() 
							+ informeTecnicoRetce.getRecomendaciones());
				}

				formatoInforme.setEnteResponsable(enteResponsable());

				formatoInforme.setTecnico_evaluador(usuarioElabora.getPersona().getNombre());
				
				if(numeroObservaciones > 0)
					formatoInforme.setTexto_observacion(clausulaObservacion());
				else
					formatoInforme.setTexto_observacion("");
				
				formatoInforme.setNombreElabora(usuarioElabora.getPersona().getNombre());
				if(usuarioRevisa != null)
					formatoInforme.setNombreRevisa(usuarioRevisa.getPersona().getNombre());
				else
					formatoInforme.setNombreRevisa("");
				if(usuarioRevisa != null){
					formatoInforme.setNombreRevisa(usuarioRevisa.getPersona().getNombre());
				}			
				
				nombreFichero = "InformeTecnicoEmisiones" + emisionAtmosferica.getCodigo()+".pdf";
				nombreReporte = "InformeTecnicoEmisiones";
				
				numeroDocumento=informeTecnicoRetce.getNumeroInforme();
				idtable=informeTecnicoRetce.getId();
				
				file = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(
	                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
	                    formatoInforme), marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
	            setInformePdf(file);
				
			}
			
			Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        archivoInforme = byteArchivo;
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        FileOutputStream fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        this.urlPdf=JsfUtil.devolverContexto("/reportesHtml/"+ file.getName());
			
		
			 if(!marcaAgua){
				JsfUtil.uploadApdoDocument(file, documento);
				documento.setMime("application/pdf");
				documento.setNombreTabla(nombreReporte);
				documento.setIdTable(idtable);
				documento.setNombre(nombreReporte + "_" + numeroDocumento + ".pdf");
				documento.setExtesion(".pdf");

				DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
				documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
				documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());

				documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionAtmosferica.getCodigo(),"INFORMES_OFICIOS",documentoTarea.getProcessInstanceId(),documento,tipoDocumentoSistema,documentoTarea);		    	
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private String representanteLegal;
	private String razonSocial(){
		try {
			
			String razonSocial = "";
			if(informacionProyecto.getUsuarioCreacion().length() == 13){
				Organizacion organizacion = organizacionFacade.buscarPorRuc(informacionProyecto.getUsuarioCreacion());				
				if(organizacion != null && organizacion.getNombre() != null){
					razonSocial = organizacion.getNombre();
					representanteLegal = organizacion.getPersona().getNombre();
				}
			}else{
				Persona persona = personaFacade.buscarPersonaPorPin(informacionProyecto.getUsuarioCreacion());
				if(persona != null && persona.getNombre() != null)
					razonSocial = persona.getNombre();
				
				representanteLegal = "";
			}	
			return razonSocial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String enteResponsable(){	
		
		if(areaTramite.getTipoArea().getNombre().equals("Planta Central")){
			
			StringBuilder plantaCentral = new StringBuilder();
			
			plantaCentral.append("<span><strong>MINISTERIO DEL AMBIENTE Y AGUA</strong></span><br />");
			plantaCentral.append("<span><strong>SUBSECRETAR&Iacute;A DE CALIDAD AMBIENTAL</strong></span><br />");
			plantaCentral.append("<span><strong>DIRECCI&Oacute;N NACIONAL DE CONTROL AMBIENTAL</strong></span><br />");
			plantaCentral.append("<span><strong>UNIDAD DE CALIDAD DE LOS RECURSO NATURALES</strong></span><br />");
			
			return plantaCentral.toString();
			
		}else if(areaTramite.getTipoArea().getNombre().equals("Dirección Provincial")){
			
			StringBuilder direccion = new StringBuilder();
			
			direccion.append("<span><strong>MINISTERIO DEL AMBIENTE Y AGUA</strong></span><br />");
			direccion.append("<span><strong>" + areaTramite.getAreaName() + "</strong></span><br />");
			return direccion.toString();
			
		}else if(areaTramite.getTipoArea().getNombre().equals("Ente Acreditado")){
			return areaTramite.getAreaName();
		}
		return "";
	}
	
	
	public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (archivoInforme != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    archivoInforme), "application/octet-stream");
            content.setName(nombreReporte);
        }
        return content;
    }

	private String devuelvePeriodoMedicion(){
		String duracion=""; 
		for(DetalleEmisionesAtmosfericas detalleEmision : listaDetalles){
			String mesInicio = "", anioInicio = "", mesFin = "", anioFin = "";
			if (detalleEmision.getFechaInicioMonitoreo() != null) {
				String string = detalleEmision.getFechaInicioMonitoreo();
				String[] parts = string.split("-");
				mesInicio = parts[0];
				anioInicio = parts[1];
			}

			if (detalleEmision.getFechaFinMonitoreo() != null) {
				String string = detalleEmision.getFechaFinMonitoreo();
				String[] parts = string.split("-");
				mesFin = parts[0];
				anioFin = parts[1];
			}
			duracion += (duracion.isEmpty()?"":"<br/>")+mesInicio + " de " + anioInicio + " - " + mesFin + " de " + anioFin;
		}
		return duracion;
	}

	private String devuelveFrecuenciaMonitoreo(){
		String frecuencia=""; 
		if (listaDetalles!=null){
			for(DetalleEmisionesAtmosfericas detalleEmision : listaDetalles){
				frecuencia +=(detalleEmision.getFrecuenciaMonitoreo()!=null)? (frecuencia.isEmpty()?"":"<br/>")+detalleEmision.getFrecuenciaMonitoreo().getDescripcion():"";
			}	
		}
		return frecuencia;
	}

	private String clausulaObservacion(){
		String clausula = "<span style=\"font-size:12px;\"><b>2.2.</b> Mediante oficio " + informeTecnicoRetce.getCodigoTramite()
				+ " de fecha " + JsfUtil.devuelveFechaEnLetrasSinHora(emisionAtmosferica.getFechaReporte())
				+ ", el operador " + razonSocial() 
				+ ", presenta la respuesta a las observaciones emitidas por esta Cartera de Estado mediante oficio "
				+ oficioAnterior.getNumeroOficio() //colocar la fecha correcta de la observacion
				+ " de fecha " + JsfUtil.devuelveFechaEnLetrasSinHora(oficioAnterior.getFechaOficio()) //colocar la fecha correcta de la observacion
				+ ", sobre el monitorio de <b>\"emisiones\"</b></span>";

		return clausula;
	}

	private String puntosMonitoreo(){
		try {
			StringBuilder puntos = new StringBuilder();
		        puntos.append("<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
		        		+ "<thead> <tr BGCOLOR=\"#C5C5C5\"> <th style='font-size:12px'>" 
		        		+ "C&oacute;digo fuente</th> <th style='font-size:12px'> Fuente Fija de Combusti&oacute;n</th>" 
		        		+ "<th style='font-size:12px'> N&uacute;mero o identificaci&oacute;n de la fuente</th>" 
		                + "<th style='font-size:12px'>Estado de la fuente</th>"
		                + "<th style='font-size:12px'>N&uacute;mero de serie</th>"
		                + "<th style='font-size:12px'>Locaci&oacute;n</th>"
		                + "<th style='font-size:12px'>Frecuencia de monitoreo</th>"
		                + "</tr> </thead> <tbody>");
								
			for(DetalleEmisionesAtmosfericas detalle : listaDetalles){
				String descripcionDetalle=(detalle.getEstadoFuenteDetalleCatalogo()!=null)?detalle.getEstadoFuenteDetalleCatalogo().getDescripcion():"";
				String descripcionFrecuencia=(detalle.getFrecuenciaMonitoreo()!=null)?detalle.getFrecuenciaMonitoreo().getDescripcion():"";				
				puntos.append("<tr> <td style='font-size:12px'><center>" + detalle.getCodigoFuente() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getFuenteFijaCombustion().getFuente() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getCodigoPuntoMonitoreo() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + descripcionDetalle + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getNumeroSerie() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getLugarPuntoMuestreo() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + descripcionFrecuencia + "</center>" +
		                    "</td> </tr>");				
			}
			puntos.append("</tbody> </table></center>");
			if(workspace!=null){
				puntos.append("<br><strong>Ubicación de los puntos de monitoreo</strong><center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"+
						"<tr><td><img src='"+workspace+"' width='100%' height='100%' ></td></tr>"+
						"</table></center><br/>");			
			}
			return puntos.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "";
	}
	
	private String analisis(){
		try {
			StringBuilder analisis = new StringBuilder();
			
			for(DetalleEmisionesAtmosfericas detalle : listaDetalles){
				listaDatosObtenidosTotal = datoObtenidoMedicionFacade.findByEmisionAtmosferica(detalle);
				
				analisis.append("<p><span style=\"font-size:12px;\"><strong>CARACTERISTICAS DE LA FUENTE DE MEDICI&Oacute;N:</strong></span><br />");
				analisis.append("<span style=\"font-size:12px;\"><strong>C&oacute;digo del punto de monitoreo aprobado: </strong>"
								+ detalle.getCodigoPuntoMonitoreo() + " </span><br />");
				analisis.append("<span style=\"font-size:12px;\"><strong>Fuente fija de Combusti&oacute;n: </strong>"
						+ detalle.getFuenteFijaCombustion().getFuente() + " </span><br />");
				
				String tipoCombustible = "";
				if(detalle.getTipoCombustible() != null)
					tipoCombustible = detalle.getTipoCombustible().getDescripcion();
				else
					tipoCombustible = "N/A";					
				
				analisis.append("<strong><span style=\"font-size:12px;\">Tipo de combustible: </span></strong><span style=\"font-size:12px;\">"	+ tipoCombustible + "</span><br />");
				String descripcionFuente=(detalle.getEstadoFuenteDetalleCatalogo()!= null)?detalle.getEstadoFuenteDetalleCatalogo().getDescripcion():"";
				analisis.append("<strong><span style=\"font-size:12px;\">Estado: </span></strong><span style=\"font-size:12px;\">" + descripcionFuente + "</span></p>");						
				
				if(detalle.getEstadoFuenteDetalleCatalogo()!= null){
				if(detalle.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa")){
					
					String nombreTabla = "";
					if(detalle.getTipoCombustible() != null){
						nombreTabla = detalle.getTipoCombustible().getNombreTabla();
					}else{
						nombreTabla = detalle.getFuenteFijaCombustion().getNombreTabla();
					}
					analisis.append("<br /><br />");
					
					 analisis.append("<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\"> "
							 	+ "<thead> <tr BGCOLOR=\"#C5C5C5\"><td colspan=\"5\" style='font-size:12px'><strong><center>"+nombreTabla+"</center></strong></td></tr>"
					 			+ " <tr BGCOLOR=\"#C5C5C5\">"
					 			+ "<td style='font-size:12px' rowspan='2'><strong><center>P&aacute;rametro</center></strong></td> "
				                + "<td style='font-size:12px' rowspan='2'><strong><center>Unidad</center></strong></td>" 
				                + "<td style='font-size:12px' rowspan='2'><strong><center> L&iacute;mite m&aacute;ximo permisible</center></strong></td>" 
				                + "<td style='font-size:12px' colspan='2'><strong><center>Reporte</center></strong></td>"
				                + "</tr> "					                
				                + "<tr BGCOLOR=\"#C5C5C5\">"
				                + "<td style='font-size:12px'><strong><center>Resultado</center></strong></td>" 
				                + "<td style='font-size:12px'><strong><center>Estado</center></strong></td>"				                
				                + "</tr></thead> <tbody>");
					
					for(DatoObtenidoMedicion dato : listaDatosObtenidosTotal){
						if(dato.getValorCorregido() > dato.getLimiteMaximoPermitido().getValor()){
							dato.setCumple("No Cumple LMP");
						}else{
							dato.setCumple("Cumple LMP");
						}
						 analisis.append("<tr> <td style='font-size:12px'><center><span style=\"font-size:12px;\">" + dato.getLimiteMaximoPermitido().getParametro().getDescripcion() + "</span></center>" +
				                    "</td> <td style='font-size:12px'><center><span style=\"font-size:12px;\">" + dato.getLimiteMaximoPermitido().getParametro().getUnidad() + "</span></center>" +
				                    "</td> <td style='font-size:12px'><center><span style=\"font-size:12px;\">" + dato.getLimiteMaximoPermitido().getValor() + "</span></center>" +
				                    "</td> <td style='font-size:12px'><center><span style=\"font-size:12px;\">" + dato.getValorCorregido() + "</span></center>" +
				                    "</td> <td style='font-size:12px'><center><span style=\"font-size:12px;\">" + dato.getCumple()+ "</span></center>" +				                    
				                    "</td> </tr>");	
					}	
					 analisis.append("</tbody> </table>");
				}
				}
				analisis.append("<br />");
			}
			return analisis.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "";
	}

	private String laboratorios(){
		try {
			List<DatosLaboratorio> listaLaboratoriosTotal = new ArrayList<DatosLaboratorio>();
			
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			Map<String, DatosLaboratorio> laboratoriosFiltrados = new HashMap<String, DatosLaboratorio>();

			for (DetalleEmisionesAtmosfericas detalle : listaDetalles) {
				List<DatosLaboratorio> listaLaboratorios = datosLaboratorioFacade.findByDetalleEmisiones(detalle.getEmisionesAtmosfericas());

				for (DatosLaboratorio lab : listaLaboratorios) {
					laboratoriosFiltrados.put(lab.getRuc(), lab);
				}
			}

			for (Entry<String, DatosLaboratorio> entry : laboratoriosFiltrados.entrySet()) {
				listaLaboratoriosTotal.add(entry.getValue());
			}

			StringBuilder labs = new StringBuilder();
			labs.append("<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\"> "
					+ "<thead> <tr BGCOLOR=\"#C5C5C5\"> <th style='font-size:12px'>"
					+ "RUC</th> <th style='font-size:12px'>Nombre o raz&oacute;n social</th>"
					+ "<th style='font-size:12px'> N° de Registro de SAE</th>"
					+ "<th style='font-size:12px'>Vigencia del Registro</th>"
					+ "</tr> </thead> <tbody>");

			for (DatosLaboratorio laboratorio : listaLaboratoriosTotal) {

				labs.append("<tr> <td style='font-size:12px'><center>"
						+ laboratorio.getRuc() + "</center>"
						+ "</td> <td style='font-size:12px'><center>"
						+ laboratorio.getNombre() + "</center>"
						+ "</td> <td style='font-size:12px'><center>"
						+ laboratorio.getNumeroRegistroSAE() + "</center>"
						+ "</td> <td style='font-size:12px'><center>"
						+ format.format(laboratorio.getFechaVigenciaRegistro()) + "</center>"
						+ "</td> </tr>");
			}
			labs.append("</tbody> </table>");

			return labs.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public boolean validarExistenciaObservacionesProponente() {
		boolean existenObservacionesProponente = true;

		if (observacionesTramite == null || observacionesTramite.isEmpty())
			existenObservacionesProponente = false;

		return existenObservacionesProponente;
	}
	
	public void guardar(){
		
		try {
			informeTecnicoRetceFacade.guardar(informeTecnicoRetce,emisionAtmosferica.getInformacionProyecto().getAreaResponsable(),JsfUtil.getLoggedUser());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			habilitarSiguiente = true;
			visualizarDocumento(true, TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void verOficioListener(){
		if(revisar){
			if(!validarObservaciones(!requiereModificaciones))
				return;
		}

		JsfUtil.redirectTo("/control/retce/emisionesAtmosfericas/oficioPronunciamiento.jsf");
	}

	public Boolean validarObservaciones(Boolean estado){
		try {
			 List<ObservacionesFormularios> observaciones = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
						informeTecnicoRetce.getId(), "InformeTecnicoRetce");
			
		        if(observaciones != null && !observaciones.isEmpty()){
		        	 if (estado) {
		                 for (ObservacionesFormularios observacion : observaciones) {
		                     if (observacion.getUsuario().equals(loginBean
		                    		 .getUsuario()) && !observacion.isObservacionCorregida()) {

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
		                 if (!encontrado) {
		                     JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
		                     return false;
		                 }
		             }
		        }else{
		        	JsfUtil.addMessageError("No existen observaciones sin corregir. Por favor rectifique los datos.");
		        	return false;
		        }
		       
		        return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
       return true;
    }
}
