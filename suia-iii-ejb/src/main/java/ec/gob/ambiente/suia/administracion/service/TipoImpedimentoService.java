/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoImpedimento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class TipoImpedimentoService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public List<TipoImpedimento> listarTipoImpedimentosActivos() throws ServiceException {
        List<TipoImpedimento> lista = null;
        try {
            lista = (List<TipoImpedimento>) crudServiceBean.findByNamedQuery("TipoImpedimento.listarActivos", null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }
}
