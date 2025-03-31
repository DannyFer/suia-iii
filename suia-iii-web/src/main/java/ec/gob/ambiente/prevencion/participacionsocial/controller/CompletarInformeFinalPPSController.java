package ec.gob.ambiente.prevencion.participacionsocial.controller;

import ec.gob.ambiente.control.denuncias.bean.DenunciaBean;
import ec.gob.ambiente.control.denuncias.controllers.DenunciaController;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.*;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
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
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.*;

@ViewScoped
@ManagedBean
public class CompletarInformeFinalPPSController implements Serializable {

    private static final Logger LOGGER = Logger
            .getLogger(CompletarInformeFinalPPSController.class);
    private static final long serialVersionUID = 8464688603365819500L;

    @EJB
    private CatalogoMediosParticipacionSocialFacade catalogoMediosParticipacionSocialFacade;

    @EJB
    private RegistroMediosParticipacionSocialFacade registroMediosParticipacionSocialFacade;

    @EJB
    private MecanismosParticipacionSocialFacade mecanismosParticipacionSocialFacade;

    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    @EJB
    private PreguntasFacilitadoresAmbientalesFacade preguntasFacilitadoresAmbientalesFacade;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private DocumentosFacade documentosFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{denunciaBean}")
    private DenunciaBean denunciaBean;
    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{denunciaController}")
    private DenunciaController denunciaController;

    @Getter
    @Setter
    private List<CatalogoMediosParticipacionSocial> listaMedios;
    @Getter
    @Setter
    private List<CatalogoMediosParticipacionSocial> listaMecanismosCatalogo;
    @Getter
    @Setter
    private List<RegistroMediosParticipacionSocial> listaRegistroMedios;
    @Getter
    @Setter
    private List<RegistroMediosParticipacionSocial> listaRegistroMediosEliminados;
    @Getter
    @Setter
    private List<MecanismoParticipacionSocialAmbiental> listaMecanismos;
    @Getter
    @Setter
    private List<MecanismoParticipacionSocialAmbiental> listaMecanismosEliminados;
    @Getter
    @Setter
    private List<PreguntasFacilitadoresAmbientales> listaPreguntas;
    @Getter
    @Setter
    private List<PreguntasFacilitadoresAmbientales> listaPreguntasEliminados;

    @Getter
    @Setter
    private RegistroMediosParticipacionSocial registroMedios;
    @Getter
    @Setter
    private MecanismoParticipacionSocialAmbiental mecanismo;
    @Getter
    @Setter
    private ParticipacionSocialAmbiental participacionSocialAmbiental;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesController}")
    private ObservacionesController observacionesController;

    @Getter
    @Setter
    private Documento documento;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proy;

    @Getter
    @Setter
    private Documento documentoMedios;

    @Getter
    @Setter
    private Documento documentoMecanismo;

    @Getter
    @Setter
    private Documento informeFinal;

    @Getter
    @Setter
    private Documento actaApertura;

    @Getter
    @Setter
    private Documento registroCIP;

    @Getter
    @Setter
    private Documento actaAsamblea;

    @Getter
    @Setter
    private Documento registroAsistencia;

    @Getter
    @Setter
    private Documento invitacionPersonal;

    @Getter
    @Setter
    private Documento otro;

    @Getter
    @Setter
    private boolean revisar;
    @Getter
    @Setter
    private boolean accionesComplementarias;

    @Getter
    @Setter
    private Boolean informacionCompleta;
    @Getter
    @Setter
    private Boolean requiereInformacionPromotor;

    @Getter
    @Setter
    private String tipo = "";


    private Boolean isEditing;


    @Getter
    @Setter
    private Map<String, Documento> documentos;

    @Getter
    @Setter
    private List<String> listaClaves;

    @Getter
    @Setter
    private Date fechaOficio;


    @PostConstruct
    public void init() throws CmisAlfrescoException {
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
            if (params.get("tipo").equals("revisar") || params.get("tipo").equals("revisarDatos")) {
                revisar = true;
            }
        }

        Object oficioAprobacion = bandejaTareasBean.getTarea().getVariable("oficioAprobacion");
        if (oficioAprobacion != null && !Boolean.parseBoolean(oficioAprobacion.toString())) {
            Object observacionAprobacion = bandejaTareasBean.getTarea().getVariable("observacionAprobacion");
            if (observacionAprobacion != null) {
                accionesComplementarias = Boolean.parseBoolean(observacionAprobacion.toString());
            }
        }


        this.isEditing = false;
        this.listaRegistroMediosEliminados = new ArrayList<RegistroMediosParticipacionSocial>();
        this.listaMecanismosEliminados = new ArrayList<MecanismoParticipacionSocialAmbiental>();
        //participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proyectosBean.getProyecto());

        proy = proyectosBean.getProyecto();
        this.cargarDatos();
        listaMedios = catalogoMediosParticipacionSocialFacade.buscarCatalogoMediosParticipacionSocialPorId(1);
        listaMecanismosCatalogo = catalogoMediosParticipacionSocialFacade.buscarCatalogoMediosParticipacionSocialPorId(4);


        documentos = new HashMap<>();
        if (accionesComplementarias) {


            List<Documento> documentosOficio = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                    (ParticipacionSocialAmbiental.class.getSimpleName()), TipoDocumentoSistema.PS_OFICIO_FINAL);
            if (documentosOficio.size() > 0) {
              Documento  documentoOf = documentosOficio.get(0);
                fechaOficio = documentoOf.getFechaCreacion();
            } else {
                throw new RuntimeException("No se encontró el documento");
            }
            listaClaves = new ArrayList<>(1);
            listaClaves.add("convocatoriaPublica");
            listaClaves.add("compMecanismos");
            listaClaves.add("compInformeFinal");
            listaClaves.add("actaAperturaCierre");
            listaClaves.add("registroCip");
            listaClaves.add("actaAsamblea");
            listaClaves.add("registroAsistencia");
            listaClaves.add("invitacionesPersonales");
            listaClaves.add("compOtro");


            try {
                documentos = participacionSocialFacade.recuperarDocumentosTipo(proy.getId(), "comp" + ParticipacionSocialFacade.class.getSimpleName(), listaClaves);
            } catch (Exception e) {
                LOGGER.error(JsfUtil.ERROR_INICIALIZAR_DATOS, e);
                JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
            }

        }

    }

    public void cargarDatos() throws CmisAlfrescoException {
        listaRegistroMedios = new ArrayList<RegistroMediosParticipacionSocial>();
        listaMecanismos = new ArrayList<MecanismoParticipacionSocialAmbiental>();
        participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proy);
        listaRegistroMedios = registroMediosParticipacionSocialFacade.consultar(participacionSocialAmbiental);
        listaMecanismos = mecanismosParticipacionSocialFacade.consultar(participacionSocialAmbiental);

        this.cargardocumentos();


        try {
            listaPreguntasEliminados = new ArrayList<>();
            listaPreguntas = preguntasFacilitadoresAmbientalesFacade.obtenerPreguntasPorParticipacion(participacionSocialAmbiental.getId());
        } catch (ServiceException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }


    }

    public void crearRegistro() {
        this.isEditing = false;
        this.registroMedios = new RegistroMediosParticipacionSocial();
        this.registroMedios.setCatalogoMedio(new CatalogoMediosParticipacionSocial());
        this.registroMedios.setParticipacionSocialAmbiental(this.participacionSocialAmbiental);
    }

    public void crearMecanismo() {
        this.isEditing = false;
        this.mecanismo = new MecanismoParticipacionSocialAmbiental();
        this.mecanismo.setCatalogoMedio(new CatalogoMediosParticipacionSocial());
        this.mecanismo.setParticipacionSocialAmbiental(this.participacionSocialAmbiental);
        this.mecanismo.setFechaCreacion(new Date());
        this.denunciaBean.setProvincia(null);
        this.denunciaBean.setCanton(null);
        this.denunciaBean.setParroquia(null);
    }


    public void agregarRegistro() {

        if (!this.isEditing) {
            registroMedios.setFechaCreacion(new Date());
            this.listaRegistroMedios.add(this.registroMedios);
        }
        RequestContext.getCurrentInstance().execute(
                "PF('dlg1').hide();");
    }

    public void crearPregunta() {
        PreguntasFacilitadoresAmbientales preguntasFacilitadoresAmbientales = new PreguntasFacilitadoresAmbientales();
        preguntasFacilitadoresAmbientales.setParticipacionSocialAmbiental(participacionSocialAmbiental);
        listaPreguntas.add(preguntasFacilitadoresAmbientales);

    }

    public void editarRegistro(RegistroMediosParticipacionSocial registro) {
        this.isEditing = true;
        this.registroMedios = registro;

    }

    public void eliminarRegistro(RegistroMediosParticipacionSocial registro) {
        this.isEditing = false;
        this.listaRegistroMedios.remove(registro);
        this.listaRegistroMediosEliminados.add(registro);
    }

    public void editarMecanismo(MecanismoParticipacionSocialAmbiental mecanismo) {
        this.isEditing = true;
        this.mecanismo = mecanismo;

        this.denunciaBean.setParroquia(this.mecanismo.getUbicacionesGeografica());
        this.denunciaBean.setCanton(this.mecanismo.getUbicacionesGeografica().getUbicacionesGeografica());
        this.denunciaBean.setProvincia(this.mecanismo.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica());

        this.denunciaController.cargarCantones();
        this.denunciaController.cargarParroquias();
    }

    public void eliminarMecanismo(MecanismoParticipacionSocialAmbiental mecanismo) {
        this.isEditing = false;
        this.listaMecanismos.remove(mecanismo);
        this.listaMecanismosEliminados.add(mecanismo);
    }


    public void eliminarPregunta(PreguntasFacilitadoresAmbientales preguntasFacilitadoresAmbientales) {
        this.isEditing = false;
        preguntasFacilitadoresAmbientales.setEstado(false);
        this.listaPreguntas.remove(preguntasFacilitadoresAmbientales);
        listaPreguntasEliminados.add(preguntasFacilitadoresAmbientales);
    }

    public void agregarMecanismo() {

        this.mecanismo.setUbicacionesGeografica(this.denunciaBean.getParroquia());
        if (!this.isEditing) {

            this.listaMecanismos.add(this.mecanismo);
        }

        RequestContext.getCurrentInstance().execute(
                "PF('dlg2').hide();");

    }

    public void uploadListenerDocumentosMedios(FileUploadEvent event) {
        this.documentoMedios = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        this.documentoMedios.setEditar(true);
    }

    public void uploadListenerDocumentosMecanismos(FileUploadEvent event) {
        this.documentoMecanismo = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        this.documentoMecanismo.setEditar(true);
    }

    public void uploadListenerDocumentosInformeFinal(FileUploadEvent event) {
        this.informeFinal = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        this.informeFinal.setEditar(true);
    }

    public void uploadListenerDocumentosActaApertura(FileUploadEvent event) {
        this.actaApertura = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        actaApertura.setEditar(true);
    }

    public void uploadListenerDocumentosRegistroCIP(FileUploadEvent event) {
        this.registroCIP = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        registroCIP.setEditar(true);
    }

    public void uploadListenerDocumentosActaAsamblea(FileUploadEvent event) {
        this.actaAsamblea = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        actaAsamblea.setEditar(true);
    }

    public void uploadListenerDocumentosRegistroAsistencia(FileUploadEvent event) {
        this.registroAsistencia = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        registroAsistencia.setEditar(true);
    }

    public void uploadListenerDocumentosInvitacionPersonal(FileUploadEvent event) {
        this.invitacionPersonal = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        invitacionPersonal.setEditar(true);
    }

    public void uploadListenerDocumentosOtro(FileUploadEvent event) {
        this.otro = this.uploadListener(event, ParticipacionSocialAmbiental.class);
        this.otro.setEditar(true);
    }

    private Documento uploadListener(FileUploadEvent event, Class<?> clazz) {
        return uploadListener(event, clazz, "");
    }

    private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String prefijo) {

        String nombre = event.getFile().getFileName();
        int dot = nombre.lastIndexOf('.');
        String extension = (dot == -1) ? "" : nombre.substring(dot + 1);

        byte[] contenidoDocumento = event.getFile().getContents();
        String mime = event.getFile().getContentType();
        Documento documento = crearDocumento(contenidoDocumento, prefijo + clazz.getSimpleName(), extension, mime);
        documento.setNombre(event.getFile().getFileName());
        documento.setEditar(true);
        documento.setIdTable(proy.getId());
        return documento;
    }


    public Documento crearDocumento(byte[] contenidoDocumento, String clazz, String extension, String mime) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(clazz);
        documento.setIdTable(0);
        documento.setExtesion("." + extension);
        documento.setMime(mime);
        return documento;
    }

    /**
     * Descarga documento desde el Alfresco
     *
     * @param documento
     * @return
     * @throws CmisAlfrescoException
     */
    public Documento descargarAlfresco(Documento documento)
            throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null && documento.getContenidoDocumento() == null) {
            documentoContenido = documentosFacade
                    .descargar(documento.getIdAlfresco());
        }
        if (documentoContenido != null) {
            documento.setContenidoDocumento(documentoContenido);
        }
        return documento;
    }


    public StreamedContent getStreamContent(String tipo) throws Exception {
        DefaultStreamedContent content = null;

        Documento doc = null;
        try {
            if (tipo.equalsIgnoreCase("Medios")) {
                this.descargarAlfresco(this.documentoMedios);
                doc = this.documentoMedios;
            }
            if (tipo.equalsIgnoreCase("Mecanismos")) {
                this.descargarAlfresco(this.documentoMecanismo);
                doc = this.documentoMecanismo;
            }
            if (tipo.equalsIgnoreCase("InformeFinal")) {
                this.descargarAlfresco(this.informeFinal);
                doc = this.informeFinal;
            }
            if (tipo.equalsIgnoreCase("ActaApertura")) {
                this.descargarAlfresco(this.actaApertura);
                doc = this.actaApertura;
            }
            if (tipo.equalsIgnoreCase("RegistroCIP")) {
                this.descargarAlfresco(this.registroCIP);
                doc = this.registroCIP;
            }
            if (tipo.equalsIgnoreCase("ActaAsamblea")) {
                this.descargarAlfresco(this.actaAsamblea);
                doc = this.actaAsamblea;
            }
            if (tipo.equalsIgnoreCase("RegistroAsistencia")) {
                this.descargarAlfresco(this.registroAsistencia);
                doc = this.registroAsistencia;
            }
            if (tipo.equalsIgnoreCase("InvitacionPersonal")) {
                this.descargarAlfresco(this.invitacionPersonal);
                doc = this.invitacionPersonal;
            }
            if (tipo.equalsIgnoreCase("Otro")) {
                this.descargarAlfresco(this.otro);
                doc = this.otro;
            }


            if (doc != null && doc.getNombre() != null
                    && doc.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(
                        doc.getContenidoDocumento()));
                content.setName(doc.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }


    public StreamedContent getStreamContentComplementario(String documentoActivo) throws Exception {
        DefaultStreamedContent content = null;
        Documento documento = this.descargarAlfresco(documentos.get(documentoActivo));
        try {
            if (documento != null && documento.getNombre() != null
                    && documento.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
                content.setName(documento.getNombre());
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
            }
        } catch (Exception exception) {
            LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }


    public void salvarDocumentos() {
        try {

            if (this.documentoMedios != null && this.documentoMedios.getContenidoDocumento() != null && this.documentoMedios.isEditar()) {

                this.documentoMedios.setIdTable(participacionSocialAmbiental.getId());
                this.documentoMedios.setDescripcion("Medios de convocatoria");
                this.documentoMedios.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.documentoMedios
                        , TipoDocumentoSistema.PS_CONVOCATORIA_PÚBLICA, null);

            }
            if (this.documentoMecanismo != null && this.documentoMecanismo.getContenidoDocumento() != null && this.documentoMecanismo.isEditar()) {
                this.documentoMecanismo.setIdTable(participacionSocialAmbiental.getId());
                this.documentoMecanismo.setDescripcion("Medios de convocatoria");
                this.documentoMecanismo.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.documentoMecanismo
                        , TipoDocumentoSistema.PS_MECANISMO_PPS, null);

            }
            if (this.informeFinal != null && this.informeFinal.getContenidoDocumento() != null && this.informeFinal.isEditar()) {

                this.informeFinal.setIdTable(participacionSocialAmbiental.getId());
                this.informeFinal.setDescripcion("Informe final");
                this.informeFinal.setEstado(true);
              //Cris F: se aumento bandejaTareasBean.getProcessId() para que aparezca el documento en la vista de documentos en el listado de procesos
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, bandejaTareasBean.getProcessId(), this.informeFinal
                        , TipoDocumentoSistema.PS_INFORME_SISTEMATIZACION, null); 

            }
            if (this.actaApertura != null && this.actaApertura.getContenidoDocumento() != null && this.actaApertura.isEditar()) {

                this.actaApertura.setIdTable(participacionSocialAmbiental.getId());
                this.actaApertura.setDescripcion("Acta apertura");
                this.actaApertura.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.actaApertura
                        , TipoDocumentoSistema.PS_ACTA_APERTURA_Y_CIERRE_CIP, null);

            }
            if (this.registroCIP != null && this.registroCIP.getContenidoDocumento() != null && this.registroCIP.isEditar()) {

                this.registroCIP.setIdTable(participacionSocialAmbiental.getId());
                this.registroCIP.setDescripcion("Registro CIP");
                this.registroCIP.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.registroCIP
                        , TipoDocumentoSistema.PS_REGISTRO_CIP, null);

            }
            if (this.actaAsamblea != null && this.actaAsamblea.getContenidoDocumento() != null && this.actaAsamblea.isEditar()) {

                this.actaAsamblea.setIdTable(participacionSocialAmbiental.getId());
                this.actaAsamblea.setDescripcion("Acta asamblea");
                this.actaAsamblea.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.actaAsamblea
                        , TipoDocumentoSistema.PS_ACTA_DE_LA_ASAMBLEA, null);

            }
            if (this.registroAsistencia != null && this.registroAsistencia.getContenidoDocumento() != null && this.registroAsistencia.isEditar()) {

                this.registroAsistencia.setIdTable(participacionSocialAmbiental.getId());
                this.registroAsistencia.setDescripcion("Registro Asistencia");
                this.registroAsistencia.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.registroAsistencia
                        , TipoDocumentoSistema.PS_REGISTRO_DE_ASISTENCIA, null);

            }
            if (this.invitacionPersonal != null && this.invitacionPersonal.getContenidoDocumento() != null && this.invitacionPersonal.isEditar()) {

                this.invitacionPersonal.setIdTable(participacionSocialAmbiental.getId());
                this.invitacionPersonal.setDescripcion("Invitacion personal");
                this.invitacionPersonal.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.invitacionPersonal
                        , TipoDocumentoSistema.PS_INVITACIONES_PERSONALES, null);

            }

            if (this.otro != null && this.otro.getContenidoDocumento() != null && this.otro.isEditar()) {

                this.otro.setIdTable(participacionSocialAmbiental.getId());
                this.otro.setDescripcion("Otros");
                this.otro.setEstado(true);
                documentosFacade.guardarDocumentoAlfrescoSinProyecto(this.proy.getCodigo(), Constantes.CARPETA_PARTICIPACION_SOCIAL, 0L, this.otro
                        , TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS, null);

            }

        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    public void guardar() throws CmisAlfrescoException {
        try {
        	        	
        	 if (!validarInformacion()) {        		 
                 JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
             }else{        	 
	            this.registroMediosParticipacionSocialFacade.guardar(this.listaRegistroMedios, this.listaRegistroMediosEliminados);
	            this.mecanismosParticipacionSocialFacade.guardar(this.listaMecanismos, this.listaMecanismosEliminados);
	            this.salvarDocumentos();
	            participacionSocialAmbiental.setFechaEntregaInformeFinalPPS(new Date());
	            participacionSocialFacade.guardar(participacionSocialAmbiental);
	
	            participacionSocialFacade.ingresarInformes(documentos, bandejaTareasBean.getProcessId(),
	                    bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());
	
	            guardarPreguntas();
	            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	            this.cargarDatos();
             }
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    public void guardarPreguntas() {
        List<PreguntasFacilitadoresAmbientales> preguntasGuardar = new ArrayList<>();
        preguntasGuardar.addAll(listaPreguntas);
        preguntasGuardar.addAll(listaPreguntasEliminados);
        try {
            preguntasFacilitadoresAmbientalesFacade.guardarPreguntasPorParticipacion(preguntasGuardar);
        } catch (ServiceException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    private void cargardocumentos() throws CmisAlfrescoException {

        List<Documento> documentosXEIA = documentosFacade.documentoXTablaId(this.participacionSocialAmbiental.getId(),
                "ParticipacionSocialAmbiental");

        if (documentosXEIA.size() > 0) {

            for (Documento doc : documentosXEIA) {

                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_CONVOCATORIA_PÚBLICA.getIdTipoDocumento())) {
                    this.documentoMedios = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_MECANISMO_PPS.getIdTipoDocumento())) {
                    this.documentoMecanismo = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_INFORME_SISTEMATIZACION.getIdTipoDocumento())) {
                    this.informeFinal = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_ACTA_APERTURA_Y_CIERRE_CIP.getIdTipoDocumento())) {
                    this.actaApertura = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_REGISTRO_CIP.getIdTipoDocumento())) {
                    this.registroCIP = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_ACTA_DE_LA_ASAMBLEA.getIdTipoDocumento())) {
                    this.actaAsamblea = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_REGISTRO_DE_ASISTENCIA.getIdTipoDocumento())) {
                    this.registroAsistencia = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.PS_INVITACIONES_PERSONALES.getIdTipoDocumento())) {
                    this.invitacionPersonal = doc;
                }
                if (doc.getTipoDocumento().getId().equals(TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS.getIdTipoDocumento())) {
                    this.otro = doc;
                }

            }
        }
    }

//    private void cargarDocumentosComplementarios() {
//
//    }

    public boolean validarInformacion() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        StringBuilder stringBuilder3 = new StringBuilder();
        if (listaMecanismos.isEmpty()) {
            stringBuilder.append("highlightComponent('formMecanismos:tblMecanismosPPS');");

        } else {
            stringBuilder2.append("removeHighLightComponent('formMecanismos:tblMecanismosPPS');");
        }

        if (listaRegistroMedios.isEmpty()) {
            stringBuilder.append("highlightComponent('form:tblFinalPPS');");

        } else {

            stringBuilder2.append("removeHighLightComponent('form:tblFinalPPS');");
        }

       /* if (this.documentoMedios == null || (this.documentoMedios.getId() == null && this.documentoMedios.getContenidoDocumento() == null)) {


            stringBuilder.append("highlightComponent('form:frm_documentoMedios');");

        } else {
            stringBuilder2.append("removeHighLightComponent('form:frm_documentoMedios');");
        }*/
        if (this.documentoMecanismo == null || (this.documentoMecanismo.getId() == null && this.documentoMecanismo.getContenidoDocumento() == null)) {
            stringBuilder.append("highlightComponent('form:frm_documentoMecanismo_content');");
        } else {
            stringBuilder2.append("removeHighLightComponent('form:frm_documentoMecanismo_content');");
        }
        if (this.informeFinal == null || (this.informeFinal.getId() == null && this.informeFinal.getContenidoDocumento() == null)) {

            stringBuilder.append("highlightComponent('frmdatos:frm_informeFinal');");

        } else {
            stringBuilder2.append("removeHighLightComponent('frmdatos:frm_informeFinal');");
        }
        if (this.actaApertura == null || (this.actaApertura.getId() == null && this.actaApertura.getContenidoDocumento() == null)) {
            stringBuilder.append("highlightComponent('formMecanismos:frm_actaApertura');");

        } else {
            stringBuilder2.append("removeHighLightComponent('formMecanismos:frm_actaApertura');");
        }
        if (this.registroCIP == null || (this.registroCIP.getId() == null && this.registroCIP.getContenidoDocumento() == null)) {

            stringBuilder.append("highlightComponent('formMecanismos:frm_registroCIP');");
        } else {
            stringBuilder2.append("removeHighLightComponent('formMecanismos:frm_registroCIP');");
        }
        if (this.actaAsamblea == null || (this.actaAsamblea.getId() == null && this.actaAsamblea.getContenidoDocumento() == null)) {
            stringBuilder.append("highlightComponent('formMecanismos:frm_actaAsamblea');");
        } else {
            stringBuilder2.append("removeHighLightComponent('formMecanismos:frm_actaAsamblea');");
        }
        if (this.registroAsistencia == null || (this.registroAsistencia.getId() == null && this.registroAsistencia.getContenidoDocumento() == null)) {
            stringBuilder2.append("highlightComponent('formMecanismos:frm_registroAsistencia');");

        } else {
            stringBuilder2.append("removeHighLightComponent('formMecanismos:frm_registroAsistencia');");
        }
        if (this.invitacionPersonal == null || (this.invitacionPersonal.getId() == null && this.invitacionPersonal.getContenidoDocumento() == null)) {

            stringBuilder.append("highlightComponent('form:frm_invitacionPersonal');");
        } else {
            stringBuilder2.append("removeHighLightComponent('form:frm_invitacionPersonal');");
        }
//
//        if (this.otro == null || (this.otro.getId() == null && this.otro.getContenidoDocumento() == null)) {
//
//            stringBuilder.append("highlightComponent('frmdatos:frm_otro');");
//
//        }

        boolean mecanismoCompleto = false;
        boolean mecanismoCompletoPublicaPermanente = false;
        for (MecanismoParticipacionSocialAmbiental mecanismo : listaMecanismos) {
            if (mecanismo.getCatalogoMedio()!=null && mecanismo.getCatalogoMedio().getId() == 18) {
                mecanismoCompleto = true;
                break;
            }
        }
        
        for (MecanismoParticipacionSocialAmbiental mecanismo : listaMecanismos) {
            if (mecanismo.getCatalogoMedio()!=null && mecanismo.getCatalogoMedio().getId() == 16) {
            	mecanismoCompletoPublicaPermanente = true;
                break;
            }
        }
        
        if (!mecanismoCompleto) {
            JsfUtil.addMessageError("El mecanismos de participación social \"Asamblea pública\" es obligatorio.");
            stringBuilder2.append("highlightComponent('formMecanismos:tblMecanismosPPS');");
        }  else {
            stringBuilder2.append("removeHighLightComponent('formMecanismos:tblMecanismosPPS');");
        }
        	
        if (!mecanismoCompletoPublicaPermanente) {
            JsfUtil.addMessageError("El mecanismos de participación social \"Centro de Información Pública Permanente\" es obligatorio.");
            stringBuilder3.append("highlightComponent('formMecanismos:tblMecanismosPPS');");
        } else {
            stringBuilder3.append("removeHighLightComponent('formMecanismos:tblMecanismosPPS');");
        }
       
        if (!stringBuilder.toString().isEmpty() || !mecanismoCompleto || !mecanismoCompletoPublicaPermanente) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute(stringBuilder.toString());
            requestContext.execute(stringBuilder2.toString());
            requestContext.execute(stringBuilder3.toString());
            JsfUtil.addMessageError("Existen campos sin llenar. Por favor complete la información");
            return false;

        }

        return true;
    }


    public String enviarDatos() {
        try {
            if (!revisar && !validarInformacion()) {
                return "";
            }
            if (!revisar) {
                this.registroMediosParticipacionSocialFacade.guardar(this.listaRegistroMedios, this.listaRegistroMediosEliminados);
                this.mecanismosParticipacionSocialFacade.guardar(this.listaMecanismos, this.listaMecanismosEliminados);
                this.salvarDocumentos();
                participacionSocialAmbiental.setFechaEntregaInformeFinalPPS(new Date());
                participacionSocialFacade.guardar(getParticipacionSocialAmbiental());
                guardarPreguntas();
                if(accionesComplementarias) {
                    participacionSocialFacade.ingresarInformes(documentos, bandejaTareasBean.getProcessId(),
                            bandejaTareasBean.getTarea().getTaskId(), proyectosBean.getProyecto().getCodigo());
                }
            }

            try {
                if (revisar) {
                    boolean validado = true;

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("removeHighLightComponent('frmdatos:requiereInformacionPromotor');removeHighLightComponent('frmdatos:requiere-modificaciones');");
                    if (!observacionesController.validarObservaciones(loginBean.getUsuario(), informacionCompleta, "completarInformeFinalPPS", "pnl1")) {
                        stringBuilder.append("highlightComponent('frmdatos:requiere-modificaciones');");
                        validado = false;
                    }

                    if (informacionCompleta && !observacionesController.validarObservaciones(loginBean.getUsuario(), !requiereInformacionPromotor, "ingresarCorreccionesDocumentacion", "pnl2")) {
                        stringBuilder.append("highlightComponent('frmdatos:requiereInformacionPromotor');");
                        validado = false;
                    }

                    if (validado) {

                        Map<String, Object> data = new HashMap<>();
                        data.put("completaCorrecta", informacionCompleta);

                        if (requiereInformacionPromotor != null) {
                            data.put("requiereInformacionPromotor", requiereInformacionPromotor);
                        }

                        procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                                .getTarea().getProcessInstanceId(), data);

                    } else {
                        RequestContext requestContext = RequestContext.getCurrentInstance();
                        requestContext.execute(stringBuilder.toString());
                        return "";
                    }
                } else {
                    participacionSocialAmbiental.setPublicacion(false);
                    participacionSocialFacade.guardar(participacionSocialAmbiental);
                }
                Map<String, Object> data = new HashMap<>();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), data);


                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                return JsfUtil
                        .actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } catch (JbpmException e) {
                LOGGER.error(e);
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            }

        } catch (Exception e) {
            LOGGER.error("Error al subir el pronunciamiento", e);
            JsfUtil.addMessageError("Error al subir el pronunciamiento al servidor.");
        }

        return "";
    }

    public void validarTareaBpm() {

        String url = "/prevencion/participacionsocial/completarInformeFinalPPS.jsf";
        if (!tipo.isEmpty()) {
            url += "?tipo=" + tipo;
        }
        if (!tipo.equals("revisarDatos")) {
            JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
        }
    }

    public void uploadListenerDocumentos(FileUploadEvent event) {
        String documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event, ParticipacionSocialFacade.class, "comp");
        documentos.put(documentoActivo, documento);
    }

}
