package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.EvaluacionParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;
import observaciones.ObservacionesController;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ManagedBean
@ViewScoped
public class OficioPSBean implements Serializable {

    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    private static final long serialVersionUID = -4243608855067530563L;

    private final Logger LOG = Logger.getLogger(OficioPSBean.class);
    @Getter
    @Setter
    private TipoDocumento tipoDocumento;
    @Getter
    @Setter
    private File oficioPdf;
    @Getter
    @Setter
    private byte[] archivoInforme;
    @Getter
    @Setter
    private String informePath;
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;
    @Getter
    @Setter
    private PlantillaReporte plantillaReporte;
    @Getter
    @Setter
    private String tipo = "";
    @Getter
    @Setter
    private boolean soloLectura;
    @Getter
    @Setter
    private boolean revisar;
    @Getter
    @Setter
    private String nombreReporte;
    @Getter
    @Setter
    private String nombreFichero;
    @Getter
    @Setter
    private File archivoFinal;
    @Getter
    @Setter
    private Boolean pronunciamientoNoAprobacion;
    @Getter
    @Setter
    private boolean requiereModificaciones;
    @Getter
    @Setter
    private String documentOffice = "";
    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private Documento documento;
    @Getter
    @Setter
    private Map<String, Documento> documentos;

    @Getter
    @Setter
    private boolean cambiarNumeroOficio;

    @Getter
    @Setter
    private Map<String, Object> variables;

    /***/
    @Getter
    @Setter
    private ParticipacionSocialAmbiental participacionSocialAmbiental;
    @EJB
    private SecuenciasFacade secuenciasFacade;
    @EJB
    private ObservacionesFacade observacionesFacade;
    @EJB
    private EvaluacionParticipacionSocialFacade evaluacionParticipacionSocialFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private AreaFacade areaFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    @EJB
    private FacilitadorProyectoFacade facilitadorProyectoFacade;
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    
    //Aumento de un EJB
    @EJB
    private DocumentosFacade documentosFacade;
    
    //Cris F: aumento de variable para validar la subida del informe técnico firmado
    private boolean firmarInforme = false;
    
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
	private CrudServiceBean crudServiceBean;
 	@EJB
	private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

    @PostConstruct
    public void init() {
        try {
            url = "/prevencion/participacionsocial/realizarInformeTecnicoOficio.jsf";
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap();
            if (params.containsKey("tipo")) {
                url += "?tipo=revisar";
                tipo = params.get("tipo");
                if (params.get("tipo").equals("revisar")) {
                    // revisar = true;

                }


            }
            variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                    .getProcessInstanceId());
            documentos = new HashMap<>();
            
            //aumento para validar la tarea en la cual debe firmarse el informe tecnico
            if (bandejaTareasBean.getTarea().getTaskName().contains("Realizar informe tecnico y oficio de pronunciamiento")){
				firmarInforme = true;		
			}
        } catch (Exception e) {
            LOG.error("Error al inicializar:  ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public void configurarVariables() throws ServiceException {
        configurarVariables(false);

    }

    public void configurarVariables( boolean token) throws ServiceException {
        try {
            if(participacionSocialAmbiental==null){

            participacionSocialAmbiental = participacionSocialFacade
                    .buscarCrearParticipacionSocialAmbiental(proyectosBean.getProyecto());

            tipoDocumento = new TipoDocumento();
            tipoDocumento.setId(TipoDocumentoSistema.PS_OFICIO_FINAL.getIdTipoDocumento());

            plantillaReporte = participacionSocialFacade.obtenerPlantillaReporte(this.getTipoDocumento().getId());

            List<String> listaClaves = new ArrayList<>();
            listaClaves.add("oficio");
            listaClaves.add("informeTecnico");

            documentos = participacionSocialFacade.recuperarDocumentosTipo(proyectosBean.getProyecto().getId(),
                    ParticipacionSocialAmbiental.class.getSimpleName(), listaClaves);

            if (variables.containsKey("cambiarNumeroOficioES")) {
                cambiarNumeroOficio = Boolean.parseBoolean((String) variables.get("cambiarNumeroOficioES"));
            }
            if (!cambiarNumeroOficio && documentos.containsKey("oficio") && documentos.get("oficio").getCodigoPublico() != null && !documentos.get("oficio").getCodigoPublico().isEmpty()) {
                String numeroOficio=documentos.get("oficio").getCodigoPublico();
                if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
                    numeroOficio=numeroOficio.replace(Constantes.SIGLAS_INSTITUCION + "-", "GAD-");
                }
                participacionSocialAmbiental.setNumeroOficio(numeroOficio);
            } else {
                participacionSocialAmbiental.setNumeroOficio(generarCodigoOficioEmisionActualizacion(proyectosBean.getProyecto().getAreaResponsable()));
            }

            if (documentos.containsKey("informeTecnico")) {
                participacionSocialAmbiental.setNumeroInforme(documentos.get("informeTecnico").getCodigoPublico());
            }

            ProyectoLicenciamientoAmbiental proyecto = proyectosBean.getProyecto();            
            participacionSocialAmbiental.setNombrePPS(proyecto.getNombre());
            
            String nombreProponente = proyecto.getUsuario().getPersona().getNombre();

            //Cris F: aumento de nombre del proyecto de participación social
            participacionSocialAmbiental.setCodigoProyecto(proyecto.getCodigo());
            //fin del cambio
            participacionSocialAmbiental.setNombreProponente(nombreProponente);

            boolean esRevision = false;
            Map<String, String> paramsUrl = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();
            if (paramsUrl.containsKey("tipo") && paramsUrl.get("tipo").equals("revisar")) 
            	esRevision = true;
            
            Usuario usuarioConsultor = usuarioFacade.buscarUsuarioCompleta((String) variables.get("u_Tecnico"));
            if(usuarioConsultor == null && esRevision)
            	usuarioConsultor = usuarioFacade.buscarUsuarioWithOutFilters((String) variables.get("u_Tecnico")); //para buscar tecnico q fue eliminado cuando el informe está en revision
            if(usuarioConsultor != null) {
            	participacionSocialAmbiental.setNombreConsultor(usuarioConsultor.getPersona().getNombre());
            }

            FacilitadorProyecto facilitadorProyecto = facilitadorProyectoFacade.obtenerFacilitadorResponsable(proyecto);

            List<FacilitadorProyecto> facilitadores = facilitadorProyectoFacade.listarFacilitadoresProyecto(proyecto);

            String listaFacilitadores = "";
            String separador = "";
            for (FacilitadorProyecto fac : facilitadores) {
                listaFacilitadores += separador + fac.getUsuario().getPersona().getNombre();
                separador = ", ";
            }
            participacionSocialAmbiental.setListaFacilitadores(listaFacilitadores);
            // participacionSocialAmbiental.setNombreFacilitador(usuarioFacilitador.getPersona().getNombre());
            participacionSocialAmbiental.setNombreFacilitador(facilitadorProyecto.getUsuario().getPersona().getNombre());

            if (participacionSocialAmbiental.getFechaEntregaInformeFinalPPS() != null) {
                participacionSocialAmbiental.setFechaEntregaInformeSistematizacion(JsfUtil
                        .devuelveFechaEnLetrasSinHora(participacionSocialAmbiental.getFechaEntregaInformeFinalPPS()));
            } else {
                participacionSocialAmbiental.setFechaEntregaInformeSistematizacion(JsfUtil
                        .devuelveFechaEnLetrasSinHora(new Date()));
            }
            participacionSocialAmbiental.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(participacionSocialAmbiental
                    .getFechaModificacion()));

        /*
         * ****************************************************
         */
            participacionSocialAmbiental.setFechaMostrar(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));

            String sujetoControlEncabez = "Sra./Sr. <br/>";
            if (proyecto.getUsuario().getPersona().getTipoTratos() != null
                    && proyecto.getUsuario().getPersona().getTipoTratos().getNombre() != null
                    && !proyecto.getUsuario().getPersona().getTipoTratos().getNombre().isEmpty()) {
                sujetoControlEncabez = " " + proyecto.getUsuario().getPersona().getTipoTratos().getNombre()+"<br/>";
            }
            if (proyecto.getUsuario().getPersona().getNombre() != null) {
                sujetoControlEncabez += " " + proyecto.getUsuario().getPersona().getNombre();
            }

            String[] cargoEmpresa = null;
            Organizacion organizacion = organizacionFacade.buscarPorPersona(proyecto.getUsuario().getPersona(), proyecto.getUsuario().getNombre());
            if (organizacion != null) {
                cargoEmpresa = new String[2];
                cargoEmpresa[0] = organizacion.getCargoRepresentante();
                cargoEmpresa[1] = organizacion.getNombre();

            }
            if (cargoEmpresa != null) {
                sujetoControlEncabez = sujetoControlEncabez + "<br/>" + cargoEmpresa[0] + "<br/>" + cargoEmpresa[1];
            }
            participacionSocialAmbiental.setSujetoControlEncabez(sujetoControlEncabez);

            if (!token) {
                Usuario usuarioAutoridad = usuarioFacade.buscarUsuarioCompleta((String) variables.get("u_Director"));

                participacionSocialAmbiental.setAutoridad("<br/><br / ><br/>" + usuarioAutoridad.getPersona().getNombre());
                //participacionSocialAmbiental.setCargoAutoridad(JsfUtil.obtenerCargoUsuario(usuarioAutoridad));
                Area areaProyectoR = proyectosBean.getProyecto().getAreaResponsable();
                if (areaProyectoR.getArea()== null){
                participacionSocialAmbiental.setCargoAutoridad(areaProyectoR.getAreaName());
//                participacionSocialAmbiental.setCargoAutoridad(usuarioAutoridad.getArea().getAreaName());
                }else {
                	participacionSocialAmbiental.setCargoAutoridad(areaProyectoR.getArea().getAreaName());}
                if(usuarioAutoridad.getRolUsuarios().size()>1){
//                    participacionSocialAmbiental.setCargoAutoridad(usuarioAutoridad.getArea().getAreaName());
                	if (areaProyectoR.getArea()== null){
                	participacionSocialAmbiental.setCargoAutoridad(areaProyectoR.getAreaName());
                }else{
                	participacionSocialAmbiental.setCargoAutoridad(areaProyectoR.getArea().getAreaName());
                	}
                }
            } else {
                participacionSocialAmbiental.setAutoridad("");
                participacionSocialAmbiental.setCargoAutoridad("");
            }
            Area areaProyectoR=proyectosBean.getProyecto().getAreaResponsable();
            String gobiernoAutonomoDescentralizado=" "; 
            if(areaProyectoR.getTipoArea().getId()==3){ 
                participacionSocialAmbiental.setGADPC(gobiernoAutonomoDescentralizado+areaProyectoR.getAreaName()); 
                participacionSocialAmbiental.setSegundoParrafoOficio("sobre la base del informe técnico No. "+participacionSocialAmbiental.getNumeroInforme()+" "+gobiernoAutonomoDescentralizado+" establece que:"); 
            }else{ 
                participacionSocialAmbiental.setGADPC("Esta Cartera de Estado");
                participacionSocialAmbiental.setSegundoParrafoOficio("mediante informe técnico Nro. "+participacionSocialAmbiental.getNumeroInforme()+", de fecha "+participacionSocialAmbiental.getFechaInforme()+" se concluye que:");
            }
            visualizarOficio();
            }
            
        } catch (Exception e) {
            LOG.error("Error al inicializar:  ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
    }

    public String visualizarOficio() {
        String pathPdf = null;
        try {

            nombreFichero = "OficioPS" + proyectosBean.getProyecto().getCodigo()
                    + ".pdf";
            nombreReporte = "OficioPS.pdf";
            File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    true, participacionSocialAmbiental);
            setOficioPdf(informePdf);

            Path path = Paths.get(getOficioPdf().getAbsolutePath());
            archivoInforme = Files.readAllBytes(path);
            archivoFinal = new File(JsfUtil.devolverPathReportesHtml(nombreFichero));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(archivoInforme);
            file.close();
            setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + nombreFichero));

            pathPdf = informePdf.getParent();

            if (cambiarNumeroOficio || !documentos.containsKey("oficio")) {

                guardarDocumento();
                if (cambiarNumeroOficio) {
                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                    params.put("cambiarNumeroOficioES", false);
                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                }
            }

        } catch (Exception e) {
            LOG.error("Error al visualizar el oficio", e);
            JsfUtil.addMessageError("Error al visualizar el oficio");
        }

        return pathPdf;
    }

    public void guardarDocumento() throws Exception {
        documento = new Documento();
        byte[] data = Files.readAllBytes(Paths.get(archivoFinal.getAbsolutePath()));
        documento.setContenidoDocumento(data);
        documento.setNombreTabla(ParticipacionSocialAmbiental.class.getSimpleName());
        documento.setIdTable(proyectosBean.getProyecto().getId());
        documento.setExtesion(".pdf");
        documento.setNombre(nombreFichero);
        documento.setMime("application/pdf");
        documentos.put("oficio", documento);
        documento.setCodigoPublico(participacionSocialAmbiental.getNumeroOficio());
        participacionSocialFacade.ingresarInformes(documentos, bandejaTareasBean.getProcessId(), bandejaTareasBean
                .getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());
    }

    public String getProponente() throws ServiceException {
        String label = proyectosBean.getProyecto().getUsuario().getPersona().getNombre();

        Organizacion organizacion = organizacionFacade.buscarPorPersona(proyectosBean.getProyecto().getUsuario()
                .getPersona(), proyectosBean.getProyecto().getUsuario().getNombre());
        if (organizacion != null) {
            return organizacion.getNombre();
        }
        return label;
    }

    public void guardar() {
        participacionSocialFacade.guardar(participacionSocialAmbiental);
        try {
			guardarDocumento();
			
//            String url = "/prevencion/participacionsocial/oficioParticipacionSocial.jsf";
//            if (!tipo.isEmpty()) {
//                url += "?tipo=" + tipo;
//               
//            }
//            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar la información.", e);
            JsfUtil.addMessageError("Error al guardar la información.");
        }
    }

    public void guardarRegresar() {
        try {
            String url = "/prevencion/participacionsocial/realizarInformeTecnicoOficio.jsf";// +
            // tipoOficio;
            if (!tipo.isEmpty()) {
                url += "?tipo=" + tipo;
            }

            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageInfo("Error al realizar la operación.");
        }
    }

    public String completarTarea() {
        try {
            // participacionSocialFacade.guardar(participacionSocialAmbiental);

            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            if (revisar) {
                boolean modificar = Boolean.parseBoolean(variables.get("existenObservaciones").toString())
                        || requiereModificaciones;
                if (observacionesController.validarObservaciones(loginBean.getUsuario(), !requiereModificaciones,
                        "oficio")) {
                    params.put("requiereMoficacion", modificar);

                    guardarDocumento();
                } else {
                    return "";
                }
            }
            
            params.put("apruebaPPS", participacionSocialAmbiental.getPronunciamientoFavorable());
            if (!participacionSocialAmbiental.getPronunciamientoFavorable())
                params.put("observacionAprobacion", !participacionSocialAmbiental.getPronunciamientoNoAprobacion());

            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                    .getProcessInstanceId(), params);

            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),
                    bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            try {
                archivoFinal.delete();
            } catch (Exception ex) {
                LOG.error(ex);
            }
            JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (Exception e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }

    public void validarTareaBpm() {

        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (archivoInforme != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(archivoInforme), "application/octet-stream");
            content.setName(nombreReporte);

        }
        return content;

    }

    public String generarCodigoOficioEmisionActualizacion(Area area) {
        try {
        	String mae=Constantes.SIGLAS_INSTITUCION + "-";
        	if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
        		mae= mae.replace(Constantes.SIGLAS_INSTITUCION + "-", "GAD-");
        	}
            return mae
                    + secuenciasFacade.getCurrentYear()
                    + "-"
                    + area.getAreaAbbreviation()
                    + "-"
                    + secuenciasFacade.getSecuenciaParaOficioAreaResponsable(area);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * Cris F: aumento de subida del informe técnico firmado
     */
    @Getter
    private boolean subido = false;
    @Getter
    @Setter
    private Documento documentoInforme;
    public void uploadListenerDocumentos(FileUploadEvent event) {
        byte[] contenidoDocumento = event.getFile().getContents();
        String nombre = event.getFile().getFileName();

        documentoInforme = new Documento();
        documentoInforme.setContenidoDocumento(contenidoDocumento);
        documentoInforme.setNombre(nombre);
        subido = true; 
    }
    
    public StreamedContent getStreamInforme(){
    	InputStream is = null;
    	
    	try {
    		byte[] informeTecnico = documentosFacade.descargarDocumentoAlfrescoQueryDocumentos(
    				"ParticipacionSocialAmbiental", proyectosBean.getProyecto().getId(), 
    				TipoDocumentoSistema.PS_INFORME_TECNICO_PPS);
    		
    		if(informeTecnico != null){
    			is = new ByteArrayInputStream(informeTecnico);
    			return new DefaultStreamedContent(is, "pdf", "InformeEvaluacionPS-" + proyectosBean.getProyecto().getCodigo() + ".pdf");
    		}else{
    			JsfUtil.addMessageError("No se logró recuperar el informe Técnico");
    			return null;
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public void abrirDialog(){
		RequestContext.getCurrentInstance().execute("PF('signDialog').show()");
    }
    
    
    public String guardarInformeTecnico(){
    	try {    		
    		if (documentoInforme != null) {
                if (subido) {

                    Documento documentoN = new Documento();
                    documentoN.setContenidoDocumento(documentoInforme.getContenidoDocumento());
                    documentoN.setNombreTabla(ParticipacionSocialAmbiental.class.getSimpleName());
                    documentoN.setIdTable(proyectosBean.getProyecto().getId());
                    documentoN.setExtesion(".pdf");
                    documentoN.setNombre(documentoInforme.getNombre());
                    documentoN.setMime("application/pdf");
                    documentoN.setCodigoPublico(participacionSocialAmbiental.getNumeroInforme());
                    Map<String, Documento> documentos = new HashMap<>();
                    documentos.put("informeTecnico", documentoN);
                    participacionSocialFacade.ingresarInformes(
                            documentos, bandejaTareasBean
                                    .getProcessId(), bandejaTareasBean.getTarea()
                                    .getTaskId(), proyectosBean.getProyecto().getCodigo());  
                    completarTarea();
//                    RequestContext.getCurrentInstance().execute("PF('signDialog').hide()");
                    JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                    
                }else{
                	JsfUtil.addMessageWarning("No ha subido el Informe Técnico Firmado");
                	return "";
                }
            }else{
            	JsfUtil.addMessageWarning("No ha subido el Informe Técnico Firmado");
            	return "";
            }
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}    	
    }
    
    public void completarTareaConFirmaInforme(){    	
    	try {
    		
    		if(firmarInforme){
    			//Cris F: aumemto de ventana para firma de documento
//                abrirDialog();     
    			completarTarea();                             
    		}else{
    			completarTarea();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    //Fin aumento de firma de informe técnico
    
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
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.PS_OFICIO_FINAL;

			tipoDocumento = TipoDocumentoSistema.PS_OFICIO_FINAL;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.PS_OFICIO_FINAL.getIdTipoDocumento());
			documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(proyectosBean.getProyecto().getId(),
					ParticipacionSocialAmbiental.class.getSimpleName(), tipoDocumento);
			
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
	 * Sube la informacion firmada En el panel
	 * 
	 * @param event
	 */
	public void cargarArchivo(FileUploadEvent event) {

		if (documentoDescargado) {
			TipoDocumento auxTipoDocumento = new TipoDocumento();
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.PS_OFICIO_FINAL;

			tipoDocumento = TipoDocumentoSistema.PS_OFICIO_FINAL;
			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.PS_OFICIO_FINAL.getIdTipoDocumento());

			byte[] contenidoDocumento = event.getFile().getContents();
			documentoInformacionManual = new Documento();
			documentoInformacionManual.setId(null);
			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
			documentoInformacionManual.setExtesion(".pdf");
			documentoInformacionManual.setMime("application/pdf");
			documentoInformacionManual.setNombre("informe_oficio_aprobacion_requisitos.pdf");

			documentoInformacionManual.setIdTable(proyectosBean.getProyecto().getId());
			documentoInformacionManual.setNombreTabla(ParticipacionSocialAmbiental.class.getSimpleName());
			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);
			participacionSocialFacade.guardar(participacionSocialAmbiental);
			

			try {
				documentosFacade.guardarDocumentoAlfrescoSinProyecto(String.valueOf(proyectosBean.getProyecto().getId()),
						String.valueOf(bandejaTareasBean.getTarea().getProcessInstanceId()),
						Long.valueOf(bandejaTareasBean.getTarea().getTaskId()), documentoInformacionManual,
						tipoDocumento);
			} catch (ServiceException | CmisAlfrescoException e) {
				// TODO Auto-generated catch block
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

			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBean.getProyecto().getId(),
					ParticipacionSocialAmbiental.class.getSimpleName(), TipoDocumentoSistema.PS_OFICIO_FINAL);

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
