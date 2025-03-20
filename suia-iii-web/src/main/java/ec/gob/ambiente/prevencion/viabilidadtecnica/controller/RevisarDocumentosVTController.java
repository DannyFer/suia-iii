package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.prevencion.viabilidadtecnica.bean.RevisarDocumentosVTBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class RevisarDocumentosVTController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3710785952483232386L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{revisarDocumentosVTBean}")
    private RevisarDocumentosVTBean revisarDocumentosVTBean;

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

    private static final Logger LOGGER = Logger.getLogger(RevisarDocumentosVTController.class);

    public String completarTarea() {
            try {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                params.put("requiereCorrecciones", revisarDocumentosVTBean.getRequiereCorreciones());
                params.put("requiereObservaciones", revisarDocumentosVTBean.getRequiereObservaciones());

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
