/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import ec.gob.ambiente.suia.administracion.service.TipoImpedimentoService;
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
public class TipoImpedimentoFacade {

    @EJB
    private TipoImpedimentoService tipoImpedimentoService;

    public List<TipoImpedimento> listarTipoImpedimentosActivos() throws ServiceException {
        return tipoImpedimentoService.listarTipoImpedimentosActivos();
    }
}
