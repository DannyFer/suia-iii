/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.TipoTratosService;
import ec.gob.ambiente.suia.domain.TipoTratos;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ishmael
 */
@Stateless
public class TipoTratosFacade {

    @EJB
    private TipoTratosService tipoTratosService;

    public List<TipoTratos> buscarPorEstado(boolean estado) throws ServiceException {
        return tipoTratosService.buscarPorEstado(estado);
    }

}
