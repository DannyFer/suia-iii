package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorProyectoLogFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class NotificarAceptacionAsignacionPagoParticipacionSocialBean implements
        Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7037100180666779170L;

    private static final Logger LOGGER = Logger
            .getLogger(NotificarAceptacionAsignacionPagoParticipacionSocialBean.class);
    private final String nombreDocumento = "Formatos_Participación_Social.zip";
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private FacilitadorProyectoLogFacade facilitadorProyectoLogFacade;
    @EJB
    private FacilitadorFacade facilitadorFacade;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
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
    private Boolean correcto;
    @Getter
    @Setter
    private String numeroFacilitadores;
    @Getter
    @Setter
    private String observacionCorreccion = "";
    @Getter
    @Setter
    private Integer cantidadRechazo;
    @Getter
    @Setter
    private String mensajeAprobacion = "";
    @Getter
    @Setter
    private String mensajeRechazo = "";
    @Getter
    @Setter
    private String mensajeUsuario = "";
    @Getter
    @Setter
    private byte[] documento;

    @Getter
    @Setter
    private boolean descargado;
    @Getter
    @Setter
    private Map<String, Object> variables;

    @Getter
    @Setter
    private String facilitadores;
    
    //CF: Aumento de variables
    @Getter
    @Setter
    private List<String> listaClaves;
    
    @Getter
    @Setter
    private String documentoActivo = "";
    
    @Getter
    @Setter
    private Map<String, Documento> documentos;
    
    private byte[] documentoFactura;
    
    private Documento documentoFacturaFacilitador;

    @PostConstruct
    public void init() {
        //correcto = true;
        try {
            variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),
                    bandejaTareasBean.getTarea().getProcessInstanceId());
            numeroFacilitadores = (String) variables.get("numeroFacilitadores");
            cantidadRechazo = facilitadorProyectoLogFacade.buscarRechazos(loginBean.getUsuario());
            configurarMensajes();
            documento = documentosFacade.descargarDocumentoPorNombre(nombreDocumento);
            
            documentoFactura = documentosFacade.descargarDocumentoAlfrescoQueryDocumentos(ParticipacionSocialAmbiental.class.getSimpleName(), 
            		proyectosBean.getProyecto().getId(), TipoDocumentoSistema.FACTURA_PAGO_FACILITADORES);
            
            if(documentoFactura != null){
            	 documentoFacturaFacilitador = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), 
                 		ParticipacionSocialAmbiental.class.getSimpleName(), TipoDocumentoSistema.FACTURA_PAGO_FACILITADORES).get(0);
            }
           
           
            
            facilitadores = participacionSocialFacade.listadoFacilitadores(
                    proyectosBean.getProyecto());

            if (facilitadores.isEmpty()) {
                facilitadores = "<b>Aun no hay facilitadores.</b>";
            }
            
            listaClaves = new ArrayList<>();
            listaClaves.add("documentoJustificacion");
            
            documentos = new HashMap<>();
        } catch (JbpmException e) {
            LOGGER.error("Error al recuperar los datos del proceso.", e);
            JsfUtil.addMessageError("Error al recuperar los datos del proceso. Intente más tarde.");
        } catch (CmisAlfrescoException e) {
            LOGGER.error("Error al recuperar los documentos", e);
            JsfUtil.addMessageError("Error al recuperar los documentos. Intente más tarde.");
        } catch (ServiceException e) {

            LOGGER.error("Error al recuperar los datos del proceso.", e);
            JsfUtil.addMessageError("Error al recuperar los datos del proceso. Intente más tarde.");
        }

    }


    private void configurarMensajes() {
        mensajeUsuario = "Yo " +
                loginBean.getUsuario().getPersona().getNombre() +
                " me comprometo a realizar el Proceso de Participación Social como facilitador socioambiental.";

        mensajeAprobacion = "Usted debe realizar la visita previa al área de influencia directa del proyecto " +
                proyectosBean.getProyecto().getNombre() +
                " en coordinación con el promotor para identificar las condiciones socio-comunicacionales locales y" +
                " establecer los mecanismos de participación social más adecuados en función a las características sociales." +
                " Finalizada la visita previa usted debe presentar un informe técnico con los debidos respaldos empíricos.";

        if (cantidadRechazo == 2) {
            mensajeRechazo = "Esta es la tercera vez que usted rechaza su participación en el proceso de Participación Social." +
                    " Recuerde que a la cuarta vez que rechace será eliminada definitivamente su condición de Facilitador socioambiental.";
        }
        if (cantidadRechazo >= 3) {
            mensajeRechazo = "Esta es la cuarta vez que usted rechaza su participación en el proceso de Participación Social." +
                    " Recuerde que a la cuarta vez que rechace será eliminada definitivamente su condición de Facilitador socioambiental.";
        }
    }


    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            descargado = true;
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento), "application/vnd.ms-excel");
            content.setName(nombreDocumento);

        }
        return content;

    }
    
    //CF: subida de documento de rechazo.
    @Getter
    @Setter
    Documento documentoRechazo = new Documento();
    public void uploadListenerDocumentos(FileUploadEvent event) {
        documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event, ParticipacionSocialAmbiental.class, "pdf");
        documentoRechazo = documento;
        documentos.put(documentoActivo, documento);
        documentoActivo = "";
    }
    
    private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
        byte[] contenidoDocumento = event.getFile().getContents();
        Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
        documento.setNombre(event.getFile().getFileName());
        return documento;
    }
    
    /**
     * Crea el documento
     *
     * @param contenidoDocumento arreglo de bytes
     * @param clazz              Clase a la cual se va a ligar al documento
     * @param extension          extension del archivo
     * @return Objeto de tipo Documento
     */
    public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(clazz.getSimpleName());
        documento.setIdTable(proyectosBean.getProyecto().getId());
        documento.setExtesion("." + extension);

        documento.setMime("application/pdf");
        return documento;
    }
        
    public StreamedContent getStreamFactura() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documentoFactura != null) {
            descargado = true;
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documentoFactura), "application/pdf");
            content.setName(documentoFacturaFacilitador.getNombre());
        }
        return content;

    }

}
