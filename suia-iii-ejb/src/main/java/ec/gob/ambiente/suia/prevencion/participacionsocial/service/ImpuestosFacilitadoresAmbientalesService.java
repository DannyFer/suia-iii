package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ImpuestosFacilitadoresAmbientales;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frank torres
 */
@Stateless
public class ImpuestosFacilitadoresAmbientalesService implements Serializable {

    private static final long serialVersionUID = -5526123828615336311L;
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB
    private CrudServiceBean crudServiceBean;

    public ImpuestosFacilitadoresAmbientales guardar(ImpuestosFacilitadoresAmbientales impuestosFacilitadoresAmbientales) {
        return crudServiceBean.saveOrUpdate(impuestosFacilitadoresAmbientales);
    }

    public ImpuestosFacilitadoresAmbientales obtenerImpuestoPorUbicacion(String codigoInec) throws ServiceException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("codigoInec", codigoInec);

        List<ImpuestosFacilitadoresAmbientales> listaImpuestosFacilitadoresAmbientales = (List<ImpuestosFacilitadoresAmbientales>) crudServiceBean
                .findByNamedQuery(ImpuestosFacilitadoresAmbientales.OBTENR_IMPUESTO_UBICACION,
                        parameters);
        if (!listaImpuestosFacilitadoresAmbientales.isEmpty()) {
            return listaImpuestosFacilitadoresAmbientales.get(0);
        } else {
            return obtenerImpuestoPorDefecto();
        }

    }

    public ImpuestosFacilitadoresAmbientales obtenerImpuestoPorDefecto() throws ServiceException {
        Map<String, Object> parameters = new HashMap<String, Object>();

        List<ImpuestosFacilitadoresAmbientales> listaImpuestosFacilitadoresAmbientales = (List<ImpuestosFacilitadoresAmbientales>) crudServiceBean
                .findByNamedQuery(ImpuestosFacilitadoresAmbientales.OBTENR_IMPUESTO_DEFECTO,
                        parameters);
        if (!listaImpuestosFacilitadoresAmbientales.isEmpty()) {
            return listaImpuestosFacilitadoresAmbientales.get(0);
        }
        throw new ServiceException("No se encontró un impuesto para su ubicación.");
    }


}
