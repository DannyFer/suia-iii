package ec.gob.ambiente.suia.eia.mediofisico.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.CuerpoHidrico;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.UsoCuerpoHidrico;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class CuerpoHidricoService {
	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<CuerpoHidrico> cuerpoHidricoXEiaId(EstudioImpactoAmbiental estudioImpactoAmbiental) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("estudioImpactoAmbiental", estudioImpactoAmbiental);

		List<CuerpoHidrico> listaCuerpo = (List<CuerpoHidrico>) crudServiceBean
				.findByNamedQuery(CuerpoHidrico.LISTAR_POR_ID_EIA, params);
		return listaCuerpo;
	}

	public List<CoordenadaGeneral> listarCoordenadaGeneral(String nombreTabla, Integer idTabla) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nombreTabla", nombreTabla);
		params.put("idTabla", idTabla);
		return (List<CoordenadaGeneral>) crudServiceBean.findByNamedQuery(CoordenadaGeneral.LISTAR_POR_ID_NOMBRE_TABLA, params);
	}

	public List<UsoCuerpoHidrico> listarUsoCuerpoHidrico(CuerpoHidrico cuerpoHidrico) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cuerpoHidrico", cuerpoHidrico);
		return (List<UsoCuerpoHidrico>) crudServiceBean.findByNamedQuery(UsoCuerpoHidrico.LISTAR_POR_CUERPO_HIDRICO, params);
	}
}
