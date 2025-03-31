/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.fauna.facade;

import ec.gob.ambiente.suia.administracion.facade.AdjuntosEiaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.MedioBiotico;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
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
public class MedioBioticoFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private AdjuntosEiaFacade adjuntosEiaFacade;
    @EJB
    private EiaOpcionesFacade eiaOpcionesFacade;

    public void guardar(MedioBiotico medioBiotico, List<EntityAdjunto> listAdjuntos, EiaOpciones eiaOpciones) throws ServiceException {
        try {
            eiaOpcionesFacade.guardar(medioBiotico.getEstudioImpactoAmbiental(), eiaOpciones);
            MedioBiotico medioBioticoPersist = crudServiceBean.saveOrUpdate(medioBiotico);
            adjuntosEiaFacade.guardarAdjunto(listAdjuntos, MedioBiotico.class.getSimpleName(), medioBioticoPersist.getId());
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public MedioBiotico recuperarPorEia(EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("estudioImpactoAmbiental", estudioImpactoAmbiental);
        List<MedioBiotico> result = (List<MedioBiotico>) crudServiceBean.findByNamedQuery(MedioBiotico.BUSCAR_POR_EIA, params);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return new MedioBiotico();
    }

}
