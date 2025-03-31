package ec.gob.ambiente.suia.mensajenotificacion.facade;

import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.service.MensajeNotificacionServiceBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * *
 * <p/>
 * <b> Facade para la entidad Usuario</b>
 *
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 21/05/2015]
 *          </p>
 */
@Stateless
public class MensajeNotificacionFacade {

    @EJB
    private MensajeNotificacionServiceBean mensajeNotificacionServiceBean;


    /**
     * Recuperar la lista de variables.
     *
     * @return
     */
    public List<MensajeNotificacion> listarMensajesNotificaciones() {
        return mensajeNotificacionServiceBean.listarMensajesNotificacion();
    }

    /**
     * <b> Buscar variable por nombre. </b>
     *
     * @param nombre
     * @return MensajeNotificacion
     * @author frank
     * @version Revision: 1.0
     * <p>
     * [Autor: frank torres, Fecha: 21/05/2015]
     * </p>
     */
    public MensajeNotificacion buscarMensajesNotificacion(String nombre) throws ServiceException {
        return mensajeNotificacionServiceBean.buscarMensajeNotificacion(nombre);
    }

    public void guardar(MensajeNotificacion mensajeNotificacion) throws ServiceException {
        mensajeNotificacionServiceBean.guardarMensajeNotificacion(mensajeNotificacion);
    }

    /**
     * <b> Retorna el valor de la evaluación de la cadena. Si no está definida retorna "". </b>
     *
     * @param nombre
     * @return String
     * @author frank
     * @version Revision: 1.0
     * <p>
     * [Autor: frank torres, Fecha: 21/05/2015]
     * </p>
     */
    public String recuperarValorMensajeNotificacion(String nombre, Object[] valores) throws ServiceException {
        try {
            MensajeNotificacion mensajeNotificacion = mensajeNotificacionServiceBean.buscarMensajeNotificacion(nombre);
            return String.format(mensajeNotificacion.getValor(), valores);

        } catch (ServiceException e) {
            return "";
        }

    }
    
  //nueva notificacion
    public String recuperarValorMensajeNotificacionSD(String nombre) throws ServiceException {
        try {
            MensajeNotificacion mensajeNotificacion = mensajeNotificacionServiceBean.buscarMensajeNotificacion(nombre);
            return mensajeNotificacion.getValor();

        } catch (ServiceException e) {
            return "";
        }

    }


}
