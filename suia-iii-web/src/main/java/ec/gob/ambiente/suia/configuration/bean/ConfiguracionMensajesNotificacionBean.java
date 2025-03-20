/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.configuration.bean;

import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * <b>Configura los mensajes de notificación de la aplicación. </b>
 *
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 21/05/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ConfiguracionMensajesNotificacionBean implements Serializable {

    private static final long serialVersionUID = 3968717261909051746L;

    private static final Logger LOGGER = Logger.getLogger(ConfiguracionMensajesNotificacionBean.class);
    @Getter
    @Setter
    List<MensajeNotificacion> mensajes;
    @Getter
    @Setter
    MensajeNotificacion mensajesActivo;

    @EJB
    private MensajeNotificacionFacade mensajeNoteificacionFacade;

    @PostConstruct
    public void init() {
        try {
            clear();
        } catch (Exception exception) {
            LOGGER.error("Error al cargar las notificaciones", exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }

    public void clear() {
        mensajesActivo = new MensajeNotificacion();
        mensajes = mensajeNoteificacionFacade.listarMensajesNotificaciones();
    }

    public void edit(MensajeNotificacion mensajeNotificacion) {
        mensajesActivo = mensajeNotificacion;
    }

    public void delete(MensajeNotificacion mensajeNotificacion) {
        mensajeNotificacion.setEstado(false);
        mensajesActivo = mensajeNotificacion;
        save();
    }

    public void save() {
        try {
            mensajeNoteificacionFacade.guardar(mensajesActivo);
            JsfUtil.addMessageInfo("Datos guardados satisfactoriamente.");
            clear();
        } catch (ServiceException e) {
            JsfUtil.addMessageError("Error al guardar la información.");
            LOGGER.error("Error al guardar la información.", e);
        }
    }
}
