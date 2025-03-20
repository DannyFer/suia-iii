package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.bean.AdjuntarOficioViabilidadBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class AdjuntarOficioViabilidadController implements Serializable {

    private static final long serialVersionUID = -35903562434524686L;
    private static final Logger LOGGER = Logger
            .getLogger(AdjuntarOficioViabilidadController.class);
    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private AreaFacade areaFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

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
    @ManagedProperty(value = "#{adjuntarOficioRequisitosTecnicosBean}")
    private AdjuntarOficioViabilidadBean adjuntarOficioViabilidadBean;


    public String realizarTarea() {
        if (adjuntarOficioViabilidadBean.isAprobacionFavorable()) {
            if (adjuntarOficioViabilidadBean.isSubido()) {
                try {
                    //Subir Oficio
                    licenciaAmbientalFacade.ingresarPronunciamiento(adjuntarOficioViabilidadBean.getOficio(),
                            adjuntarOficioViabilidadBean.getProyecto().getId(), adjuntarOficioViabilidadBean.getProyecto().getCodigo(),
                            bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_OFICIO_SOLICITAR_PRONUNCIAMIENTO);

                    procesoFacade.aprobarTarea(loginBean.getUsuario(),
                            bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
                    JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                    return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
                }catch (Exception e) {
                    LOGGER.error("Error al enviar los datos de la tarea de ingreso de pronunciamiento.", e);
                    JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
                }
            } else {
                JsfUtil.addMessageError("Para continuar debe adjuntar el documento de oficio.");
            }
        }
        else {
            JsfUtil.addMessageInfo("Para continuar con el proceso de Licenciamiento Ambiental, debe proceder a presentar los\n" +
                    "Requisitos de Viabilidad Técnica.");
        }
        return "";
    }
}
