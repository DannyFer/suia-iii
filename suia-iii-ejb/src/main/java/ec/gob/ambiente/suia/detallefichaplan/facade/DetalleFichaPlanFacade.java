package ec.gob.ambiente.suia.detallefichaplan.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DetalleFichaPlan;
import ec.gob.ambiente.suia.domain.PlanSector;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class DetalleFichaPlanFacade {
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;

	public void guadar(DetalleFichaPlan detalleFichaPlan)
			throws ServiceException {
		crudServiceBean.saveOrUpdate(detalleFichaPlan);
	}

	public void guadar(List<DetalleFichaPlan> detalleFichaPlan)
			throws ServiceException {
		crudServiceBean.saveOrUpdate(detalleFichaPlan);
	}

	public List<PlanSector> obtenerListaPlanesPorSector(String codigoSector)
			throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_codigoSector", codigoSector);
			return crudServiceBean.findByNamedQueryGeneric(
					PlanSector.FIND_CODIGO_SECTOR, parameters);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
        
	public List<TipoPlanManejoAmbiental> obtenerListaPlanes(String proceso) throws ServiceException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("tipoProceso", proceso);
			return crudServiceBean.findByNamedQueryGeneric(
					TipoPlanManejoAmbiental.FIND_PLAN, parameters);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public List<DetalleFichaPlan> buscarDetallePorFicha(Integer fichaId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fichaId", fichaId);
		List<DetalleFichaPlan> detalles = crudServiceBean
				.findByNamedQueryGeneric(DetalleFichaPlan.LISTAR_POR_ID_FICHA,
						parameters);
		return detalles;
	}

	public void eliminarDetalleFicha(Integer fichaId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fichaId", fichaId);
		List<DetalleFichaPlan> detalles = crudServiceBean
				.findByNamedQueryGeneric(DetalleFichaPlan.LISTAR_POR_ID_FICHA,
						parameters);
		crudServiceBean.delete(detalles);
	}

	public byte[] descargarPlanPorSector(String nombreDocumento) throws Exception {

		try {
			return alfrescoServiceBean.downloadDocumentByNameAndFolder(nombreDocumento, Constantes.getRootStaticDocumentsId(), true);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<DetalleFichaPlan> buscarDetallePorFichaMineria(Integer fichaId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fichaId", fichaId);
		List<DetalleFichaPlan> detalles = crudServiceBean
				.findByNamedQueryGeneric(DetalleFichaPlan.LISTAR_POR_ID_FICHA_MINERIA,
						parameters);
		return detalles;
	}

	public void eliminarDetalleFichaMineria(Integer fichaId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fichaId", fichaId);
		List<DetalleFichaPlan> detalles = crudServiceBean
				.findByNamedQueryGeneric(DetalleFichaPlan.LISTAR_POR_ID_FICHA_MINERIA,
						parameters);
		crudServiceBean.delete(detalles);
	}

}
