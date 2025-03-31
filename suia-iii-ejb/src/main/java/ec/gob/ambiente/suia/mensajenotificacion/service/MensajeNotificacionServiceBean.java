package ec.gob.ambiente.suia.mensajenotificacion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class MensajeNotificacionServiceBean {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;
    
    private static final Logger LOG = Logger.getLogger(MensajeNotificacionServiceBean.class);

    @SuppressWarnings("unchecked")
    public List<MensajeNotificacion> listarMensajesNotificacion() {

        List<MensajeNotificacion> mensajeNotificacions = (List<MensajeNotificacion>) crudServiceBean.findAll(MensajeNotificacion.class);
        return mensajeNotificacions;
    }

    public MensajeNotificacion buscarMensajeNotificacion(String nombre) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nombre", nombre != null ? nombre.trim() : "");
        try {
            List<MensajeNotificacion> mensajeNotificacions = (List<MensajeNotificacion>) crudServiceBean.findByNamedQuery(MensajeNotificacion.FIND_BY_NAME, params);
            if (!mensajeNotificacions.isEmpty()) {
                return mensajeNotificacions.get(0);
            } else {
            	LOG.error(">>>>>>>>>>>>>>>>>>No se hallo un mensaje para "+nombre);
                throw new ServiceException("No se encontró la variable solicitada.");
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }

    public void guardarMensajeNotificacion(final MensajeNotificacion mensajeNotificacion) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(mensajeNotificacion);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

}
