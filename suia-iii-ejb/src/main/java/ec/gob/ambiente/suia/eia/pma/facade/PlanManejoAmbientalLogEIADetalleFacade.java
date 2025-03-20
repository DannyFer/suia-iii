package ec.gob.ambiente.suia.eia.pma.facade;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalLogEIA;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalLogEIADetalle;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class PlanManejoAmbientalLogEIADetalleFacade implements Serializable {

	private static final long serialVersionUID = 177772884628168407L;
	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(final List<PlanManejoAmbientalLogEIADetalle> listaPlanManejoAmbientalLogEIADetalle,
			final List<PlanManejoAmbientalLogEIADetalle> listaPlanManejoAmbientalLogEIADetalleEliminados) throws ServiceException {
		try {
			crudServiceBean.saveOrUpdate(listaPlanManejoAmbientalLogEIADetalle);
			if (!listaPlanManejoAmbientalLogEIADetalleEliminados.isEmpty()) {
				crudServiceBean.delete(listaPlanManejoAmbientalLogEIADetalleEliminados);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@SuppressWarnings("unused")
	public void eliminar(List<PlanManejoAmbientalLogEIADetalle> listaPlanManejoAmbientalLogEIADetalleEliminados)
	{
		crudServiceBean.delete(listaPlanManejoAmbientalLogEIADetalleEliminados);
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
	public List<PlanManejoAmbientalLogEIADetalle> listarPorTipo(final PlanManejoAmbientalLogEIA planManejoAmbientalLogEIA, String tipo)
			throws ServiceException {
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("p_pma", planManejoAmbientalLogEIA.getId());
			params.put("p_tipo", tipo);
			
			List<PlanManejoAmbientalLogEIADetalle> lista = (List<PlanManejoAmbientalLogEIADetalle>) crudServiceBean.findByNamedQuery(
					PlanManejoAmbientalLogEIADetalle.LISTAR_POR_TIPO, params);
			
			return lista;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

}
