/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ishmael
 */
@Stateless
public class TipoOrganizacionService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<TipoOrganizacion> buscarPorEstado(boolean estado) throws ServiceException {
        List<TipoOrganizacion> listaTipoOrganizacion = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("estado", estado);
            listaTipoOrganizacion = (List<TipoOrganizacion>) crudServiceBean.findByNamedQuery(TipoOrganizacion.FIND_BY_STATE, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaTipoOrganizacion;
    }

}
