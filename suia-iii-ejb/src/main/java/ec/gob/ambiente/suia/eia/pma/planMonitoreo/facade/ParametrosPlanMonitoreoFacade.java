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
import ec.gob.ambiente.suia.domain.ParametrosPlanMonitoreo;
import ec.gob.ambiente.suia.domain.TablasPlanMonitoreo;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class ParametrosPlanMonitoreoFacade {


    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<ParametrosPlanMonitoreo> listaParametrosPlanMonitoreo, final List<ParametrosPlanMonitoreo> listaParametrosPlanMonitoreoEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaParametrosPlanMonitoreo);
            if (!listaParametrosPlanMonitoreoEliminados.isEmpty()) {
                crudServiceBean.delete(listaParametrosPlanMonitoreoEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de ParametrosPlanMonitoreo por TablasPlanMonitoreo
     * @param tabla TablaPlanMonitoreo
     * @return Lista de ParametrosPlanMonitoreo
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<ParametrosPlanMonitoreo> listarPorTabla(final TablasPlanMonitoreo tabla) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idTablaMonitoreo", tabla.getId());
            List<ParametrosPlanMonitoreo> lista = (List<ParametrosPlanMonitoreo>) crudServiceBean.findByNamedQuery(ParametrosPlanMonitoreo.LISTAR_POR_TABLA, params);
       
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    

}
