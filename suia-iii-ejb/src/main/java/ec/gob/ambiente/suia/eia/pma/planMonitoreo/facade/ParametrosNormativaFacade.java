/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class ParametrosNormativaFacade {


    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<ParametrosNormativas> listaParametrosNormativas, final List<ParametrosNormativas> listaParametrosNormativasEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaParametrosNormativas);
            if (!listaParametrosNormativasEliminados.isEmpty()) {
                crudServiceBean.delete(listaParametrosNormativasEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de ParametrosNormativas por Normativa
     * @param codTabla Normativa
     * @return Lista de ParametrosNormativas
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<ParametrosNormativas> listarPorTabla(final Integer codTabla) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idTablaNormativa", codTabla);
            List<ParametrosNormativas> lista = (List<ParametrosNormativas>) crudServiceBean.findByNamedQuery(ParametrosNormativas.LISTAR_POR_TABLA, params);
       
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de ParametrosNormativas por Normativa. Retorna los elementos con sus respectivos Tablas y Normativas
     * @param codTabla Normativa
     * @return Lista de ParametrosNormativas
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<ParametrosNormativas> listarPorTablaFull(final Integer codTabla) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idTablaNormativa", codTabla);
            List<ParametrosNormativas> lista = (List<ParametrosNormativas>) crudServiceBean.findByNamedQuery(ParametrosNormativas.LISTAR_POR_TABLA, params);
            for(ParametrosNormativas param : lista){
                TablasNormativas tabla = param.getTablaNormativas();
                Normativas normativa = tabla.getNormativa();
                normativa.getDescripcion();
            }

            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

}
