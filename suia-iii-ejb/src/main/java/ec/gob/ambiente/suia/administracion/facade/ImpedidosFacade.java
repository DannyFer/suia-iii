/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.ImpedidosService;
import ec.gob.ambiente.suia.domain.Impedido;
import ec.gob.ambiente.suia.domain.enums.TipoImpedidoEnum;
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
public class ImpedidosFacade {

    @EJB
    private ImpedidosService impedidosService;

    public void guardar(final Impedido impedido) throws ServiceException {
        impedidosService.guardar(impedido);
    }

    public void saveOrUpdate(final List<Impedido> listaImpedidos) throws ServiceException {
        impedidosService.saveOrUpdate(listaImpedidos);
    }

    public List<Impedido> listarTodo() throws ServiceException {
        return impedidosService.listarTodo();
    }

    public List<Impedido> listarFechaNotNull() throws ServiceException {
        return impedidosService.listarFechaNotNull();
    }

    public List<String> listarNumeroDocumentoTiposImpedimento(final String numeroDocumento, final TipoImpedidoEnum... tipoImpedidoEnum) throws ServiceException {
        return impedidosService.listarNumeroDocumentoTiposImpedimento(numeroDocumento, tipoImpedidoEnum);
    }

}
