/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
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
public class NotificacionesMailService {

    @EJB
    private CrudServiceBean crudServiceBean;
    
    private static final int DESDE = 0;
    private static final int HASTA = 0;

    @SuppressWarnings("unchecked")
    public void guardar(final NotificacionesMail notificacionesMail) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(notificacionesMail);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveOrUpdate(final List<NotificacionesMail> listaNotificacionesMail) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaNotificacionesMail);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<NotificacionesMail> listarNoEnviados() throws ServiceException {
        List<NotificacionesMail> lista = null;
        try {
            lista = (List<NotificacionesMail>) crudServiceBean.findByNamedQueryPaginado(NotificacionesMail.LISTAR_NO_ENVIADOS, null, DESDE, HASTA);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }
}
