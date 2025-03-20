package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.VehiculoDesechoSanitarioProcesoPma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarPronunciamientoLABean implements Serializable {

    private static final long serialVersionUID = 298859681323455827L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DescargarPronunciamientoLABean.class);

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

    @EJB
    private ProcesoFacade procesoFacade;

//    @EJB
//    private CertificadoInterseccionFacade certificadoInterseccionFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;


    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    private byte[] documento;

    private String nombreDocumento;

    @Getter
    @Setter
    private boolean descargado = false;

    @PostConstruct
    public void init() {


        Map<String, Object> variables = null;
        try {
            variables = procesoFacade
                    .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                            .getProcessInstanceId());


            Integer idProyecto = Integer.parseInt((String) variables
                    .get(Constantes.ID_PROYECTO));

            proyecto = proyectosBean.getProyecto();


            documento = documentosFacade.descargarDocumentoAlfrescoQueryDocumentos(VehiculoDesechoSanitarioProcesoPma.class.getSimpleName()
                    , proyecto.getId(), TipoDocumentoSistema.TIPO_OFICIO_APROBACION_EIA);
            nombreDocumento = "Oficio de aprobación.pdf";
        } catch (CmisAlfrescoException e) {
            JsfUtil.addMessageError("Error al descargar el documento. Intente más tarde.");
            LOG.error("Error al descargar el documento. Intente más tarde.", e);
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
            LOG.error("Error al realizar la operación.", e);
        }

    }

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            descargado = true;
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento), "application/pdf");
            content.setName(nombreDocumento);

        }
        return content;

    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/licenciamiento-ambiental/documentos/descargarPronunciamiento.jsf");
    }

}

