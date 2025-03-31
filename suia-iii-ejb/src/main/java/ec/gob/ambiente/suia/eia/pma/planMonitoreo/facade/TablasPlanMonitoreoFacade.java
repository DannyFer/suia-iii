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
import ec.gob.ambiente.suia.domain.PlanMonitoreoEia;
import ec.gob.ambiente.suia.domain.TablasPlanMonitoreo;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class TablasPlanMonitoreoFacade {


    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<TablasPlanMonitoreo> listaTablasPlanMonitoreo, final List<TablasPlanMonitoreo> listaTablasPlanMonitoreoEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaTablasPlanMonitoreo);
            if (!listaTablasPlanMonitoreoEliminados.isEmpty()) {
                crudServiceBean.delete(listaTablasPlanMonitoreoEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de TablasPlanMonitoreo por PlanMonitoreo
     * @param planMonitoreo PlanMonitoreoEia
     * @return Lista de TablasPlanMonitoreo
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<TablasPlanMonitoreo> listarPorPlan(final PlanMonitoreoEia planMonitoreo) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idPlanMonitoreo", planMonitoreo.getId());
            List<TablasPlanMonitoreo> lista = (List<TablasPlanMonitoreo>) crudServiceBean.findByNamedQuery(TablasPlanMonitoreo.LISTAR_POR_PLAN, params);
       
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    

}
