package ec.gob.ambiente.prevencion.participacionsocial.bean;

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
import java.util.Collections;
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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CatalogoEvaluacionParticipacionSocial;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.EvaluacionParticipacionSocial;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.MecanismoParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RegistroMediosParticipacionSocial;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinancieraLog;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.CatalogoEvaluacionParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.EvaluacionParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.MecanismosParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.RegistroMediosParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeTecnicoPSBean implements Serializable {

    public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
    private static final long serialVersionUID = -2125107884545666417L;

    private final Logger LOG = Logger
            .getLogger(InformeTecnicoPSBean.class);
    @Getter
    @Setter
    List<RegistroMediosParticipacionSocial> listaRegistroMedios;
    @Getter
    @Setter
    List<MecanismoParticipacionSocialAmbiental> listaMecanismos;
    @EJB
    RegistroMediosParticipacionSocialFacade registroMediosParticipacionSocialFacade;
    @EJB
    MecanismosParticipacionSocialFacade mecanismosParticipacionSocialFacade;
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
    private Boolean pronunciamiento;
    @Getter
    @Setter
    private Boolean pronunciamientoNoAprobacion;
    @Getter
    @Setter
    private Boolean requiereModificaciones;
    @Getter
    @Setter
    private String documentOffice = "";
    @Getter
    @Setter
    private String url;
    /***/
    @Getter
    @Setter
    private ParticipacionSocialAmbiental participacionSocialAmbiental;
    @Getter
    @Setter
    private List<CatalogoEvaluacionParticipacionSocial> normativaDisposiciones;
    @Getter
    @Setter
    private List<CatalogoEvaluacionParticipacionSocial> cumplimientoObjetivosProceso;
    @Getter
    @Setter
    private List<EvaluacionParticipacionSocial> listaEvaluacionParticipacionSocialNormativas;
    @Getter
    @Setter
    private List<EvaluacionParticipacionSocial> listaEvaluacionParticipacionSocialCumplimiento;
    @Getter
    @Setter
    private Map<String, Object> variables;
    @EJB
    private SecuenciasFacade secuenciasFacade;
    @EJB
    private CatalogoEvaluacionParticipacionSocialFacade catalogoEvaluacionParticipacionSocialFacade;
    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
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
    private PronunciamientoFacade pronunciamientoFacade;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @EJB
    private DocumentosFacade documentosFacade;

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

    @Getter
    @Setter
    private Documento documento;
    @Getter
    @Setter
    private Map<String, Documento> documentos;

    @Getter
    @Setter
    private boolean cambiarNumeroOficio;
    
    //Cris F: aumento de ejb
    @EJB
    private TransaccionFinancieraFacade transaccionFinancieraFacade;
    
    //Fin variables
    
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
	private Documento documentoRecuperado, documentoSubido, documentoInformacionManual;

	@EJB
	private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@Getter
	@Setter
	private Boolean soloEnviar = false;  
   

    @PostConstruct
    public void init() {
        try {
            url = "/prevencion/participacionsocial/realizarInformeTecnicoOficio.jsf";


            normativaDisposiciones = catalogoEvaluacionParticipacionSocialFacade.buscarCatalogosEvaluacionPSPorGrupo("Normativas y Disposiciones");
            cumplimientoObjetivosProceso = catalogoEvaluacionParticipacionSocialFacade.buscarCatalogosEvaluacionPSPorGrupo("Cumplimiento a los Objetivos del proceso");
            listaEvaluacionParticipacionSocialNormativas = new ArrayList<>(normativaDisposiciones.size());
            listaEvaluacionParticipacionSocialCumplimiento = new ArrayList<>(cumplimientoObjetivosProceso.size());

            List<EvaluacionParticipacionSocial> listaEvaluaciones =
                    evaluacionParticipacionSocialFacade.buscarEvaluacionParticipacionSocial(proyectosBean.getProyecto().getId());
            participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proyectosBean.getProyecto());
            
            Map<Integer, EvaluacionParticipacionSocial> mapaEvaluaciones = new HashMap<>();
            for (EvaluacionParticipacionSocial evaluacion : listaEvaluaciones) {
                mapaEvaluaciones.put(evaluacion.getCatalogoEvaluacionParticipacionSocial().getId(), evaluacion);
            }

            for (CatalogoEvaluacionParticipacionSocial catEv : normativaDisposiciones) {
            	if(catEv.getId() == 4){
                	String descripcion = catEv.getDescripcion() + " " + proyectosBean.getProyecto().getNombre();
                	catEv.setDescripcion(descripcion);
                }  
            	
                if (!mapaEvaluaciones.keySet().contains(catEv.getId())) {
                    EvaluacionParticipacionSocial cat = new EvaluacionParticipacionSocial();
                    
                    cat.setCatalogoEvaluacionParticipacionSocial(catEv);
                    cat.setParticipacionSocialAmbiental(participacionSocialAmbiental);
                    mapaEvaluaciones.put(catEv.getId(), cat);
                    cat.setValor(true);
                    listaEvaluacionParticipacionSocialNormativas.add(cat);
                } else {                	
                	if(catEv.getId() == 4){
                    	String descripcion = catEv.getDescripcion() + " " + proyectosBean.getProyecto().getNombre();
                    	mapaEvaluaciones.get(catEv.getId()).getCatalogoEvaluacionParticipacionSocial().setDescripcion(descripcion);                    	
                    }  
                    listaEvaluacionParticipacionSocialNormativas.add(mapaEvaluaciones.get(catEv.getId()));
                }
            }

            for (CatalogoEvaluacionParticipacionSocial catEv : cumplimientoObjetivosProceso) {
                if (!mapaEvaluaciones.keySet().contains(catEv.getId())) {
                    EvaluacionParticipacionSocial cat = new EvaluacionParticipacionSocial();
                    cat.setCatalogoEvaluacionParticipacionSocial(catEv);
                    cat.setParticipacionSocialAmbiental(participacionSocialAmbiental);
                    cat.setValor(true);
                    mapaEvaluaciones.put(catEv.getId(), cat);
                    listaEvaluacionParticipacionSocialCumplimiento.add(cat);
                } else {
                    listaEvaluacionParticipacionSocialCumplimiento.add(mapaEvaluaciones.get(catEv.getId()));
                }
            }
            listaRegistroMedios = registroMediosParticipacionSocialFacade.consultar(participacionSocialAmbiental);
            
            variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
            
            configurarVariables(variables);  
            
        } catch (Exception e) {
            LOG.error("Error al inicializar:  ", e);
            JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
        }
        
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
            url += "?tipo=" + tipo;
            if (params.get("tipo").equals("revisar")) {
                revisar = true;

            }

            if (variables.containsKey("esPronunciamientoAprobacion")) {
                pronunciamiento = Boolean.parseBoolean(variables.get("esPronunciamientoAprobacion").toString());
            }
        }
        
        if(bandejaTareasBean.getTarea().getTaskName().equals("Revisar documentos")){
			soloEnviar = true;
		}
        
        visualizarOficio();
    }

    private String obtenerTablaEvalucion(List<EvaluacionParticipacionSocial> listaEvaluacion) {
        StringBuilder sbND = new StringBuilder();
        
        String nombreTabla = "";
        if(listaEvaluacion.get(0).getCatalogoEvaluacionParticipacionSocial().getGrupo().equals("Normativas y Disposiciones")){
        	nombreTabla = "Normativas y Disposiciones";
        }else{
        	nombreTabla = "Objetivos";
        }
        
        sbND.append("<table   border=\"1\" cellpadding=\"2\" cellspacing=\"2\"  style=\"width:650px\">");
        sbND.append("<tr style=\"text-align: center;\"><td><strong>"+ nombreTabla + "</strong></td><td></td><td><strong>Descripción</strong></td></tr>");
        for (EvaluacionParticipacionSocial evaluacionParticipacionSocial : listaEvaluacion) {
            sbND.append("<tr style=\"text-align: left;\"><td>" +

                    StringEscapeUtils.escapeHtml(evaluacionParticipacionSocial.getCatalogoEvaluacionParticipacionSocial().getDescripcion())

                    + "</td>");
            
            String eval = "";
            if(evaluacionParticipacionSocial.getValorRegistro() != null){
            	eval = evaluacionParticipacionSocial.getValorRegistro() == 1
            		? "Si" : evaluacionParticipacionSocial.getValorRegistro() == 2 ? "No" : "Parcialmente";
            }
            sbND.append("<td   style=\"width:50px;text-align: center;\">" + eval + "</td>");

            
            String descripcion = evaluacionParticipacionSocial.getDescripcion() == null ? "" : StringEscapeUtils.escapeHtml(evaluacionParticipacionSocial.getDescripcion());
            
            sbND.append("<td style=\"width:260px;text-align: left;\">" +  descripcion	+ "</td></tr>");
        }

        sbND.append("</table>");
        return sbND.toString();
    }


    public String getProponente() throws ServiceException {
        String label = proyectosBean.getProyecto().getUsuario().getPersona().getNombre();

        Organizacion organizacion = organizacionFacade.buscarPorPersona(proyectosBean.getProyecto().getUsuario().getPersona(),
                proyectosBean.getProyecto().getUsuario().getNombre());
        if (organizacion != null) {
            return organizacion.getNombre();
        }
        return label;
    }


    public void guardar() {
        try {
            participacionSocialFacade.guardar(participacionSocialAmbiental);
            evaluacionParticipacionSocialFacade.guardarLista(listaEvaluacionParticipacionSocialCumplimiento);
            evaluacionParticipacionSocialFacade.guardarLista(listaEvaluacionParticipacionSocialNormativas);
            List<RegistroMediosParticipacionSocial> lista = new ArrayList<>();
            registroMediosParticipacionSocialFacade.guardar(listaRegistroMedios, lista);
            guardarDocumento();
            
            if (revisar) {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("existenObservacionesTMP", requiereModificaciones.toString());
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);

            }
            JsfUtil.redirectTo(url);
        } catch (Exception e) {
            LOG.error("Error al guardar la información.", e);
            JsfUtil.addMessageError("Error al guardar la información.");
        }
    }


    public void guardarContinuar() {
        try {
            String urlR = "/prevencion/participacionsocial/oficioParticipacionSocial.jsf";//+ tipoOficio;
            if (!tipo.isEmpty()) {
                urlR += "?tipo=" + tipo;
            }
            if (revisar) {

                if (observacionesController.validarObservaciones(loginBean.getUsuario(), !requiereModificaciones, "informe")) {
                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                    params.put("existenObservaciones", requiereModificaciones);
                    params.put("existenObservacionesTMP", "");
                    params.put("oficioAprobacion", participacionSocialAmbiental.getPronunciamientoFavorable());
                    params.put("cumpleFacilitador", participacionSocialAmbiental.getCumplioFacilitador());
                    if (!participacionSocialAmbiental.getPronunciamientoFavorable())
                        params.put("observacionAprobacion", participacionSocialAmbiental.getPronunciamientoNoAprobacion());

                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params); /* */

                    JsfUtil.redirectTo(urlR);

                }

            } else {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("existenObservacionesInformacionProponente", !evaluacionNegativa());
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                        .getTarea().getProcessInstanceId(), params);

                participacionSocialFacade.guardar(participacionSocialAmbiental);
                evaluacionParticipacionSocialFacade.guardarLista(listaEvaluacionParticipacionSocialCumplimiento);
                evaluacionParticipacionSocialFacade.guardarLista(listaEvaluacionParticipacionSocialNormativas);


                //String tipoOficio = participacionSocialAmbiental.getPronunciamientoFavorable() ? "oficioFaborable.jsf" : "oficioNoFavorable.jsf";
                //org.apache.commons.lang.StringEscapeUtils.escapeHtml(informeTecnicoEia.getAntecendentes());
                participacionSocialFacade.guardar(participacionSocialAmbiental);
//                guardarDocumento();
                JsfUtil.redirectTo(urlR);
            }

        } catch (Exception e) {
            LOG.error("Error al guardar oficio", e);
            JsfUtil.addMessageInfo("Error al realizar la operación.");
        }

    }


    public String completarTarea() {
        try {
            //informeTecnicoEia.setPronunciamiento(pronunciamiento ? "Aprobación" : "Observación");

            participacionSocialFacade.guardar(participacionSocialAmbiental);

            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            if (revisar) {
                if (observacionesController.validarObservaciones(loginBean.getUsuario(), !requiereModificaciones, "informe")) {
                    params.put("requiereMoficacion",
                            requiereModificaciones);
                } else {
                    return "";
                }

            }
            params.put("esPronunciamientoAprobacion",
                    pronunciamiento);

            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                    .getTarea().getProcessInstanceId(), params);

            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            try {
                archivoFinal.delete();
            } catch (Exception ex) {
                LOG.error(ex);
            }
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
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    archivoInforme), "application/octet-stream");
            content.setName(nombreReporte);

        }
        return content;

    }

    public boolean evaluacionNegativa() {

        for (EvaluacionParticipacionSocial evaluacionParticipacionSocial : listaEvaluacionParticipacionSocialNormativas) {
            if (!evaluacionParticipacionSocial.getValor()) {
                return true;
            }
        }
        for (EvaluacionParticipacionSocial evaluacionParticipacionSocial : listaEvaluacionParticipacionSocialCumplimiento) {
            if (!evaluacionParticipacionSocial.getValor()) {
                return true;
            }
        }
        return false;
    }

    private void guardarDocumento() throws Exception {
    	visualizarOficio();
        documento = new Documento();
        byte[] data = Files.readAllBytes(Paths.get(archivoFinal.getAbsolutePath()));
        documento.setContenidoDocumento(data);
        documento.setNombreTabla(ParticipacionSocialAmbiental.class.getSimpleName());
        documento.setIdTable(proyectosBean.getProyecto().getId());
        documento.setExtesion(".pdf");
        documento.setNombre(nombreFichero);
        documento.setMime("application/pdf");
        documentos = new HashMap<String, Documento>();
        documentos.put("informeTecnico", documento);
        documento.setCodigoPublico(participacionSocialAmbiental.getNumeroInforme());
        participacionSocialFacade.ingresarInformes(
                 documentos, bandejaTareasBean
                        .getProcessId(), bandejaTareasBean.getTarea()
                        .getTaskId(), proyectosBean.getProyecto().getCodigo());
    }

    private void configurarVariables(Map<String, Object> variables) throws ServiceException {
        ProyectoLicenciamientoAmbiental proyecto = proyectosBean.getProyecto();
        participacionSocialAmbiental.setNombrePPS(proyecto.getNombre());

        participacionSocialAmbiental.setNombreProponente(proyecto.getUsuario().getPersona().getNombre());
        
        //Cris F: aumento de variable 
        participacionSocialAmbiental.setEntidadPublica(proyecto.getAreaResponsable().getAreaName());
        //Fin

        boolean esRevision = false;
        Map<String, String> paramsUrl = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        if (paramsUrl.containsKey("tipo") && paramsUrl.get("tipo").equals("revisar")) 
        	esRevision = true;
                
        Usuario usuarioConsultor = usuarioFacade.buscarUsuarioCompleta((String) variables.get("u_Tecnico"));
        if(usuarioConsultor == null && esRevision)
        	usuarioConsultor = usuarioFacade.buscarUsuarioWithOutFilters((String) variables.get("u_Tecnico")); //para buscar tecnico q fue eliminado cuando el informe está en revision
        
        participacionSocialAmbiental.setNombreConsultor(usuarioConsultor.getPersona().getNombre());
        
        if(usuarioConsultor != null)
        	participacionSocialAmbiental.setNombreConsultor(usuarioConsultor.getPersona().getNombre());

        //Usuario usuarioFacilitador = usuarioFacade.buscarUsuarioCompleta((String) variables.get("u_Facilitador"));

        FacilitadorProyecto facilitadorProyecto = facilitadorProyectoFacade.obtenerFacilitadorResponsable(proyecto);
        // participacionSocialAmbiental.setNombreFacilitador(usuarioFacilitador.getPersona().getNombre());
        participacionSocialAmbiental.setNombreFacilitador(facilitadorProyecto.getUsuario().getPersona().getNombre());

        participacionSocialAmbiental.setFechaInforme(JsfUtil
                .devuelveFechaEnLetrasSinHora(new Date()));


//        participacionSocialAmbiental.setFechaInicioProceso(JsfUtil
//                .devuelveFechaEnLetrasSinHora(participacionSocialAmbiental.getFechaCreacion()));
        
      List<MecanismoParticipacionSocialAmbiental> fechaInicio=  mecanismosParticipacionSocialFacade.consultarPublicaPermanente(participacionSocialAmbiental);
        if(fechaInicio != null && !fechaInicio.isEmpty()){
        	participacionSocialAmbiental.setFechaInicioProceso(JsfUtil
        		      .devuelveFechaEnLetrasSinHora(fechaInicio.get(0).getFechaInicio()));           

            participacionSocialAmbiental.setFechaCierreProceso(JsfUtil
        		                .devuelveFechaEnLetrasSinHora(fechaInicio.get(0).getFechaFin()));
        } else {        
	        //Mariela G
	        if(verificarTramiteSinInformacionPublicaPermanente()) {
	        	participacionSocialAmbiental.setFechaInicioProceso(JsfUtil
	                    .devuelveFechaEnLetrasSinHora(participacionSocialAmbiental.getFechaCreacion()));

	            participacionSocialAmbiental.setFechaCierreProceso(JsfUtil
	                    .devuelveFechaEnLetrasSinHora(new Date()));
	        }
        }      
        
        //Cris F: fecha de aceptacion facilitador
        List<TransaccionFinanciera> pagoList = transaccionFinancieraFacade.cargarTransacciones(proyecto.getId());
        for(TransaccionFinanciera transaccion : pagoList){
        	List<TransaccionFinancieraLog> listaTransaccionFinancieraLog = transaccionFinancieraFacade.buscarTransaccionFinancieraLog(transaccion.getId());
        	if(listaTransaccionFinancieraLog != null && !listaTransaccionFinancieraLog.isEmpty()){
        		
        		if(listaTransaccionFinancieraLog.get(0).getNombreTarea().equals("Validar pago de tasas de facilitador/es")){
        			participacionSocialAmbiental.setFechaAsignacionFacilitador(JsfUtil
                        .devuelveFechaEnLetrasSinHora(listaTransaccionFinancieraLog.get(0).getFechaCreacion()));
        			break;
        		}
        		else
        			participacionSocialAmbiental.setFechaAsignacionFacilitador("");
        	}
        }
        
        participacionSocialAmbiental.setFechaAceptacionFacilitador(JsfUtil
                .devuelveFechaEnLetrasSinHora(facilitadorProyecto.getFechaAceptacion()));
        try{
        	List<UbicacionesGeografica> listaUbicaciones = proyectosBean.getProyecto().getUbicacionesGeograficas();
        	
        	String ubicacion = "";
        	for(UbicacionesGeografica ubicacionGeografica : listaUbicaciones){
        		ubicacion += ubicacionGeografica.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "-" + 
        					ubicacionGeografica.getUbicacionesGeografica().getNombre() + "-" + 
        					ubicacionGeografica.getNombre() + "<br />";
        	}
        
        	participacionSocialAmbiental.setUbicacion(ubicacion);
        
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        participacionSocialAmbiental.setCodigoProyecto(proyectosBean.getProyecto().getCodigo());
        
        
        //fin

        participacionSocialAmbiental.setFechaVisitaPrevia(JsfUtil
                .devuelveFechaEnLetrasSinHora(participacionSocialAmbiental.getVisitaPreviaFechaInicio()));
        participacionSocialAmbiental.setFechaAprobacionVistaPrevia(JsfUtil
                .devuelveFechaEnLetrasSinHora(participacionSocialAmbiental.getVisitaPreviaFechaFin()));

//        listaRegistroMedios = registroMediosParticipacionSocialFacade.consultar(participacionSocialAmbiental);
        participacionSocialAmbiental.setMediosConvocatoria(configurarMediosConv(listaRegistroMedios));
        
        listaMecanismos = mecanismosParticipacionSocialFacade.consultar(participacionSocialAmbiental);
        participacionSocialAmbiental.setMecansimosPS(configurarMecanismos(listaMecanismos));
        
        
        Area areaProyecto=proyectosBean.getProyecto().getAreaResponsable(); 

//variables

        Usuario responsable = usuarioFacade.buscarUsuarioCompleta(variables.get("u_Tecnico").toString());
        if(responsable == null && esRevision)
        	responsable = usuarioFacade.buscarUsuarioWithOutFilters(variables.get("u_Tecnico").toString());
        
        if(responsable != null)
        	participacionSocialAmbiental.setElaboradoPor(responsable.getPersona().getNombre());
                
        Usuario revisa = usuarioFacade.buscarUsuarioCompleta(variables.get("u_Director").toString());
        Usuario firma = usuarioFacade.buscarUsuarioCompleta(variables.get("u_Director").toString());
        
		if (revisa == null) {
			Usuario director = null;
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			if (areaProyecto == null
					|| areaProyecto.getTipoArea().getSiglas()
							.equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
				director = areaFacade.getDirectorPlantaCentral();
				params.put("u_Director", director.getNombre());
			} else {
				if(areaProyecto.getTipoArea().getSiglas()
						.equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
					areaProyecto = areaProyecto.getArea();
				
				director = areaFacade.getDirectorProvincial(areaProyecto);
				params.put("u_Director", director.getNombre());
			}
			try {
				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(),
						bandejaTareasBean.getTarea().getProcessInstanceId(),
						params);
			} catch (Exception e) {
				LOG.error("Error al guardar oficio", e);
			}
			
			revisa = director;
			firma = director;
		}
		
        participacionSocialAmbiental.setRevisadoPor(revisa.getPersona().getNombre());
        if(responsable != null && responsable.getRolUsuarios() != null)
        	participacionSocialAmbiental.setCargoTecnico(responsable.getRolUsuarios().get(0).getRol().getNombre());        
        participacionSocialAmbiental.setAprobadoPor(firma.getPersona().getNombre());
        participacionSocialAmbiental.setCargoAutoridad(firma.getRolUsuarios().get(0).getRol().getNombre());	

        Area areaProyectoR=proyectosBean.getProyecto().getAreaResponsable(); 
                if(areaProyectoR.getTipoArea().getId()==3){ 
                	if(firma.getRolUsuarios().size()>1){
                		participacionSocialAmbiental.setCargoAutoridad(firma.getRolUsuarios().get(1).getRol().getNombre());
                	}
                	
                	participacionSocialAmbiental.setVisibleGAD("<div style=\"margin:0px; width:100%;\"><div style=\"float:left; margin:0; width:50%; border=1\">" 
                			+ "<p style=\"font-size:12.7px;font-family:sans-serif;\">Elaboración:<br/>"+participacionSocialAmbiental.getElaboradoPor()+"<br/>" 
                			+ participacionSocialAmbiental.getCargoTecnico()+"</p></div>" 
                			+ "<div style=\"float:left; padding-left: 30px; margin:0; width:40%;\">" 
                			+ "<p style=\"font-size:12.7px;font-family:sans-serif;\">Aprobación:<br/>"+participacionSocialAmbiental.getAprobadoPor() 
                			+ "<br/>"+participacionSocialAmbiental.getCargoAutoridad()+"</p></div></div>"); 
                	participacionSocialAmbiental.setVisiblePC(""); 
                }else{ 
                	participacionSocialAmbiental.setVisibleGAD(""); 
                    participacionSocialAmbiental.setVisiblePC("<div>" 
                			+ "<p>Elaborado por:&nbsp;"+participacionSocialAmbiental.getElaboradoPor()+"<br/>"+ 
        		"Revisado por:&nbsp;"+participacionSocialAmbiental.getRevisadoPor()+"<br/>"+ 
                			"Aprobado por:&nbsp;"+participacionSocialAmbiental.getAprobadoPor()+"</p></div>"); 
                } 

        if (participacionSocialAmbiental.getFechaEntregaInformeFinalPPS() != null) {
            participacionSocialAmbiental.setFechaEntregaInformeSistematizacion(JsfUtil
                    .devuelveFechaEnLetrasSinHora(participacionSocialAmbiental.getFechaEntregaInformeFinalPPS()));
        } else {
            participacionSocialAmbiental.setFechaEntregaInformeSistematizacion(JsfUtil
                    .devuelveFechaEnLetrasSinHora(new Date()));
        }
    }

    public String configurarMediosConv(List<RegistroMediosParticipacionSocial> listaRegistroMedios) {

        StringBuilder sbND = new StringBuilder();
        sbND.append("<table   border=\"1\" cellpadding=\"2\" cellspacing=\"2\"  style=\"width:650px\">");
        //sbND.append("<tr style=\"text-align: left;\"><td>Medios de Convocatoria</td><td>Medios Utilizados - Lugares</td><td>Fecha</td></tr>");
        sbND.append("<tr style=\"text-align: left;width: 20px;\"><td>Medios de Convocatoria</td><td style=\"width: 200px;\">Fecha(Periodo)</td><td style=\"width: 200px;\">Evaluación</td></tr>");

        for (RegistroMediosParticipacionSocial medio : listaRegistroMedios) {
            sbND.append("<tr style=\"text-align: left;\"><td>" + StringEscapeUtils.escapeHtml(medio.getCatalogoMedio().getNombreMedio()) + "</td>");

//            String lugar = medio != null && medio.getLugar() != null ? StringEscapeUtils.escapeHtml(medio.getLugar()) : "-";
            String fecha = "";
            fecha += medio != null && medio.getFechaInicio() != null ? JsfUtil.devuelveFechaEnLetrasSinHora(medio.getFechaInicio()) + " - " : "-";
            fecha += medio != null && medio.getFechaFin() != null ? JsfUtil.devuelveFechaEnLetrasSinHora(medio.getFechaFin()) : "-";
            
            String evaluacion = medio != null && medio.getEvaluacion() != null ? medio.getEvaluacion() : "";
            
//            sbND.append("<td   style=\"width:50px;text-align: center;\">" + lugar + "</td>");
            sbND.append("<td   style=\"width:50px;text-align: center;\">" + fecha + "</td>");
            sbND.append("<td   style=\"width:50px;text-align: left;\">" + evaluacion + "</td></tr>");            
        }

        sbND.append("</table>");
        return sbND.toString();
    }

    public String configurarMecanismos(List<MecanismoParticipacionSocialAmbiental> listaMecanismos) {

        StringBuilder sbND = new StringBuilder();//style="width: 200px;"
        sbND.append("<table   border=\"1\" cellpadding=\"2\" cellspacing=\"2\"  style=\"width:650px\">");
        sbND.append("<tr style=\"text-align: left;width: 20px;\"><td>Mecanismo</td><td style=\"width: 200px;\">Lugar</td><td style=\"width: 200px;\">Fecha</td></tr>");

        for (MecanismoParticipacionSocialAmbiental medio : listaMecanismos) {
            sbND.append("<tr style=\"text-align: left;\"><td>" + StringEscapeUtils.escapeHtml(medio.getCatalogoMedio().getNombreMedio()) + "</td>");


            sbND.append("<td   style=\"width:50px;text-align: center;\">" + StringEscapeUtils.escapeHtml(medio.getLugar()) + "</td>");
            sbND.append("<td   style=\"width:50px;text-align: center;\">" + JsfUtil.devuelveFechaEnLetrasSinHora(medio.getFechaFin()) + "</td></tr>");
            if (medio.getCatalogoMedio().getId() == 18) {
                participacionSocialAmbiental.setFechaAsambleaPublica(JsfUtil.devuelveFechaEnLetrasSinHora(medio.getFechaFin()));
            }
        }

        sbND.append("</table>");
        return sbND.toString();
    }
    
    public String visualizarOficio() {
        String pathPdf = null;
        try {
        	String numeroInforme="";
        	if(participacionSocialAmbiental.getNumeroInforme()!=null && !participacionSocialAmbiental.getNumeroInforme().isEmpty()){
        		numeroInforme=participacionSocialAmbiental.getNumeroInforme();
        		if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
                	numeroInforme= numeroInforme.replaceAll("-MAATE", "");
                }
        	}else {
        		 String area = proyectosBean.getProyecto().getAreaResponsable().getAreaAbbreviation();
                 String numeroSecuencia = secuenciasFacade.getSecuenciaParaInformeTecnicoAreaResponsable(proyectosBean.getProyecto().getAreaResponsable());
                 //000-2014-PS-DNPCA-MAE
                 String nombreMae="-MAATE";
                 if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
                 	nombreMae="";
                 }
                 numeroInforme = numeroSecuencia
                         + "-"
                         + JsfUtil.getCurrentYear()
                         + "-PS-"
                         + area + nombreMae;
                 participacionSocialAmbiental.setNumeroInforme(numeroInforme);
			}
        	
        	
        	
            String observacionGeneralfinal = participacionSocialAmbiental.getObservacionReporfinal() != null ? participacionSocialAmbiental.getObservacionReporfinal() : "";
            String observacionGeneralfinalTmp = observacionGeneralfinal;

            String otrasOb = participacionSocialAmbiental.getObservacionGeneralfinal() != null ?
                    "<p><strong>Análisis técnico / Observaciones" + "</strong></p><br/>" + participacionSocialAmbiental.getObservacionGeneralfinal() + "<br/>" : "";
            participacionSocialAmbiental.setObservacionReporfinal(

                    otrasOb + "<p><strong>Observaciones relevantes de la Comunidad:</strong></p>" + observacionGeneralfinal + "<br />"
            );

            String conclusiones = participacionSocialAmbiental.getConclusion() != null ? participacionSocialAmbiental.getConclusion() : "";
            String conclusionestmp = conclusiones;
            String tipo = proyectosBean.getProyecto().getTipoEstudio().getNombre();
            String nombreAreaResponsable=", el/la "+ proyectosBean.getProyecto().getAreaResponsable().getAreaName();
            if(proyectosBean.getProyecto().isAreaResponsableEnteAcreditado()){
            	nombreAreaResponsable=",el "+proyectosBean.getProyecto().getAreaResponsable().getAreaName();
            }
            
            conclusiones += "<br/>Una vez concluido el análisis y la evaluación técnica del Proceso de " +
                    "Participación Social del Borrador del proyecto de "
                    + tipo +nombreAreaResponsable+
                    " determina que:";
            String determinacion = participacionSocialAmbiental.getDeterminacionDnpca() != null ? participacionSocialAmbiental.getDeterminacionDnpca() : "";
            conclusiones += determinacion + "<br/>";
            participacionSocialAmbiental.setConclusion(conclusiones);

            participacionSocialAmbiental.setEvaluacionConformidad(obtenerTablaEvalucion(listaEvaluacionParticipacionSocialNormativas));
            participacionSocialAmbiental.setCumplimientoObjetivos(obtenerTablaEvalucion(listaEvaluacionParticipacionSocialCumplimiento));

            nombreFichero = "InformeEvaluacionPS" + proyectosBean.getProyecto().getCodigo() + ".pdf";
            nombreReporte = "InformeEvaluacionPS.pdf";
            plantillaReporte = participacionSocialFacade.obtenerPlantillaReporte(TipoDocumentoSistema.PS_INFORME_EVALUACION.getIdTipoDocumento());
            
            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(), nombreReporte, true,
                    participacionSocialAmbiental);
            setOficioPdf(informePdf);

            Path path = Paths.get(getOficioPdf().getAbsolutePath());
            archivoInforme = Files.readAllBytes(path);
            archivoFinal = new File(
                    JsfUtil.devolverPathReportesHtml(nombreFichero));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(archivoInforme);
            file.close();
            setInformePath(JsfUtil.devolverContexto("/reportesHtml/"
                    + nombreFichero));

            pathPdf = informePdf.getParent();
            if (cambiarNumeroOficio) {
                if (cambiarNumeroOficio) {
                    Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                    params.put("cambiarNumeroOficio", false);
                    procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                            .getTarea().getProcessInstanceId(), params);
                }
            }
            
            participacionSocialAmbiental.setConclusion(conclusionestmp);
            participacionSocialAmbiental.setObservacionReporfinal(observacionGeneralfinalTmp);
        } catch (Exception e) {
            LOG.error("Error al visualizar el oficio", e);
            JsfUtil.addMessageError("Error al visualizar el oficio");
        }

        return pathPdf;
    }
    
    public Boolean verificarTramiteSinInformacionPublicaPermanente() {
    	//proyectos anteriores que no tiene informacion de Consulta Publica fecha.bloqueo.proyectos.fisico
    	Boolean llenarInformacionAnterior = false;
        try {
	        List<TaskSummary> tareasFlujo = procesoFacade.getTaskBySelectFlow(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
	        if(tareasFlujo != null && !tareasFlujo.isEmpty()) {
	        	 Date fechaInicioCambio=new SimpleDateFormat("yyyy-MM-dd").parse(Constantes.getFechaObligatorioMecanismosPps());
	        	 DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	        	    
	        	List<Tarea> tareas = new ArrayList<Tarea>();
	        	
	        	for (TaskSummary tareaSummary : tareasFlujo) {
	    			Tarea tarea = new Tarea();
	    			ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(
	    					tareaSummary, tarea);
	    			if (!tareas.contains(tarea))
	    				tareas.add(tarea);
	    		}
	        	
	        	Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());
	        	
	        	for (Tarea tarea : tareas) {
	        		if(tarea.getEstado().equals("Completada")) {
		        		Date fechaFin = formatter.parse(formatter.format(tarea.getFechaFin()));
		        		if (tarea.getNombre().equals("Aclarar, corregir o completar informe de sistematizacion") 
								&& fechaFin.compareTo(fechaInicioCambio) <= 0) {
		        			llenarInformacionAnterior = true;
		        			break;
		        		}
		        		
		        		if (tarea.getNombre().equals("Completar el informe final del PPS") 
								&&  fechaFin.compareTo(fechaInicioCambio) <= 0) {
		        			llenarInformacionAnterior = true;
		        			break;
		        		}
	        		}
	        	}
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        return llenarInformacionAnterior;
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
   			TipoDocumento auxTipoDocumento = new TipoDocumento();
   			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.PS_INFORME_TECNICO_PPS;

   			tipoDocumento = TipoDocumentoSistema.PS_INFORME_TECNICO_PPS;
   			Integer numeroInformeStr = proyectosBean.getProyecto().getId();

   			// Convertir la cadena a un entero

   			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.PS_INFORME_TECNICO_PPS.getIdTipoDocumento());
   			documentoSubido = documentosFacade.documentoXTablaIdXIdDocUnico(numeroInformeStr,
   					ParticipacionSocialAmbiental.class.getSimpleName(), tipoDocumento);

   			System.out.println("proyecto=====>   ");
   			if (documentoSubido.getIdAlfresco() != null) {
   				String documento = documentosFacade.direccionDescarga(documentoSubido);
   				DigitalSign firmaE = new DigitalSign();
   				documentoDescargado = true;
   				return firmaE.sign(documento, loginBean.getUsuario().getNombre());

   			} else
   				return "";
   		} catch (Throwable e) {
   			JsfUtil.addMessageError("Error al realizar la operación." + e);
   			System.out.println("ERROR===>    " + e);
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
   			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.PS_INFORME_TECNICO_PPS;

   			tipoDocumento = TipoDocumentoSistema.PS_INFORME_TECNICO_PPS;
   			auxTipoDocumento = obtenerTipoDocumento(TipoDocumentoSistema.PS_INFORME_TECNICO_PPS.getIdTipoDocumento());

   			byte[] contenidoDocumento = event.getFile().getContents();
   			documentoInformacionManual = new Documento();
   			documentoInformacionManual.setId(null);
   			documentoInformacionManual.setContenidoDocumento(contenidoDocumento);
   			documentoInformacionManual.setExtesion(".pdf");
   			documentoInformacionManual.setMime("application/pdf");
   			// documentoInformacionManual.setNombre("informeTecnico.pdf");
   			documentoInformacionManual
   					.setNombre("InformeEvaluacionPS" + proyectosBean.getProyecto().getCodigo() + ".pdf");

   			documentoInformacionManual.setIdTable(proyectosBean.getProyecto().getId());
   			documentoInformacionManual.setNombreTabla(ParticipacionSocialAmbiental.class.getSimpleName());
   			documentoInformacionManual.setTipoDocumento(auxTipoDocumento);
   			participacionSocialFacade.guardar(participacionSocialAmbiental);

   			try {
   				documentosFacade.guardarDocumentoAlfrescoSinProyecto(
   						String.valueOf(proyectosBean.getProyecto().getId()),
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

   			auxdoc = documentosFacade.documentoXTablaIdXIdDocUnicos(proyectosBean.getProyecto().getId(),
   					ParticipacionSocialAmbiental.class.getSimpleName(), TipoDocumentoSistema.PS_INFORME_TECNICO_PPS);

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

   	public Documento buscarDocumentoAnterior() {

   		List<Documento> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
   				ParticipacionSocialAmbiental.class.getSimpleName(), TipoDocumentoSistema.PS_INFORME_TECNICO_PPS);
   		try {
   			documentoRecuperado=listaDocumentos.get(0);
   			obtenerPath();
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return listaDocumentos.get(0);
   	}

   	public void obtenerPath() throws Exception {
   		try {
   			byte[] oficioDoc = documentosFacade.descargar(documentoRecuperado.getIdAlfresco());
   			nombreReporte = documentoRecuperado.getNombre();
   			String reporteHtmlfinal = nombreReporte.replace("/", "-");
   			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
   			FileOutputStream file = new FileOutputStream(archivoFinal);
   			file.write(oficioDoc);
   			file.close();
   			informePath = archivoFinal.getAbsolutePath();
   			informePath = (JsfUtil.devolverContexto("/reportesHtml/" + nombreReporte));
   		} catch (Exception e) {
   			e.printStackTrace();
   			LOG.error("Error al cargar el informe tecnico", e);
   			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
   		}

   	}
}

