/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.observaciones.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author christian
 */
@LocalBean
@Stateless
public class ObservacionesLecturaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    public void guardar(List<ObservacionesFormularios> listaObservaciones, List<ObservacionesFormularios> listaEliminar) throws ServiceException {
        try {
            if (!listaEliminar.isEmpty()) {
                for (ObservacionesFormularios obj : listaEliminar) {
                    ejecutarSentenciasNativas.ejecutarSentenciasNativasDml("DELETE FROM suia_iii.form_comments WHERE foco_id = " + obj.getId());
                }
            }

            crudServiceBean.saveOrUpdate(listaObservaciones);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    public List<ObservacionesFormularios> listarPorIdClaseNombreClase(Integer idClase, String nombreClase, String... secciones) throws ServiceException {
        try {
            if (secciones.length == 1 && secciones[0] != null && secciones[0].equals("*")) {
                return listarPorIdClaseNombreClase(idClase, nombreClase);
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            params.put("seccion", Arrays.asList(secciones));
            @SuppressWarnings("unchecked")
			List<ObservacionesFormularios> lista = (List<ObservacionesFormularios>) crudServiceBean.findByNamedQuery(ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, params);
            for (ObservacionesFormularios ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<ObservacionesFormularios> listarPorIdClaseNombreClase(Integer idClase, String nombreClase) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idClase", idClase);
            params.put("nombreClase", nombreClase);
            @SuppressWarnings("unchecked")
			List<ObservacionesFormularios> lista = (List<ObservacionesFormularios>) crudServiceBean.findByNamedQuery(ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, params);
            for (ObservacionesFormularios ob : lista) {
                ob.setDisabled(true);
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

}
