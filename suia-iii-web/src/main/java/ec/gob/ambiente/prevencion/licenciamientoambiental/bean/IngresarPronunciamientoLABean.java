package ec.gob.ambiente.prevencion.licenciamientoambiental.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.OficioAprobacionEia;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IngresarPronunciamientoLABean implements Serializable {

    private static final long serialVersionUID = 2975076042323496247L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(IngresarPronunciamientoLABean.class);

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

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
    private File oficioPdf;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    private File pronunciamiento;

    @Getter
    @Setter
    private boolean pronunciamientoFavorable;

    @Getter
    @Setter
    private boolean descargado = false;

    @PostConstruct
    public void init() {
        try {
            Map<String, Object> variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());

            Integer idProyecto = Integer.parseInt((String) variables.get(Constantes.ID_PROYECTO));
            proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorId(idProyecto);

            List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(),
                    OficioAprobacionEia.class.getName(), TipoDocumentoSistema.TIPO_OFICIO_SOLICITAR_PRONUNCIAMIENTO);

            if (documentos != null && !documentos.isEmpty()) {
                Documento descripcionProcesoDoc = documentos.get(0);
                byte[] content = documentosFacade.descargar(descripcionProcesoDoc.getIdAlfresco());
                File tempFile = File.createTempFile(descripcionProcesoDoc.getNombre(), "." + descripcionProcesoDoc.getExtesion());
                FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());
                fos.write(content);
                fos.close();
                oficioPdf = tempFile;
            } else {
                JsfUtil.addMessageError("Error al descargar el documento. Intente más tarde.");
            }
        } catch (CmisAlfrescoException e) {
            JsfUtil.addMessageError("Error al descargar el documento. Intente más tarde.");
            LOG.error("Error al descargar el documento. Intente más tarde.", e);
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }
    }

    public StreamedContent fileStreamOficio() throws IOException {
        InputStream stream = new FileInputStream(oficioPdf);
        DefaultStreamedContent content = new DefaultStreamedContent(stream, "application/pdf", oficioPdf.getName());
        descargado = true;
        return content;
    }

    public void adjuntarPronunciamiento(FileUploadEvent event) {
        if (event != null) {
            pronunciamiento = JsfUtil.upload(event);
            JsfUtil.addMessageInfo("El archivo " + event.getFile().getFileName() + " fue adjuntado correctamente.");
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/documentos/ingresarPronunciamiento.jsf");
    }
}

