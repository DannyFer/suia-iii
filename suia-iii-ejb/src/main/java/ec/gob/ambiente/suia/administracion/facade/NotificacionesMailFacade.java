/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.NotificacionesMailService;
import ec.gob.ambiente.suia.domain.NotificacionesMail;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class NotificacionesMailFacade {

    @EJB
    private NotificacionesMailService notificacionesMailService;

    public void guardar(final NotificacionesMail notificacionesMail) throws ServiceException {
        notificacionesMailService.guardar(notificacionesMail);
    }

    public void saveOrUpdate(final List<NotificacionesMail> listaNotificacionesMail) throws ServiceException {
        notificacionesMailService.saveOrUpdate(listaNotificacionesMail);
    }

    public List<NotificacionesMail> listarNoEnviados() throws ServiceException {
        return notificacionesMailService.listarNoEnviados();
    }
}
