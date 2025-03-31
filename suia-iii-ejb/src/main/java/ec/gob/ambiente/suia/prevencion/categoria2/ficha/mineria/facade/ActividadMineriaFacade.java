/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author lili
 */
@LocalBean
@Stateless
public class ActividadMineriaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardarActividadMinera(ActividadMinera actividadMinera) {
        crudServiceBean.saveOrUpdate(actividadMinera);
    }

    public void eliminarActividadMinera(ActividadMinera actividadMinera) {
        crudServiceBean.delete(actividadMinera);
    }

    @SuppressWarnings("unchecked")
    public List<ActividadMinera> listarPorFichaAmbiental(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idMineria", fichaAmbientalMineria.getId());
            return (List<ActividadMinera>) crudServiceBean.findByNamedQuery(ActividadMinera.LISTAR_POR_FICHA, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<ActividadMinera> listarPorFichaAmbientalHistorial(FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
    	try {
    		
    		Query query = crudServiceBean.getEntityManager().createQuery("SELECT a FROM ActividadMinera a WHERE a.estado = true AND a.idMineria = :idMineria AND a.idRegistroOriginal != null");
    		query.setParameter("idMineria", fichaAmbientalMineria.getId());
    		
    		List<ActividadMinera> resultado = query.getResultList();
    		if(resultado != null && !resultado.isEmpty()){
    			return resultado;
    		}
    		return null;
    		
    	 } catch (RuntimeException e) {
             throw new ServiceException(e);
         }
    }
    
}
