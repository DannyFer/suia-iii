/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Normativas;
import ec.gob.ambiente.suia.domain.TablasNormativas;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Oscar Campana
 */
@LocalBean
@Stateless
public class TablasNormativasFacade {


    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(final List<TablasNormativas> listaTablasNormativas, final List<TablasNormativas> listaTablasNormativasEliminados) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaTablasNormativas);
            if (!listaTablasNormativasEliminados.isEmpty()) {
                crudServiceBean.delete(listaTablasNormativasEliminados);
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recupera la lista de ParametrosNormativas por Normativa
     *
     * @param normativa Normativa
     * @return Lista de ParametrosNormativas
     * @throws ServiceException Si existe un error en la consulta hacia la base de datos
     */
    public List<TablasNormativas> listarPorNormativa(final Normativas normativa) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idNormativa", normativa.getId());
            List<TablasNormativas> lista = (List<TablasNormativas>) crudServiceBean.findByNamedQuery(TablasNormativas.LISTAR_POR_NORMATIVA, params);
            for (TablasNormativas tabla : lista) {
                tabla.getNormativa().getId();
            }
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }


}
