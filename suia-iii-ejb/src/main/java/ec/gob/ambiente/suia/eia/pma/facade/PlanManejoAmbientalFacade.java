package ec.gob.ambiente.suia.eia.pma.facade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaPmaEia;
import ec.gob.ambiente.suia.domain.CronogramaValoradoEiaPma;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;

@Stateless
public class PlanManejoAmbientalFacade implements Serializable {

	Logger LOG = Logger.getLogger(PlanManejoAmbientalFacade.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 154642884628168407L;

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<TipoPlanManejoAmbiental> obtenerListaTipoPlanManejoAmbiental()
			throws Exception {
		try {
			return (List<TipoPlanManejoAmbiental>) crudServiceBean
					.findAll(TipoPlanManejoAmbiental.class);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

	public TipoPlanManejoAmbiental obtenerTipoPlanManejoAmbiental(Integer id)
			throws Exception {
		try {
			return (TipoPlanManejoAmbiental) crudServiceBean.find(
					TipoPlanManejoAmbiental.class, id);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

	public void guardarPMA(List<PlanManejoAmbiental> listaPMA,
			boolean actualizar) throws Exception {
		try {
			if (!listaPMA.isEmpty()) {
				if (actualizar) {

					List<PlanManejoAmbiental> planes = obtenerListaPMA(listaPMA
							.get(0).getTipoPlanManejoAmbiental().getId(),
							listaPMA.get(0).getEstudioImpactoAmbiental().getId());
					for (PlanManejoAmbiental planManejoAmbiental : planes) {
						crudServiceBean.delete(planManejoAmbiental);
					}
					crudServiceBean.saveOrUpdate(listaPMA);
				} else {
					crudServiceBean.saveOrUpdate(listaPMA);
				}
			}
		} catch (Exception e) {
			LOG.error("Error al guardar plan de manejo ambiental", e);
			throw new Exception(e);
		}
	}

	public List<PlanManejoAmbiental> obtenerListaPMA(Integer tipoPlanId,
			Integer eiaId) throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_tipoId", tipoPlanId);
			parameters.put("p_eiaId", eiaId);

			return crudServiceBean.findByNamedQueryGeneric(
					PlanManejoAmbiental.LISTAR_POR_TIPOID_EIAID, parameters);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

	public void guardarCronogramaPMA(
			List<CronogramaValoradoEiaPma> listaCronogramaPMA) throws Exception {
		try {
			crudServiceBean.saveOrUpdate(listaCronogramaPMA);
		} catch (Exception e) {
			LOG.error("Error al guardar los cronogramas del PMA", e);
			throw new Exception(e);
		}
	}

	public List<CronogramaValoradoEiaPma> obtenerListaCronogramaPMA(Integer eiaId)
			throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_eiaId", eiaId);

			return crudServiceBean.findByNamedQueryGeneric(
					CronogramaValoradoEiaPma.LISTAR_POR_EIAID, parameters);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

	public List<CronogramaPmaEia> obtenerListaCronogramaPMAEIA(Integer pmaId)
			throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("p_pmaId", pmaId);

			return crudServiceBean.findByNamedQueryGeneric(
					CronogramaPmaEia.LISTAR_POR_PMA, parameters);
		} catch (Exception e) {
			LOG.error("Error al cargar los tipos de PMA", e);
			throw new Exception(e);
		}
	}

}
