package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import ec.gob.ambiente.prevencion.viabilidadtecnica.bean.ObservacionesInspeccionVTBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class ObservacionesInspeccionVTController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3710985247483232386L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{observacionesInspeccionVTBean}")
    private ObservacionesInspeccionVTBean observacionesInspeccionVTBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @EJB
    private ProcesoFacade procesoFacade;

    private static final Logger LOGGER = Logger.getLogger(ObservacionesInspeccionVTController.class);

    public void Guardar(){
        JsfUtil.addMessageInfo(observacionesInspeccionVTBean.getObservaciones());
    }

    public String completarTarea() {
            try {
                Guardar();
                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>());
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } catch (Exception e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
                return "";
            }
    }
}
