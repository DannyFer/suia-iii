package ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.bean.ElaborarOficioObservacionesBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.OficioObservaciones;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Juan Carlos Gras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Juan Carlos Gras, Fecha: 23/01/2015]
 *          </p>
 */

@RequestScoped
@ManagedBean
public class ElaborarOficioObservacionesController {

    private static final Logger LOGGER = Logger
            .getLogger(ElaborarOficioObservacionesController.class);

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

    @EJB
    private CrudServiceBean crudServiceBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{elaborarOficioObservacionesBean}")
    private ElaborarOficioObservacionesBean elaborarOficioObservacionesBean;

    public String sendAction() {
        try {
            boolean edit = false;
            OficioObservaciones oficioObservaciones = new OficioObservaciones();
            try {
                if (elaborarOficioObservacionesBean.getOficioObservaciones() != null) {
                    oficioObservaciones = elaborarOficioObservacionesBean
                            .getOficioObservaciones();
                    edit = true;
                }
            } catch (Exception e) {
                LOGGER.error("Error al obtener la información.", e);
            }
            oficioObservaciones.setAsunto(elaborarOficioObservacionesBean
                    .getAsunto());
            oficioObservaciones.setObservacion(elaborarOficioObservacionesBean
                    .getObservacion());
            oficioObservaciones.setProceso(bandejaTareasBean.getTarea()
                    .getProcessInstanceId());

            if (edit) {
                crudServiceBean.saveOrUpdate(oficioObservaciones);
            } else {
                crudServiceBean.saveOrUpdate(oficioObservaciones);
            }
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();
            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), data);


            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (JbpmException e) {
            LOGGER.error("Error al enviar la información.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        } catch (Exception e) {
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        }
        return "";

    }

    public String elaborarOficioObservaciones() {
        try {
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();

            params.put("observaciones",
                    !elaborarOficioObservacionesBean.getCorrecto());

            if (elaborarOficioObservacionesBean.getCorrecto()) {
                params.put("correccion", "");
            } else {
                params.put("correccion",
                        elaborarOficioObservacionesBean.getCorreccion());
            }
            procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean
                    .getTarea().getProcessInstanceId(), params);
            Map<String, Object> data = new ConcurrentHashMap<String, Object>();

            procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), data);

            JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
            return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
        } catch (JbpmException e) {
            LOGGER.error("Error al enviar la información.", e);
            JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
        }
        return "";

    }
}
