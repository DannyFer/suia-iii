/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Actividad;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CronogramaActividadesPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
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
public class CronogramaConstruccionFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(CronogramaActividadesPma cronogramaActividadesPma, List<Actividad> listaEliminar, FichaAmbientalPma fichaAmbientalPma) throws ServiceException {
        List<Actividad> listaActividad = cronogramaActividadesPma.getActividadList();
        CronogramaActividadesPma cronogramaActividadesPmaPersist = crudServiceBean.saveOrUpdate(cronogramaActividadesPma);
        for (Actividad a : listaActividad) {
            a.setCronogramaActividadesPma(cronogramaActividadesPmaPersist);
            crudServiceBean.saveOrUpdate(a);
        }
        fichaAmbientalPma.setValidarCronogramaConstruccionOperacionProyecto(true);
        crudServiceBean.saveOrUpdate(fichaAmbientalPma);
        crudServiceBean.delete(listaEliminar);
    }

    @SuppressWarnings("unchecked")
	public CronogramaActividadesPma obtenerPorFichaPmaCatalogo(FichaAmbientalPma fichaAmbientalPma, CatalogoCategoriaFase catalogoCategoriaFase) throws ServiceException {
        List<CronogramaActividadesPma> result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fichaAmbientalPma", fichaAmbientalPma);
        params.put("catalogoCategoriaFase", catalogoCategoriaFase);
        result = (List<CronogramaActividadesPma>) crudServiceBean.findByNamedQuery(CronogramaActividadesPma.OBTENER_POR_FICHAPMA_CATALOGO, params);
        if (result != null && !result.isEmpty()) {
            CronogramaActividadesPma cronogramaActividadesPma = result.get(0);
            cronogramaActividadesPma.setActividadList(obtenerActividadePorCronogramaPma(cronogramaActividadesPma));
            return cronogramaActividadesPma;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public List<Actividad> obtenerActividadePorCronogramaPma(CronogramaActividadesPma cronogramaActividadesPma) throws ServiceException {
        List<Actividad> result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cronogramaActividadesPma", cronogramaActividadesPma);
        result = (List<Actividad>) crudServiceBean.findByNamedQuery(Actividad.OBTENER_POR_CRONOGRAMA, params);
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }

}
