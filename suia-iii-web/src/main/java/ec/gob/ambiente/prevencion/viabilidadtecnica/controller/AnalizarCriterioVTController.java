package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import ec.gob.ambiente.prevencion.viabilidadtecnica.bean.AnalizarCriterioVTBean;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestScoped
@ManagedBean
public class AnalizarCriterioVTController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6987285232083232386L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{analizarCriterioVTBean}")
    private AnalizarCriterioVTBean analizarCriterioVTBean;

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

    private static final Logger LOGGER = Logger.getLogger(AnalizarCriterioVTController.class);

    public String completarTarea() {
            try {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("criterioCorrecto", analizarCriterioVTBean.getCriterioCorrecto());
                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                        .getProcessInstanceId(), params);

                procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(),
                        bandejaTareasBean.getTarea().getProcessInstanceId(), params);

                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } catch (Exception e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
                return "";
            }
    }
}
