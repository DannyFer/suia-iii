package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaValoradoPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fernando.borja
 *
 */
@Stateless
public class CronogramaValoradoServiceBean {

    /**
     * Se pega contra las entidades y llama al CRUD
     */
    @EJB
    private CrudServiceBean crudServiceBean;

    public void guardar(List<CronogramaValoradoPma> listaEntidad, List<CronogramaValoradoPma> listaEntidadEliminar, FichaAmbientalPma fichaAmbientalPma) {
        crudServiceBean.saveOrUpdate(listaEntidad);
        crudServiceBean.delete(listaEntidadEliminar);
        fichaAmbientalPma.setValidarCronogramaValoradoPlanManejoAmbiental(true);
        crudServiceBean.saveOrUpdate(fichaAmbientalPma);
    }
    
    public void guardarMineria(List<CronogramaValoradoPma> listaEntidad, List<CronogramaValoradoPma> listaEntidadEliminar, FichaAmbientalMineria fichaAmbientalMineria) {
        crudServiceBean.saveOrUpdate(listaEntidad);
        crudServiceBean.delete(listaEntidadEliminar);
        fichaAmbientalMineria.setValidarPlanManejoAmbiental(true);
        crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
    }

    @SuppressWarnings("unchecked")
	public List<CronogramaValoradoPma> recuperarPorFichaPma(FichaAmbientalPma fichaAmbientalPma) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fichaAmbientalPma", fichaAmbientalPma);
        return (List<CronogramaValoradoPma>) crudServiceBean.findByNamedQuery(CronogramaValoradoPma.OBTENER_POR_FICHA, params);
    }
    
    @SuppressWarnings("unchecked")
	public List<CronogramaValoradoPma> recuperarPorFichaMineria(FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fichaAmbientalMineria", fichaAmbientalMineria);
        return (List<CronogramaValoradoPma>) crudServiceBean.findByNamedQuery(CronogramaValoradoPma.OBTENER_POR_FICHA_MINERIA, params);
    }
    
    public List<CronogramaValoradoPma> listarTodoPorFichaPma(FichaAmbientalPma fichaAmbientalPma) throws ServiceException {
		List<CronogramaValoradoPma> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("SELECT c FROM CronogramaValoradoPma c WHERE c.fichaAmbientalPma = :fichaAmbientalPma ORDER BY c.idPlan")
					.setParameter("fichaAmbientalPma", fichaAmbientalPma).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}
	}
    
    public List<CronogramaValoradoPma> recuperarTodoPorFichaMineria(FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
		List<CronogramaValoradoPma> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("SELECT c FROM CronogramaValoradoPma c WHERE c.fichaAmbientalMineria = :fichaAmbientalMineria ORDER BY c.idPlan")
					.setParameter("fichaAmbientalMineria", fichaAmbientalMineria).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}
	}
}
