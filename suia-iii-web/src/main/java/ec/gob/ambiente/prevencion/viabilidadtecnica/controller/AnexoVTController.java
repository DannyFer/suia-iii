package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.EstudioViabilidadTecnicalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * Created by DLM on 30/10/15.
 */
@ManagedBean
@ViewScoped
public class AnexoVTController implements Serializable{


    private static final long serialVersionUID = 2472257983552062194L;
//    private static final Logger LOGGER = Logger.getLogger(AnexoVTController.class);

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
    private String documentoActivo = "";
    @Getter
    @Setter
    private Map<String, Documento> documentos;

    private Integer idProyecto;

    @EJB
    private EstudioViabilidadTecnicalFacade estudioViabilidadTecnicaFacade;

    @PostConstruct
    private void init() {
        Object variable = this.bandejaTareasBean.getTarea().getVariable(Constantes.ID_PROYECTO);
        if (variable!=null){
            this.idProyecto = new Integer(variable.toString());
        }
    }

    /*GUARDAR DOCUMENTO inicio*/
    public void uploadListenerDocumentos(FileUploadEvent event) {
        documentoActivo = event.getComponent().getAttributes().get("clave").toString();
        Documento documento = this.uploadListener(event,EstudioViabilidadTecnica.class, "pdf");
        documento.setEditar(true);//Poner el false cuando se en el método del botón guardar de cada bean que incluya a este bean.
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

        if (this.idProyecto!=null){
            documento.setIdTable(this.idProyecto);
        }
        else{
          //  documento.setIdTable(estudioViabilidadTecnicaFacade.);
        }
        documento.setExtesion("." + extension);

        documento.setMime("application/"+extension);
        return documento;
    }
}
