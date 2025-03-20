package ec.gob.ambiente.suia.plansector.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlanSector;

@Stateless
public class PlanSectorFacade {
	@EJB
	private CrudServiceBean crudServiceBean;

	public List<PlanSector> buscarPlanSector(Integer sectorId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sectorId", sectorId);
		@SuppressWarnings("unchecked")
		List<PlanSector> planes = (List<PlanSector>) crudServiceBean
				.findByNamedQuery(PlanSector.FIND_ID_SECTOR, parameters);
		return planes;
	}
}
