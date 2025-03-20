package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.retce.model.DatoObtenidoMedicion;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
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
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ViewScoped
@ManagedBean
public class RevisionInformeOficioEmisionesController {
	
	private final Logger LOG = Logger.getLogger(RevisionInformeOficioGeneradorController.class);

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
	@EJB
	MensajeNotificacionFacade mensajeNotificacionFacade;
	
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
    private boolean esAutoridad;
    
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
        
    private Usuario usuarioOperador,usuarioElabora,usuarioRevisa, usuarioFirma;
        
	@Setter
	@Getter
	private boolean oficioGenerado=false;
    
	@Getter
	private OficioPronunciamientoRetce oficio;
	
	@Setter
	@Getter
	private boolean habilitarFirmar=false;
	
	@Setter
	@Getter
	private boolean habilitarObservaciones=true;
	
	@Setter
	@Getter
	private boolean editarObservaciones=false;
	
	@Getter
	@Setter
	private boolean informeOficioCorrectos;
	
	private String nombreOperador,nombreRepresentanteLegal;
	
	@Setter
	@Getter	
	private String urlPdf;
    
    private List<ObservacionesFormularios> observacionesTramite;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;
    
    @Getter
    @Setter
    private Area areaTramite;
    
	@Getter
	@Setter
	private boolean token, subido;
	
	@Getter
	@Setter
	private boolean revisar = false;
	
	@Getter
	@Setter
	private Integer panelMostrar;
		
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
    @Getter
	@Setter
	private Boolean descargarOficio=false;
    
    @Getter
    @Setter
    private Documento documentoOficio, documentoInforme, documento;
    
    @PostConstruct
    public void init(){
    	try {
			if(bandejaTareasBean.getProcessId() <= 0){
				return;
			}
    		variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);	
			String numObservaciones = (String)variables.get("numero_observaciones");
			if(numObservaciones != null)
				numeroObservaciones= Integer.valueOf(numObservaciones);
			String usuarioProponente=(String)variables.get("usuario_operador");
			String usuarioTecnico=(String)variables.get("usuario_tecnico");
			String usuarioCoordinador=(String)variables.get("usuario_coordinador");
			informeTecnicoRetce = new InformeTecnicoRetce();
			String variableModificaciones = (String)variables.get("tiene_observaciones_informe_oficio");
			if(variableModificaciones != null){
				informeOficioCorrectos = Boolean.parseBoolean(variableModificaciones);
			}else{
				informeOficioCorrectos = true;
			}
			
			if(numeroObservaciones==null){
				numeroObservaciones=0;
			}
			
			numeroRevision=1+numeroObservaciones;
			
			panelMostrar = 1;
			
			usuarioRevisa=usuarioFacade.buscarUsuario(usuarioCoordinador);		
			usuarioOperador=usuarioFacade.buscarUsuario(usuarioProponente);
			usuarioElabora=usuarioFacade.buscarUsuario(usuarioTecnico);				
			
			esAutoridad = false;
			if (JsfUtil.getCurrentTask() != null && JsfUtil.getCurrentTask().getTaskName().contains("Firmar")) {
				esAutoridad = true;
				verificaToken();
			}else{
				revisar = true;
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
    		    		
    		//Se debe buscar por observación para realizar la busqueda para tener un nuevo o un anterior 
    		if(informeTecnicoRetce == null){
    			informeTecnicoRetce = new InformeTecnicoRetce(emisionAtmosferica.getCodigo(),
    					TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS,numeroRevision);    			    			
    		}
    		    		
    		oficio = oficioRetceFacade.getOficio(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA, numeroRevision);
    		if(oficio == null){
    			oficio = new OficioPronunciamientoRetce(emisionAtmosferica.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA, numeroRevision);
    		}
    		
    		areaTramite = emisionAtmosferica.getInformacionProyecto().getAreaResponsable();
    		
    		visualizarDocumento(true, TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}    	
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
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}
    
    public void validarTareaBpm() {
		String url = "/control/retce/emisionesAtmosfericas/revisarInforme.jsf";
		if(JsfUtil.getCurrentTask().getTaskName().contains("Firmar"))
			url = "/control/retce/emisionesAtmosfericas/firmarPronunciamiento.jsf";
			
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(), url);
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
							+ "<br />" + informeTecnicoRetce.getRecomendaciones());
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
				
				numeroDocumento=informeTecnicoRetce.getCodigoTramite();
				idtable=informeTecnicoRetce.getId();
				
				file = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(
	                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
	                    formatoInforme), marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
	            setInformePdf(file);
				
			}else if(tipoDocumentoSistema== TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA){
				tipoDocumento.setId(TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA.getIdTipoDocumento());				
				plantillaReporte = informeOficioFacade.obtenerPlantillaReporte(this.getTipoDocumento().getId());				
				
				nombreFichero = "OficioEmisionesAtmosfericas" + emisionAtmosferica.getCodigo()+".pdf";
				nombreReporte = "OficioEmisionesAtmosfericas";
				
				/*if(oficio.getNumeroOficio() == null){
					oficio.setNumeroOficio("MAAE-XXXX-RETCE-20XX-XXXX");
				}*/
				
				oficio.setFechaOficio(new Date());				
				
				formatoOficio = new FormatoOficioEmisiones(oficio, emisionAtmosferica, JsfUtil.getLoggedUser(), emisionAtmosferica.getUsuarioCreacion());
				formatoOficio.setUbicacion(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				formatoOficio.setRazonSocial(nombreOperador);
				formatoOficio.setRepresentanteLegal(nombreRepresentanteLegal);
				
				buscarUsuarioFirma();
				formatoOficio.setNombreFirma(usuarioFirma.getPersona().getNombre());
				
				numeroDocumento=oficio.getCodigoTramite();
				idtable=oficio.getId();	
				
				file =  JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(
	                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
	                    formatoOficio), marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
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

				if(tipoDocumentoSistema== TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS){
					this.documentoInforme = documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionAtmosferica.getCodigo(),"INFORMES_OFICIOS",emisionAtmosferica.getId().longValue(),documento,tipoDocumentoSistema,null);
				}else if(tipoDocumentoSistema== TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA){
					this.documentoOficio = documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionAtmosferica.getCodigo(),"INFORMES_OFICIOS",emisionAtmosferica.getId().longValue(),documento,tipoDocumentoSistema,null);
//					oficioRetceFacade.guardar(oficio, informacionProyecto.getAreaResponsable(), loginBean.getUsuario());
				}				
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void buscarUsuarioFirma() {
		usuarioFirma = JsfUtil.getBean(ProcesoRetceController.class)
				.buscarAutoridadFirma(emisionAtmosferica.getInformacionProyecto().getAreaSeguimiento(),
						informeTecnicoRetce.getEsReporteAprobacion());
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
            content.setName(nombreFichero);
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
			duracion += (duracion.isEmpty()?"":"<br/>")+ mesInicio + " de " + anioInicio + " - " + mesFin + " de " + anioFin;
		}
		return duracion;
	}

	private String devuelveFrecuenciaMonitoreo(){
		String frecuencia=""; 
		for(DetalleEmisionesAtmosfericas detalleEmision : listaDetalles){
			frecuencia += (frecuencia.isEmpty()?"":"<br/>")+detalleEmision.getFrecuenciaMonitoreo().getDescripcion();
		}
		return frecuencia;
	}

	private String clausulaObservacion(){
		
		String clausula = "<span style=\"font-size:12px;\"><b>2.2.</b> Mediante oficio " + informeTecnicoRetce.getCodigoTramite() 
				+ " de fecha " + JsfUtil.devuelveFechaEnLetrasSinHora(emisionAtmosferica.getFechaReporte())
				+ ", el operador " + razonSocial() 
				+ ", presenta la respuesta a las observaciones emitidas por esta Cartera de Estado mediante oficio "
				+ informeTecnicoRetce.getCodigoTramite() //colocar la fecha correcta de la observacion
				+ " de fecha " + informeTecnicoRetce.getFechaInforme() //colocar la fecha correcta de la observacion
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
								
				puntos.append("<tr> <td style='font-size:12px'><center>" + detalle.getCodigoFuente() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getFuenteFijaCombustion().getFuente() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getCodigoPuntoMonitoreo() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getEstadoFuenteDetalleCatalogo().getDescripcion() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getNumeroSerie() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getLugarPuntoMuestreo() + "</center>" +
		                    "</td> <td style='font-size:12px'><center>" + detalle.getFrecuenciaMonitoreo().getDescripcion() + "</center>" +
		                    "</td> </tr>");				
			}
			puntos.append("</tbody> </table></center>");
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
				
				analisis.append("<span style=\"font-size:12px;\"><strong>CARACTERISTICAS DE LA FUENTE DE MEDICI&Oacute;N:</strong></span><br />");
				analisis.append("<span style=\"font-size:12px;\"><strong>C&oacute;digo del punto de monitoreo aprobado:</strong>"
								+ detalle.getCodigoPuntoMonitoreo() + " </span><br />");
				analisis.append("<span style=\"font-size:12px;\"><strong>Fuente fija de Combusti&oacute;n: </strong>"
						+ detalle.getFuenteFijaCombustion().getFuente() + " </span><br />");
				
				String tipoCombustible = "";
				if(detalle.getTipoCombustible() != null)
					tipoCombustible = detalle.getTipoCombustible().getDescripcion();
				else
					tipoCombustible = "N/A";					
				
				analisis.append("<strong><span style=\"font-size:12px;\">Tipo de combustible: </span></strong><span style=\"font-size:12px;\">"	+ tipoCombustible + "</span><br />");
				analisis.append("<strong><span style=\"font-size:12px;\">Estado: </span></strong><span style=\"font-size:12px;\">" + detalle.getEstadoFuenteDetalleCatalogo().getDescripcion() + "</span><br />");						
				
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
				
				analisis.append("<br /><br />");
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
				
		visualizarDocumento(true, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
		
	}
	
	public void enviar() {
		try {			
			Map<String, Object> parametros = new HashMap<>();
									
			Usuario usuarioAutoridad = new Usuario();
			if(informeOficioCorrectos){
				usuarioAutoridad = JsfUtil.getBean(ProcesoRetceController.class)
						.buscarAutoridadFirma(emisionAtmosferica.getInformacionProyecto().getAreaSeguimiento(),
								informeTecnicoRetce.getEsReporteAprobacion());
				
				String usuarioAutoridadBpm = (String)JsfUtil.getCurrentTask().getVariable("usuario_autoridad");
				
				if(usuarioAutoridadBpm == null){				
					parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
				}else{
					parametros.put("usuario_autoridad", usuarioAutoridad.getNombre());
				}
			}
			
			parametros.put("tiene_observaciones_informe_oficio", !informeOficioCorrectos);			
			
			try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				notificacion();
	
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} catch (JbpmException e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}	
	
	private void notificacion(){
		try {
			if(informeOficioCorrectos){
				if(esAutoridad){	
					
					String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionPronunciamientoEmitido", new Object[]{razonSocial(), tramite});
					Email.sendEmail(usuarioOperador, "Emisión del pronunciamiento del trámite", mensaje, tramite, loginBean.getUsuario());	
									
				}else{
					//envia notificacion al subsecretario y director
					String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRemiteInformeOficioRetce", new Object[]{"Coordinador",loginBean.getUsuario().getPersona().getNombre(), tramite});
					Email.sendEmail(usuarioFirma, "Envio de Informe técnico y oficio para revisión", mensaje, tramite, loginBean.getUsuario());
				}				
			}else{
				//si no están correctos los oficios				
				if(esAutoridad){
					if(!informeTecnicoRetce.getEsReporteAprobacion()){
						String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRemiteObservacionesInformeRetce", new Object[]{"Director", loginBean.getUsuario().getPersona().getNombre(), tramite});
						Email.sendEmail(usuarioElabora, "Envio de trámite para corrección de observaciones", mensaje, tramite, loginBean.getUsuario());
					}else{
						//aprobacion
						if(areaTramite.getTipoArea().getSiglas().equals("PC")){
							String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRemiteObservacionesInformeRetce", new Object[]{"Subsecretario", loginBean.getUsuario().getPersona().getNombre(), tramite});
							Email.sendEmail(usuarioElabora, "Envio de trámite para corrección de observaciones", mensaje, tramite, loginBean.getUsuario());
						}else{
							String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRemiteObservacionesInformeRetce", new Object[]{"Director", loginBean.getUsuario().getPersona().getNombre(), tramite});
							Email.sendEmail(usuarioElabora, "Envio de trámite para corrección de observaciones", mensaje, tramite, loginBean.getUsuario());
						}
					}					
				}else{
					String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRemiteObservacionesInformeRetce", new Object[]{"Coordinador", loginBean.getUsuario().getPersona().getNombre(), tramite});
					Email.sendEmail(usuarioElabora, "Envio de trámite para corrección de observaciones", mensaje, tramite, loginBean.getUsuario());
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	
	public void atras() {
		switch (panelMostrar) {
		case 2:
			visualizarDocumento(true,TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS);						
			panelMostrar = 1;
			break;
		case 3:
			visualizarDocumento(true,TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);	
			panelMostrar = 2;
		default:
			break;
		}		
	}
	
	public void siguiente() {
		switch (panelMostrar) {
		case 1:
			guardar();			
			panelMostrar = 2;
			break;
		
		default:
			break;
		}
	}
	
	public void guardarInformeOficio(){
				
		panelMostrar = 3;
	}
	
	public String firmarDocumento() {
		try {
			oficio.setNumeroRevision(null);
			oficioRetceFacade.guardar(oficio, informacionProyecto.getAreaResponsable(), loginBean.getUsuario());
			visualizarDocumento(false,TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
			
			if(documentoOficio != null){
				String documentOffice = documentosFacade.direccionDescarga(documentoOficio);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}			
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del oficio por el director", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	    
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documento != null){
			byte[] contenidoDocumento = event.getFile().getContents();

			documento.setContenidoDocumento(contenidoDocumento);
			documento.setNombre(event.getFile().getFileName());

			subido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento del oficio");
		}		
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			oficio.setNumeroOficio(null);
			oficioRetceFacade.guardar(oficio, informacionProyecto.getAreaResponsable(), loginBean.getUsuario());
			visualizarDocumento(false, TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
			
			documento = documentoOficio;
			
			byte[] documentoContent = null;
						
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	
	public void completarTarea() {
		try {

			if (documento != null) {	
				
				if (subido) {
					oficio.setArchivoOficio(documento.getContenidoDocumento());
					guardarDocumentoOficio();
				}

				String idAlfresco = documento.getIdAlfresco();

				if (token && !documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El oficio no está firmado electrónicamente.");
					return;
				} else if (!token && !subido) {
					JsfUtil.addMessageError("Debe adjuntar el oficio firmado.");
					return;
				}
				
				visualizarDocumento(false, TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS);

				Map<String, Object> parametros = new HashMap<>();
				
				parametros.put("pronunciamiento_aprobado", informeTecnicoRetce.getEsReporteAprobacion());
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				
				notificacion();

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			} else {
				JsfUtil.addMessageError("Error al realizar la operación.");
			}
		} catch (Exception e) {
			LOG.error(e);
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}

	
	public void guardarDocumentoOficio() throws ServiceException, CmisAlfrescoException {
		Documento documentoOficio = new Documento();
		documentoOficio.setNombre("OficioEmisionesAtmosfericas" + "_" + oficio.getCodigoTramite()+ ".pdf");
		documentoOficio.setContenidoDocumento(oficio.getArchivoOficio());
		documentoOficio.setNombreTabla("OficioPronunciamientoEmisionesAtmosfericas");
		documentoOficio.setIdTable(oficio.getId());
		documentoOficio.setDescripcion("Oficio Emisiones Atmosfericas");
		documentoOficio.setMime("application/pdf");
		documentoOficio.setExtesion(".pdf");

		documento= documentosFacade.guardarDocumentoAlfrescoSinProyecto(
				emisionAtmosferica.getCodigo(),
				"INFORMES_OFICIOS", emisionAtmosferica.getId().longValue(), 
				documentoOficio,
				TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA,
				null);		
	}
	
	public void validarExisteObservacionesInformeOficio() {
		try {
			informeOficioCorrectos = false;
			List<ObservacionesFormularios> observacionesInforme = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
							informeTecnicoRetce.getId(), "informeEmisionesRetce");
			
			
			List<ObservacionesFormularios> observacionesOficio = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
					oficio.getId(), "oficioEmisionesRetce");
			
			if(observacionesInforme.size() == 0 && observacionesOficio.size() == 0) {
				informeOficioCorrectos = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar las observaciones del informe oficio RETCE.");
		}
	}
	 
}
