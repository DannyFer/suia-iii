/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadFactorImpactoPma;
import ec.gob.ambiente.suia.domain.CatalogoActividadComercial;
import ec.gob.ambiente.suia.domain.FactorPma;
import ec.gob.ambiente.suia.domain.ImpactoPma;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
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
public class ActividadFactorImpactoFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<ActividadFactorImpactoPma> obtenerPorActividadPma(CatalogoActividadComercial actividadComercial) throws ServiceException {
        List<ActividadFactorImpactoPma> result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("actividadComercial", actividadComercial);
        result = (List<ActividadFactorImpactoPma>) crudServiceBean.findByNamedQuery(ActividadFactorImpactoPma.LISTAR_POR_ACTIVIDAD, params);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }

    public List<ActividadFactorImpactoPma> obtenerPorActividadFactorPma(CatalogoActividadComercial actividadComercial, FactorPma factor) throws ServiceException {
        List<ActividadFactorImpactoPma> result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("actividadComercial", actividadComercial);
        params.put("factor", factor);
        result = (List<ActividadFactorImpactoPma>) crudServiceBean.findByNamedQuery(ActividadFactorImpactoPma.LISTAR_POR_ACTIVIDAD_Y_FACTOR, params);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }

    public ActividadFactorImpactoPma obtenerPorId(Integer id) {
        return crudServiceBean.find(ActividadFactorImpactoPma.class, id);
    }

    public ImpactoPma obtenerImpactoPorId(Integer id) {
        return crudServiceBean.find(ImpactoPma.class, id);
    }

    public FactorPma obtenerFactorPorId(Integer id) {
        return crudServiceBean.find(FactorPma.class, id);
    }

    public List<FactorPma> listarFactor() {
        List<FactorPma> result = null;
        result = (List<FactorPma>) crudServiceBean.findByNamedQuery(FactorPma.LISTAR_TODO, null);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }
    
    public List<MatrizFactorImpacto> listarTodoMatriz(final Integer idImpactoAmbientalPma) throws ServiceException{
        List<MatrizFactorImpacto> result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idImpactoAmbientalPma", idImpactoAmbientalPma);
        result = (List<MatrizFactorImpacto>) crudServiceBean.findByNamedQuery(MatrizFactorImpacto.LISTAR_TODO_POR_IMPACTO_AMBIENTAL, params);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }
    
    public List<MatrizFactorImpacto> listarTodoMatrizMineria(final Integer idMatrizAmbientalMineria) throws ServiceException{
        List<MatrizFactorImpacto> result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idMatrizAmbientalMineria", idMatrizAmbientalMineria);
        result = (List<MatrizFactorImpacto>) crudServiceBean.findByNamedQuery(MatrizFactorImpacto.LISTAR_TODO_POR_IMPACTO_MINERIA, params);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }

}
