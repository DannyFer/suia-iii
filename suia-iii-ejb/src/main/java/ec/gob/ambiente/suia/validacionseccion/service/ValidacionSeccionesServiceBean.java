package ec.gob.ambiente.suia.validacionseccion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ValidacionSecciones;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ValidacionSeccionesServiceBean {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    @SuppressWarnings("unchecked")
    public List<ValidacionSecciones> listarValidacionSecciones() {

        List<ValidacionSecciones> ValidacionSeccioness = (List<ValidacionSecciones>) crudServiceBean.findAll(ValidacionSecciones.class);
        return ValidacionSeccioness;
    }

    public List<ValidacionSecciones> buscarValidacionesSecciones(String nombre, String idClase) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nombreClase", nombre != null ? nombre.trim() : "");
        params.put("idClase", idClase != null ? idClase.trim() : "");
        try {
            @SuppressWarnings("unchecked")
			List<ValidacionSecciones> ValidacionSeccioness = (List<ValidacionSecciones>) crudServiceBean.findByNamedQuery(ValidacionSecciones.FIND_BY_NAME_ID_CLASS, params);
            return ValidacionSeccioness;

        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }

    public ValidacionSecciones buscarValidacionSeccionesNombreSeccion(String nombre, String seccion, String idClase) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nombreClase", nombre != null ? nombre.trim() : "");
        params.put("valorSeccion", seccion != null ? seccion.trim() : "");
        params.put("idClase", idClase != null ? idClase.trim() : "");
        try {
            @SuppressWarnings("unchecked")
			List<ValidacionSecciones> ValidacionSeccioness = (List<ValidacionSecciones>) crudServiceBean.findByNamedQuery(ValidacionSecciones.FIND_BY_NAME_SECTION_ID_CLASS, params);
            if (!ValidacionSeccioness.isEmpty()) {
                return ValidacionSeccioness.get(0);
            } else {
                throw new ServiceException("No se encontró la sección solicitada.");
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }

    public Boolean existeValidacionSeccionesNombreSeccion(String nombre, String seccion, String idClase) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nombreClase", nombre != null ? nombre.trim() : "");
        params.put("valorSeccion", seccion != null ? seccion.trim() : "");
        params.put("idClase", idClase != null ? idClase.trim() : "");
        try {
            @SuppressWarnings("unchecked")
			List<ValidacionSecciones> ValidacionSeccioness = (List<ValidacionSecciones>) crudServiceBean.findByNamedQuery(ValidacionSecciones.FIND_BY_NAME_SECTION_ID_CLASS, params);
            if (!ValidacionSeccioness.isEmpty()) {
                return true;
            } else {
            }

        } catch (Exception e) {
        }

        return false;

    }

    public void guardarValidacionSecciones(final ValidacionSecciones ValidacionSecciones) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(ValidacionSecciones);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }


}
