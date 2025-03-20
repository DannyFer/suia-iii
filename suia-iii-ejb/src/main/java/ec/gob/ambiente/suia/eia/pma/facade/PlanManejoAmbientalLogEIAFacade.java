package ec.gob.ambiente.suia.eia.pma.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalLogEIA;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class PlanManejoAmbientalLogEIAFacade implements Serializable {

	private static final long serialVersionUID = 154642884628168407L;
	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(PlanManejoAmbientalLogEIA planManejoAmbientalLogEIA,
			final PlanManejoAmbientalLogEIA planManejoAmbientalLogEIAEliminados) throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(planManejoAmbientalLogEIA);
			if (planManejoAmbientalLogEIAEliminados!=null) {
				crudServiceBean.delete(planManejoAmbientalLogEIAEliminados);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/**
	 * Recupera la lista de Plan de Manejo Ambiental de acuerdo al Estudio
	 * 
	 * @param estudioImpactoAmbiental
	 *            El estudio de impacto ambiental
	 * @return Lista Plan de Manejo Ambiental
	 * @throws ServiceException
	 *             Si existe un error en la consulta hacia la base de datos
	 */
	@SuppressWarnings("unchecked")
	public List<PlanManejoAmbientalLogEIA> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental)
			throws ServiceException {
		try {

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
			params.put("tipoPlan", "EIA");
			List<PlanManejoAmbientalLogEIA> lista = (List<PlanManejoAmbientalLogEIA>) crudServiceBean.findByNamedQuery(
					PlanManejoAmbientalLogEIA.LISTAR_POR_EIA, params);
			for (PlanManejoAmbientalLogEIA planManejoAmbientalLogEIA : lista) {
				planManejoAmbientalLogEIA.getPlanManejoAmbientalLogEIADetalle().size();
				planManejoAmbientalLogEIA.getCronogramaPmaEIA().size();

			}
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

}
