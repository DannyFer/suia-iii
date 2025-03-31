/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.TipoOrganizacionService;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ishmael
 */
@Stateless
public class TipoOrganizacionFacade {

    @EJB
    private TipoOrganizacionService tipoOrganizacionService;

    public List<TipoOrganizacion> buscarPorEstado(boolean estado) throws ServiceException {
        return tipoOrganizacionService.buscarPorEstado(estado);
    }

}
