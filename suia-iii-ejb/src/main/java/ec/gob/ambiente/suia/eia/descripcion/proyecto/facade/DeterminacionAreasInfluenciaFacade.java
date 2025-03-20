/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.facade;

import ec.gob.ambiente.suia.administracion.facade.AdjuntosEiaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DeterminacionAreasInfluenciaProyecto;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
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
public class DeterminacionAreasInfluenciaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private AdjuntosEiaFacade adjuntosEiaFacade;

    public void guardarConAdjunto(final List<DeterminacionAreasInfluenciaProyecto> listaDatos, final EstudioImpactoAmbiental estudioImpactoAmbiental, final EntityAdjunto entityAdjunto, final EiaOpciones eiaOpciones) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaDatos);
            adjuntosEiaFacade.guardarAdjunto(entityAdjunto, estudioImpactoAmbiental.getClass().getSimpleName(), estudioImpactoAmbiental.getId(), eiaOpciones, estudioImpactoAmbiental);            
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void guardar(final List<DeterminacionAreasInfluenciaProyecto> listaDatos) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaDatos);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<DeterminacionAreasInfluenciaProyecto> listarPorEIATipo(final EstudioImpactoAmbiental eia, final Character tipo) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", eia.getId());
            params.put("tipo", tipo);
            List<DeterminacionAreasInfluenciaProyecto> lista = (List<DeterminacionAreasInfluenciaProyecto>) crudServiceBean.findByNamedQuery(DeterminacionAreasInfluenciaProyecto.LISTAR_POR_EIA_TIPO, params);
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
