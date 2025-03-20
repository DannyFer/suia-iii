/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.NacionalidadService;
import ec.gob.ambiente.suia.domain.Nacionalidad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ishmael
 */
@Stateless
public class NacionalidadFacade {

    @EJB
    private NacionalidadService nacionalidadService;

    public List<Nacionalidad> buscarPorEstado(boolean estado) throws ServiceException {
        return nacionalidadService.buscarPorEstado(estado);
    }

    public Nacionalidad buscarPorId(Integer id) throws ServiceException{
    	return nacionalidadService.buscarPorId(id);
    }
}
