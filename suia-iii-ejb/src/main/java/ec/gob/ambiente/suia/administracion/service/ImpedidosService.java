/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Impedido;
import ec.gob.ambiente.suia.domain.enums.TipoImpedidoEnum;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class ImpedidosService {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public void guardar(final Impedido impedido) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(impedido);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveOrUpdate(final List<Impedido> listaImpedidos) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaImpedidos);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Impedido> listarTodo() throws ServiceException {
        List<Impedido> lista = null;
        try {
            lista = (List<Impedido>) crudServiceBean.findByNamedQuery(Impedido.LISTAR_TODO, null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public List<Impedido> listarFechaNotNull() throws ServiceException {
        List<Impedido> lista = null;
        try {
            lista = (List<Impedido>) crudServiceBean.findByNamedQuery(Impedido.LISTAR_FECHA_NOT_NULL, null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

    @SuppressWarnings("unchecked")
    public List<String> listarNumeroDocumentoTiposImpedimento(final String numeroDocumento, final TipoImpedidoEnum... tipoImpedidoEnum) throws ServiceException {
        List<String> lista = null;
        try {
            List<String> listaParametros = new ArrayList<String>();
            for (TipoImpedidoEnum t : tipoImpedidoEnum) {
                listaParametros.add(t.toString());
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("numeroDocumento", numeroDocumento);
            params.put("tipoImpedimento", listaParametros);
            lista = crudServiceBean.findByNamedQueryGeneric(Impedido.LISTAR_NUMERO_DOCUMENTO_TIPO_IMPEDIMENTO_ACTIVO, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }

}
